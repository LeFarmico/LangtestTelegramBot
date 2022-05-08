package entity

data class QuizViewData(
    val languageName: String,
    val categoryName: String,
    val breakTime: String,
    val wordsInQuiz: Int,
    val currentWordNumber: Int
)
