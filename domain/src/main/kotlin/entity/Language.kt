package entity

import com.google.gson.annotations.SerializedName

data class Language(
    @SerializedName("id") val id: Long,
    @SerializedName("languageName") val languageName: String
)
