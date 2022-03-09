package repository

interface UserWordRepository {
    
    suspend fun addWordsForUser(chatId: Long, wordIdList: List<Long>): Boolean
    
    suspend fun getWordIdsForUser(chatId: Long, wordsLimit: Int): List<Long>

    suspend fun getWordIdsForUser(chatId: Long): List<Long>

    suspend fun removeWordForUser(chatId: Long, wordId: Long): Boolean

    suspend fun removeWordsForUser(chatId: Long, wordIdList: List<Long>): Boolean
}
