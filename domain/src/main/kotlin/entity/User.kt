package entity

data class User(
    val id: Long = 0,
    val chatId: Long,
    val categoryId: Long = 1,
    val breakTimeInMillis: Long = 10000,
    val wordsLimit: Int = 5, // if it's 0, send all the words in a test
    val languageId: Long,
    val status: QuizStatus = QuizStatus.DEFAULT
)
