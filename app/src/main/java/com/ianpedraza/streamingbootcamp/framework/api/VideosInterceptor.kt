package com.ianpedraza.streamingbootcamp.framework.api

import okhttp3.Interceptor
import okhttp3.Response

class VideosInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader(
                "authorization",
                "Basic OGRDNkxvdEh6a0p2SHA1NzZPNGRpY0JRdko2SGlHdFlNMHR0amQ0TjVLbTo="
            )
            .build()

        return chain.proceed(request)
    }
}
