package repository

import dataSource.UserDataSource
import dataSource.WordsDataSource
import entity.WordData

class WordsRepositoryImpl(
    private val wordsDataSource: WordsDataSource,
    private val userDataSource: UserDataSource
) : WordsRepository {
    
    override suspend fun getUnansweredWordsCategoryByChatId(chatId: Long): List<WordData> {
        return when (val wordsInTest = userDataSource.getUserByChatId(chatId)?.wordsInTest) {
            null -> wordsDataSource.getUnansweredWords(chatId)
            else -> wordsDataSource.getUnansweredWords(chatId).shuffled().take(wordsInTest)
        }
    }

    override suspend fun createWordsCategoryByChatId(chatId: Long, categoryId: Long): Boolean {
        return wordsDataSource.createWordsForUser(chatId, categoryId)
    }

    override suspend fun addCorrectAnswer(wordId: Long, chatId: Long): Boolean {
        return wordsDataSource.addCorrectAnswer(chatId, wordId)
    }
}
