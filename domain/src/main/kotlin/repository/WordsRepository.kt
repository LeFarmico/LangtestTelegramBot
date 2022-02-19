package repository

import entity.WordData

interface WordsRepository {

    suspend fun getUnansweredWordsCategoryByChatId(chatId: Long): List<WordData>

    suspend fun createWordsCategoryByChatId(chatId: Long, categoryId: Long): Boolean

    suspend fun addCorrectAnswer(wordId: Long, chatId: Long): Boolean
}
