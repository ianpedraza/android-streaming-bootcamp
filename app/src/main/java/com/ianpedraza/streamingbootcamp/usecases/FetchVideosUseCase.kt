package com.ianpedraza.streamingbootcamp.usecases

import com.ianpedraza.streamingbootcamp.data.repository.videos.VideosRepository
import com.ianpedraza.streamingbootcamp.domain.Video
import com.ianpedraza.streamingbootcamp.utils.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchVideosUseCase @Inject constructor(
    private val repository: VideosRepository
) {
    operator fun invoke(): Flow<DataState<List<Video>>> = repository.fetchVideos()
}
