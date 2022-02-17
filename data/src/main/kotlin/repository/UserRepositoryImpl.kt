package repository

import dataSource.UserDataSource
import entity.User

class UserRepositoryImpl : UserRepository {

    private val dataSource = UserDataSource()
    
    override suspend fun addUser(chatId: Long): Long {
        return dataSource.add(chatId)
    }

    override suspend fun deleteUser(userId: Long): Boolean {
        return dataSource.userList.removeIf { it.chatId == userId }
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

    override suspend fun setCategoryForUserById(userId: Long, categoryId: Long) {

    }

    override suspend fun setCategoryForUserByChatId(chatId: Long, categoryId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun setBreakTimeById(userId: Long, timeInMillis: Long) {
        dataSource.setBreakTimeById(userId, timeInMillis)
    }

    override suspend fun setBreakTimeByChatId(chatId: Long, timeInMillis: Long) {
        dataSource.setBreakTimeByChatId(chatId, timeInMillis)
    }

}
