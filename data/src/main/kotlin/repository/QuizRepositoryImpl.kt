package repository

import dataSource.QuizWordsDataSource
import dataSource.UserDataSource
import dataSource.WordsDataSource
import entity.QuizStats
import entity.QuizWord
import entity.QuizWordInfo
import state.DataState

class QuizRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val quizWordsDataSource: QuizWordsDataSource,
    private val wordsDataSource: WordsDataSource
) : QuizRepository {

    override suspend fun getNextQuizWord(chatId: Long): DataState<QuizWord> {
        TODO("Not yet implemented")
    }

    override suspend fun createQuizWords(chatId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setAnswerForQuizWord(chatId: Long, wordId: Long, answer: Boolean): DataState<QuizStats> {
        TODO("Not yet implemented")
    }

    override suspend fun resetQuiz(chatId: Long): Boolean {
        TODO("Not yet implemented")
    }
}
