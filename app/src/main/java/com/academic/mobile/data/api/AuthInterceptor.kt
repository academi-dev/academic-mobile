package com.academic.mobile.data.api

import com.academic.mobile.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor HTTP que adjunta automáticamente la cabecera `Authorization: Bearer <token>`
 * a las peticiones dirigidas a endpoints protegidos de `academic-api`.
 */
class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // No adjuntar token en los endpoints públicos de autenticación
        if (path.endsWith("/auth/login") ||
            path.endsWith("/auth/registro") ||
            path.endsWith("/auth/refresh")
        ) {
            return chain.proceed(originalRequest)
        }

        val accessToken = tokenManager.getAccessToken()
        if (accessToken.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
