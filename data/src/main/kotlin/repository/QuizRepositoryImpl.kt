package repository

import dataSource.UserWordsDataSource

class UserWordRepositoryImpl(
    private val userWordsDataSource: UserWordsDataSource
) : UserWordRepository {

    override suspend fun addWordsForUser(chatId: Long, wordIdList: List<Long>): Boolean {
        return userWordsDataSource.addWordsForUser(chatId, wordIdList)
    }

    override suspend fun getWordIdsForUser(chatId: Long): List<Long> {
        return userWordsDataSource.getWordsForUser(chatId)
    }

    override suspend fun getWordIdsForUser(chatId: Long, wordsLimit: Int): List<Long> {
        return userWordsDataSource.getWordsForUserLimited(chatId, wordsLimit)
    }

    override suspend fun removeWordForUser(chatId: Long, wordId: Long): Boolean {
        return userWordsDataSource.removeWordForUser(chatId, wordId)
    }

    override suspend fun removeWordsForUser(chatId: Long, wordIdList: List<Long>): Boolean {
        return userWordsDataSource.removeWordsForUser(chatId, wordIdList)
    }
}
