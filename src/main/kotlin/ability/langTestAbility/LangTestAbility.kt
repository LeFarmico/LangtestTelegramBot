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
) : IAbility<LangTestCommand> {

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

    override fun commandAction(data: LangTestCommand) {
        CoroutineScope(Dispatchers.Default).launch {
            when (val command = data.command) {
                is Command.CorrectAnswerCallback -> {
                    quizRepository.removeWordForUser(data.chatId, command.wordId)
                    messageSender.editMessage(data.chatId, data.messageId, SystemMessages.rightAnswer)
                    startQuiz(data.chatId)
                }
                is Command.IncorrectAnswerCallback -> {
                    messageSender.editMessage(data.chatId, data.messageId, SystemMessages.wrongAnswer)
                    startQuiz(data.chatId)
                }
                is Command.StartQuizCallback -> {
                    when (command.start) {
                        true -> {
                            quizRepository.addQuizWords(data.chatId)
                            messageSender.editMessage(data.chatId, data.messageId, SystemMessages.startQuizMsg)
                            startQuiz(data.chatId)
                        }
                        false -> messageSender.editMessage(data.chatId, data.messageId, SystemMessages.startQuizHelpMsg)
                    }
                }
                Command.ContinueQuiz -> {
                    when (val quizTest = quizRepository.getQuizTest(data.chatId)) {
                        DataState.Empty -> {
                            messageSender.editMessage(data.chatId, data.messageId, "Нету слов")
                            askForStartQuiz(data.chatId)
                        }
                        is DataState.Success -> {
                            sendQuizTest(data.chatId, quizTest.data)
                        }
                        is DataState.Failure -> TODO()
                    }
                }
                is Command.AskExamCallback -> {
                    // TODO implement
                }
                is Command.SetCategoryCallback -> {
                    try {
                        val category = categoryRepository.getCategory(command.categoryId)!!
                        userRepo.addUser(
                            chatId = data.chatId,
                            categoryId = category.id,
                            languageId = category.languageId
                        )
                        messageSender.editMessage(data.chatId, data.messageId, "Вы выбрали категорию: ${category.categoryName}.")
                        askForStartQuiz(data.chatId)
                    } catch (e: NullPointerException) {
                        messageSender.sendMessage(data.chatId, "Упс! Что-то пошло не так...")
                    }
                }
                is Command.SetLanguageCallBack -> {
                    val language = languageRepository.getLanguageById(command.languageId)!!
                    messageSender.editMessage(data.chatId, data.messageId, "Вы выбрали язык: ${language.languageName}.")
                    askForCategory(data.chatId, command.languageId)
                }
                Command.TimeToNextTestCommand -> {
                    sendTimeToNextQuiz(data.chatId)
                }
            }
        }
    }

    private suspend fun sendTimeToNextQuiz(chatId: Long) {
        when (val user = userRepo.getUserByChatId(chatId)) {
            DataState.Empty -> {
                log.warn("[WARN] User not found: $chatId")
                messageSender.sendMessage(chatId, "User not found")
            }
            is DataState.Failure -> {
                log.error("[ERROR] unexpected error: $chatId", user.exception)
                messageSender.sendMessage(chatId, "User not found")
            }
            is DataState.Success -> {
                val time = user.data.breakTimeInMillis
                messageSender.sendMessage(chatId, SystemMessages.nextTestNotifyMessage(time))
            }
        }
    }

    private suspend fun askForStartQuiz(chatId: Long) {
        when (quizRepository.getQuizTest(chatId)) {
            DataState.Empty -> {
                val askMessage = MessageBuilder.setChatId(chatId)
                    .setText("Хотети начать викторину?")
                    .setButtons(
                        ButtonBuilder.setUp()
                            .addButton(SystemMessages.yes, Command.StartQuizCallback.buildCallBackQuery(true))
                            .addButton(SystemMessages.no, Command.StartQuizCallback.buildCallBackQuery(false))
                            .build()
                    ).build()
                messageSender.send(UserMessage(chatId, askMessage))
            }
            is DataState.Success -> {
                val askMessage = MessageBuilder.setChatId(chatId)
                    .setText("Хотите продолжить викторину?")
                    .setButtons(
                        ButtonBuilder.setUp()
                            .addButton(SystemMessages.yes, Command.ContinueQuiz.buildCallBackQuery())
                            .addButton(SystemMessages.no, Command.StartQuizCallback.buildCallBackQuery(false))
                            .addButton("Начать заново?", Command.StartQuizCallback.buildCallBackQuery(true))
                            .build()
                    ).build()
                messageSender.send(UserMessage(chatId, askMessage))
            }
            is DataState.Failure -> TODO()
        }
    }

    private suspend fun startQuiz(chatId: Long) {
        when (val quizTest = quizRepository.getQuizTest(chatId)) {
            DataState.Empty -> endQuiz(chatId)
            is DataState.Success -> sendQuizTest(chatId, quizTest.data)
            is DataState.Failure -> {
                log.error("[ERROR] something went wrong: $chatId", quizTest.exception)
                messageSender.sendMessage(chatId, SystemMessages.unexpectedError)
            }
        }
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

    private suspend fun startUserRegistration(chatId: Long) {
        askForLanguage(chatId)
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
            messageSender.sendMessage(chatId, "Упс! Что-то пошло не так...")
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
                quizRepository.addQuizWords(chatId)
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
