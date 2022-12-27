package com.ianpedraza.streamingbootcamp.framework.api.videos

import com.ianpedraza.streamingbootcamp.framework.api.videos.response.VideosResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface VideosService {
    @GET("videos")
    suspend fun fetchVideos(
        @Query("currentPage")
        currentPage: Int = 1,

        @Query("pageSize")
        pageSize: Int = 25
    ): VideosResponse
}
