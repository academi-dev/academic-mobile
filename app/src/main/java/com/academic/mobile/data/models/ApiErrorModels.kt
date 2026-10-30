package com.academic.mobile.data.models

import com.google.gson.annotations.SerializedName

/**
 * Envoltorio del formato estándar de error de `academic-api`:
 * `{"error": {"codigo": "...", "mensaje": "..."}}`.
 */
data class ApiErrorEnvelope(
    @SerializedName("error") val error: ApiErrorDetail?
)

/**
 * Detalle interno del error devuelto por el backend.
 */
data class ApiErrorDetail(
    @SerializedName("codigo") val codigo: String?,
    @SerializedName("mensaje") val mensaje: String?
)
