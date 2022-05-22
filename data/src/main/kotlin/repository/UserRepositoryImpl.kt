package repository

import entity.QuizData
import http.LangTestApi
import state.DataState

class UserRepositoryImpl(
    private val langTestApi: LangTestApi
) : UserRepository {

    override suspend fun addUser(
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long,
        wordsInTest: Int
    ): DataState<QuizData> {
        try {
            val quizData = langTestApi.createQuiz(
                chatId = chatId,
                wordsInQuiz = wordsInTest,
                breakTimeInMillis = nextQuizTime,
                languageId = languageId,
                categoryId = categoryId
            ).execute().body() ?: return DataState.Empty
            return DataState.Success(quizData)
        } catch (e: NullPointerException) {
            return DataState.Failure(e)
        }
    }

    override suspend fun deleteUserChatId(chatId: Long): Boolean {
        val response = langTestApi.deleteQuizData(chatId).execute()
        return response.code() == 200
    }

    override suspend fun getUserByChatId(chatId: Long): DataState<QuizData> {
        return try {
            val response = langTestApi.getQuizData(chatId).execute()
            if (response.body() == null) {
                DataState.Empty
            } else {
                DataState.Success(response.body()!!)
            }
        } catch (e: Exception) {
            DataState.Failure(e)
        }
    }

    override suspend fun setCategoryForUserByChatId(chatId: Long, categoryId: Long): Boolean {
        return try {
            val response = langTestApi.updateQuizData(
                chatId = chatId,
                categoryId = categoryId
            ).execute()
            response.code() == 200
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun setBreakTimeByChatId(chatId: Long, timeInMillis: Long): Boolean {
        return try {
            val response = langTestApi.updateBreakTimeQuizData(
                chatId = chatId,
                breakTimeInMillis = timeInMillis
            ).execute()
            response.code() == 200
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun setLanguageByChatId(chatId: Long, languageId: Long): Boolean {
        return try {
            val response = langTestApi.updateQuizData(
                chatId = chatId,
                languageId = languageId
            ).execute()
            response.code() == 200
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun setWordsInTestByChatId(chatId: Long, wordsInTest: Int): Boolean {
        return try {
            val response = langTestApi.updateQuizData(
                chatId = chatId,
                wordsInQuiz = wordsInTest
            ).execute()
            response.code() == 200
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateUser(
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long,
        wordsInTest: Int
    ): DataState<QuizData> {
        try {
            val response = langTestApi.updateQuizData(
                chatId = chatId,
                wordsInQuiz = wordsInTest,
                languageId = languageId,
                categoryId = categoryId,
                breakTimeInMillis = nextQuizTime,
            ).execute()
            return if (response.body() == null) {
                DataState.Empty
            } else {
                DataState.Success(response.body()!!)
            }
        } catch (e: Exception) {
            return DataState.Failure(e)
        }
    }
}
