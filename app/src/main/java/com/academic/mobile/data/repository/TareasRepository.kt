package com.academic.mobile.data.repository

import com.academic.mobile.data.api.AcademicApiService
import com.academic.mobile.data.api.ApiErrorParser
import com.academic.mobile.data.models.TareaCreateRequest
import com.academic.mobile.data.models.TareaResponse
import com.academic.mobile.data.models.TareaUpdateRequest
import java.io.IOException

/**
 * Repositorio para consultar, crear, actualizar y eliminar tareas académicas en `/api/v1/tareas`.
 */
class TareasRepository(
    private val apiService: AcademicApiService
) {

    /**
     * Obtiene la lista de tareas filtradas opcionalmente por materia y estado.
     */
    suspend fun obtenerTareas(
        materiaId: String? = null,
        estado: String? = null,
        limite: Int = 50,
        desplazamiento: Int = 0
    ): Result<List<TareaResponse>> {
        return try {
            val response = apiService.obtenerTareas(
                limite = limite,
                desplazamiento = desplazamiento,
                materiaId = materiaId,
                estado = estado
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.Success(body.items)
            } else {
                Result.Error(ApiErrorParser.parseError(response), response.code())
            }
        } catch (_: IOException) {
            Result.Error("No fue posible conectar con el servidor. Verifica tu conexión.")
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Error inesperado al cargar las tareas.")
        }
    }

    /**
     * Obtiene el detalle de una tarea específica por su UUID.
     */
    suspend fun obtenerTareaPorId(tareaId: String): Result<TareaResponse> {
        return try {
            val response = apiService.obtenerTareaPorId(tareaId)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.Success(body)
            } else {
                Result.Error(ApiErrorParser.parseError(response), response.code())
            }
        } catch (_: IOException) {
            Result.Error("No fue posible conectar con el servidor. Verifica tu conexión.")
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Error inesperado al consultar la tarea.")
        }
    }

    /**
     * Crea una nueva tarea en `/api/v1/tareas`.
     */
    suspend fun crearTarea(
        titulo: String,
        descripcion: String?,
        fechaEntregaIso: String?,
        prioridad: String,
        materiaId: String
    ): Result<TareaResponse> {
        return try {
            val request = TareaCreateRequest(
                titulo = titulo.trim(),
                descripcion = descripcion?.trim()?.takeIf { it.isNotEmpty() },
                fechaEntrega = fechaEntregaIso,
                prioridad = prioridad,
                materiaId = materiaId
            )
            val response = apiService.crearTarea(request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.Success(body)
            } else {
                Result.Error(ApiErrorParser.parseError(response), response.code())
            }
        } catch (_: IOException) {
            Result.Error("No fue posible conectar con el servidor. Verifica tu conexión.")
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Error inesperado al crear la tarea.")
        }
    }

    /**
     * Actualiza el estado de una tarea existente (`pendiente`, `en_progreso`, `completada`).
     */
    suspend fun actualizarEstadoTarea(tareaId: String, nuevoEstado: String): Result<TareaResponse> {
        return try {
            val response = apiService.actualizarTarea(
                tareaId = tareaId,
                request = TareaUpdateRequest(estado = nuevoEstado)
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.Success(body)
            } else {
                Result.Error(ApiErrorParser.parseError(response), response.code())
            }
        } catch (_: IOException) {
            Result.Error("No fue posible conectar con el servidor. Verifica tu conexión.")
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Error inesperado al actualizar la tarea.")
        }
    }

    /**
     * Elimina una tarea existente por su identificador UUID.
     */
    suspend fun eliminarTarea(tareaId: String): Result<Unit> {
        return try {
            val response = apiService.eliminarTarea(tareaId)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(ApiErrorParser.parseError(response), response.code())
            }
        } catch (_: IOException) {
            Result.Error("No fue posible conectar con el servidor. Verifica tu conexión.")
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Error inesperado al eliminar la tarea.")
        }
    }
}
