package dataSource

import entity.QuizWordInfo

class QuizWordsDataSource {

    private val lock = Any()
    private val quizWordInfos = mutableListOf<QuizWordInfo>()
    private var id: Long = 1

    fun addQuizWords(chatId: Long, wordIdList: List<Long>) {
        synchronized(lock) {
            wordIdList.forEach { wordId ->
                val quizWordInfo = QuizWordInfo(
                    id = this.id++,
                    wordId = wordId,
                    chatId = chatId
                )
                quizWordInfos.add(quizWordInfo)
            }
        }
    }

    fun deleteQuizWord(chatId: Long, wordId: Long): Boolean {
        synchronized(lock) {
            return quizWordInfos.removeIf { quizWord ->
                quizWord.chatId == chatId && quizWord.wordId == wordId
            }
        }
    }

    fun getQuizWords(chatId: Long): List<QuizWordInfo> {
        synchronized(lock) {
            return quizWordInfos.filter { it.chatId == chatId }
        }
    }

    fun getQuizWord(chatId: Long): QuizWordInfo? {
        synchronized(lock) {
            return try {
                val index = quizWordInfos.indexOfFirst { it.chatId == chatId }
                quizWordInfos[index]
            } catch (e: IndexOutOfBoundsException) {
                null
            }
        }
    }

    fun getQuizWordsLimited(chatId: Long, limit: Int): List<QuizWordInfo> {
        synchronized(lock) {
            return quizWordInfos.filter { it.chatId == chatId }.shuffled().take(limit)
        }
    }
}
