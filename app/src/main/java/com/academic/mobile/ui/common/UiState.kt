package com.academic.mobile.ui.common

/**
 * Estado genérico de la interfaz de usuario para flujos reactivos en ViewModels.
 */
sealed class UiState<out T> {
    /**
     * Estado inicial en reposo antes de ejecutar una acción.
     */
    data object Idle : UiState<Nothing>()

    /**
     * Operación en curso; la vista debe mostrar un indicador de progreso.
     */
    data object Loading : UiState<Nothing>()

    /**
     * Operación completada con datos listos para presentar en pantalla.
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Error producido durante la operación con mensaje explicativo para el usuario.
     */
    data class Error(val message: String) : UiState<Nothing>()
}
