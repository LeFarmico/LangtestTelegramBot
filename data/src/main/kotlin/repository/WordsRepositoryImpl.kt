package repository

import dataSource.WordsDataSource
import entity.WordData

class WordsRepositoryImpl(
    private val wordsDataSource: WordsDataSource
) : WordsRepository {
    override suspend fun getWordsByCategory(categoryId: Long): List<WordData> {
        return wordsDataSource.getWords(categoryId)
    }

    override suspend fun getWordsById(wordIdList: List<Long>): List<WordData> {
        return wordsDataSource.getWordsById(wordIdList)
    }

    override suspend fun getWordById(wordId: Long): WordData? {
        return wordsDataSource.getWordById(wordId)
    }
}
