package com.ianpedraza.streamingbootcamp.framework.api.videos

import com.ianpedraza.streamingbootcamp.data.datasource.VideosDataSource
import com.ianpedraza.streamingbootcamp.di.dispatchers.IoDispatcher
import com.ianpedraza.streamingbootcamp.domain.Video
import com.ianpedraza.streamingbootcamp.domain.mappers.VideosDTOMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideosRemoteDataSource
@Inject constructor(
    private val service: VideosService,
    private val mapper: VideosDTOMapper,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher
) : VideosDataSource {
    override suspend fun fetchVideos(): List<Video> {
        return withContext(dispatcher) {
            val response = service.fetchVideos()
            mapper.fromResponseListToDomainModelList(response.data)
        }
    }
}
