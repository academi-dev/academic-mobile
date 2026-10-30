package com.academic.mobile.data.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de respuesta de una materia devuelta por `/api/v1/materias`.
 */
data class MateriaResponse(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("semestre") val semestre: String,
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("created_at") val createdAt: String
)

/**
 * Cuerpo de solicitud para crear una nueva materia en `/api/v1/materias`.
 */
data class MateriaCreateRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("semestre") val semestre: String
)

/**
 * Cuerpo de solicitud para actualizar una materia existente en `/api/v1/materias/{id}`.
 */
data class MateriaUpdateRequest(
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("codigo") val codigo: String? = null,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("semestre") val semestre: String? = null
)

/**
 * Respuesta paginada de materias devuelta por `GET /api/v1/materias`.
 */
data class MateriaPaginatedResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("limite") val limite: Int,
    @SerializedName("desplazamiento") val desplazamiento: Int,
    @SerializedName("items") val items: List<MateriaResponse>
)
