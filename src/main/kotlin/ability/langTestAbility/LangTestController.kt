package ability.langTestAbility

import ability.IController
import bot.handler.IHandlerReceiver
import command.Command
import data.IRequestData
import data.ResponseFactory
import entity.QuizTest
import inject.DataInjector
import kotlinx.coroutines.*
import messageBuilders.*
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import repository.*
import res.SystemMessages
import state.DataState
import java.util.*

class LangTestController(private val responseReceiver: IHandlerReceiver) : IController {

    private val log = LoggerFactory.getLogger(javaClass.simpleName)

    private val timer by lazy { Timer(true) }

    private val userRepo: UserRepository = DataInjector.userRepo
    private val quizRepository: QuizRepository = DataInjector.quizRepository
    private val categoryRepository: CategoryRepository = DataInjector.categoryRepository
    private val languageRepository: LanguageRepository = DataInjector.languageRepository

    override fun commandAction(requestData: IRequestData) {
        CoroutineScope(Dispatchers.Default).launch {
            when (val command = requestData.command) {
                is Command.CorrectAnswerCallback -> {
                    handleAnswerCallback(
                        chatId = requestData.chatId,
                        messageId = requestData.messageId,
                        wordId = command.wordId,
                        isCorrect = true
                    )
                }
                is Command.IncorrectAnswerCallback -> {
                    handleAnswerCallback(
                        chatId = requestData.chatId,
                        messageId = requestData.messageId,
                        wordId = command.wordId,
                        isCorrect = false
                    )
                }
                is Command.StartQuizCallback -> {
                    handleStartCallback(
                        chatId = requestData.chatId,
                        messageId = requestData.messageId,
                        start = command.start
                    )
                }
                Command.GetQuizTest -> {
                    trySendNexQuizWord(requestData.chatId)
                }
                is Command.AskExamCallback -> {
                }
                
                is Command.SetCategoryCallback -> {
                    registerUser(
                        chatId = requestData.chatId,
                        messageId = requestData.messageId,
                        categoryId = command.categoryId
                    )
                }
                is Command.SetLanguageCallBack -> {
                    handleSetLanguageCallback(
                        chatId = requestData.chatId,
                        messageId = requestData.messageId,
                        languageId = command.languageId
                    )
                }
                Command.TimeToNextTestCommand -> {
                    sendTimeToNextQuiz(requestData.chatId)
                }
                Command.StartCommand -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        when (val user = userRepo.getUserByChatId(requestData.chatId)) {
                            DataState.Empty -> startUserRegistration(requestData.chatId)
                            is DataState.Success -> askForStartQuiz(requestData.chatId)
                            is DataState.Failure -> {
                                log.error("[ERROR] unexpected error: ${requestData.chatId}", user.exception)
                                val response = ResponseFactory.builder(requestData.chatId)
                                    .message(SystemMessages.unexpectedError)
                                    .build()
                                responseReceiver.receiveData(response)
                            }
                        }
                    }
                }
                Command.StopCommand -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        userRepo.deleteUserChatId(requestData.chatId)
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun startUserRegistration(chatId: Long) {
        askForLanguage(chatId)
    }

    private suspend fun registerUser(chatId: Long, messageId: Int, categoryId: Long) {
        val category = categoryRepository.getCategory(categoryId)!! // TODO Handle error through DataState in domain
        userRepo.addUser(
            chatId = chatId,
            categoryId = categoryId,
            languageId = category.languageId
        )
        val response = ResponseFactory.builder(chatId)
            .editCurrent(messageId)
            .message(SystemMessages.categoryChooseMessage(category.categoryName))
            .build()
        responseReceiver.receiveData(response)
        askForStartQuiz(chatId)
    }

    private suspend fun sendTimeToNextQuiz(chatId: Long) {
        when (val user = userRepo.getUserByChatId(chatId)) {
            DataState.Empty -> {
                log.warn("[WARN] User not found: $chatId")
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.userNotFound)
                    .build()
                responseReceiver.receiveData(response)
            }
            is DataState.Failure -> {
                log.error("[ERROR] unexpected error: $chatId", user.exception)
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.userNotFound)
                    .build()
                responseReceiver.receiveData(response)
            }
            is DataState.Success -> {
                val time = user.data.breakTimeInMillis
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.nextTestNotifyMessage(time))
                    .build()
                responseReceiver.receiveData(response)
            }
        }
    }

    private suspend fun askForStartQuiz(chatId: Long) {
        when (val quiz = quizRepository.getQuizTest(chatId)) {
            DataState.Empty -> {
                sendCurrentUserSettings(chatId)
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.quizStartQuestion)
                    .addButton(SystemMessages.yes, Command.StartQuizCallback.buildCallBackQuery(true))
                    .addButton(SystemMessages.no, Command.StartQuizCallback.buildCallBackQuery(false))
                    .build()
                responseReceiver.receiveData(response)
            }
            is DataState.Success -> {
                sendCurrentUserSettings(chatId)
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.quizContinueQuestion)
                    .addButton(SystemMessages.yes, Command.GetQuizTest.buildCallBackQuery())
                    .addButton(SystemMessages.no, Command.StartQuizCallback.buildCallBackQuery(false))
                    .addButton(SystemMessages.startAgain, Command.StartQuizCallback.buildCallBackQuery(true))
                    .build()
                responseReceiver.receiveData(response)
            }
            is DataState.Failure -> {
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.unexpectedError)
                    .build()
                responseReceiver.receiveData(response)
            }
        }
    }
    
    private suspend fun sendCurrentUserSettings(chatId: Long) {
        when (val user = userRepo.getUserByChatId(chatId)) {
            DataState.Empty -> {
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.userNotFound)
                    .build()
                responseReceiver.receiveData(response)
            }
            is DataState.Failure -> {
                log.error("[ERROR] unexpected error $chatId", user.exception)
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.unexpectedError)
                    .build()
                responseReceiver.receiveData(response)
            }
            is DataState.Success -> {
                try {
                    val category = categoryRepository.getCategory(user.data.categoryId)!!
                    val language = languageRepository.getLanguageById(user.data.languageId)!!
                    val message = SystemMessages.userSettingsMessage(language.languageName, category.categoryName)
                    val response = ResponseFactory.builder(chatId)
                        .message(message)
                        .build()
                    responseReceiver.receiveData(response)
                } catch (e: NullPointerException) {
                    log.error("[ERROR] Category or message not found", e)
                    val response = ResponseFactory.builder(chatId)
                        .message(SystemMessages.unexpectedError)
                        .build()
                    responseReceiver.receiveData(response)
                }
            }
        }
    }

    private suspend fun handleAnswerCallback(chatId: Long, messageId: Int, wordId: Long, isCorrect: Boolean) {
        when (isCorrect) {
            true -> { 
                quizRepository.removeWordForUser(chatId, wordId)
                val response = ResponseFactory.builder(chatId)
                    .editCurrent(messageId)
                    .message(SystemMessages.rightAnswer)
                    .build()
                responseReceiver.receiveData(response)
                trySendNexQuizWord(chatId)
            }
            false -> {
                val response = ResponseFactory.builder(chatId)
                    .editCurrent(messageId)
                    .message(SystemMessages.wrongAnswer)
                    .build()
                responseReceiver.receiveData(response)
                trySendNexQuizWord(chatId)
            }
        }
    }

    private suspend fun handleStartCallback(chatId: Long, messageId: Int, start: Boolean) {
        when (start) {
            true -> {
                quizRepository.addQuizWords(chatId)
                val response = ResponseFactory.builder(chatId)
                    .editCurrent(messageId)
                    .message(SystemMessages.startQuizMsg)
                    .build()
                responseReceiver.receiveData(response)
                trySendNexQuizWord(chatId)
            }
            false -> {
                val response = ResponseFactory.builder(chatId)
                    .editCurrent(messageId)
                    .message(SystemMessages.startQuizHelpMsg)
                    .build()
                responseReceiver.receiveData(response)
            }
        }
    }

    private suspend fun trySendNexQuizWord(chatId: Long) {
        when (val quizTest = quizRepository.getQuizTest(chatId)) {
            DataState.Empty -> endQuiz(chatId)
            is DataState.Success -> sendQuizTest(chatId, quizTest.data)
            is DataState.Failure -> {
                log.error("[ERROR] something went wrong: $chatId", quizTest.exception)
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.unexpectedError)
                    .build()
                responseReceiver.receiveData(response)
            }
        }
    }
    
    private suspend fun handleSetLanguageCallback(chatId: Long, messageId: Int, languageId: Long) {
        val language = languageRepository.getLanguageById(languageId)!!
        val response = ResponseFactory.builder(chatId)
            .editCurrent(messageId)
            .message(SystemMessages.languageChooseMessage(language.languageName))
            .build()
        responseReceiver.receiveData(response)
        askForCategory(chatId, languageId)
    }

    private fun endQuiz(chatId: Long) {
        CoroutineScope(Dispatchers.Default).launch {
            scheduleNextTest(chatId)
        }
    }

    private fun sendQuizTest(chatId: Long, quizTest: QuizTest) {
        try {
            val message = TestMessageBuilder.setChatId(chatId, quizTest.wordId)
                .setQuizText(SystemMessages.quizText, quizTest.wordToTranslate)
                .addIncorrectButtonList(quizTest.incorrectAnswers)
                .addCorrectButton(quizTest.correctAnswer)
                .build()
            val response = ResponseFactory.builder(chatId)
                .buildSendMessageObject(message)
            responseReceiver.receiveData(response)
        } catch (e: NullPointerException) {
            log.error("Can't find words for $chatId", e)
            endQuiz(chatId)
        }
    }

    private suspend fun askForLanguage(chatId: Long) {
        val langList = languageRepository.getAvailableLanguages()
        val response = ResponseFactory.builder(chatId)
            .message(SystemMessages.chooseLanguage)
            .setButtons {
                val buttonList = mutableListOf<InlineKeyboardButton>()
                for (i in langList.indices) {
                    buttonList.add(
                        InlineKeyboardButton().apply {
                            this.text = langList[i].languageName
                            this.callbackData = Command.SetLanguageCallBack.buildCallBackQuery(langList[i].id)
                        }
                    )
                }
                buttonList
            }.build()
        responseReceiver.receiveData(response)
    }

    private suspend fun askForCategory(chatId: Long, languageId: Long) {
        val categoryList = categoryRepository.getCategoriesByLanguage(languageId)
        val response = ResponseFactory.builder(chatId)
            .message(SystemMessages.chooseCategory)
            .setButtons {
                val buttonList = mutableListOf<InlineKeyboardButton>()
                for (i in categoryList.indices) {
                    buttonList.add(
                        InlineKeyboardButton().apply {
                            this.text = categoryList[i].categoryName
                            this.callbackData = Command.SetCategoryCallback.buildCallBackQuery(categoryList[i].id)
                        }
                    )
                }
                buttonList
            }.build()
        responseReceiver.receiveData(response)
    }

    private suspend fun askForExam(chatId: Long) {
        try {
            val response = ResponseFactory.builder(chatId)
                .message(SystemMessages.askForExam)
                .addButton(SystemMessages.yes, Command.AskExamCallback.buildCallBackQuery(true))
                .addButton(SystemMessages.no, Command.AskExamCallback.buildCallBackQuery(false))
                .build()
            responseReceiver.receiveData(response)
        } catch (e: NullPointerException) {
            val response = ResponseFactory.builder(chatId)
                .message(SystemMessages.unexpectedError)
                .build()
            responseReceiver.receiveData(response)
        }
    }

    private suspend fun scheduleNextTest(chatId: Long) {
        when (val user = userRepo.getUserByChatId(chatId)) {
            DataState.Empty -> {
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.userNotFound)
                    .build()
                responseReceiver.receiveData(response)
            }
            is DataState.Failure -> {
                log.error("[ERROR] Something went wrong: $chatId", user.exception)
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.unexpectedError)
                    .build()
                responseReceiver.receiveData(response)
            }
            is DataState.Success -> {
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.nextTestNotifyMessage(user.data.breakTimeInMillis))
                    .build()
                responseReceiver.receiveData(response)
                timer.schedule(ScheduleQuiz(chatId), user.data.breakTimeInMillis)
                log.info("Next test scheduled for $chatId")
            }
        }
    }

    inner class ScheduleQuiz(private val chatId: Long) : TimerTask() {
        override fun run() {
            CoroutineScope(Dispatchers.Default).launch {
                when (val user = userRepo.getUserByChatId(chatId)) {
                    DataState.Empty -> startUserRegistration(chatId)
                    is DataState.Success -> askForStartQuiz(user.data.chatId)
                    is DataState.Failure -> {
                        log.error("[ERROR] something went wrong: $chatId", user.exception)
                    }
                }
            }
        }
    }
}
