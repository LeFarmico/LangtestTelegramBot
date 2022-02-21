package entity

data class WordData(
    val id: Long,
    val word: String,
    val translate: String,
    val categoryId: Long,
    val languageId: Long
)
