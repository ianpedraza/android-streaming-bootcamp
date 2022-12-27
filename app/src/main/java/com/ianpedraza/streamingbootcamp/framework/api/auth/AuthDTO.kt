package com.ianpedraza.streamingbootcamp.framework.api.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AuthDTO(
    @Expose
    @SerializedName("apiKey")
    val apiKey: String
)
