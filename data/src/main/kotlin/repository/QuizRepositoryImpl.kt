package repository

import entity.QuizStats
import entity.QuizWord
import http.LangTestApi
import state.DataState

class QuizRepositoryImpl(
    private val langTestApi: LangTestApi
) : QuizRepository {

    override suspend fun getNextQuizWord(chatId: Long): DataState<QuizWord> {
        return try {
            val response = langTestApi.getNextQuizWord(chatId).execute()
            if (response.body() == null) {
                DataState.Empty
            } else {
                DataState.Success(response.body()!!)
            }
        } catch (e: Exception) {
            DataState.Failure(e)
        }
    }

    override suspend fun createQuizWords(chatId: Long): Boolean {
        val response = langTestApi.createQuizWords(chatId).execute()
        return response.code() == 200
    }

    override suspend fun setAnswerForQuizWord(chatId: Long, wordId: Long, answer: Boolean): DataState<QuizStats> {
        return try {
            val response = langTestApi.setAnswerForQuizWord(chatId, wordId, answer).execute()
            if (response.body() == null) {
                DataState.Empty
            } else {
                DataState.Success(response.body()!!)
            }
        } catch (e: Exception) {
            DataState.Failure(e)
        }
    }

    override suspend fun resetQuiz(chatId: Long): Boolean {
        val response = langTestApi.resetQuiz(chatId).execute()
        return response.code() == 200
    }
}
