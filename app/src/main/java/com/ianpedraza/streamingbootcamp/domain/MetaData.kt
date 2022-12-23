package com.ianpedraza.streamingbootcamp.domain

import androidx.media3.common.MediaMetadata

data class MetaData(
    val title: String,
    val description: String,
    val date: String
)

private const val DEFAULT_TITLE = "No Title"
private const val DEFAULT_DESCRIPTION = ""
private const val DEFAULT_DATE = ""

fun MediaMetadata.toMetaData(): MetaData {
    return MetaData(
        title = title as String? ?: DEFAULT_TITLE,
        description = description as String? ?: DEFAULT_DESCRIPTION,
        date = releaseDate ?: DEFAULT_DATE
    )
}

val MediaMetadata.releaseDate: String?
    get() {
        return if (releaseYear == null || releaseMonth == null || releaseDay == null) {
            null
        } else {
            "$releaseYear-$releaseMonth-$releaseDay"
        }
    }
