package entity

data class QuizStats(
    val currentWordNumber: Int,
    val summaryWordsInTest: Int,
    val nextQuizTime: Long?
)
