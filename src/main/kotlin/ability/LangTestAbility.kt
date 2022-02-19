package ability

import bot.IMessageController
import entity.CallbackType
import entity.User
import inject.DataInjector
import kotlinx.coroutines.*
import messageBuilders.ButtonBuilder
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import repository.UserRepository
import repository.WordsRepository
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.List

class LangTestAbility(
    private val messageController: IMessageController,
    val chatId: Long
) : AbstractAbility(chatId) {

    private val log = LoggerFactory.getLogger(javaClass.simpleName)
    private val timer by lazy { Timer(true) }
    private val testQueue: Queue<Triple<Long, CallbackType, Long>> = ConcurrentLinkedQueue()
    private val userRepo: UserRepository = DataInjector.userRepo
    private val wordsRepository: WordsRepository = DataInjector.wordsRepo
    private var user: User? = null

    override fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            if (abilityState != AbilityState.STARTED) {
                userRepo.addUser(chatId)
                user = userRepo.getUserByChatId(chatId)!!
                wordsRepository.createWordsCategoryByChatId(chatId, user!!.categoryId)

                try {
                    createExam(chatId)
                    action()
                } catch (e: NullPointerException) {
                    log.error("User with chatId: $chatId is not exist")
                }
            }
            super.start()
        }
    }

    override fun action(actionData: Any?) {
        try {
            if (actionData!! as Boolean) {
                val wordId = testQueue.poll().third
                CoroutineScope(Dispatchers.IO).launch {
                    wordsRepository.addCorrectAnswer(wordId, chatId)
                }
            } else {
                val test = testQueue.poll()
                testQueue.add(test)
            }
        } catch (e: Exception) {
            when (e) {
                is NullPointerException -> {
                    log.info("[INFO] ActionData is null for chatId: $chatId")
                }
                is TypeCastException -> {
                    log.error("[ERROR] Unexpected type for actionData: ${actionData!!.javaClass.simpleName}", e)
                }
                else -> {
                    log.error("[ERROR] Unexpected error: ", e)
                }
            }
        }
        sendTest()
    }

    override fun finish() {
        testQueue.clear()
        super.finish()
    }

    private fun sendTest() {
        val messageType = testQueue.peek()
        if (messageType != null) {
            log.info("Sending a new test.")
            messageController.schedule(messageType.first, messageType.second)
        } else {
            scheduleNextExam()
            abilityState = AbilityState.FINISHED
        }
    }

    private fun scheduleNextExam() {
        try {
            timer.schedule(ScheduleExam(user), user!!.breakTimeInMillis)
        } catch (e: NullPointerException) {
            log.error("Next exam not scheduled", e)
        }
    }

    private suspend fun createExam(chatId: Long) {
        val wordsList = wordsRepository.getUnansweredWordsCategoryByChatId(chatId)
        val answers = wordsList.map { it.translate }

        for (i in wordsList.indices) {
            val wordData = wordsList[i]
            val wordId = wordData.id
            val message = createTestMessage(
                chatId = chatId,
                wordToTranslate = wordData.word,
                correctAnswer = wordData.translate,
                wrongAnswers = answers.filter { it != wordData.translate }
            )
            val callbackType = CallbackType.SendMessage(message)
            testQueue.add(Triple(chatId, callbackType, wordId))
        }
    }

    private fun createTestMessage(
        chatId: Long,
        wordToTranslate: String,
        correctAnswer: String,
        wrongAnswers: List<String>
    ): SendMessage {
        return SendMessage().apply {
            this.chatId = chatId.toString()
            text = "Choose right translation of the next word: $wordToTranslate"
            replyMarkup = createAnswerButtonList(correctAnswer, wrongAnswers)
        }
    }

    private fun createAnswerButtonList(correctAnswer: String, wrongAnswers: List<String>): InlineKeyboardMarkup {
        var buttonBuilder = ButtonBuilder.createFirstButton(correctAnswer, "langtestright")
        val answers = wrongAnswers.shuffled().take(2)

        for (i in answers.indices) {
            buttonBuilder = buttonBuilder.addButton(answers[i], "langtestwrong")
        }
        return buttonBuilder.build(isVertical = true, shuffled = true)
    }

    inner class ScheduleExam(private val user: User?) : TimerTask() {
        override fun run() {
            if (user != null) {
                CoroutineScope(Dispatchers.Default).launch {
                    createExam(user.chatId)
                    action()
                }
            }
        }
    }
}
