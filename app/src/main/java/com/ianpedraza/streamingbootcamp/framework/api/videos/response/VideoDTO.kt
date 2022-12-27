package com.ianpedraza.streamingbootcamp.framework.api.videos.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VideoDTO(
    @Expose
    @SerializedName("assets")
    val assets: Assets,

    @Expose
    @SerializedName("createdAt")
    val createdAt: String,

    @Expose
    @SerializedName("description")
    val description: String,

    @Expose
    @SerializedName("metadata")
    val metadata: List<Pair<String, String>>,

    @Expose
    @SerializedName("mp4Support")
    val mp4Support: Boolean,

    @Expose
    @SerializedName("panoramic")
    val panoramic: Boolean,

    @Expose
    @SerializedName("public")
    val public: Boolean,

    @Expose
    @SerializedName("publishedAt")
    val publishedAt: String,

    @Expose
    @SerializedName("source")
    val source: Source,

    @Expose
    @SerializedName("tags")
    val tags: List<String>,

    @Expose
    @SerializedName("title")
    val title: String,

    @Expose
    @SerializedName("updatedAt")
    val updatedAt: String,

    @Expose
    @SerializedName("videoId")
    val videoId: String
)
