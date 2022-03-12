package dataSource

import entity.QuizWord

class QuizWordsDataSource {

    private val lock = Any()
    private val quizWords = mutableListOf<QuizWord>()
    private var id: Long = 1

    fun addQuizWords(chatId: Long, wordIdList: List<Long>) {
        synchronized(lock) {
            wordIdList.forEach { wordId ->
                val quizWord = QuizWord(
                    id = this.id++,
                    wordId = wordId,
                    chatId = chatId
                )
                quizWords.add(quizWord)
            }
        }
    }

    fun deleteQuizWord(chatId: Long, wordId: Long) {
        synchronized(lock) {
            quizWords.find { quizWord ->
                quizWord.chatId == chatId && quizWord.wordId == wordId
            }
        }
    }

    fun getQuizWords(chatId: Long): List<QuizWord> {
        synchronized(lock) {
            return quizWords.filter { it.chatId == chatId }
        }
    }

    fun getQuizWord(chatId: Long): QuizWord? {
        synchronized(lock) {
            return try {
                val index = quizWords.indexOfFirst { it.chatId == chatId }
                quizWords.removeAt(index)
            } catch (e: IndexOutOfBoundsException) {
                null
            }
        }
    }

    fun getQuizWordsLimited(chatId: Long, limit: Int): List<QuizWord> {
        synchronized(lock) {
            return quizWords.filter { it.chatId == chatId }.shuffled().take(limit)
        }
    }
}
