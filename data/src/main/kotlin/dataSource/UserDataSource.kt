package dataSource

import entity.User

class UserDataSource {
    
    private val lock = Any()
    
    val userList = mutableListOf<User>()
    var id: Long = 1

    fun add(chatId: Long): Long {
        synchronized(lock) {
            val user = User(id++, chatId)
            userList.add(user)
            return user.id
        }
    }

    fun deleteByChatId(chatId: Long): Boolean {
        synchronized(lock) {
            if (userList.isEmpty()) return false
            return userList.removeIf { it.chatId == chatId }
        }
    }

    fun deleteById(userId: Long): Boolean {
        synchronized(lock) {
            if (userList.isEmpty()) return false
            return userList.removeIf { it.id == userId }
        }
    }

    fun getUserById(userId: Long): User? {
        synchronized(lock) {
            return userList.find { it.id == userId }
        }
    }

    fun getUserByChatId(chatId: Long): User? {
        synchronized(lock) {
            return userList.find { it.chatId == chatId }
        }
    }

    fun getUsers(): List<User> {
        synchronized(lock) {
            return userList
        }
    }

    fun setBreakTimeById(userId: Long, timeInMillis: Long): Boolean {
        synchronized(lock) {
            return try {
                val index = userList.indexOfFirst { it.id == userId }
                userList[index] = userList[index].copy(breakTimeInMillis = timeInMillis)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    fun setBreakTimeByChatId(chatId: Long, timeInMillis: Long): Boolean {
        synchronized(lock) {
            return try {
                val index = userList.indexOfFirst { it.chatId == chatId }
                userList[index] = userList[index].copy(breakTimeInMillis = timeInMillis)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}