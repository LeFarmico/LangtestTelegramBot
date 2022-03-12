package repository

import dataSource.QuizWordsDataSource
import dataSource.UserDataSource
import dataSource.WordsDataSource
import entity.QuizTest
import entity.QuizWord
import state.DataState

class QuizRepositoryImpl(
//    private val userWordsDataSource: UserWordsDataSource,
    private val userDataSource: UserDataSource,
    private val quizWordsDataSource: QuizWordsDataSource,
    private val wordsDataSource: WordsDataSource
) : QuizRepository {

    override suspend fun getQuizTest(chatId: Long): DataState<QuizTest> {
        return try {
            when (val quizWord = quizWordsDataSource.getQuizWord(chatId)) {
                null -> DataState.Empty
                else -> {
                    val user = userDataSource.getUserByChatId(chatId)!!
                    val wordId = quizWord.wordId
                    val wordData = wordsDataSource.getWordById(wordId)!!
                    val wordToTranslate = wordData.word
                    val correctAnswer = wordData.translate
                    val incorrectAnswers = wordsDataSource
                        .getWords(user.categoryId)
                        .toMutableList()
                        .apply { removeIf { it.id == wordId } }
                        .shuffled()
                        .take(2)
                        .map { it.translate }
                    val quizTest = QuizTest(
                        wordId = wordId,
                        wordToTranslate = wordToTranslate,
                        correctAnswer = correctAnswer,
                        incorrectAnswers = incorrectAnswers
                    )
                    DataState.Success(quizTest)
                }
            }
        } catch (e: NullPointerException) {
            DataState.Failure(e)
        }
    }

    override suspend fun getQuizWord(chatId: Long): DataState<QuizWord> {
        return try {
            userDataSource.getUserByChatId(chatId)!!
            when (val quizWord = quizWordsDataSource.getQuizWord(chatId)) {
                null -> DataState.Empty
                else -> DataState.Success(quizWord)
            }
        } catch (e: NullPointerException) {
            DataState.Failure(e)
        }
    }

    override suspend fun addQuizWords(chatId: Long): Boolean {
        return try {
            val user = userDataSource.getUserByChatId(chatId)!!
            val wordDataList = wordsDataSource.getWords(user.categoryId).shuffled().take(user.wordsLimit)
            quizWordsDataSource.addQuizWords(chatId, wordDataList.map { it.id })
            true
        } catch (e: NullPointerException) {
            false
        }
    }

    override suspend fun removeWordForUser(chatId: Long, wordId: Long): Boolean {
        return quizWordsDataSource.deleteQuizWord(chatId, wordId)
    }
}
