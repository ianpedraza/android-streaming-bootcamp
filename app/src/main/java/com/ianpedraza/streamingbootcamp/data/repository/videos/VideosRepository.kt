package com.ianpedraza.streamingbootcamp.data.repository.videos

import com.ianpedraza.streamingbootcamp.domain.Video
import com.ianpedraza.streamingbootcamp.utils.DataState
import kotlinx.coroutines.flow.Flow

interface VideosRepository {
    fun fetchVideos(): Flow<DataState<List<Video>>>
}
