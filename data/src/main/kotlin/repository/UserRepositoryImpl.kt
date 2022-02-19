package repository

import dataSource.UserDataSource
import entity.User

class UserRepositoryImpl(
    private val dataSource: UserDataSource
) : UserRepository {

    override suspend fun addUser(chatId: Long): Long {
        return dataSource.add(chatId)
    }

    override suspend fun deleteUserById(userId: Long): Boolean {
        return dataSource.deleteById(userId)
    }

    override suspend fun deleteUserChatId(chatId: Long): Boolean {
        return dataSource.deleteByChatId(chatId)
    }

    override suspend fun getUserByChatId(chatId: Long): User? {
        return dataSource.getUserByChatId(chatId)
    }

    override suspend fun getUserById(userId: Long): User? {
        return dataSource.getUserById(userId)
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

    override suspend fun setBreakTimeById(userId: Long, timeInMillis: Long) {
        dataSource.setBreakTimeById(userId, timeInMillis)
    }

    override suspend fun setBreakTimeByChatId(chatId: Long, timeInMillis: Long) {
        dataSource.setBreakTimeByChatId(chatId, timeInMillis)
    }
}
