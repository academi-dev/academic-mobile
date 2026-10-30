package com.academic.mobile.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.academic.mobile.ServiceLocator
import com.academic.mobile.ui.auth.AuthViewModel
import com.academic.mobile.ui.materias.MateriasViewModel
import com.academic.mobile.ui.tareas.TareasViewModel

/**
 * Fábrica centralizada de [ViewModel] que inyecta los repositorios provistos por [ServiceLocator].
 */
class ViewModelFactory(
    private val serviceLocator: ServiceLocator
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(serviceLocator.authRepository) as T
            }
            modelClass.isAssignableFrom(MateriasViewModel::class.java) -> {
                MateriasViewModel(serviceLocator.materiasRepository) as T
            }
            modelClass.isAssignableFrom(TareasViewModel::class.java) -> {
                TareasViewModel(
                    tareasRepository = serviceLocator.tareasRepository,
                    materiasRepository = serviceLocator.materiasRepository
                ) as T
            }
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
