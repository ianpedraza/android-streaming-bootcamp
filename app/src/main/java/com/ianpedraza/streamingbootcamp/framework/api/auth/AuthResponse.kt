package com.ianpedraza.streamingbootcamp.framework.api.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @Expose
    @SerializedName("token_type")
    val tokenType: String,

    @Expose
    @SerializedName("expires_in")
    val expiresIn: Long,

    @Expose
    @SerializedName("access_token")
    val accessToken: String,

    @Expose
    @SerializedName("refresh_token")
    val refreshToken: String
)
