package intent

import controller.IStateHandler
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
import res.SystemMessages
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
                val state = StartedUserRegistration(chatId, messageId, "Начинаем регистрацию")
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
                    val state = ErrorState(chatId, messageId, e, "Данные не найдены")
                    handler.handleState(state, chatId, messageId)
                }
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, quizDataState.exception, "Что-то пошло не так")
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    override suspend fun getUserData(chatId: Long, messageId: Int) {
        when (val quizData = userRepo.getUserByChatId(chatId)) {
            DataState.Empty -> {
                val state = NotFound(chatId, messageId, "Пользователь не найден")
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, quizData.exception, "Что-то пошло не так")
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Success -> {
                try {
                    val quizViewData = getQuizViewData(quizData.data)
                    val state = UserDataSent(chatId, messageId, quizViewData)
                    handler.handleState(state, chatId, messageId)
                } catch (e: ClassCastException) {
                    val state = ErrorState(chatId, messageId, e, "Данные не найдены")
                    handler.handleState(state, chatId, messageId)
                }
            }
        }
    }

    override suspend fun startUserRegistration(chatId: Long, messageId: Int) {
        val state = when (val langListState = langRepo.getAvailableLanguages()) {
            DataState.Empty -> {
                NotFound(chatId, messageId, "Язык не найден")
            }
            is DataState.Failure -> {
                ErrorState(chatId, messageId, langListState.exception, "Что-то пошло не так")
            }
            is DataState.Success -> {
                LanguagesFounded(chatId, messageId, langListState.data)
            }
        }
        handler.handleState(state, chatId, messageId)
    }

    override suspend fun finishRegistration(chatId: Long, messageId: Int, languageId: Long, categoryId: Long) {
        when (val quizDataState = userRepo.addUser(chatId, languageId, categoryId)) {
            DataState.Empty -> {
                val state = NotFound(chatId, messageId, "Пользователь не зарегистрирован")
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, quizDataState.exception, "Что-то пошло не так")
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Success -> {
                try {
                    val quizViewData = getQuizViewData(quizDataState.data)
                    val state = UserRegistered(chatId, messageId, quizViewData)
                    handler.handleState(state, chatId, messageId)
                    createQuizWords(chatId, messageId)
                } catch (e: TypeCastException) {
                    ErrorState(chatId, messageId, e, "Данные не найдены")
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
                val state = NotFound(chatId, messageId, "Пользователь не найден")
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, data.exception, "Что-то пошло не так")
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Success -> {
                try {
                    val quizViewData = getQuizViewData(data.data)
                    val state = UpdatedUserData(chatId, messageId, quizViewData)
                    handler.handleState(state, chatId, messageId)
                    resetQuizWords(chatId, messageId)
                } catch (e: java.lang.ClassCastException) {
                    val state = ErrorState(chatId, messageId, e, "Данные не найдены")
                    handler.handleState(state, chatId, messageId)
                }
            }
        }
    }

    override suspend fun removeUser(chatId: Long, messageId: Int) {
        val state = when (userRepo.deleteUserChatId(chatId)) {
            true -> {
                UserRemoved(chatId, messageId, "Пользовательские данные удалены")
            }
            false -> {
                NotFound(chatId, messageId, "Пользовательн не существует")
            }
        }
        handler.handleState(state, chatId, messageId)
    }

    override suspend fun getLanguages(chatId: Long, messageId: Int) {
        val state = when (val langData = langRepo.getAvailableLanguages()) {
            DataState.Empty -> {
                NotFound(chatId, messageId, "Язык не найден")
            }
            is DataState.Failure -> {
                ErrorState(chatId, messageId, langData.exception, "Не удалось получить языки")
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
                val state = NotFound(chatId, messageId, "Язык не найден")
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, langData.exception, "Не удалось выбрать язык")
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
                NotFound(chatId, messageId, "Категории не найдены")
            }
            is DataState.Failure -> {
                ErrorState(chatId, messageId, categoriesData.exception, "Не удалось получить категории")
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
                val state = NotFound(chatId, messageId, "Категоря не найдена")
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, categoryData.exception, "Не удалось выбрать категорию")
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
        val state = AskToResetQuiz(chatId, messageId, "Хотите начать заново?")
        handler.handleState(state, chatId, messageId)
    }

    override suspend fun startQuiz(chatId: Long, messageId: Int, start: Boolean) {
        if (start) {
            when (val quizDataState = userRepo.getUserByChatId(chatId)) {
                DataState.Empty -> {
                    val state = NotFound(chatId, messageId, "Прежде чем начать, пройдите регистрацию.")
                    handler.handleState(state, chatId, messageId)
                }
                is DataState.Success -> {
                    val state = QuizStarted(chatId, messageId, "Начинаем викторину!")
                    handler.handleState(state, chatId, messageId)
                    getNextQuizWord(chatId, messageId)
                }
                is DataState.Failure -> {
                    val state = ErrorState(chatId, messageId, quizDataState.exception, "Что-то пошло не так")
                    handler.handleState(state, chatId, messageId)
                }
            }
        } else {
            val state = NotFound(chatId, messageId, "Для того чтобы начать викторину введите команду /start")
            handler.handleState(state, chatId, messageId)
        }
    }

    override suspend fun getNextQuizWord(chatId: Long, messageId: Int) {
        when (val quiz = quizRepository.getNextQuizWord(chatId)) {
            DataState.Empty -> {
                val state = NotFound(chatId, messageId, "Вы ответили на все слова!")
                handler.handleState(state, chatId, messageId)
                askToResetQuiz(chatId, messageId)
            }
            is DataState.Failure -> {
                val state = ErrorState(chatId, messageId, quiz.exception, "Слова не найдены!")
                handler.handleState(state, chatId, messageId)
            }
            is DataState.Success -> {
                val state = NextQuizWord(chatId, messageId, quiz.data)
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    override suspend fun answerToQuizQuestion(chatId: Long, messageId: Int, wordId: Long, answer: Boolean) {
        when (val answerData = quizRepository.setAnswerForQuizWord(chatId, wordId, answer)) {
            DataState.Empty -> {
                NotFound(chatId, messageId, "Слово не найдено!")
            }
            is DataState.Failure -> {
                ErrorState(chatId, messageId, answerData.exception, "Неудалось зарегистрировать ответ.")
            }
            is DataState.Success -> {
                if (answerData.data.nextQuizTime == null) {
                    val state = if (answer) {
                        QuizAnswered(chatId, messageId, "Верный ответ")
                    } else {
                        QuizAnswered(chatId, messageId, "Неверный ответ")
                    }
                    handler.handleState(state, chatId, messageId)
                    getNextQuizWord(chatId, messageId)
                } else {
                    timer.schedule(
                        ScheduleQuiz(chatId, messageId),
                        answerData.data.nextQuizTime!!
                    )
                    val state = NextQuizTime(chatId, messageId, SystemMessages.nextTestNotifyMessage(answerData.data.nextQuizTime!!))
                    handler.handleState(state, chatId, messageId)
                }
            }
        }
    }

    override suspend fun createQuizWords(chatId: Long, messageId: Int) {
        when (quizRepository.createQuizWords(chatId)) {
            true -> {
                askToStartQuiz(chatId, messageId)
            }
            false -> {
                val state = NotFound(chatId, messageId, "Что-то пошло не так.")
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    override suspend fun resetQuizWords(chatId: Long, messageId: Int) {
        when (quizRepository.resetQuiz(chatId)) {
            true -> {
                val state = QuizResetted(chatId, messageId, "Викторина сброшена")
                handler.handleState(state, chatId, messageId)
                askToStartQuiz(chatId, messageId)
            }
            false -> {
                val state = NotFound(chatId, messageId, "Что-то пошло не так.")
                handler.handleState(state, chatId, messageId)
            }
        }
    }

    override suspend fun timeToNextQuiz(chatId: Long, messageId: Int) {
        val state = NotFound(chatId, messageId, "Not implemented yet")
        handler.handleState(state, chatId, messageId)
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
            SystemMessages.breakBetweenQuiz(quizData.breakTimeInMillis),
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
