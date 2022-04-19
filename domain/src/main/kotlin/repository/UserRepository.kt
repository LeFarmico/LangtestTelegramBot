package repository

import entity.QuizData
import state.DataState

interface UserRepository {
    
    suspend fun addClient(
        clientId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long = 10000,
        wordsInTest: Int = 5
    ): DataState<QuizData>

    suspend fun deleteUserChatId(chatId: Long): Boolean

    suspend fun getUserByChatId(chatId: Long): DataState<QuizData>

    suspend fun setCategoryForUserByChatId(chatId: Long, categoryId: Long): Boolean

    suspend fun setBreakTimeByChatId(chatId: Long, timeInMillis: Long): Boolean

    suspend fun setLanguageByChatId(chatId: Long, languageId: Long): Boolean

    suspend fun setWordsInTestByChatId(chatId: Long, languageId: Long): Boolean

    suspend fun updateUser(
        clientId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long,
        wordsInTest: Int
    ): DataState<QuizData>
}
