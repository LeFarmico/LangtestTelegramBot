package repository

import dataSource.UserDataSource
import entity.QuizData
import state.DataState

class UserRepositoryImpl(
    private val dataSource: UserDataSource
) : UserRepository {

    override suspend fun addClient(
        clientId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long,
        wordsInTest: Int
    ): DataState<QuizData> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserChatId(chatId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getUserByChatId(chatId: Long): DataState<QuizData> {
        TODO("Not yet implemented")
    }

    override suspend fun setCategoryForUserByChatId(chatId: Long, categoryId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setBreakTimeByChatId(chatId: Long, timeInMillis: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setLanguageByChatId(chatId: Long, languageId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setWordsInTestByChatId(chatId: Long, languageId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(
        clientId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long,
        wordsInTest: Int
    ): DataState<QuizData> {
        TODO("Not yet implemented")
    }
}
