package entity

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("email") val email: String,
    @SerializedName("password") private val password: String
)
