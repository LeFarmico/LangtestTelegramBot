package intent

import entity.*

sealed class LangTestState

data class ErrorState(val chatId: Long, val messageId: Int, val e: Exception, val message: String) : LangTestState()

data class NotFound(val chatId: Long, val messageId: Int, val message: String) : LangTestState()

data class Notification(val chatId: Long, val messageId: Int, val message: String) : LangTestState()

// User
data class UserDataSent(
    val chatId: Long,
    val messageId: Int,
    val quizViewData: QuizViewData
) : LangTestState()

data class StartedUserRegistration(val chatId: Long, val messageId: Int, val message: String) : LangTestState()

data class UserRegistered(val chatId: Long, val messageId: Int, val quizViewData: QuizViewData) : LangTestState()

data class UpdatedUserData(val chatId: Long, val messageId: Int, val quizViewData: QuizViewData) : LangTestState()

data class UserRemoved(val chatId: Long, val messageId: Int, val message: String) : LangTestState()

// Language
data class LanguagesFounded(val chatId: Long, val messageId: Int, val languageList: List<Language>) : LangTestState()

data class LanguagePicked(val chatId: Long, val messageId: Int, val language: Language) : LangTestState()

// Category
data class CategoryFounded(val chatId: Long, val messageId: Int, val categoryList: List<Category>) : LangTestState()

data class CategoryPicked(val chatId: Long, val messageId: Int, val category: Category) : LangTestState()

// Quiz
data class AskStartQuiz(val chatId: Long, val messageId: Int) : LangTestState()

data class AskToContinueQuiz(val chatId: Long, val messageId: Int) : LangTestState()

data class QuizStarted(val chatId: Long, val messageId: Int, val message: String) : LangTestState()

data class NextQuizWord(val chatId: Long, val messageId: Int, val quizWord: QuizWord) : LangTestState()

data class QuizEnded(val chatId: Long, val messageId: Int, val nextQuizTime: String) : LangTestState()

data class QuizAnswered(val chatId: Long, val messageId: Int, val answerText: String) : LangTestState()

data class AskToResetQuiz(val chatId: Long, val messageId: Int, val message: String) : LangTestState()

data class QuizResetted(val chatId: Long, val messageId: Int, val message: String) : LangTestState()

data class QuizRestarted(val chatId: Long, val messageId: Int, val message: String) : LangTestState()

data class QuizWordPressed(val chatId: Long, val messageId: Int, val message: String) : LangTestState()

data class QuizStopped(val chatId: Long, val messageId: Int, val message: String) : LangTestState()

// Data
data class NextQuizTime(val chatId: Long, val messageId: Int, val nextQuizTimeText: String) : LangTestState()
