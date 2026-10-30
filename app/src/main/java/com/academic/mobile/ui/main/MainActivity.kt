package com.academic.mobile.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.academic.mobile.AcademicApp
import com.academic.mobile.R
import com.academic.mobile.databinding.ActivityMainBinding
import com.academic.mobile.ui.auth.AuthViewModel
import com.academic.mobile.ui.auth.LoginActivity
import com.academic.mobile.ui.common.ViewModelFactory
import com.academic.mobile.ui.materias.MateriasFragment
import com.academic.mobile.ui.tareas.TareasFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Actividad principal autenticada que aloja la navegación inferior entre
 * [MateriasFragment] y [TareasFragment], además de la opción de cierre de sesión.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory((application as AcademicApp).serviceLocator)
    }

    private var filtroMateriaIdPendiente: String? = null
    private var filtroMateriaNombrePendiente: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!authViewModel.tieneSesionActiva()) {
            redirigirALogin()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarToolbar()
        configurarNavegacionInferior()

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_materias
        }
    }

    private fun configurarToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    mostrarDialogoCerrarSesion()
                    true
                }
                else -> false
            }
        }
    }

    private fun configurarNavegacionInferior() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_materias -> {
                    binding.toolbar.title = getString(R.string.nav_materias)
                    reemplazarFragmento(MateriasFragment.newInstance())
                    true
                }
                R.id.nav_tareas -> {
                    binding.toolbar.title = getString(R.string.nav_tareas)
                    val fragment = TareasFragment.newInstance(
                        materiaId = filtroMateriaIdPendiente,
                        materiaNombre = filtroMateriaNombrePendiente
                    )
                    filtroMateriaIdPendiente = null
                    filtroMateriaNombrePendiente = null
                    reemplazarFragmento(fragment)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Cambia a la pestaña de tareas aplicando un filtro por la materia seleccionada.
     */
    fun abrirTareasFiltradasPorMateria(materiaId: String, materiaNombre: String) {
        filtroMateriaIdPendiente = materiaId
        filtroMateriaNombrePendiente = materiaNombre
        binding.bottomNavigation.selectedItemId = R.id.nav_tareas
    }

    private fun reemplazarFragmento(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun mostrarDialogoCerrarSesion() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.aceptar) { _, _ ->
                authViewModel.cerrarSesion()
                redirigirALogin()
            }
            .show()
    }

    private fun redirigirALogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
