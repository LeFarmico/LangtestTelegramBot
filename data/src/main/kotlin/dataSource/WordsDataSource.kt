package dataSource

import entity.WordData

class WordsDataSource {

    private val lock = Any()
    private val words = mutableListOf<WordData>(
        WordData(1, "Wolf", "Волк", 1),
        WordData(2, "Rabbit", "Кролик", 1),
        WordData(3, "Deer", "Олень", 1),
        WordData(4, "Monkey", "Обезьяна", 1),
        WordData(5, "Snake", "Змея", 1),
        WordData(6, "Dog", "Собака", 1),
        WordData(7, "Cat", "Кошка", 1),
        WordData(8, "Fish", "Рыба", 1),
        WordData(9, "Duck", "Утка", 1),
        WordData(10, "Horse", "Лошадь", 1),
        WordData(11, "Eagle", "Орел", 1),
        WordData(12, "Fox", "Лиса", 1),
    )

    private val wordsForUserMap: MutableMap<Long, List<WordData>> = mutableMapOf()

    fun createWordsForUser(chatId: Long, categoryId: Long): Boolean {
        synchronized(lock) {
            return try {
                wordsForUserMap[chatId] = words
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    fun getUnansweredWords(chatId: Long): List<WordData> {
        synchronized(lock) {
            return try {
                wordsForUserMap[chatId]!!
            } catch (e: NullPointerException) {
                listOf()
            }
        }
    }

    fun addCorrectAnswer(chatId: Long, wordId: Long): Boolean {
        synchronized(lock) {
            return try {
                val words = wordsForUserMap[chatId]!!.toMutableList()
                words.removeIf { it.id == wordId }
                wordsForUserMap[chatId] = words
                true
            } catch (e: NullPointerException) {
                false
            }
        }
    }
}
