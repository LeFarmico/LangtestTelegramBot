package ability.langTestAbility

import ability.AbilityState
import ability.AbstractAbility
import bot.IMessageController
import entity.SendData
import entity.User
import entity.WordData
import inject.DataInjector
import kotlinx.coroutines.*
import messageBuilders.ButtonBuilder
import messageBuilders.EditMessageBuilder
import messageBuilders.MessageBuilder
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import repository.LanguageRepository
import repository.UserRepository
import repository.WordsRepository
import res.SystemMessages
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.List

class LangTestAbility(
    private val messageController: IMessageController,
    private val chatId: Long
) : AbstractAbility(chatId) {

    private val log = LoggerFactory.getLogger(javaClass.simpleName)
    
    private val timer by lazy { Timer(true) }
    private val testQueue: Queue<TestQuestionData> = ConcurrentLinkedQueue()
    
    private val userRepo: UserRepository = DataInjector.userRepo
    private val wordsRepository: WordsRepository = DataInjector.wordsRepo
    lateinit var languageRepository: LanguageRepository
    
    private var user: User? = null

    override fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            if (abilityState != AbilityState.STARTED) {
                userRepo.addUser(chatId, 1)
                user = userRepo.getUserByChatId(chatId)!!
                wordsRepository.createWordsCategoryByChatId(chatId, user!!.categoryId)
                val wordsList = wordsRepository.getUnansweredWordsCategoryByChatId(chatId)
                createWordsForTest(wordsList)
                action()
            }
            super.start()
        }
    }

    override fun action(actionData: Any?) {
        try {
            val answerData = actionData as TestAnswerData
            when (answerData.isCorrect) {
                true -> {
                    val wordId = testQueue.poll().wordId
                    acceptAnswer(wordId, chatId)
                    editMessage(chatId, answerData.messageId, SystemMessages.rightAnswer)
                }
                false -> {
                    val testQuestionData = testQueue.poll()
                    deniedAnswer(testQuestionData)
                    editMessage(chatId, answerData.messageId, SystemMessages.wrongAnswer)
                }
            }
        } catch (e: Exception) {
            when (e) {
                is NullPointerException -> {}
                is TypeCastException -> {
                    log.error("[ERROR] Unexpected type for actionData: ${actionData!!.javaClass.simpleName}", e)
                }
                else -> {
                    log.error("[ERROR] Unexpected error: ", e)
                }
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            if (checkForQuestions()) {
                sendMessage(chatId, getNext()!!.sendData)
            } else {
                if (checkForExam()) {
                    askForExam(chatId)
                } else {
                    scheduleNextTest()
                }
            }
        }
    }

    override fun finish() {
        testQueue.clear()
        super.finish()
    }

    private fun editMessage(chatId: Long, messageId: Int, message: String) {
        val editMessage = EditMessageBuilder
            .chatAndMessageId(chatId, messageId)
            .newMessage(message)
            .build()
        messageController.schedule(chatId, SendData.EditMessage(editMessage))
    }

    private fun sendMessage(chatId: Long, type: SendData) {
        messageController.schedule(chatId, type)
    }
    
    private fun checkForQuestions(): Boolean {
        return testQueue.isNotEmpty()
    }

    private suspend fun checkForExam(): Boolean {
        return wordsRepository.getUnansweredWordsCategoryByChatId(chatId)!!.isEmpty()
    }

    private fun getNext(): TestQuestionData? {
        return try {
            testQueue.peek()
        } catch (e: NullPointerException) {
            null
        }
    }
    
    private fun acceptAnswer(wordId: Long, chatId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            wordsRepository.addCorrectAnswer(wordId, chatId)
        }
    }
    
    private fun deniedAnswer(testQuestionData: TestQuestionData) {
        testQueue.add(testQuestionData)
    }

    private suspend fun askForLanguage(chatId: Long) {
        val langList = languageRepository.getAvailableLanguages()
        val askMessage = MessageBuilder.setChatId(chatId)
            .setText(SystemMessages.chooseLanguage)
            .setButtons {
                var builder = ButtonBuilder.setUp()
                for (i in langList.indices) {
                    builder = builder.addButton(langList[i].languageName, "langtestlangId-${langList[i].id}")
                }
                builder.build()
            }
    }

    private fun askForExam(chatId: Long) {
        val askMessage = MessageBuilder.setChatId(chatId)
            .setText(SystemMessages.askForExam)
            .setButtons {
                ButtonBuilder.setUp()
                    .addButton(SystemMessages.yes, EXAM_YES)
                    .addButton(SystemMessages.no, EXAM_NO)
                    .build()
            }.build()
        messageController.schedule(chatId, SendData.SendMessage(askMessage))
    }

    private fun scheduleNextTest() {
        try {
            val notifySendData = SendData.SendMessage(
                MessageBuilder.setChatId(chatId)
                    .setText(SystemMessages.nextTestNotifyMessage(user!!.breakTimeInMillis))
                    .build()
            )
            messageController.schedule(chatId, notifySendData)
            timer.schedule(ScheduleExam(user), user!!.breakTimeInMillis)
            log.info("Next test scheduled for $chatId")
        } catch (e: NullPointerException) {
            log.error("Next test not scheduled", e)
        }
    }

    private fun createWordsForTest(wordsList: List<WordData>?) {
        try {
            for (i in wordsList!!.indices) {
                val wordData = wordsList[i]
                val wordId = wordData.id
                val message = createTestMessage(
                    chatId = chatId,
                    wordToTranslate = wordData.word,
                    correctAnswer = wordData.translate,
                    wrongAnswers = wordsList
                        .map { it.translate }
                        .filter { it != wordData.translate }
                )
                val sendType = SendData.SendMessage(message)
                testQueue.add(TestQuestionData(chatId, sendType, wordId))
            }
        } catch (e: NullPointerException) {
            log.error("Can't find words for $chatId", e)
        }
    }

    private fun createTestMessage(
        chatId: Long,
        wordToTranslate: String,
        correctAnswer: String,
        wrongAnswers: List<String>
    ): SendMessage {
        return MessageBuilder.setChatId(chatId)
            .setText("Choose right translation of the next word: $wordToTranslate")
            .setButtons(createAnswerButtonList(correctAnswer, wrongAnswers))
            .build()
    }

    private fun createAnswerButtonList(correctAnswer: String, wrongAnswers: List<String>): InlineKeyboardMarkup {
        var buttonBuilder = ButtonBuilder.setUp()
            .addButton(correctAnswer, RIGHT_ANSWER)
        val answers = wrongAnswers.shuffled().take(2)

        for (i in answers.indices) {
            buttonBuilder = buttonBuilder.addButton(answers[i], WRONG_ANSWER)
        }
        return buttonBuilder.build(isVertical = true, shuffled = true)
    }

    inner class ScheduleExam(private val user: User?) : TimerTask() {
        override fun run() {
            if (user != null) {
                CoroutineScope(Dispatchers.Default).launch {
                    val wordsList = wordsRepository.getUnansweredWordsCategoryByChatId(chatId)
                    createWordsForTest(wordsList)
                    action()
                }
            }
        }
    }

    companion object {
        const val RIGHT_ANSWER = "langtestright"
        const val WRONG_ANSWER = "langtestwrong"
        const val FINISH = "langtestfinish"
        const val EXAM_YES = "langtestexamyes"
        const val EXAM_NO = "langtestexamno"
    }
}
