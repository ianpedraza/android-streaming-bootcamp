package com.ianpedraza.streamingbootcamp.framework.api.videos.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VideosResponse(
    @Expose
    @SerializedName("data")
    val data: List<VideoDTO>,

    @Expose
    @SerializedName("pagination")
    val pagination: Pagination
)
