package com.academic.mobile.data.repository

import com.academic.mobile.data.api.AcademicApiService
import com.academic.mobile.data.api.ApiErrorParser
import com.academic.mobile.data.models.MateriaCreateRequest
import com.academic.mobile.data.models.MateriaResponse
import java.io.IOException

/**
 * Repositorio para consultar y gestionar las materias académicas del usuario autenticado.
 */
class MateriasRepository(
    private val apiService: AcademicApiService
) {
    /**
     * Caché en memoria de la última lista de materias obtenida para resolución rápida de nombres.
     */
    private var materiasCache: List<MateriaResponse> = emptyList()

    /**
     * Obtiene la lista de materias del usuario desde `/api/v1/materias`.
     */
    suspend fun obtenerMaterias(limite: Int = 50, desplazamiento: Int = 0): Result<List<MateriaResponse>> {
        return try {
            val response = apiService.obtenerMaterias(limite = limite, desplazamiento = desplazamiento)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                materiasCache = body.items
                Result.Success(body.items)
            } else {
                Result.Error(ApiErrorParser.parseError(response), response.code())
            }
        } catch (_: IOException) {
            Result.Error("No fue posible conectar con el servidor. Verifica tu conexión.")
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Error inesperado al cargar las materias.")
        }
    }

    /**
     * Crea una nueva materia en `/api/v1/materias`.
     */
    suspend fun crearMateria(
        nombre: String,
        codigo: String,
        semestre: String,
        descripcion: String?
    ): Result<MateriaResponse> {
        return try {
            val request = MateriaCreateRequest(
                nombre = nombre.trim(),
                codigo = codigo.trim().uppercase(),
                semestre = semestre.trim(),
                descripcion = descripcion?.trim()?.takeIf { it.isNotEmpty() }
            )
            val response = apiService.crearMateria(request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                materiasCache = listOf(body) + materiasCache
                Result.Success(body)
            } else {
                Result.Error(ApiErrorParser.parseError(response), response.code())
            }
        } catch (_: IOException) {
            Result.Error("No fue posible conectar con el servidor. Verifica tu conexión.")
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Error inesperado al crear la materia.")
        }
    }

    /**
     * Busca una materia en la caché local por su identificador UUID.
     */
    fun obtenerMateriaEnCache(materiaId: String): MateriaResponse? {
        return materiasCache.firstOrNull { it.id == materiaId }
    }
}
