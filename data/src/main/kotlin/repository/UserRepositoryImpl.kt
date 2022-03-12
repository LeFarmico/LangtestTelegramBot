package repository

import dataSource.UserDataSource
import entity.User
import state.DataState

class UserRepositoryImpl(
    private val dataSource: UserDataSource
) : UserRepository {

    override suspend fun addUser(chatId: Long, languageId: Long, categoryId: Long): Long {
        return dataSource.add(chatId, languageId, categoryId)
    }

    override suspend fun deleteUserById(userId: Long): Boolean {
        return dataSource.deleteById(userId)
    }

    override suspend fun deleteUserChatId(chatId: Long): Boolean {
        return dataSource.deleteByChatId(chatId)
    }

    override suspend fun getUserByChatId(chatId: Long): DataState<User> {
        return try {
            when (val user = dataSource.getUserByChatId(chatId)) {
                null -> DataState.Empty
                else -> DataState.Success(user)
            }
        } catch (e: Exception) {
            DataState.Failure(e)
        }
    }

    override suspend fun getUserById(userId: Long): DataState<User> {
        return try {
            when (val user = dataSource.getUserById(userId)) {
                null -> DataState.Empty
                else -> DataState.Success(user)
            }
        } catch (e: Exception) {
            DataState.Failure(e)
        }
    }

    override suspend fun getUsers(): List<User> {
        return dataSource.getUsers()
    }

    override suspend fun setCategoryForUserById(userId: Long, categoryId: Long): Boolean {
        return dataSource.setCategoryTimeById(userId, categoryId)
    }

    override suspend fun setCategoryForUserByChatId(chatId: Long, categoryId: Long): Boolean {
        return dataSource.setCategoryTimeByChatId(chatId, categoryId)
    }

    override suspend fun setBreakTimeById(userId: Long, timeInMillis: Long): Boolean {
        return dataSource.setBreakTimeById(userId, timeInMillis)
    }

    override suspend fun setBreakTimeByChatId(chatId: Long, timeInMillis: Long): Boolean {
        return dataSource.setBreakTimeByChatId(chatId, timeInMillis)
    }

    override suspend fun setLanguageById(userId: Long, languageId: Long): Boolean {
        return dataSource.setLanguageById(userId, languageId)
    }

    override suspend fun setLanguageByChatId(chatId: Long, languageId: Long): Boolean {
        return dataSource.setLanguageByChatId(chatId, languageId)
    }
}
