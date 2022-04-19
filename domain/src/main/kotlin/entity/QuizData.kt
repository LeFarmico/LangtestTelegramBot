package entity

import com.google.gson.annotations.SerializedName

data class QuizData(
    @SerializedName("clientId")
    val clientId: String,

    @SerializedName("status")
    val status: String = "DEFAULT",

    @SerializedName("wordsInQuiz")
    val wordsInQuiz: Long = 5,

    @SerializedName("currentWordNumber")
    val currentWordNumber: Long = 0,

    @SerializedName("breakTimeInMillis")
    val breakTimeInMillis: Long = 10_000,

    @SerializedName("languageId")
    val languageId: Long,

    @SerializedName("categoryId")
    val categoryId: Long,

    @SerializedName("chatId")
    val chatId: Long
)
