package repository

import entity.QuizWord
import entity.QuizWordStats
import state.DataState

interface QuizRepository {

    suspend fun getNextQuizWord(chatId: Long): DataState<QuizWord>

    suspend fun createQuizWords(chatId: Long): Boolean

    suspend fun setCorrectAnswerForQuizWord(chatId: Long, wordId: Long): DataState<QuizWordStats>

    suspend fun setIncorrectAnswerForQuizWord(chatId: Long, wordId: Long): DataState<QuizWord>

    suspend fun resetQuiz(chatId: Long): Boolean

    suspend fun resetQuizWordNumber(chatId: Long): Boolean
}
