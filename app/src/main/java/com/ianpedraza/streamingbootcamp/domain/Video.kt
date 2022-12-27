package com.ianpedraza.streamingbootcamp.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Video(
    val id: String,
    val title: String,
    val description: String,
    val creationDate: String,
    val tags: List<String>,
    val url: String,
    val thumbnail: String
) : Parcelable
