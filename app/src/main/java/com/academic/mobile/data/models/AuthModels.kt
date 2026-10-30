package com.academic.mobile.data.models

import com.google.gson.annotations.SerializedName

/**
 * Solicitud de inicio de sesión para `/api/v1/auth/login`.
 */
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

/**
 * Solicitud de registro de usuario para `/api/v1/auth/registro`.
 */
data class RegistroRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("nombre") val nombre: String
)

/**
 * Solicitud de renovación de token JWT para `/api/v1/auth/refresh`.
 */
data class RefreshTokenRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

/**
 * Respuesta con el par de tokens JWT emitido por `academic-api`.
 */
data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String = "bearer"
)

/**
 * Respuesta con los datos públicos del usuario autenticado o recién registrado.
 */
data class UsuarioResponse(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("created_at") val createdAt: String
)
