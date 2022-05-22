package intent

import controller.IStateHandler
import dataSource.BreakTimeDataSource
import entity.QuizData
import entity.QuizViewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import repository.CategoryRepository
import repository.LanguageRepository
import repository.QuizRepository
import repository.UserRepository
import res.TextResources
import state.DataState
import java.util.*

class LangTestIntentImpl(private val handler: IStateHandler) : LangTestIntent {

    private val userRepo: UserRepository by KoinJavaComponent.inject(UserRepository::class.java)
    private val langRepo: LanguageRepository by KoinJavaComponent.inject(LanguageRepository::class.java)
    private val quizRepository: QuizRepository by KoinJavaComponent.inject(QuizRepository::class.java)
    private val categoryRepository: CategoryRepository by KoinJavaComponent.inject(CategoryRepository::class.java)

    private val timer by lazy { Timer(true) }
    
    override suspend fun checkForUser(chatId: Long, messageId: Int) {
        when (val quizDataState = userRepo.getUserByChatId(chatId)) {
            DataState.Empty -> {
                val state = StartedUserRegistration(chatId, messageId, TextResources.startRegistration)
                handler.handleState(state, chatId, messageId)
                startUserRegistration(chatId, messageId)
            }
            is DataState.Success -> {
                try {
                    val quizViewData = getQuizViewData(quizDataState.data)
                    val state = UserRegistered(chatId, messageId, quizViewData)
                    handler.handleState(state, chatId, messageId)
                    if (quizViewData.currentWordNumber == 0) {
                        askToStartQuiz(chatId, messageId)
                    } else {
                        askToContinueQuiz(chatId, messageId)
                    }
                } catch (e: ClassCastException) {
                    val state = ErrorState(chatId, messageId, e, TextResources.dataNotFound)
                    handler.handleState(state, chatId, messageId)
                }
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, quizDataState.exception, TextResources.getDataFail)
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    override suspend fun getUserData(chatId: Long, messageId: Int) {
        when (val quizData = userRepo.getUserByChatId(chatId)) {
            DataState.Empty -> {
                val state = NotFound(chatId, messageId, TextResources.userNotFound)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, quizData.exception, TextResources.getUserDataFail)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Success -> {
                try {
                    val quizViewData = getQuizViewData(quizData.data)
                    val state = UserDataSent(chatId, messageId, quizViewData)
                    handler.handleState(state, chatId, messageId)
                } catch (e: ClassCastException) {
                    val state = ErrorState(chatId, messageId, e, TextResources.dataNotFound)
                    handler.handleState(state, chatId, messageId)
                }
            }
        }
    }

    override suspend fun startUserRegistration(chatId: Long, messageId: Int) {
        val state = when (val langListState = langRepo.getAvailableLanguages()) {
            DataState.Empty -> {
                NotFound(chatId, messageId, TextResources.langNotFound)
            }
            is DataState.Failure -> {
                ErrorState(chatId, messageId, langListState.exception, TextResources.startRegistrationFail)
            }
            is DataState.Success -> {
                LanguagesFounded(chatId, messageId, langListState.data)
            }
        }
        handler.handleState(state, chatId, messageId)
    }

    override suspend fun finishRegistration(chatId: Long, messageId: Int, languageId: Long, categoryId: Long) {
        when (val quizDataState = userRepo.addUser(chatId, languageId, categoryId, 7_200_000, 5)) {
            DataState.Empty -> {
                val state = NotFound(chatId, messageId, TextResources.userNotRegistered)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, quizDataState.exception, TextResources.finishRegistrationFail)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Success -> {
                try {
                    val quizViewData = getQuizViewData(quizDataState.data)
                    val state = UserRegistered(chatId, messageId, quizViewData)
                    handler.handleState(state, chatId, messageId)
                    createQuizWords(chatId, messageId)
                } catch (e: TypeCastException) {
                    ErrorState(chatId, messageId, e, TextResources.dataNotFound)
                }
            }
        }
    }

    override suspend fun updateUserInformation(chatId: Long, messageId: Int, quizData: QuizData) {
        when (
            val data = userRepo.updateUser(
                chatId,
                quizData.languageId,
                quizData.categoryId,
                quizData.breakTimeInMillis,
                quizData.wordsInQuiz
            )
        ) {
            DataState.Empty -> {
                val state = NotFound(chatId, messageId, TextResources.userNotFound)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, data.exception, TextResources.updateDataFail)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Success -> {
                try {
                    val quizViewData = getQuizViewData(data.data)
                    val state = UpdatedUserData(chatId, messageId, quizViewData)
                    handler.handleState(state, chatId, messageId)
                    resetQuizWords(chatId, messageId)
                } catch (e: java.lang.ClassCastException) {
                    val state = ErrorState(chatId, messageId, e, TextResources.dataNotFound)
                    handler.handleState(state, chatId, messageId)
                }
            }
        }
    }

    override suspend fun removeUser(chatId: Long, messageId: Int) {
        val state = when (userRepo.deleteUserChatId(chatId)) {
            true -> {
                UserRemoved(chatId, messageId, TextResources.userDataDeleted)
            }
            false -> {
                NotFound(chatId, messageId, TextResources.userNotExist)
            }
        }
        handler.handleState(state, chatId, messageId)
    }

    override suspend fun getLanguages(chatId: Long, messageId: Int) {
        val state = when (val langData = langRepo.getAvailableLanguages()) {
            DataState.Empty -> {
                NotFound(chatId, messageId, TextResources.langNotFound)
            }
            is DataState.Failure -> {
                ErrorState(chatId, messageId, langData.exception, TextResources.getLangListFail)
            }
            is DataState.Success -> {
                LanguagesFounded(chatId, messageId, langData.data)
            }
        }
        handler.handleState(state, chatId, messageId)
    }

    override suspend fun selectLanguage(chatId: Long, messageId: Int, languageId: Long) {
        when (val langData = langRepo.getLanguageById(languageId)) {
            DataState.Empty -> {
                val state = NotFound(chatId, messageId, TextResources.langNotFound)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, langData.exception, TextResources.setLangFail)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Success -> {
                val state = LanguagePicked(chatId, messageId, langData.data)
                handler.handleState(state, chatId, messageId)
                getCategories(chatId, messageId, languageId)
            }
        }
    }

    override suspend fun getCategories(chatId: Long, messageId: Int, languageId: Long) {
        val state = when (val categoriesData = categoryRepository.getCategoriesByLanguage(languageId)) {
            DataState.Empty -> {
                NotFound(chatId, messageId, TextResources.categoryListNotFound)
            }
            is DataState.Failure -> {
                ErrorState(chatId, messageId, categoriesData.exception, TextResources.getCategoryListFail)
            }
            is DataState.Success -> {
                CategoryFounded(chatId, messageId, categoriesData.data)
            }
        }
        handler.handleState(state, chatId, messageId)
    }

    override suspend fun selectCategory(chatId: Long, messageId: Int, categoryId: Long) {
        when (val categoryData = categoryRepository.getCategory(categoryId)) {
            DataState.Empty -> {
                val state = NotFound(chatId, messageId, TextResources.categoryNotFound)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, categoryData.exception, TextResources.setCategoryFail)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Success -> {
                val languageId = categoryData.data.languageId
                val state = CategoryPicked(chatId, messageId, categoryData.data)
                handler.handleState(state, chatId, messageId)
                finishRegistration(chatId, messageId, languageId, categoryId)
            }
        }
    }

    override suspend fun askToStartQuiz(chatId: Long, messageId: Int) {
        val state = AskStartQuiz(chatId, messageId)
        handler.handleState(state, chatId, messageId)
    }

    override suspend fun askToContinueQuiz(chatId: Long, messageId: Int) {
        val state = AskToContinueQuiz(chatId, messageId)
        handler.handleState(state, chatId, messageId)
    }

    override suspend fun askToResetQuiz(chatId: Long, messageId: Int) {
        val state = AskToResetQuiz(chatId, messageId, TextResources.startAgain)
        handler.handleState(state, chatId, messageId)
    }

    override suspend fun startQuiz(chatId: Long, messageId: Int, start: Boolean) {
        if (start) {
            when (val quizDataState = userRepo.getUserByChatId(chatId)) {
                DataState.Empty -> {
                    val state = NotFound(chatId, messageId, TextResources.registerWarning)
                    handler.handleState(state, chatId, messageId)
                }
                is DataState.Success -> {
                    val state = QuizStarted(chatId, messageId, TextResources.startQuizMsg)
                    handler.handleState(state, chatId, messageId)
                    getNextQuizWord(chatId, messageId)
                }
                is DataState.Failure -> {
                    val state = ErrorState(chatId, messageId, quizDataState.exception, TextResources.startQuizFail)
                    handler.handleState(state, chatId, messageId)
                }
            }
        } else {
            val state = QuizStopped(chatId, messageId, TextResources.quizNotification)
            handler.handleState(state, chatId, messageId)
        }
    }

    override suspend fun getNextQuizWord(chatId: Long, messageId: Int) {
        when (val quiz = quizRepository.getNextQuizWord(chatId)) {
            DataState.Empty -> {
                val state = NotFound(chatId, messageId, TextResources.allWordsAnswered)
                handler.handleState(state, chatId, messageId)
                askToResetQuiz(chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, quiz.exception, TextResources.wordsNotFound)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Success -> {
                val state = NextQuizWord(chatId, messageId, quiz.data)
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    override suspend fun correctAnswerToQuizQuestion(
        chatId: Long,
        messageId: Int,
        wordId: Long
    ) {
        when (val answerData = quizRepository.setCorrectAnswerForQuizWord(chatId, wordId)) {
            DataState.Empty -> {
                NotFound(chatId, messageId, TextResources.wordNotFound)
            }
            is DataState.Failure -> {
                ErrorState(chatId, messageId, answerData.exception, TextResources.setAnswerFail)
            }
            is DataState.Success -> {
                val originalWord = answerData.data.originalWord
                val translation = answerData.data.translation
                if (answerData.data.nextQuizTime == null) {
                    handleCorrectAnswerState(chatId, messageId, originalWord, translation)
                    getNextQuizWord(chatId, messageId)
                } else {
                    val nextQuizTimeDelay = answerData.data.nextQuizTime!! - System.currentTimeMillis()

                    handleCorrectAnswerState(chatId, messageId, originalWord, translation)
                    scheduleNextQuiz(chatId, messageId, nextQuizTimeDelay)
                    quizRepository.resetQuizWordNumber(chatId)

                    val state = NextQuizTime(chatId, messageId, TextResources.nextTestNotifyMessage(nextQuizTimeDelay))
                    handler.handleState(state, chatId, messageId)
                }
            }
        }
    }

    override suspend fun incorrectAnswerToQuizQuestion(chatId: Long, messageId: Int, wordId: Long) {
        when (val answerData = quizRepository.setIncorrectAnswerForQuizWord(chatId, wordId)) {
            DataState.Empty -> {
                NotFound(chatId, messageId, TextResources.wordNotFound)
            }
            is DataState.Failure -> {
                ErrorState(chatId, messageId, answerData.exception, TextResources.setAnswerFail)
            }
            is DataState.Success -> {
                val quizWord = answerData.data
                val quizState = QuizAnswered(chatId, messageId, TextResources.wrongAnswer)
                val state = NextQuizWord(chatId, messageId, quizWord)
                handler.handleState(quizState, chatId, messageId)
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    private fun handleCorrectAnswerState(
        chatId: Long,
        messageId: Int,
        wordOriginal: String,
        translation: String
    ) {
        val message = "${TextResources.rightAnswer}: $wordOriginal -> $translation"
        val state = QuizAnswered(chatId, messageId, message)
        handler.handleState(state, chatId, messageId)
    }

    private fun scheduleNextQuiz(chatId: Long, messageId: Int, nextQuizTimeDelay: Long) {
        val state = ScheduleQuiz(chatId, messageId)
        timer.schedule(state, nextQuizTimeDelay)
    }

    override suspend fun createQuizWords(chatId: Long, messageId: Int) {
        when (quizRepository.createQuizWords(chatId)) {
            true -> {
                askToStartQuiz(chatId, messageId)
            }
            false -> {
                val state = NotFound(chatId, messageId, TextResources.createQuizFail)
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    override suspend fun resetQuizWords(chatId: Long, messageId: Int) {
        when (quizRepository.resetQuiz(chatId)) {
            true -> {
                val state = QuizResetted(chatId, messageId, TextResources.quizResetted)
                handler.handleState(state, chatId, messageId)
                askToStartQuiz(chatId, messageId)
            }
            false -> {
                val state = NotFound(chatId, messageId, TextResources.quizNotFound)
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    override suspend fun restartQuizWords(chatId: Long, messageId: Int) {
        when (quizRepository.resetQuiz(chatId)) {
            true -> {
                val state = QuizResetted(chatId, messageId, TextResources.quizResetted)
                handler.handleState(state, chatId, messageId)
                askToStartQuiz(chatId, messageId)
            }
            false -> {
                val state = NotFound(chatId, messageId, TextResources.quizNotFound)
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    override suspend fun timeToNextQuiz(chatId: Long, messageId: Int) {
        val state = NotFound(chatId, messageId, "Not implemented yet")
        handler.handleState(state, chatId, messageId)
    }

    override suspend fun getBreakTimeList(chatId: Long, messageId: Int) {
        when (userRepo.getUserByChatId(chatId)) {
            DataState.Empty -> {
                val state = NotFound(chatId, messageId, TextResources.userNotFound)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Failure -> {
                val state = NotFound(chatId, messageId, TextResources.getUserDataFail)
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Success -> {
                val state = AskBreakTime(chatId, messageId, TextResources.selectBreakTime, BreakTimeDataSource.breakTimeList)
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    override suspend fun selectBreakTime(chatId: Long, messageId: Int, breakTimeInMillis: Long) {
        when (userRepo.setBreakTimeByChatId(chatId, breakTimeInMillis)) {
            true -> {
                val timeString = BreakTimeDataSource.breakTimeList[breakTimeInMillis]
                val notifString = TextResources.breakTimeQuizNotification
                val state = BreakTimeSelected(chatId, messageId, notifString + timeString)
                handler.handleState(state, chatId, messageId)
            }
            false -> {
                val state = SetBreakTimeFailed(chatId, messageId, TextResources.setBreakTimeFail)
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    private suspend fun getQuizViewData(quizData: QuizData): QuizViewData {
        val scope = CoroutineScope(Dispatchers.IO)
        val languageName = scope.async {
            (langRepo.getLanguageById(quizData.languageId) as DataState.Success).data.languageName
        }
        val categoryName = scope.async {
            (categoryRepository.getCategory(quizData.categoryId) as DataState.Success).data.categoryName
        }
        return QuizViewData(
            languageName.await(),
            categoryName.await(),
            TextResources.breakBetweenQuiz(quizData.breakTimeInMillis),
            quizData.wordsInQuiz,
            quizData.currentWordNumber
        )
    }

    inner class ScheduleQuiz(private val chatId: Long, private val messageId: Int) : TimerTask() {
        override fun run() {
            CoroutineScope(Dispatchers.Default).launch {
                getNextQuizWord(chatId, messageId)
            }
        }
    }
}
