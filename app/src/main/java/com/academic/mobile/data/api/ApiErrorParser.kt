package com.academic.mobile.data.api

import com.academic.mobile.data.models.ApiErrorEnvelope
import com.google.gson.Gson
import retrofit2.Response

/**
 * Utilidad para extraer mensajes legibles en español a partir del formato de error
 * estándar de `academic-api`: `{"error": {"codigo": "...", "mensaje": "..."}}`.
 */
object ApiErrorParser {

    private val gson = Gson()

    /**
     * Extrae el mensaje de error desde un [Response] fallido de Retrofit.
     */
    fun parseError(response: Response<*>): String {
        val rawBody = try {
            response.errorBody()?.string()
        } catch (_: Exception) {
            null
        }
        return parseErrorJson(rawBody, response.code())
    }

    /**
     * Analiza una cadena JSON de error y devuelve el mensaje para el usuario o un mensaje por defecto
     * acorde al código de estado HTTP.
     */
    fun parseErrorJson(rawJson: String?, httpCode: Int = 500): String {
        if (!rawJson.isNullOrBlank()) {
            try {
                val envelope = gson.fromJson(rawJson, ApiErrorEnvelope::class.java)
                val mensaje = envelope?.error?.mensaje?.trim()
                if (!mensaje.isNullOrEmpty()) {
                    return mensaje
                }
            } catch (_: Exception) {
                // Continuar al mensaje por defecto según el código HTTP
            }
        }

        return when (httpCode) {
            400 -> "Solicitud inválida. Verifica los datos enviados."
            401 -> "Credenciales inválidas o sesión expirada."
            403 -> "No tienes permisos para realizar esta acción."
            404 -> "El recurso solicitado no fue encontrado."
            409 -> "El registro ya existe o genera un conflicto."
            422 -> "Los datos ingresados no cumplen con el formato requerido."
            else -> "Ocurrió un error en el servidor (HTTP $httpCode)."
        }
    }
}
