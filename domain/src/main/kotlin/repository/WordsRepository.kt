package repository

import entity.WordData

interface WordsRepository {
    suspend fun getWordsByCategory(categoryId: Long): List<WordData>

    suspend fun getWordsById(wordIdList: List<Long>): List<WordData>

    suspend fun getWordById(wordId: Long): WordData?
}
