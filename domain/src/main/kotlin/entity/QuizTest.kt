package entity

data class QuizTest(
    val wordId: Long,
    val wordToTranslate: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
)
