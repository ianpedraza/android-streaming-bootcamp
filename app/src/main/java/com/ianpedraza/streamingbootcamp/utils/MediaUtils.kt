package com.ianpedraza.streamingbootcamp.utils

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import com.ianpedraza.streamingbootcamp.domain.MetaData
import com.ianpedraza.streamingbootcamp.domain.Video

object MediaUtils {
    private const val EXTRA_CREATION_DATE = "creationDate"
    private const val EXTRA_TAGS = "tags"

    private const val DEFAULT_TITLE = "No Title"
    private const val DEFAULT_DESCRIPTION = ""
    private const val DEFAULT_DATE = ""

    fun MediaMetadata.toMetaData(): MetaData {
        return MetaData(
            title = title as String? ?: DEFAULT_TITLE,
            description = description as String? ?: DEFAULT_DESCRIPTION,
            date = releaseDate ?: DEFAULT_DATE,
            tags = tags
        )
    }

    private val MediaMetadata.releaseDate: String?
        get() = extras?.getString(EXTRA_CREATION_DATE)

    private val MediaMetadata.tags: List<String>
        get() = extras?.getStringArrayList(EXTRA_TAGS) ?: emptyList()

    fun Video.toMediaItem(): MediaItem {
        val extras = Bundle().apply {
            putString(EXTRA_CREATION_DATE, creationDate)
            putStringArrayList(EXTRA_TAGS, ArrayList(tags))
        }

        val mediaMetaData = MediaMetadata.Builder()
            .setTitle(title)
            .setDescription(description)
            .setExtras(extras)
            .build()

        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(Uri.parse(url))
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .setMediaMetadata(mediaMetaData)
            .build()
    }

    fun tagsFormat(tags: List<String>): String {
        return tags.fold("") { string, tag ->
            "$string #$tag"
        }.trim()
    }
}
