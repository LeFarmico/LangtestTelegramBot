package repository

import entity.QuizWord
import entity.QuizWordStats
import state.DataState

interface QuizRepository {

    suspend fun getNextQuizWord(chatId: Long): DataState<QuizWord>

    suspend fun createQuizWords(chatId: Long): Boolean

    suspend fun setAnswerForQuizWord(chatId: Long, wordId: Long, answer: Boolean): DataState<QuizWordStats>

    suspend fun resetQuiz(chatId: Long): Boolean
}
