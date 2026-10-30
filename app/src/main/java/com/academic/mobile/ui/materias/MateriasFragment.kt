package com.academic.mobile.ui.materias

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.academic.mobile.AcademicApp
import com.academic.mobile.R
import com.academic.mobile.databinding.DialogCrearMateriaBinding
import com.academic.mobile.databinding.FragmentMateriasBinding
import com.academic.mobile.ui.common.UiState
import com.academic.mobile.ui.common.ViewModelFactory
import com.academic.mobile.ui.main.MainActivity
import com.academic.mobile.utils.Validators
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Fragmento que muestra el catálogo de materias del usuario y permite agregar nuevas materias
 * o tocar una materia para filtrar sus tareas asociadas.
 */
class MateriasFragment : Fragment() {

    private var _binding: FragmentMateriasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MateriasViewModel by viewModels {
        val app = requireActivity().application as AcademicApp
        ViewModelFactory(app.serviceLocator)
    }

    private lateinit var adapter: MateriasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMateriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurarRecyclerView()
        configurarEventos()
        observarEstados()

        viewModel.cargarMaterias()
    }

    private fun configurarRecyclerView() {
        adapter = MateriasAdapter { materia ->
            (activity as? MainActivity)?.abrirTareasFiltradasPorMateria(
                materiaId = materia.id,
                materiaNombre = materia.nombre
            )
        }
        binding.rvMaterias.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMaterias.adapter = adapter
    }

    private fun configurarEventos() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.cargarMaterias()
        }

        binding.fabAddMateria.setOnClickListener {
            mostrarDialogoCrearMateria()
        }
    }

    private fun observarEstados() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.materiasState.collect { state ->
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
                                    .setAction(R.string.reintentar) { viewModel.cargarMaterias() }
                                    .show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.crearMateriaState.collect { state ->
                        when (state) {
                            is UiState.Idle, is UiState.Loading -> Unit
                            is UiState.Success -> {
                                Snackbar.make(
                                    binding.root,
                                    R.string.materia_creada,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                viewModel.limpiarEstadoCreacion()
                            }
                            is UiState.Error -> {
                                Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                                viewModel.limpiarEstadoCreacion()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun mostrarDialogoCrearMateria() {
        val dialogBinding = DialogCrearMateriaBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.nueva_materia)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.guardar, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nombre = dialogBinding.etNombre.text?.toString().orEmpty()
                val codigo = dialogBinding.etCodigo.text?.toString().orEmpty()
                val semestre = dialogBinding.etSemestre.text?.toString().orEmpty()
                val descripcion = dialogBinding.etDescripcion.text?.toString()

                val nombreError = Validators.validateNombre(nombre)
                val codigoError = Validators.validateCodigoMateria(codigo)
                val semestreError = Validators.validateSemestre(semestre)

                dialogBinding.tilNombre.error = nombreError
                dialogBinding.tilCodigo.error = codigoError
                dialogBinding.tilSemestre.error = semestreError

                if (nombreError == null && codigoError == null && semestreError == null) {
                    viewModel.crearMateria(nombre, codigo, semestre, descripcion)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): MateriasFragment = MateriasFragment()
    }
}
