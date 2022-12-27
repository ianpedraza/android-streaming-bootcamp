package com.ianpedraza.streamingbootcamp.data.repository.videos

import com.ianpedraza.streamingbootcamp.data.datasource.VideosDataSource
import com.ianpedraza.streamingbootcamp.domain.Video
import com.ianpedraza.streamingbootcamp.utils.DataState
import com.ianpedraza.streamingbootcamp.utils.NetworkUtils.Companion.makeNetworkCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultVideosRepository
@Inject constructor(
    private val remoteDataSource: VideosDataSource
) : VideosRepository {
    override fun fetchVideos(): Flow<DataState<List<Video>>> =
        makeNetworkCall { remoteDataSource.fetchVideos() }
}
