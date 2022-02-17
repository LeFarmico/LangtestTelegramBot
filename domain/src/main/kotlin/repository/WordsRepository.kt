package repository

import entity.TestWordData

interface WordsRepository {

    suspend fun getUnansweredWordsCategoryById(chatId: Long, id: Long): List<TestWordData>

}
