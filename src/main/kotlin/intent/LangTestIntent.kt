package intent

import entity.QuizData

interface LangTestIntent {

    // ---------- user ----------
    suspend fun checkForUser(chatId: Long, messageId: Int)

    suspend fun getUserData(chatId: Long, messageId: Int)

    suspend fun startUserRegistration(chatId: Long, messageId: Int)

    suspend fun finishRegistration(chatId: Long, messageId: Int, languageId: Long, categoryId: Long)

    suspend fun updateUserInformation(chatId: Long, messageId: Int, quizData: QuizData)

    suspend fun removeUser(chatId: Long, messageId: Int)

    // ---------- language ----------
    suspend fun getLanguages(chatId: Long, messageId: Int)

    suspend fun selectLanguage(chatId: Long, messageId: Int, languageId: Long)

    // ---------- category ----------
    suspend fun getCategories(chatId: Long, messageId: Int, languageId: Long)

    suspend fun selectCategory(chatId: Long, messageId: Int, categoryId: Long)

    // --------- quiz ---------

    suspend fun askToStartQuiz(chatId: Long, messageId: Int)

    suspend fun askToContinueQuiz(chatId: Long, messageId: Int)

    suspend fun askToResetQuiz(chatId: Long, messageId: Int)

    suspend fun startQuiz(chatId: Long, messageId: Int, start: Boolean)

    suspend fun getNextQuizWord(chatId: Long, messageId: Int)
    
    suspend fun answerToQuizQuestion(chatId: Long, messageId: Int, wordId: Long, answer: Boolean)

    suspend fun createQuizWords(chatId: Long, messageId: Int)

    suspend fun resetQuizWords(chatId: Long, messageId: Int)

    // ---------- data --------
    suspend fun timeToNextQuiz(chatId: Long, messageId: Int)
}
