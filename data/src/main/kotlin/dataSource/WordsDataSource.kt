package dataSource

import entity.Category
import entity.Language
import entity.WordData

class WordsDataSource {

    private val lock = Any()
    private val languageList = listOf<Language>(
        Language(1, "English"),
        Language(2, "Spanish")
    )

    private val categoryList = listOf<Category>(
        Category(1, "Animals", 1),
        Category(2, "Food", 1),
        Category(3, "Animals", 2)
    )

    private val words = mutableListOf<WordData>(
        WordData(1, "Wolf", "Волк", 1, 1),
        WordData(2, "Rabbit", "Кролик", 1, 1),
        WordData(3, "Deer", "Олень", 1, 1),
        WordData(4, "Monkey", "Обезьяна", 1, 1),
        WordData(5, "Snake", "Змея", 1, 1),
        WordData(6, "Dog", "Собака", 1, 1),
        WordData(7, "Cat", "Кошка", 1, 1),
        WordData(8, "Fish", "Рыба", 1, 1),
        WordData(9, "Duck", "Утка", 1, 1),
        WordData(10, "Horse", "Лошадь", 1, 1),
        WordData(11, "Eagle", "Орел", 1, 1),
        WordData(12, "Fox", "Лиса", 1, 1),

        WordData(13, "Rice", "Рис", 2, 1),
        WordData(14, "Potato", "Кортофель", 2, 1),
        WordData(15, "Dish", "Блюдо", 2, 1),
        WordData(16, "Breakfast", "Завтра", 2, 1),
        WordData(17, "Lunch", "Обед", 2, 1),
        WordData(18, "Dinner", "Ужин", 2, 1),
        WordData(19, "Fries", "Жареная кортошка", 2, 1),
        WordData(20, "Ice cream", "Мороженное", 2, 1),
        WordData(21, "Milk", "Молоко", 2, 1),
        WordData(22, "Apple", "Яблоко", 2, 1),

        WordData(23, "hormiga", "муравей", 3, 2),
        WordData(24, "oso", "медведь", 3, 2),
        WordData(25, "pájaro", "птица", 3, 2),
        WordData(26, "toro", "бык", 3, 2),
        WordData(27, "mariposa", "бабочка", 3, 2),
        WordData(28, "camello", "верблюд", 3, 2),
        WordData(29, "gato", "кот", 3, 2),
        WordData(30, "pollo cream", "цыплёнок", 3, 2),
        WordData(31, "vaca", "корова", 3, 2),
        WordData(32, "ciervo", "олень", 3, 2),
    )

    fun getCategories(languageId: Long): List<Category> {
        synchronized(lock) {
            return categoryList.filter { it.languageId == languageId }
        }
    }

    fun getLanguages(): List<Language> {
        synchronized(lock) {
            return languageList
        }
    }

    fun getWords(categoryId: Long): List<WordData> {
        synchronized(lock) {
            return try {
                words.filter { it.categoryId == categoryId }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun getCategory(categoryId: Long): Category? {
        synchronized(lock) {
            return categoryList.find { it.id == categoryId }
        }
    }

    fun getWordsById(wordIdList: List<Long>): List<WordData> {
        synchronized(lock) {
            val wordList = mutableListOf<WordData>()
            for (i in wordIdList.indices) {
                words.find { it.id == wordIdList[i] }?.let { wordList.add(it) }
            }
            return wordList
        }
    }

    fun getWordById(wordId: Long): WordData? {
        synchronized(lock) {
            return words.find { it.id == wordId }
        }
    }

    fun getLanguage(languageId: Long): Language? {
        synchronized(lock) {
            return languageList.find { it.id == languageId }
        }
    }
}
