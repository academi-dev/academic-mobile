package com.academic.mobile.data.api

import com.academic.mobile.BuildConfig
import com.academic.mobile.data.local.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Fábrica para construir la instancia de [AcademicApiService] respaldada por OkHttp y Retrofit.
 */
object RetrofitClient {

    private const val TIMEOUT_SECONDS = 30L

    /**
     * Crea una instancia configurada de [AcademicApiService] con interceptores de autenticación y registro.
     */
    fun create(tokenManager: TokenManager): AcademicApiService {
        lateinit var apiService: AcademicApiService

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor)
            .authenticator(TokenAuthenticator(tokenManager) { apiService })
            .build()

        val baseUrl = normalizarBaseUrl(BuildConfig.BASE_URL)

        apiService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AcademicApiService::class.java)

        return apiService
    }

    /**
     * Garantiza que la URL base finalice con `/` según lo requiere Retrofit.
     */
    fun normalizarBaseUrl(rawUrl: String): String {
        val trimmed = rawUrl.trim()
        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }
}
