package entity

data class User(
    val id: Long = 0,
    val chatId: Long,
    val categoryId: Long = 1,
    val breakTimeInMillis: Long = 10000,
    val wordsInTest: Int? = 5 // if it's null, send all the words in a test
)
