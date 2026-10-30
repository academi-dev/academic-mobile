package com.academic.mobile.ui.tareas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academic.mobile.data.models.MateriaResponse
import com.academic.mobile.data.models.TareaResponse
import com.academic.mobile.data.repository.MateriasRepository
import com.academic.mobile.data.repository.Result
import com.academic.mobile.data.repository.TareasRepository
import com.academic.mobile.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el listado, filtrado, detalle, creación, cambio de estado y
 * eliminación de tareas académicas.
 */
class TareasViewModel(
    private val tareasRepository: TareasRepository,
    private val materiasRepository: MateriasRepository
) : ViewModel() {

    private val _tareasState = MutableStateFlow<UiState<List<TareaResponse>>>(UiState.Idle)
    val tareasState: StateFlow<UiState<List<TareaResponse>>> = _tareasState.asStateFlow()

    private val _detalleTareaState = MutableStateFlow<UiState<TareaResponse>>(UiState.Idle)
    val detalleTareaState: StateFlow<UiState<TareaResponse>> = _detalleTareaState.asStateFlow()

    private val _operacionState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val operacionState: StateFlow<UiState<String>> = _operacionState.asStateFlow()

    private val _materiasDisponibles = MutableStateFlow<List<MateriaResponse>>(emptyList())
    val materiasDisponibles: StateFlow<List<MateriaResponse>> = _materiasDisponibles.asStateFlow()

    var filtroEstadoActual: String? = null
        private set

    var filtroMateriaIdActual: String? = null
        private set

    /**
     * Carga la lista de tareas aplicando los filtros activos de materia y estado.
     */
    fun cargarTareas(
        materiaId: String? = filtroMateriaIdActual,
        estado: String? = filtroEstadoActual
    ) {
        filtroMateriaIdActual = materiaId
        filtroEstadoActual = estado
        _tareasState.value = UiState.Loading

        viewModelScope.launch {
            when (val result = tareasRepository.obtenerTareas(materiaId = materiaId, estado = estado)) {
                is Result.Success -> _tareasState.value = UiState.Success(result.data)
                is Result.Error -> _tareasState.value = UiState.Error(result.message)
            }
        }
    }

    /**
     * Carga las materias disponibles para poblar el selector al crear una tarea o mostrar el detalle.
     */
    fun cargarMateriasDisponibles() {
        viewModelScope.launch {
            val res = materiasRepository.obtenerMaterias()
            if (res is Result.Success) {
                _materiasDisponibles.value = res.data
            }
        }
    }

    /**
     * Obtiene el nombre legible de una materia a partir de su UUID si se encuentra en caché.
     */
    fun obtenerNombreMateria(materiaId: String): String? {
        return materiasRepository.obtenerMateriaEnCache(materiaId)?.let {
            "${it.codigo} · ${it.nombre}"
        }
    }

    /**
     * Carga el detalle de una tarea específica por su identificador UUID.
     */
    fun cargarDetalleTarea(tareaId: String) {
        _detalleTareaState.value = UiState.Loading
        viewModelScope.launch {
            if (_materiasDisponibles.value.isEmpty()) {
                val matResult = materiasRepository.obtenerMaterias()
                if (matResult is Result.Success) {
                    _materiasDisponibles.value = matResult.data
                }
            }
            when (val result = tareasRepository.obtenerTareaPorId(tareaId)) {
                is Result.Success -> _detalleTareaState.value = UiState.Success(result.data)
                is Result.Error -> _detalleTareaState.value = UiState.Error(result.message)
            }
        }
    }

    /**
     * Registra una nueva tarea en `academic-api`.
     */
    fun crearTarea(
        titulo: String,
        descripcion: String?,
        fechaEntregaIso: String?,
        prioridad: String,
        materiaId: String
    ) {
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            when (
                val result = tareasRepository.crearTarea(
                    titulo = titulo,
                    descripcion = descripcion,
                    fechaEntregaIso = fechaEntregaIso,
                    prioridad = prioridad,
                    materiaId = materiaId
                )
            ) {
                is Result.Success -> _operacionState.value = UiState.Success("CREADA")
                is Result.Error -> _operacionState.value = UiState.Error(result.message)
            }
        }
    }

    /**
     * Actualiza el estado de una tarea existente (`en_progreso` o `completada`).
     */
    fun actualizarEstado(tareaId: String, nuevoEstado: String) {
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = tareasRepository.actualizarEstadoTarea(tareaId, nuevoEstado)) {
                is Result.Success -> {
                    _detalleTareaState.value = UiState.Success(result.data)
                    _operacionState.value = UiState.Success("ACTUALIZADA")
                }
                is Result.Error -> _operacionState.value = UiState.Error(result.message)
            }
        }
    }

    /**
     * Elimina una tarea existente por su UUID.
     */
    fun eliminarTarea(tareaId: String) {
        _operacionState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = tareasRepository.eliminarTarea(tareaId)) {
                is Result.Success -> _operacionState.value = UiState.Success("ELIMINADA")
                is Result.Error -> _operacionState.value = UiState.Error(result.message)
            }
        }
    }

    /**
     * Reinicia el estado de operación puntual.
     */
    fun limpiarEstadoOperacion() {
        _operacionState.value = UiState.Idle
    }
}
