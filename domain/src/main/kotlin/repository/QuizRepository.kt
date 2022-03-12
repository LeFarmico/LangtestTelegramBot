package repository

import entity.QuizTest
import entity.QuizWord
import state.DataState

interface QuizRepository {

    suspend fun getQuizTest(chatId: Long): DataState<QuizTest>

    suspend fun getQuizWord(chatId: Long): DataState<QuizWord>

    suspend fun addQuizWords(chatId: Long): Boolean

    suspend fun removeWordForUser(chatId: Long, wordId: Long): Boolean
}
