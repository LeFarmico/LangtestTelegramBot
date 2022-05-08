package controller

import bot.handler.IHandlerReceiver
import command.Command
import data.IRequestData
import data.ResponseFactory
import intent.*
import kotlinx.coroutines.*
import messageBuilders.*
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import res.SystemMessages

class LangTestController(override val responseReceiver: IHandlerReceiver) : IController, IStateHandler {

    private val log = LoggerFactory.getLogger(javaClass.simpleName)

    private val langTestIntent: LangTestIntent = LangTestIntentImpl(this)

    override fun commandAction(requestData: IRequestData) {
        CoroutineScope(Dispatchers.Default).launch {
            val chatId = requestData.chatId
            val messageId = requestData.messageId
            when (val command = requestData.command) {
                is Command.CorrectAnswerCallback -> {
                    langTestIntent.answerToQuizQuestion(chatId, messageId, command.wordId, true)
                }
                is Command.IncorrectAnswerCallback -> {
                    langTestIntent.answerToQuizQuestion(chatId, messageId, command.wordId, false)
                }
                is Command.StartQuizCallback -> {
                    langTestIntent.startQuiz(chatId, messageId, command.start)
                }
                Command.GetQuizTest -> {
                    langTestIntent.getNextQuizWord(chatId, messageId)
                }
                is Command.SetCategoryCallback -> {
                    langTestIntent.selectCategory(chatId, messageId, command.categoryId)
                }
                is Command.SetLanguageCallBack -> {
                    langTestIntent.selectLanguage(chatId, messageId, command.languageId)
                }
                Command.TimeToNextTestCommand -> {
                    langTestIntent.timeToNextQuiz(chatId, messageId)
                }
                Command.StartCommand -> {
                    langTestIntent.checkForUser(chatId, messageId)
                }
                Command.StopCommand -> {
                    langTestIntent.removeUser(chatId, messageId)
                }
                Command.GetUserData -> {
                    langTestIntent.getUserData(chatId, messageId)
                }
                Command.ResetQuiz -> {
                    langTestIntent.resetQuizWords(chatId, messageId)
                }
                else -> {}
            }
        }
    }

    override fun handleState(state: LangTestState, chatId: Long, messageId: Int) {
        when (state) {
            is StartedUserRegistration -> {
                val response = ResponseFactory.builder(state.chatId)
                    .message(state.message)
                    .build()
                responseReceiver.receiveData(response)
            }
            is UserRegistered -> {
                val response = ResponseFactory.builder(state.chatId)
                    .message(
                        SystemMessages.userSettingsMessage(
                            language = state.quizViewData.languageName,
                            category = state.quizViewData.categoryName
                        )
                    )
                    .build()
                responseReceiver.receiveData(response)
            }
            is UpdatedUserData -> {
                val response = ResponseFactory.builder(state.chatId)
                    .message(
                        SystemMessages.userSettingsMessage(
                            language = state.quizViewData.languageName,
                            category = state.quizViewData.categoryName
                        )
                    )
                    .build()
                responseReceiver.receiveData(response)
            }
            is UserRemoved -> {
                val response = ResponseFactory.builder(state.chatId)
                    .message(state.message)
                    .build()
                responseReceiver.receiveData(response)
            }
            is CategoryFounded -> {
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.chooseCategory)
                    .setButtons {
                        val buttonList = mutableListOf<InlineKeyboardButton>()
                        for (i in state.categoryList.indices) {
                            buttonList.add(
                                InlineKeyboardButton().apply {
                                    this.text = state.categoryList[i].categoryName
                                    this.callbackData = Command.SetCategoryCallback.buildCallBackQuery(state.categoryList[i].id)
                                }
                            )
                        }
                        buttonList
                    }.build()
                responseReceiver.receiveData(response)
            }
            is CategoryPicked -> {
                val response = ResponseFactory.builder(state.chatId)
                    .editCurrent(messageId)
                    .message(SystemMessages.categoryChooseMessage(state.category.categoryName))
                    .build()
                responseReceiver.receiveData(response)
            }
            is LanguagePicked -> {
                val response = ResponseFactory.builder(state.chatId)
                    .editCurrent(messageId)
                    .message(SystemMessages.languageChooseMessage(state.language.languageName))
                    .build()
                responseReceiver.receiveData(response)
            }
            is LanguagesFounded -> {
                val response = ResponseFactory.builder(state.chatId)
                    .message(SystemMessages.chooseLanguage)
                    .setButtons {
                        val buttonList = mutableListOf<InlineKeyboardButton>()
                        for (i in state.languageList.indices) {
                            buttonList.add(
                                InlineKeyboardButton().apply {
                                    this.text = state.languageList[i].languageName
                                    this.callbackData = Command.SetLanguageCallBack.buildCallBackQuery(state.languageList[i].id)
                                }
                            )
                        }
                        buttonList
                    }.build()
                responseReceiver.receiveData(response)
            }
            is QuizStarted -> {
                val response = ResponseFactory.builder(chatId)
                    .editCurrent(messageId)
                    .message(state.message)
                    .build()
                responseReceiver.receiveData(response)
            }
            is QuizEnded -> {
                val response = ResponseFactory.builder(chatId)
                    .message(state.nextQuizTime)
                    .build()
                responseReceiver.receiveData(response)
            }
            is AskStartQuiz -> {
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.quizStartQuestion)
                    .addButton(SystemMessages.yes, Command.StartQuizCallback.buildCallBackQuery(true))
                    .addButton(SystemMessages.no, Command.StartQuizCallback.buildCallBackQuery(false))
                    .build()
                responseReceiver.receiveData(response)
            }
            is AskToResetQuiz -> {
                val response = ResponseFactory.builder(chatId)
                    .message(state.message)
                    .addButton(SystemMessages.yes, Command.ResetQuiz.COMMAND)
                    .addButton(SystemMessages.no, Command.StartQuizCallback.buildCallBackQuery(false))
                    .build()
                responseReceiver.receiveData(response)
            }
            is AskToContinueQuiz -> {
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.quizContinueQuestion)
                    .addButton(SystemMessages.yes, Command.GetQuizTest.buildCallBackQuery())
                    .addButton(SystemMessages.no, Command.StartQuizCallback.buildCallBackQuery(false))
                    .addButton(SystemMessages.startAgain, Command.ResetQuiz.COMMAND)
                    .build()
                responseReceiver.receiveData(response)
            }
            is NextQuizWord -> {
                val message = TestMessageBuilder.setChatId(chatId, state.quizWord.id)
                    .setQuizText(SystemMessages.quizText, state.quizWord.originalWord)
                    .addIncorrectButtonList(state.quizWord.wrongTranslations)
                    .addCorrectButton(state.quizWord.correctTranslation)
                    .build()
                val response = ResponseFactory.builder(chatId)
                    .buildSendMessageObject(message)
                responseReceiver.receiveData(response)
            }
            is NotFound -> {
                log.warn("[WARN] ${state.message}")
                val response = ResponseFactory.builder(state.chatId)
                    .message(state.message)
                    .build()
                responseReceiver.receiveData(response)
            }
            is ErrorState -> {
                log.error(state.message, state.e)
                val response = ResponseFactory.builder(state.chatId)
                    .message(SystemMessages.unexpectedError)
                    .build()
                responseReceiver.receiveData(response)
            }
            is QuizAnswered -> {
                val response = ResponseFactory.builder(chatId)
                    .editCurrent(messageId)
                    .message(state.answerText)
                    .build()
                responseReceiver.receiveData(response)
            }
            is NextQuizTime -> {
                val response = ResponseFactory.builder(chatId)
                    .message(state.nextQuizTimeText)
                    .build()
                responseReceiver.receiveData(response)
            }
            is UserDataSent -> {
                val response = ResponseFactory.builder(chatId)
                    .message(SystemMessages.userDataMessage(state.quizViewData))
                    .build()
                responseReceiver.receiveData(response)
            }
            is QuizResetted -> {
                val response = ResponseFactory.builder(chatId)
                    .editCurrent(messageId)
                    .message(state.message)
                    .build()
                responseReceiver.receiveData(response)
            }
        }
    }
}
