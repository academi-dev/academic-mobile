package com.academic.mobile.data.repository

import com.academic.mobile.data.api.AcademicApiService
import com.academic.mobile.data.api.ApiErrorParser
import com.academic.mobile.data.local.TokenManager
import com.academic.mobile.data.models.LoginRequest
import com.academic.mobile.data.models.RegistroRequest
import com.academic.mobile.data.models.TokenResponse
import com.academic.mobile.data.models.UsuarioResponse
import java.io.IOException

/**
 * Repositorio encargado de las operaciones de autenticación, registro y gestión de sesión JWT.
 */
class AuthRepository(
    private val apiService: AcademicApiService,
    private val tokenManager: TokenManager
) {

    /**
     * Inicia sesión en `academic-api` y almacena de forma segura los tokens JWT obtenidos.
     */
    suspend fun login(email: String, password: String): Result<TokenResponse> {
        return try {
            val response = apiService.iniciarSesion(LoginRequest(email.trim(), password))
            val body = response.body()
            if (response.isSuccessful && body != null) {
                tokenManager.saveTokens(body.accessToken, body.refreshToken)
                tokenManager.saveUserInfo(email.trim())
                Result.Success(body)
            } else {
                Result.Error(ApiErrorParser.parseError(response), response.code())
            }
        } catch (_: IOException) {
            Result.Error("No fue posible conectar con el servidor. Verifica tu conexión.")
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Error inesperado al iniciar sesión.")
        }
    }

    /**
     * Registra un nuevo usuario en `academic-api` e inicia sesión automáticamente tras el registro.
     */
    suspend fun registrar(nombre: String, email: String, password: String): Result<UsuarioResponse> {
        return try {
            val cleanEmail = email.trim()
            val cleanNombre = nombre.trim()
            val response = apiService.registrarUsuario(
                RegistroRequest(email = cleanEmail, password = password, nombre = cleanNombre)
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                // Iniciar sesión automáticamente para que el usuario entre directo a la app
                val loginResponse = apiService.iniciarSesion(LoginRequest(cleanEmail, password))
                val tokens = loginResponse.body()
                if (loginResponse.isSuccessful && tokens != null) {
                    tokenManager.saveTokens(tokens.accessToken, tokens.refreshToken)
                    tokenManager.saveUserInfo(cleanEmail, cleanNombre)
                }
                Result.Success(body)
            } else {
                Result.Error(ApiErrorParser.parseError(response), response.code())
            }
        } catch (_: IOException) {
            Result.Error("No fue posible conectar con el servidor. Verifica tu conexión.")
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Error inesperado al registrar la cuenta.")
        }
    }

    /**
     * Verifica si existe una sesión autenticada activa en el dispositivo.
     */
    fun tieneSesionActiva(): Boolean = tokenManager.hasActiveSession()

    /**
     * Cierra la sesión actual eliminando los tokens almacenados localmente.
     */
    fun cerrarSesion() {
        tokenManager.clearSession()
    }
}
