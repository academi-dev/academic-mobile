package com.academic.mobile.ui.materias

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.academic.mobile.data.models.MateriaResponse
import com.academic.mobile.databinding.ItemMateriaBinding

/**
 * Adaptador de [RecyclerView] basado en [ListAdapter] y [DiffUtil] para presentar materias académicas.
 */
class MateriasAdapter(
    private val onMateriaClick: (MateriaResponse) -> Unit
) : ListAdapter<MateriaResponse, MateriasAdapter.MateriaViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriaViewHolder {
        val binding = ItemMateriaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MateriaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MateriaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MateriaViewHolder(
        private val binding: ItemMateriaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(materia: MateriaResponse) {
            binding.tvCodigo.text = materia.codigo
            binding.tvSemestre.text = materia.semestre
            binding.tvNombre.text = materia.nombre

            if (materia.descripcion.isNullOrBlank()) {
                binding.tvDescripcion.visibility = View.GONE
            } else {
                binding.tvDescripcion.text = materia.descripcion
                binding.tvDescripcion.visibility = View.VISIBLE
            }

            binding.root.setOnClickListener {
                onMateriaClick(materia)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<MateriaResponse>() {
        override fun areItemsTheSame(oldItem: MateriaResponse, newItem: MateriaResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MateriaResponse, newItem: MateriaResponse): Boolean {
            return oldItem == newItem
        }
    }
}
