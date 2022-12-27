package com.ianpedraza.streamingbootcamp.framework.api.videos.response

import com.google.gson.annotations.SerializedName

data class Source(
    @SerializedName("type")
    val type: String,

    @SerializedName("uri")
    val uri: String
)
