package entity

import com.google.gson.annotations.SerializedName

data class QuizData(

    @SerializedName("chatId")
    val chatId: Long,

    @SerializedName("status")
    val status: String = "DEFAULT",

    @SerializedName("wordsInQuiz")
    val wordsInQuiz: Int = 5,

    @SerializedName("currentWordNumber")
    val currentWordNumber: Int = 0,

    @SerializedName("breakTimeInMillis")
    val breakTimeInMillis: Long = 7_200_000,

    @SerializedName("languageId")
    val languageId: Long,

    @SerializedName("categoryId")
    val categoryId: Long
)
