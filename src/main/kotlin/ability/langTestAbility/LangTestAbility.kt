package ability.langTestAbility

import ability.IAbility
import bot.MessageSender
import command.Command
import entity.EditMessage
import entity.UserMessage
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
import kotlin.collections.List

class LangTestAbility(
    private val messageSender: MessageSender
) : IAbility {

    private val log = LoggerFactory.getLogger(javaClass.simpleName)

    private val userList = mutableMapOf<Long, LangTestUserStatus>()
    private val timer by lazy { Timer(true) }

    private val userRepo: UserRepository = DataInjector.userRepo
    private val wordsRepository: WordsRepository = DataInjector.wordsRepo
    private lateinit var languageRepository: LanguageRepository

    override fun subscribe(chatId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            userRepo.addUser(chatId, 1)
            val user = userRepo.getUserByChatId(chatId)
            wordsRepository.createWordsCategoryByChatId(chatId, user!!.categoryId)

            try {
                userList[chatId] = LangTestUserStatus(user).apply {
                    setWords(
                        wordsRepository.getUnansweredWordsCategoryByChatId(chatId, user.wordsInTest)!!
                    )
                }
                startTest(chatId)
            } catch (e: NullPointerException) {
                log.error("[ERROR] Something went wrong with chatId: $chatId.", e)
            }
        }
    }

    override fun commandAction(data: AbilityCommand?) {
        val user = userList[data!!.chatId]!!
        when (val command = data.command) {
            is Command.Answer -> {
                user.answer(command.isCorrect)
                user.next()
            }
            Command.BeginTest -> TODO()
            is Command.Exam -> TODO()
            is Command.SetCategory -> TODO()
            is Command.SetLanguage -> TODO()
            Command.TimeToNextTest -> TODO()
            else -> { log.warn("[WARN] The action not for ${javaClass.simpleName}") }
        }
    }

    private fun startTest(chatId: Long) {
        val test = userList[chatId]?.next()
        if (test != null) {
            sendTest(chatId, test)
        } else {
            // TODO not registered message
        }
    }

    override fun unsubscribe(chatId: Long) {
        userList.remove(chatId)
    }

    private fun editMessage(chatId: Long, messageId: Int, message: String) {
        val editMessage = EditMessageBuilder
            .chatAndMessageId(chatId, messageId)
            .newMessage(message)
            .build()
        messageSender.send(EditMessage(chatId, editMessage))
    }

    private fun acceptAnswer(wordId: Long, chatId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            wordsRepository.addCorrectAnswer(wordId, chatId)
        }
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
        messageSender.send(UserMessage(chatId, askMessage))
    }
    
//    private suspend fun checkForExam(): Boolean {
//        return wordsRepository.getUnansweredWordsCategoryByChatId(chatId)!!.isEmpty()
//    }
//    private fun scheduleNextTest() {
//        try {
//            val notifySendData = SendData.SendMessage(
//                MessageBuilder.setChatId(chatId)
//                    .setText(SystemMessages.nextTestNotifyMessage(user!!.breakTimeInMillis))
//                    .build()
//            )
//            messageController.schedule(chatId, notifySendData)
//            timer.schedule(ScheduleQuiz(user), user!!.breakTimeInMillis)
//            log.info("Next test scheduled for $chatId")
//        } catch (e: NullPointerException) {
//            log.error("Next test not scheduled", e)
//        }
//    }
//    inner class ScheduleQuiz(private val user: User?) : TimerTask() {
//        override fun run() {
//            if (user != null) {
//                CoroutineScope(Dispatchers.Default).launch {
//                    val wordsList = wordsRepository.getUnansweredWordsCategoryByChatId(user.chatId, user.wordsInTest)
//                    sendTest(wordsList)
//                    commandAction()
//                }
//            }
//        }
//    }

    private fun sendTest(chatId: Long, testData: TestData) {
        try {
            val message = createTestMessage(
                chatId = chatId,
                wordToTranslate = testData.wordToTranslate,
                correctAnswer = testData.answer,
                wrongAnswers = testData.falseAnswers
            )
            messageSender.send(UserMessage(chatId, message))
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
            .addButton(correctAnswer, Command.Answer.buildCallBackQuery(true))
        val answers = wrongAnswers.shuffled().take(2)

        for (i in answers.indices) {
            buttonBuilder = buttonBuilder.addButton(answers[i], Command.Answer.buildCallBackQuery(false))
        }
        return buttonBuilder.build(isVertical = true, shuffled = true)
    }
    
    companion object {
        const val RIGHT_ANSWER = "langtestright"
        const val WRONG_ANSWER = "langtestwrong"
        const val FINISH = "langtestfinish"
        const val EXAM_YES = "langtestexamyes"
        const val EXAM_NO = "langtestexamno"
    }
}
