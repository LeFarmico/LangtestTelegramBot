package repository

import entity.QuizStats
import entity.QuizWord
import state.DataState

interface QuizRepository {

    suspend fun getNextQuizWord(chatId: Long): DataState<QuizWord>

    suspend fun createQuizWords(chatId: Long): Boolean

    suspend fun setAnswerForQuizWord(chatId: Long, wordId: Long, answer: Boolean): DataState<QuizStats>

    suspend fun resetQuiz(chatId: Long): Boolean
}
