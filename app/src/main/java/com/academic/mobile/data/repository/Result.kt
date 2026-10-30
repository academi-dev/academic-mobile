package com.academic.mobile.data.repository

/**
 * Contenedor tipado que representa el resultado exitoso o fallido de una operación de repositorio.
 */
sealed class Result<out T> {
    /**
     * Operación completada con éxito junto con su valor resultante.
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Operación fallida con un mensaje legible en español y código HTTP opcional.
     */
    data class Error(val message: String, val httpCode: Int? = null) : Result<Nothing>()
}
