package ability.langTestAbility

import ability.IAbility
import bot.MessageSender
import command.Command
import entity.QuizTest
import entity.UserMessage
import inject.DataInjector
import kotlinx.coroutines.*
import messageBuilders.*
import org.slf4j.LoggerFactory
import repository.*
import res.SystemMessages
import state.DataState
import java.util.*

class LangTestAbility(
    private val messageSender: MessageSender
) : IAbility<LangTestAbilityCommand> {

    private val log = LoggerFactory.getLogger(javaClass.simpleName)

    private val timer by lazy { Timer(true) }

    private val userRepo: UserRepository = DataInjector.userRepo
    private val quizRepository: QuizRepository = DataInjector.quizRepository
    private val categoryRepository: CategoryRepository = DataInjector.categoryRepository
    private val languageRepository: LanguageRepository = DataInjector.languageRepository

    override fun subscribe(chatId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val user = userRepo.getUserByChatId(chatId)) {
                DataState.Empty -> startUserRegistration(chatId)
                is DataState.Success -> askForStartQuiz(chatId)
                is DataState.Failure -> {
                    log.error("[ERROR] unexpected error: $chatId", user.exception)
                    messageSender.sendMessage(chatId, SystemMessages.unexpectedError)
                }
            }
        }
    }

    override fun unsubscribe(chatId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            userRepo.deleteUserChatId(chatId)
        }
    }

    override fun commandAction(data: LangTestAbilityCommand) {
        CoroutineScope(Dispatchers.Default).launch {
            when (val command = data.command) {
                is Command.CorrectAnswerCallback -> {
                    handleAnswerCallback(
                        chatId = data.chatId,
                        messageId = data.messageId,
                        wordId = command.wordId,
                        isCorrect = true
                    )
                }
                is Command.IncorrectAnswerCallback -> {
                    handleAnswerCallback(
                        chatId = data.chatId,
                        messageId = data.messageId,
                        wordId = command.wordId,
                        isCorrect = false
                    )
                }
                is Command.StartQuizCallback -> {
                    handleStartCallback(
                        chatId = data.chatId,
                        messageId = data.messageId,
                        start = command.start
                    )
                }
                Command.GetQuizTest -> {
                    trySendNexQuizWord(data.chatId)
                }
                is Command.AskExamCallback -> {
                }
                
                is Command.SetCategoryCallback -> {
                    registerUser(
                        chatId = data.chatId,
                        messageId = data.messageId,
                        categoryId = command.categoryId
                    )
                }
                is Command.SetLanguageCallBack -> {
                    handleSetLanguageCallback(
                        chatId = data.chatId,
                        messageId = data.messageId,
                        languageId = command.languageId
                    )
                }
                Command.TimeToNextTestCommand -> {
                    sendTimeToNextQuiz(data.chatId)
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
        messageSender.editMessage(chatId, messageId, SystemMessages.categoryChooseMessage(category.categoryName))
        askForStartQuiz(chatId)
    }

    private suspend fun sendTimeToNextQuiz(chatId: Long) {
        when (val user = userRepo.getUserByChatId(chatId)) {
            DataState.Empty -> {
                log.warn("[WARN] User not found: $chatId")
                messageSender.sendMessage(chatId, SystemMessages.userNotFound)
            }
            is DataState.Failure -> {
                log.error("[ERROR] unexpected error: $chatId", user.exception)
                messageSender.sendMessage(chatId, SystemMessages.userNotFound)
            }
            is DataState.Success -> {
                val time = user.data.breakTimeInMillis
                messageSender.sendMessage(chatId, SystemMessages.nextTestNotifyMessage(time))
            }
        }
    }

    private suspend fun askForStartQuiz(chatId: Long) {
        when (val quiz = quizRepository.getQuizTest(chatId)) {
            DataState.Empty -> {
                sendCurrentUserSettings(chatId)
                val askMessage = MessageBuilder.setChatId(chatId)
                    .setText(SystemMessages.quizStartQuestion)
                    .setButtons(
                        ButtonBuilder.setUp()
                            .addButton(SystemMessages.yes, Command.StartQuizCallback.buildCallBackQuery(true))
                            .addButton(SystemMessages.no, Command.StartQuizCallback.buildCallBackQuery(false))
                            .build()
                    ).build()
                messageSender.send(UserMessage(chatId, askMessage))
            }
            is DataState.Success -> {
                sendCurrentUserSettings(chatId)
                val askMessage = MessageBuilder.setChatId(chatId)
                    .setText(SystemMessages.quizContinueQuestion)
                    .setButtons(
                        ButtonBuilder.setUp()
                            .addButton(SystemMessages.yes, Command.GetQuizTest.buildCallBackQuery())
                            .addButton(SystemMessages.no, Command.StartQuizCallback.buildCallBackQuery(false))
                            .addButton(SystemMessages.startAgain, Command.StartQuizCallback.buildCallBackQuery(true))
                            .build()
                    ).build()
                messageSender.send(UserMessage(chatId, askMessage))
            }
            is DataState.Failure -> {
                log.error("[ERROR] ${quiz.exception.message}", quiz.exception)
                messageSender.sendMessage(chatId, SystemMessages.unexpectedError)
            }
        }
    }
    
    private suspend fun sendCurrentUserSettings(chatId: Long) {
        when (val user = userRepo.getUserByChatId(chatId)) {
            DataState.Empty -> {
                messageSender.sendMessage(chatId, SystemMessages.userNotFound)
            }
            is DataState.Failure -> {
                log.error("[ERROR] unexpected error $chatId", user.exception)
                messageSender.sendMessage(chatId, SystemMessages.unexpectedError)
            }
            is DataState.Success -> {
                try {
                    val category = categoryRepository.getCategory(user.data.categoryId)!!
                    val language = languageRepository.getLanguageById(user.data.languageId)!!
                    val message = SystemMessages.userSettingsMessage(language.languageName, category.categoryName)
                    messageSender.sendMessage(chatId, message)
                } catch (e: NullPointerException) {
                    log.error("[ERROR] Category or message not found", e)
                    messageSender.sendMessage(chatId, SystemMessages.unexpectedError)
                }
            }
        }
    }

    private suspend fun handleAnswerCallback(chatId: Long, messageId: Int, wordId: Long, isCorrect: Boolean) {
        when (isCorrect) {
            true -> { 
                quizRepository.removeWordForUser(chatId, wordId)
                messageSender.editMessage(chatId, messageId, SystemMessages.rightAnswer)
                trySendNexQuizWord(chatId)
            }
            false -> {
                messageSender.editMessage(chatId, messageId, SystemMessages.wrongAnswer)
                trySendNexQuizWord(chatId)
            }
        }
    }

    private suspend fun handleStartCallback(chatId: Long, messageId: Int, start: Boolean) {
        when (start) {
            true -> {
                quizRepository.addQuizWords(chatId)
                messageSender.editMessage(chatId, messageId, SystemMessages.startQuizMsg)
                trySendNexQuizWord(chatId)
            }
            false -> messageSender.editMessage(chatId, messageId, SystemMessages.startQuizHelpMsg)
        }
    }

    private suspend fun trySendNexQuizWord(chatId: Long) {
        when (val quizTest = quizRepository.getQuizTest(chatId)) {
            DataState.Empty -> endQuiz(chatId)
            is DataState.Success -> sendQuizTest(chatId, quizTest.data)
            is DataState.Failure -> {
                log.error("[ERROR] something went wrong: $chatId", quizTest.exception)
                messageSender.sendMessage(chatId, SystemMessages.unexpectedError)
            }
        }
    }
    
    private suspend fun handleSetLanguageCallback(chatId: Long, messageId: Int, languageId: Long) {
        val language = languageRepository.getLanguageById(languageId)!!
        messageSender.editMessage(chatId, messageId, SystemMessages.languageChooseMessage(language.languageName))
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
            messageSender.send(UserMessage(chatId, message))
        } catch (e: NullPointerException) {
            log.error("Can't find words for $chatId", e)
            endQuiz(chatId)
        }
    }

    private suspend fun askForLanguage(chatId: Long) {
        val langList = languageRepository.getAvailableLanguages()
        val askMessage = MessageBuilder.setChatId(chatId)
            .setText(SystemMessages.chooseLanguage)
            .setButtons {
                var builder = ButtonBuilder.setUp()
                for (i in langList.indices) {
                    builder = builder.addButton(
                        langList[i].languageName,
                        Command.SetLanguageCallBack.buildCallBackQuery(langList[i].id)
                    )
                }
                builder.build()
            }.build()
        messageSender.send(UserMessage(chatId, askMessage))
    }

    private suspend fun askForCategory(chatId: Long, languageId: Long) {
        val categoryList = categoryRepository.getCategoriesByLanguage(languageId)
        val askMessage = MessageBuilder.setChatId(chatId)
            .setText(SystemMessages.chooseCategory)
            .setButtons {
                var builder = ButtonBuilder.setUp()
                for (i in categoryList.indices) {
                    builder = builder.addButton(categoryList[i].categoryName, Command.SetCategoryCallback.buildCallBackQuery(categoryList[i].id))
                }
                builder.build()
            }.build()
        messageSender.send(UserMessage(chatId, askMessage))
    }

    private suspend fun askForExam(chatId: Long) {
        try {
            val askMessage = MessageBuilder.setChatId(chatId)
                .setText(SystemMessages.askForExam)
                .setButtons {
                    ButtonBuilder.setUp()
                        .addButton(SystemMessages.yes, Command.AskExamCallback.buildCallBackQuery(true))
                        .addButton(SystemMessages.no, Command.AskExamCallback.buildCallBackQuery(false))
                        .build()
                }.build()
            messageSender.send(UserMessage(chatId, askMessage))
        } catch (e: NullPointerException) {
            messageSender.sendMessage(chatId, SystemMessages.unexpectedError)
        }
    }

    private suspend fun scheduleNextTest(chatId: Long) {
        when (val user = userRepo.getUserByChatId(chatId)) {
            DataState.Empty -> {
                messageSender.sendMessage(chatId, SystemMessages.userNotFound)
            }
            is DataState.Failure -> {
                log.error("[ERROR] Something went wrong: $chatId", user.exception)
                messageSender.sendMessage(chatId, SystemMessages.unexpectedError)
            }
            is DataState.Success -> {
                messageSender.sendMessage(chatId, SystemMessages.nextTestNotifyMessage(user.data.breakTimeInMillis))
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
