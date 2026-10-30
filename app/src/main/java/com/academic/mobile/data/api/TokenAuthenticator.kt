package com.academic.mobile.data.api

import com.academic.mobile.data.local.TokenManager
import com.academic.mobile.data.models.RefreshTokenRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Autenticador de OkHttp que intercepta respuestas `401 Unauthorized` e intenta renovar
 * automáticamente el par de tokens JWT mediante `/api/v1/auth/refresh`.
 */
class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val apiServiceProvider: () -> AcademicApiService
) : Authenticator {

    @Synchronized
    override fun authenticate(route: Route?, response: Response): Request? {
        val path = response.request.url.encodedPath
        if (path.endsWith("/auth/login") ||
            path.endsWith("/auth/registro") ||
            path.endsWith("/auth/refresh")
        ) {
            return null
        }

        // Evitar bucles infinitos de reintento
        if (contarReintentos(response) >= 2) {
            tokenManager.clearSession()
            return null
        }

        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            tokenManager.clearSession()
            return null
        }

        return try {
            val refreshResponse = apiServiceProvider()
                .refrescarTokenSync(RefreshTokenRequest(refreshToken))
                .execute()

            val body = refreshResponse.body()
            if (refreshResponse.isSuccessful && body != null) {
                tokenManager.saveTokens(body.accessToken, body.refreshToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${body.accessToken}")
                    .build()
            } else {
                tokenManager.clearSession()
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun contarReintentos(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
