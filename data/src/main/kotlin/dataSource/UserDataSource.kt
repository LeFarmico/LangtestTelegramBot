package dataSource

import entity.QuizData

class UserDataSource {
    
    private val lock = Any()
    
    private val quizDataList = mutableListOf<QuizData>()
    private var id: Long = 1

    fun add(chatId: Long, languageId: Long, categoryId: Long): QuizData {
        synchronized(lock) {
            val quizData = QuizData(
                clientId = id++.toString(),
                chatId = chatId,
                languageId = languageId,
                categoryId = categoryId
            )
            quizDataList.add(quizData)
            return quizData
        }
    }

    fun deleteByChatId(chatId: Long): Boolean {
        synchronized(lock) {
            if (quizDataList.isEmpty()) return false
            return quizDataList.removeIf { it.chatId == chatId }
        }
    }

    fun deleteById(userId: String): Boolean {
        synchronized(lock) {
            if (quizDataList.isEmpty()) return false
            return quizDataList.removeIf { it.clientId == userId }
        }
    }

    fun getUserById(userId: String): QuizData? {
        synchronized(lock) {
            return quizDataList.find { it.clientId == userId }
        }
    }

    fun getUserByChatId(chatId: String): QuizData? {
        synchronized(lock) {
            return quizDataList.find { it.clientId == chatId }
        }
    }

    fun getUsers(): List<QuizData> {
        synchronized(lock) {
            return quizDataList
        }
    }

    fun setBreakTimeById(userId: String, timeInMillis: Long): Boolean {
        synchronized(lock) {
            return try {
                val index = quizDataList.indexOfFirst { it.clientId == userId }
                quizDataList[index] = quizDataList[index].copy(breakTimeInMillis = timeInMillis)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    fun setBreakTimeByChatId(chatId: Long, timeInMillis: Long): Boolean {
        synchronized(lock) {
            return try {
                val index = quizDataList.indexOfFirst { it.chatId == chatId }
                quizDataList[index] = quizDataList[index].copy(breakTimeInMillis = timeInMillis)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    fun setCategoryTimeByChatId(chatId: Long, categoryId: Long): Boolean {
        synchronized(lock) {
            return try {
                val index = quizDataList.indexOfFirst { it.chatId == chatId }
                quizDataList[index] = quizDataList[index].copy(categoryId = categoryId)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    fun setCategoryTimeById(userId: String, categoryId: Long): Boolean {
        synchronized(lock) {
            return try {
                val index = quizDataList.indexOfFirst { it.clientId == userId }
                quizDataList[index] = quizDataList[index].copy(categoryId = categoryId)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    fun setLanguageById(userId: String, languageId: Long): Boolean {
        synchronized(lock) {
            return try {
                val index = quizDataList.indexOfFirst { it.clientId == userId }
                quizDataList[index] = quizDataList[index].copy(languageId = languageId)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    fun setLanguageByChatId(userId: String, languageId: Long): Boolean {
        synchronized(lock) {
            return try {
                val index = quizDataList.indexOfFirst { it.clientId == userId }
                quizDataList[index] = quizDataList[index].copy(languageId = languageId)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
