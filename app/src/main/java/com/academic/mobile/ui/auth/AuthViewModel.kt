package com.academic.mobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.academic.mobile.data.repository.AuthRepository
import com.academic.mobile.data.repository.Result
import com.academic.mobile.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable del flujo de inicio de sesión, registro de cuenta y estado de sesión.
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val authState: StateFlow<UiState<Unit>> = _authState.asStateFlow()

    /**
     * Indica si el usuario ya dispone de una sesión activa guardada localmente.
     */
    fun tieneSesionActiva(): Boolean = authRepository.tieneSesionActiva()

    /**
     * Inicia sesión con correo electrónico y contraseña.
     */
    fun iniciarSesion(email: String, password: String) {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> _authState.value = UiState.Success(Unit)
                is Result.Error -> _authState.value = UiState.Error(result.message)
            }
        }
    }

    /**
     * Registra una nueva cuenta de usuario e inicia sesión automáticamente.
     */
    fun registrarUsuario(nombre: String, email: String, password: String) {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.registrar(nombre, email, password)) {
                is Result.Success -> _authState.value = UiState.Success(Unit)
                is Result.Error -> _authState.value = UiState.Error(result.message)
            }
        }
    }

    /**
     * Cierra la sesión actual borrando los tokens almacenados.
     */
    fun cerrarSesion() {
        authRepository.cerrarSesion()
        _authState.value = UiState.Idle
    }
}
