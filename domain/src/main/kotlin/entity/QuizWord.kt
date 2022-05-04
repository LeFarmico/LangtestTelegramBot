package entity

import com.google.gson.annotations.SerializedName

data class QuizWord(
    @SerializedName("id") val id: Long,
    @SerializedName("chatId") val chatId: Long,
    @SerializedName("originalWord") val originalWord: String,
    @SerializedName("correctTranslation") val correctTranslation: String,
    @SerializedName("quizId") val quizId: Long,
    @SerializedName("wrongTranslations") val wrongTranslations: List<String>
)
