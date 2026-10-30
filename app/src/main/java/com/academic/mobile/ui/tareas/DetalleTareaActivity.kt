package com.academic.mobile.ui.tareas

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.academic.mobile.AcademicApp
import com.academic.mobile.R
import com.academic.mobile.data.models.EstadoTarea
import com.academic.mobile.data.models.PrioridadTarea
import com.academic.mobile.data.models.TareaResponse
import com.academic.mobile.databinding.ActivityDetalleTareaBinding
import com.academic.mobile.ui.common.UiState
import com.academic.mobile.ui.common.ViewModelFactory
import com.academic.mobile.utils.DateUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Actividad que muestra el detalle completo de una tarea y permite cambiar su estado
 * (`en_progreso`, `completada`) o eliminarla.
 */
class DetalleTareaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleTareaBinding

    private val viewModel: TareasViewModel by viewModels {
        ViewModelFactory((application as AcademicApp).serviceLocator)
    }

    private var tareaId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleTareaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tareaId = intent.getStringExtra(EXTRA_TAREA_ID).orEmpty()
        if (tareaId.isBlank()) {
            finish()
            return
        }

        configurarToolbar()
        configurarAcciones()
        observarEstados()

        viewModel.cargarDetalleTarea(tareaId)
    }

    private fun configurarToolbar() {
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun configurarAcciones() {
        binding.btnEnProgreso.setOnClickListener {
            viewModel.actualizarEstado(tareaId, EstadoTarea.EN_PROGRESO.valor)
        }

        binding.btnCompletada.setOnClickListener {
            viewModel.actualizarEstado(tareaId, EstadoTarea.COMPLETADA.valor)
        }

        binding.btnEliminar.setOnClickListener {
            confirmarEliminacion()
        }
    }

    private fun confirmarEliminacion() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.btn_eliminar_tarea)
            .setMessage(R.string.confirmar_eliminar_tarea)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.aceptar) { _, _ ->
                viewModel.eliminarTarea(tareaId)
            }
            .show()
    }

    private fun observarEstados() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.detalleTareaState.collect { state ->
                        when (state) {
                            is UiState.Idle -> Unit
                            is UiState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.contentGroup.visibility = View.GONE
                            }
                            is UiState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.contentGroup.visibility = View.VISIBLE
                                mostrarDetalle(state.data)
                            }
                            is UiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.operacionState.collect { state ->
                        when (state) {
                            is UiState.Idle -> Unit
                            is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                            is UiState.Success -> {
                                binding.progressBar.visibility = View.GONE
                                if (state.data == "ELIMINADA") {
                                    Toast.makeText(
                                        this@DetalleTareaActivity,
                                        R.string.tarea_eliminada,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    viewModel.limpiarEstadoOperacion()
                                    finish()
                                } else {
                                    Snackbar.make(
                                        binding.root,
                                        R.string.tarea_actualizada,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    viewModel.limpiarEstadoOperacion()
                                }
                            }
                            is UiState.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                                viewModel.limpiarEstadoOperacion()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun mostrarDetalle(tarea: TareaResponse) {
        binding.tvTitulo.text = tarea.titulo

        val prioridad = PrioridadTarea.desdeValor(tarea.prioridad)
        binding.chipPrioridad.text = "${getString(R.string.label_prioridad)}: ${prioridad.etiqueta}"

        val estado = EstadoTarea.desdeValor(tarea.estado)
        binding.chipEstado.text = estado.etiqueta

        val nombreMateria = viewModel.obtenerNombreMateria(tarea.materiaId)
        binding.tvMateria.text = nombreMateria ?: "${getString(R.string.label_materia)} ID: ${tarea.materiaId}"

        binding.tvFechaEntrega.text = DateUtils.formatIsoToDisplay(
            tarea.fechaEntrega,
            getString(R.string.sin_fecha_entrega)
        )

        binding.tvDescripcion.text = tarea.descripcion?.takeIf { it.isNotBlank() }
            ?: getString(R.string.sin_descripcion)

        binding.btnEnProgreso.isEnabled = estado != EstadoTarea.EN_PROGRESO
        binding.btnCompletada.isEnabled = estado != EstadoTarea.COMPLETADA
    }

    companion object {
        const val EXTRA_TAREA_ID = "extra_tarea_id"
    }
}
