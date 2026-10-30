package com.academic.mobile.ui.materias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academic.mobile.data.models.MateriaResponse
import com.academic.mobile.data.repository.MateriasRepository
import com.academic.mobile.data.repository.Result
import com.academic.mobile.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el listado y la creación de materias académicas.
 */
class MateriasViewModel(
    private val materiasRepository: MateriasRepository
) : ViewModel() {

    private val _materiasState = MutableStateFlow<UiState<List<MateriaResponse>>>(UiState.Idle)
    val materiasState: StateFlow<UiState<List<MateriaResponse>>> = _materiasState.asStateFlow()

    private val _crearMateriaState = MutableStateFlow<UiState<MateriaResponse>>(UiState.Idle)
    val crearMateriaState: StateFlow<UiState<MateriaResponse>> = _crearMateriaState.asStateFlow()

    /**
     * Consulta la lista de materias del usuario autenticado.
     */
    fun cargarMaterias() {
        _materiasState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = materiasRepository.obtenerMaterias()) {
                is Result.Success -> _materiasState.value = UiState.Success(result.data)
                is Result.Error -> _materiasState.value = UiState.Error(result.message)
            }
        }
    }

    /**
     * Registra una nueva materia y recarga la lista al finalizar con éxito.
     */
    fun crearMateria(nombre: String, codigo: String, semestre: String, descripcion: String?) {
        _crearMateriaState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = materiasRepository.crearMateria(nombre, codigo, semestre, descripcion)) {
                is Result.Success -> {
                    _crearMateriaState.value = UiState.Success(result.data)
                    cargarMaterias()
                }
                is Result.Error -> _crearMateriaState.value = UiState.Error(result.message)
            }
        }
    }

    /**
     * Reinicia el estado de creación de materia después de haber sido procesado por la vista.
     */
    fun limpiarEstadoCreacion() {
        _crearMateriaState.value = UiState.Idle
    }
}
