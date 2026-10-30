package com.academic.mobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Gestor de almacenamiento seguro para los tokens JWT del usuario autenticado.
 *
 * Utiliza [EncryptedSharedPreferences] en dispositivos Android y recurre a
 * [SharedPreferences] convencionales si el proveedor criptográfico del sistema no está disponible.
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences = crearPreferenciasSeguras(context.applicationContext)

    private fun crearPreferenciasSeguras(appContext: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(appContext)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                appContext,
                PREFS_FILENAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Exception) {
            appContext.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        }
    }

    /**
     * Almacena el token de acceso y el token de refresco tras iniciar o renovar sesión.
     */
    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    /**
     * Guarda la información básica del usuario autenticado.
     */
    fun saveUserInfo(email: String, nombre: String? = null) {
        prefs.edit()
            .putString(KEY_USER_EMAIL, email)
            .apply {
                if (nombre != null) {
                    putString(KEY_USER_NAME, nombre)
                }
            }
            .apply()
    }

    /**
     * Obtiene el token de acceso actual o `null` si no existe sesión activa.
     */
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    /**
     * Obtiene el token de refresco actual o `null` si no existe sesión activa.
     */
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    /**
     * Obtiene el correo electrónico almacenado del usuario.
     */
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    /**
     * Indica si el usuario cuenta con un token de acceso válido almacenado localmente.
     */
    fun hasActiveSession(): Boolean = !getAccessToken().isNullOrBlank()

    /**
     * Elimina todas las credenciales almacenadas al cerrar sesión o expirar el refresh token.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_FILENAME = "academic_secure_prefs"
        private const val KEY_ACCESS_TOKEN = "jwt_access_token"
        private const val KEY_REFRESH_TOKEN = "jwt_refresh_token"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }
}
