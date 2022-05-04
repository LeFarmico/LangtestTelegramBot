package entity

import com.google.gson.annotations.SerializedName

data class JwtData(@SerializedName("jwt-token") val jwtToken: String)
