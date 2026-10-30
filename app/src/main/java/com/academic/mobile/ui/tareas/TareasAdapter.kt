package com.academic.mobile.ui.tareas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.academic.mobile.R
import com.academic.mobile.data.models.EstadoTarea
import com.academic.mobile.data.models.PrioridadTarea
import com.academic.mobile.data.models.TareaResponse
import com.academic.mobile.databinding.ItemTareaBinding
import com.academic.mobile.utils.DateUtils

/**
 * Adaptador de [RecyclerView] basado en [ListAdapter] y [DiffUtil] para presentar tareas académicas.
 */
class TareasAdapter(
    private val onTareaClick: (TareaResponse) -> Unit
) : ListAdapter<TareaResponse, TareasAdapter.TareaViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val binding = ItemTareaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TareaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TareaViewHolder(
        private val binding: ItemTareaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tarea: TareaResponse) {
            val context = binding.root.context
            binding.tvTitulo.text = tarea.titulo

            val prioridad = PrioridadTarea.desdeValor(tarea.prioridad)
            binding.tvPrioridad.text = prioridad.etiqueta.uppercase()
            val (textColorRes, bgColorRes) = when (prioridad) {
                PrioridadTarea.ALTA -> R.color.priority_high to R.color.priority_high_bg
                PrioridadTarea.MEDIA -> R.color.priority_medium to R.color.priority_medium_bg
                PrioridadTarea.BAJA -> R.color.priority_low to R.color.priority_low_bg
            }
            binding.tvPrioridad.setTextColor(ContextCompat.getColor(context, textColorRes))
            binding.tvPrioridad.setBackgroundColor(ContextCompat.getColor(context, bgColorRes))

            val estado = EstadoTarea.desdeValor(tarea.estado)
            binding.tvEstado.text = estado.etiqueta
            val statusColorRes = when (estado) {
                EstadoTarea.PENDIENTE -> R.color.status_pending
                EstadoTarea.EN_PROGRESO -> R.color.status_in_progress
                EstadoTarea.COMPLETADA -> R.color.status_completed
            }
            binding.tvEstado.setTextColor(ContextCompat.getColor(context, statusColorRes))

            if (tarea.descripcion.isNullOrBlank()) {
                binding.tvDescripcion.visibility = View.GONE
            } else {
                binding.tvDescripcion.text = tarea.descripcion
                binding.tvDescripcion.visibility = View.VISIBLE
            }

            binding.tvFechaEntrega.text = DateUtils.formatIsoToDisplay(
                tarea.fechaEntrega,
                context.getString(R.string.sin_fecha_entrega)
            )

            binding.root.setOnClickListener {
                onTareaClick(tarea)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<TareaResponse>() {
        override fun areItemsTheSame(oldItem: TareaResponse, newItem: TareaResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TareaResponse, newItem: TareaResponse): Boolean {
            return oldItem == newItem
        }
    }
}
