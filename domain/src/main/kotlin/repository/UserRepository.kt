package repository

import entity.User
import state.DataState

interface UserRepository {
    
    suspend fun addUser(chatId: Long, languageId: Long, categoryId: Long): Long
    
    suspend fun deleteUserById(userId: Long): Boolean

    suspend fun deleteUserChatId(chatId: Long): Boolean

    suspend fun getUserByChatId(chatId: Long): DataState<User>

    suspend fun getUserById(userId: Long): DataState<User>

    suspend fun getUsers(): List<User>

    suspend fun setCategoryForUserById(userId: Long, categoryId: Long): Boolean

    suspend fun setCategoryForUserByChatId(chatId: Long, categoryId: Long): Boolean

    suspend fun setBreakTimeById(userId: Long, timeInMillis: Long): Boolean

    suspend fun setBreakTimeByChatId(chatId: Long, timeInMillis: Long): Boolean

    suspend fun setLanguageById(userId: Long, languageId: Long): Boolean

    suspend fun setLanguageByChatId(chatId: Long, languageId: Long): Boolean
}
