package interactor

import entity.*
import state.DataState

interface ILangTestInteractor {

    suspend fun registerUser(
        clientId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        breakTimeInMillis: Long,
        wordsLimit: Int
    ): DataState<QuizData>

    suspend fun deleteUser(chatId: Long): Boolean

    suspend fun getClientData(chatId: Long): DataState<QuizData>

    suspend fun updateClientData(
        clientId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long,
        wordsInTest: Int
    ): DataState<QuizData>

    suspend fun getNextWordOrScheduleQuiz(chatId: Long): DataState<QuizWord>

    suspend fun setAnswerForQuiz(chatId: Long, quizWordId: Long, answer: Boolean): DataState<QuizStats>

    suspend fun resetQuiz(chatId: Long): Boolean

    suspend fun createQuizForUser(chatId: Long): Boolean
}
