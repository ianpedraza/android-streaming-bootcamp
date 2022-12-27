package com.ianpedraza.streamingbootcamp.di

import com.ianpedraza.streamingbootcamp.data.datasource.VideosDataSource
import com.ianpedraza.streamingbootcamp.data.repository.videos.DefaultVideosRepository
import com.ianpedraza.streamingbootcamp.data.repository.videos.VideosRepository
import com.ianpedraza.streamingbootcamp.framework.api.videos.VideosRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Singleton
    @Binds
    abstract fun providesVideosDataSource(
        remoteVideosDataSource: VideosRemoteDataSource
    ): VideosDataSource

    @Singleton
    @Binds
    abstract fun providesVideosRepository(
        videosRepository: DefaultVideosRepository
    ): VideosRepository
}
