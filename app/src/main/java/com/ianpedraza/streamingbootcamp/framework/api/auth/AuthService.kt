package com.ianpedraza.streamingbootcamp.framework.api.auth

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/api-key")
    suspend fun auth(
        @Body authDTO: AuthDTO
    ): AuthResponse
}
