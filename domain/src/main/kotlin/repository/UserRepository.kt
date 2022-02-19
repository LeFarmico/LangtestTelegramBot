package repository

import entity.User

interface UserRepository {
    
    suspend fun addUser(chatId: Long): Long
    
    suspend fun deleteUserById(userId: Long): Boolean

    suspend fun deleteUserChatId(chatId: Long): Boolean

    suspend fun getUserByChatId(chatId: Long): User?

    suspend fun getUserById(userId: Long): User?

    suspend fun getUsers(): List<User>

    suspend fun setCategoryForUserById(userId: Long, categoryId: Long): Boolean

    suspend fun setCategoryForUserByChatId(chatId: Long, categoryId: Long): Boolean

    suspend fun setBreakTimeById(userId: Long, timeInMillis: Long)

    suspend fun setBreakTimeByChatId(chatId: Long, timeInMillis: Long)
}
