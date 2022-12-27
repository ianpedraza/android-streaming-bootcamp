package com.ianpedraza.streamingbootcamp.di.app

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ianpedraza.streamingbootcamp.BuildConfig
import com.ianpedraza.streamingbootcamp.framework.api.VideosInterceptor
import com.ianpedraza.streamingbootcamp.framework.api.auth.AuthService
import com.ianpedraza.streamingbootcamp.framework.api.videos.VideosService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val videosInterceptor = VideosInterceptor()

        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor(logging)
            addInterceptor(videosInterceptor)
        }

        return httpClient.build()
    }

    @Singleton
    @Provides
    fun providesGsonBuilder(): Gson = GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create()

    @Singleton
    @Provides
    fun providesRetrofit(
        gson: Gson,
        client: OkHttpClient
    ): Retrofit = Retrofit
        .Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    @Singleton
    @Provides
    fun providesAuthService(
        retrofit: Retrofit
    ): AuthService = retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun providesVideosService(
        retrofit: Retrofit
    ): VideosService = retrofit.create(VideosService::class.java)
}
