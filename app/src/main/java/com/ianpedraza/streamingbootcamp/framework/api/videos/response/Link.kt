package com.ianpedraza.streamingbootcamp.framework.api.videos.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Link(
    @Expose
    @SerializedName("rel")
    val rel: String,

    @Expose
    @SerializedName("uri")
    val uri: String
)
