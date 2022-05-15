package entity

import com.google.gson.annotations.SerializedName

data class QuizWordStats(
    @SerializedName("quiz_word_id") val quizWordId: Long,
    @SerializedName("current_word_number") val currentWordNumber: Int,
    @SerializedName("summary_word_count") val summaryWordsInTest: Int,
    @SerializedName("original_word") val originalWord: String,
    @SerializedName("translation") val translation: String,
    @SerializedName("next_quiz_time") val nextQuizTime: Long?
)
