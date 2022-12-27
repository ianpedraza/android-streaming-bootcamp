package com.ianpedraza.streamingbootcamp.data.datasource

import com.ianpedraza.streamingbootcamp.domain.Video

interface VideosDataSource {
    suspend fun fetchVideos(): List<Video>
}
