package com.academic.mobile.data.api

import com.academic.mobile.data.models.LoginRequest
import com.academic.mobile.data.models.MateriaCreateRequest
import com.academic.mobile.data.models.MateriaPaginatedResponse
import com.academic.mobile.data.models.MateriaResponse
import com.academic.mobile.data.models.MateriaUpdateRequest
import com.academic.mobile.data.models.RefreshTokenRequest
import com.academic.mobile.data.models.RegistroRequest
import com.academic.mobile.data.models.TareaCreateRequest
import com.academic.mobile.data.models.TareaPaginatedResponse
import com.academic.mobile.data.models.TareaResponse
import com.academic.mobile.data.models.TareaUpdateRequest
import com.academic.mobile.data.models.TokenResponse
import com.academic.mobile.data.models.UsuarioResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Definición del contrato REST de `academic-api` (`/api/v1`) para Retrofit 2.
 */
interface AcademicApiService {

    // ==================== Autenticación ====================

    /**
     * Registra una nueva cuenta de usuario en la plataforma.
     */
    @POST("api/v1/auth/registro")
    suspend fun registrarUsuario(
        @Body request: RegistroRequest
    ): Response<UsuarioResponse>

    /**
     * Inicia sesión con correo y contraseña y devuelve el par de tokens JWT.
     */
    @POST("api/v1/auth/login")
    suspend fun iniciarSesion(
        @Body request: LoginRequest
    ): Response<TokenResponse>

    /**
     * Renueva de forma síncrona el par de tokens JWT utilizando el refresh token.
     */
    @POST("api/v1/auth/refresh")
    fun refrescarTokenSync(
        @Body request: RefreshTokenRequest
    ): Call<TokenResponse>

    // ==================== Materias ====================

    /**
     * Obtiene la lista paginada de materias pertenecientes al usuario autenticado.
     */
    @GET("api/v1/materias")
    suspend fun obtenerMaterias(
        @Query("limite") limite: Int = 50,
        @Query("desplazamiento") desplazamiento: Int = 0
    ): Response<MateriaPaginatedResponse>

    /**
     * Obtiene el detalle de una materia por su identificador UUID.
     */
    @GET("api/v1/materias/{id}")
    suspend fun obtenerMateriaPorId(
        @Path("id") materiaId: String
    ): Response<MateriaResponse>

    /**
     * Crea una nueva materia asociada al usuario autenticado.
     */
    @POST("api/v1/materias")
    suspend fun crearMateria(
        @Body request: MateriaCreateRequest
    ): Response<MateriaResponse>

    /**
     * Actualiza una materia existente por su identificador UUID.
     */
    @PUT("api/v1/materias/{id}")
    suspend fun actualizarMateria(
        @Path("id") materiaId: String,
        @Body request: MateriaUpdateRequest
    ): Response<MateriaResponse>

    /**
     * Elimina una materia existente por su identificador UUID.
     */
    @DELETE("api/v1/materias/{id}")
    suspend fun eliminarMateria(
        @Path("id") materiaId: String
    ): Response<Unit>

    // ==================== Tareas ====================

    /**
     * Obtiene la lista paginada de tareas con filtros opcionales de materia, estado y rango de fechas.
     */
    @GET("api/v1/tareas")
    suspend fun obtenerTareas(
        @Query("limite") limite: Int = 50,
        @Query("desplazamiento") desplazamiento: Int = 0,
        @Query("materia_id") materiaId: String? = null,
        @Query("estado") estado: String? = null,
        @Query("fecha_inicio") fechaInicio: String? = null,
        @Query("fecha_fin") fechaFin: String? = null
    ): Response<TareaPaginatedResponse>

    /**
     * Obtiene el detalle de una tarea específica por su identificador UUID.
     */
    @GET("api/v1/tareas/{id}")
    suspend fun obtenerTareaPorId(
        @Path("id") tareaId: String
    ): Response<TareaResponse>

    /**
     * Crea una nueva tarea académica asociada a una materia.
     */
    @POST("api/v1/tareas")
    suspend fun crearTarea(
        @Body request: TareaCreateRequest
    ): Response<TareaResponse>

    /**
     * Actualiza los campos o el estado de una tarea existente.
     */
    @PUT("api/v1/tareas/{id}")
    suspend fun actualizarTarea(
        @Path("id") tareaId: String,
        @Body request: TareaUpdateRequest
    ): Response<TareaResponse>

    /**
     * Elimina una tarea existente por su identificador UUID.
     */
    @DELETE("api/v1/tareas/{id}")
    suspend fun eliminarTarea(
        @Path("id") tareaId: String
    ): Response<Unit>
}
