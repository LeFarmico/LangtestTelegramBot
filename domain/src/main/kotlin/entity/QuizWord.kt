package entity

data class QuizWord(
    val wordId: Long,
    val wordToTranslate: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
)
