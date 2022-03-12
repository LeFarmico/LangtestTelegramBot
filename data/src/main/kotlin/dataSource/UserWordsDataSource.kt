package dataSource

import entity.UserWord

class UserWordsDataSource {
    
    private val lock = Any()

    private val userWordsMap: MutableList<UserWord> = mutableListOf()

    fun addWordsForUser(chatId: Long, wordIdList: List<Long>): Boolean {
        synchronized(lock) {
            return try {
                wordIdList.forEach { wordId ->
                    userWordsMap.add(
                        UserWord(chatId, wordId)
                    )
                }
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    
    fun getWordsForUser(chatId: Long): List<Long> {
        synchronized(lock) {
            return userWordsMap.filter { it.chatId == chatId }.map { it.wordId }
        }
    }

    fun getWordsForUserLimited(chatId: Long, limit: Int): List<Long> {
        synchronized(lock) {
            return userWordsMap.filter { it.chatId == chatId }.shuffled().take(limit).map { it.wordId }
        }
    }

    fun removeWordForUser(chatId: Long, wordId: Long): Boolean {
        synchronized(lock) {
            return try {
                userWordsMap.removeIf { it.chatId == chatId && it.wordId == wordId }
                true
            } catch (e: NullPointerException) {
                false
            }
        }
    }

    fun removeWordsForUser(chatId: Long, wordIdList: List<Long>): Boolean {
        synchronized(lock) {
            return try {
                wordIdList.forEach { wordId ->
                    userWordsMap.removeIf { it.chatId == chatId && it.wordId == wordId }
                }
                true
            } catch (e: NullPointerException) {
                false
            }
        }
    }
}
