package com.academic.mobile.ui.tareas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.academic.mobile.AcademicApp
import com.academic.mobile.R
import com.academic.mobile.data.models.EstadoTarea
import com.academic.mobile.databinding.FragmentTareasBinding
import com.academic.mobile.ui.common.UiState
import com.academic.mobile.ui.common.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Fragmento que presenta el tablero de tareas con filtros por estado (`ChipGroup`) y por materia,
 * además del acceso a [CrearTareaActivity] y [DetalleTareaActivity].
 */
class TareasFragment : Fragment() {

    private var _binding: FragmentTareasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TareasViewModel by viewModels {
        val app = requireActivity().application as AcademicApp
        ViewModelFactory(app.serviceLocator)
    }

    private lateinit var adapter: TareasAdapter
    private var materiaIdFiltro: String? = null
    private var materiaNombreFiltro: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        materiaIdFiltro = arguments?.getString(ARG_MATERIA_ID)
        materiaNombreFiltro = arguments?.getString(ARG_MATERIA_NOMBRE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTareasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarRecyclerView()
        configurarFiltros()
        configurarEventos()
        observarEstados()
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarTareas(
            materiaId = materiaIdFiltro,
            estado = obtenerEstadoSeleccionado()
        )
    }

    private fun configurarRecyclerView() {
        adapter = TareasAdapter { tarea ->
            val intent = Intent(requireContext(), DetalleTareaActivity::class.java).apply {
                putExtra(DetalleTareaActivity.EXTRA_TAREA_ID, tarea.id)
            }
            startActivity(intent)
        }
        binding.rvTareas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTareas.adapter = adapter
    }

    private fun configurarFiltros() {
        if (!materiaIdFiltro.isNullOrBlank()) {
            binding.chipFiltroMateria.text = materiaNombreFiltro ?: getString(R.string.label_materia)
            binding.chipFiltroMateria.visibility = View.VISIBLE
            binding.chipFiltroMateria.setOnCloseIconClickListener {
                materiaIdFiltro = null
                materiaNombreFiltro = null
                binding.chipFiltroMateria.visibility = View.GONE
                viewModel.cargarTareas(materiaId = null, estado = obtenerEstadoSeleccionado())
            }
        } else {
            binding.chipFiltroMateria.visibility = View.GONE
        }

        binding.chipGroupEstado.setOnCheckedStateChangeListener { _, _ ->
            viewModel.cargarTareas(
                materiaId = materiaIdFiltro,
                estado = obtenerEstadoSeleccionado()
            )
        }
    }

    private fun obtenerEstadoSeleccionado(): String? {
        return when (binding.chipGroupEstado.checkedChipId) {
            R.id.chip_pendientes -> EstadoTarea.PENDIENTE.valor
            R.id.chip_en_progreso -> EstadoTarea.EN_PROGRESO.valor
            R.id.chip_completadas -> EstadoTarea.COMPLETADA.valor
            else -> null
        }
    }

    private fun configurarEventos() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.cargarTareas(
                materiaId = materiaIdFiltro,
                estado = obtenerEstadoSeleccionado()
            )
        }

        binding.fabAddTarea.setOnClickListener {
            val intent = Intent(requireContext(), CrearTareaActivity::class.java).apply {
                if (!materiaIdFiltro.isNullOrBlank()) {
                    putExtra(CrearTareaActivity.EXTRA_PRE_MATERIA_ID, materiaIdFiltro)
                }
            }
            startActivity(intent)
        }
    }

    private fun observarEstados() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tareasState.collect { state ->
                    when (state) {
                        is UiState.Idle -> Unit
                        is UiState.Loading -> {
                            if (!binding.swipeRefresh.isRefreshing) {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            binding.tvEmpty.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.swipeRefresh.isRefreshing = false
                            adapter.submitList(state.data)
                            binding.tvEmpty.visibility =
                                if (state.data.isEmpty()) View.VISIBLE else View.GONE
                        }
                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.swipeRefresh.isRefreshing = false
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
                                .setAction(R.string.reintentar) {
                                    viewModel.cargarTareas(
                                        materiaId = materiaIdFiltro,
                                        estado = obtenerEstadoSeleccionado()
                                    )
                                }
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MATERIA_ID = "arg_materia_id"
        private const val ARG_MATERIA_NOMBRE = "arg_materia_nombre"

        fun newInstance(
            materiaId: String? = null,
            materiaNombre: String? = null
        ): TareasFragment {
            return TareasFragment().apply {
                arguments = bundleOf(
                    ARG_MATERIA_ID to materiaId,
                    ARG_MATERIA_NOMBRE to materiaNombre
                )
            }
        }
    }
}
