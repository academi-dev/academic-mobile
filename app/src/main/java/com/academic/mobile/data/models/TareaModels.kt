package com.academic.mobile.data.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de respuesta de una tarea devuelta por `/api/v1/tareas`.
 */
data class TareaResponse(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("fecha_entrega") val fechaEntrega: String?,
    @SerializedName("prioridad") val prioridad: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("materia_id") val materiaId: String,
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("created_at") val createdAt: String
)

/**
 * Cuerpo de solicitud para crear una nueva tarea en `/api/v1/tareas`.
 */
data class TareaCreateRequest(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("fecha_entrega") val fechaEntrega: String? = null,
    @SerializedName("prioridad") val prioridad: String = PrioridadTarea.MEDIA.valor,
    @SerializedName("estado") val estado: String = EstadoTarea.PENDIENTE.valor,
    @SerializedName("materia_id") val materiaId: String
)

/**
 * Cuerpo de solicitud para actualizar una tarea existente en `/api/v1/tareas/{id}`.
 */
data class TareaUpdateRequest(
    @SerializedName("titulo") val titulo: String? = null,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("fecha_entrega") val fechaEntrega: String? = null,
    @SerializedName("prioridad") val prioridad: String? = null,
    @SerializedName("estado") val estado: String? = null
)

/**
 * Respuesta paginada de tareas devuelta por `GET /api/v1/tareas`.
 */
data class TareaPaginatedResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("limite") val limite: Int,
    @SerializedName("desplazamiento") val desplazamiento: Int,
    @SerializedName("items") val items: List<TareaResponse>
)

/**
 * Enumeración de niveles de prioridad soportados por `academic-api`.
 */
enum class PrioridadTarea(val valor: String, val etiqueta: String) {
    BAJA("baja", "Baja"),
    MEDIA("media", "Media"),
    ALTA("alta", "Alta");

    companion object {
        /**
         * Obtiene la prioridad correspondiente al valor recibido de la API.
         */
        fun desdeValor(valor: String?): PrioridadTarea {
            return entries.firstOrNull { it.valor.equals(valor, ignoreCase = true) } ?: MEDIA
        }
    }
}

/**
 * Enumeración de estados de tarea soportados por `academic-api`.
 */
enum class EstadoTarea(val valor: String, val etiqueta: String) {
    PENDIENTE("pendiente", "Pendiente"),
    EN_PROGRESO("en_progreso", "En progreso"),
    COMPLETADA("completada", "Completada");

    companion object {
        /**
         * Obtiene el estado correspondiente al valor recibido de la API.
         */
        fun desdeValor(valor: String?): EstadoTarea {
            return entries.firstOrNull { it.valor.equals(valor, ignoreCase = true) } ?: PENDIENTE
        }
    }
}
