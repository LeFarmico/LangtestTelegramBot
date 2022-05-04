package entity

import com.google.gson.annotations.SerializedName

data class WordData(
    @SerializedName("id") val id: Long,
    @SerializedName("word_original") val wordOriginal: String,
    @SerializedName("word_translation") val wordTranslation: String,
    @SerializedName("category_id") val categoryId: Long,
    @SerializedName("language_id") val languageId: Long
)
