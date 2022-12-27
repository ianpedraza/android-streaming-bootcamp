package com.ianpedraza.streamingbootcamp.framework.api.videos.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Assets(
    @Expose
    @SerializedName("hls")
    val hls: String,

    @Expose
    @SerializedName("iframe")
    val iframe: String,

    @Expose
    @SerializedName("mp4")
    val mp4: String,

    @Expose
    @SerializedName("player")
    val player: String,

    @Expose
    @SerializedName("thumbnail")
    val thumbnail: String
)
