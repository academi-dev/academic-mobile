package com.academic.mobile.ui.tareas

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.academic.mobile.AcademicApp
import com.academic.mobile.R
import com.academic.mobile.data.models.MateriaResponse
import com.academic.mobile.data.models.PrioridadTarea
import com.academic.mobile.databinding.ActivityCrearTareaBinding
import com.academic.mobile.ui.common.UiState
import com.academic.mobile.ui.common.ViewModelFactory
import com.academic.mobile.utils.DateUtils
import com.academic.mobile.utils.Validators
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch

/**
 * Actividad con formulario para crear una nueva tarea académica usando [MaterialDatePicker]
 * y [MaterialTimePicker] para la fecha límite de entrega en UTC.
 */
class CrearTareaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrearTareaBinding

    private val viewModel: TareasViewModel by viewModels {
        ViewModelFactory((application as AcademicApp).serviceLocator)
    }

    private var materiasList: List<MateriaResponse> = emptyList()
    private var selectedMateriaId: String? = null
    private var selectedPrioridad: PrioridadTarea = PrioridadTarea.MEDIA
    private var selectedFechaIsoUtc: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearTareaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedMateriaId = intent.getStringExtra(EXTRA_PRE_MATERIA_ID)

        configurarToolbar()
        configurarSelectorPrioridad()
        configurarEventos()
        observarEstados()

        viewModel.cargarMateriasDisponibles()
    }

    private fun configurarToolbar() {
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun configurarSelectorPrioridad() {
        val prioridades = PrioridadTarea.entries
        val etiquetas = prioridades.map { it.etiqueta }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, etiquetas)
        binding.actvPrioridad.setAdapter(adapter)
        binding.actvPrioridad.setText(selectedPrioridad.etiqueta, false)
        binding.actvPrioridad.setOnItemClickListener { _, _, position, _ ->
            selectedPrioridad = prioridades[position]
        }
    }

    private fun actualizarSelectorMaterias(materias: List<MateriaResponse>) {
        materiasList = materias
        if (materias.isEmpty()) {
            binding.tilMateria.error = getString(R.string.error_requiere_materia)
            return
        }
        binding.tilMateria.error = null
        val etiquetas = materias.map { "${it.codigo} - ${it.nombre}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, etiquetas)
        binding.actvMateria.setAdapter(adapter)

        val preIndex = materias.indexOfFirst { it.id == selectedMateriaId }.takeIf { it >= 0 } ?: 0
        selectedMateriaId = materias[preIndex].id
        binding.actvMateria.setText(etiquetas[preIndex], false)

        binding.actvMateria.setOnItemClickListener { _, _, position, _ ->
            selectedMateriaId = materias[position].id
            binding.tilMateria.error = null
        }
    }

    private fun configurarEventos() {
        binding.btnSeleccionarFecha.setOnClickListener {
            mostrarSelectorFechaHora()
        }

        binding.btnGuardarTarea.setOnClickListener {
            intentarGuardarTarea()
        }
    }

    private fun mostrarSelectorFechaHora() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.label_fecha_entrega)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { dateMillisUtc ->
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(23)
                .setMinute(59)
                .setTitleText(R.string.label_fecha_entrega)
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val iso = DateUtils.buildIsoUtcString(
                    dateMillisUtc = dateMillisUtc,
                    hour = timePicker.hour,
                    minute = timePicker.minute
                )
                selectedFechaIsoUtc = iso
                binding.tvFechaSeleccionada.text = DateUtils.formatPickerSelection(
                    dateMillisUtc = dateMillisUtc,
                    hour = timePicker.hour,
                    minute = timePicker.minute
                )
            }

            timePicker.show(supportFragmentManager, "time_picker_entrega")
        }

        datePicker.show(supportFragmentManager, "date_picker_entrega")
    }

    private fun intentarGuardarTarea() {
        val titulo = binding.etTitulo.text?.toString().orEmpty()
        val descripcion = binding.etDescripcion.text?.toString()
        val materiaId = selectedMateriaId

        val tituloError = Validators.validateTituloTarea(titulo)
        binding.tilTitulo.error = tituloError

        if (materiaId.isNullOrBlank()) {
            binding.tilMateria.error = getString(R.string.error_requiere_materia)
            return
        }

        if (tituloError != null) {
            return
        }

        viewModel.crearTarea(
            titulo = titulo,
            descripcion = descripcion,
            fechaEntregaIso = selectedFechaIsoUtc,
            prioridad = selectedPrioridad.valor,
            materiaId = materiaId
        )
    }

    private fun observarEstados() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.materiasDisponibles.collect { materias ->
                        if (materias.isNotEmpty()) {
                            actualizarSelectorMaterias(materias)
                        }
                    }
                }

                launch {
                    viewModel.operacionState.collect { state ->
                        when (state) {
                            is UiState.Idle -> mostrarCargando(false)
                            is UiState.Loading -> mostrarCargando(true)
                            is UiState.Success -> {
                                mostrarCargando(false)
                                Toast.makeText(
                                    this@CrearTareaActivity,
                                    R.string.tarea_creada,
                                    Toast.LENGTH_SHORT
                                ).show()
                                viewModel.limpiarEstadoOperacion()
                                finish()
                            }
                            is UiState.Error -> {
                                mostrarCargando(false)
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                                viewModel.limpiarEstadoOperacion()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun mostrarCargando(cargando: Boolean) {
        binding.progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
        binding.btnGuardarTarea.isEnabled = !cargando
        binding.btnSeleccionarFecha.isEnabled = !cargando
    }

    companion object {
        const val EXTRA_PRE_MATERIA_ID = "extra_pre_materia_id"
    }
}
