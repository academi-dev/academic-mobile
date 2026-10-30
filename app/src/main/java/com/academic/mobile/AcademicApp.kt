package com.academic.mobile

import android.app.Application
import android.content.Context
import com.academic.mobile.data.api.AcademicApiService
import com.academic.mobile.data.api.RetrofitClient
import com.academic.mobile.data.local.TokenManager
import com.academic.mobile.data.repository.AuthRepository
import com.academic.mobile.data.repository.MateriasRepository
import com.academic.mobile.data.repository.TareasRepository

/**
 * Contenedor ligero de dependencias (Service Locator) para la aplicación Academic Mobile.
 */
class ServiceLocator(context: Context) {

    val tokenManager: TokenManager by lazy {
        TokenManager(context.applicationContext)
    }

    val apiService: AcademicApiService by lazy {
        RetrofitClient.create(tokenManager)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(apiService, tokenManager)
    }

    val materiasRepository: MateriasRepository by lazy {
        MateriasRepository(apiService)
    }

    val tareasRepository: TareasRepository by lazy {
        TareasRepository(apiService)
    }
}

/**
 * Clase [Application] principal de Academic Mobile que inicializa el [ServiceLocator].
 */
class AcademicApp : Application() {

    lateinit var serviceLocator: ServiceLocator
        private set

    override fun onCreate() {
        super.onCreate()
        serviceLocator = ServiceLocator(this)
    }
}
