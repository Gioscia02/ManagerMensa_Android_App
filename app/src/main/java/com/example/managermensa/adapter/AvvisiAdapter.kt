package com.example.managermensa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.managermensa.data.Avviso
import com.example.managermensa.databinding.ItemAvvisoBinding

// Adapter per la RecyclerView
class AvvisiAdapter(private val avvisi: List<Avviso>) : RecyclerView.Adapter<AvvisiAdapter.AvvisoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvvisoViewHolder {
        val binding = ItemAvvisoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvvisoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvvisoViewHolder, position: Int) {
        val avviso = avvisi[position]
        holder.bind(avviso)
    }

    override fun getItemCount(): Int = avvisi.size

    // ViewHolder per l'avviso
    inner class AvvisoViewHolder(private val binding: ItemAvvisoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(avviso: Avviso) {
            binding.textTitle.text = avviso.titolo
            binding.textData.text = avviso.data.toString()
            binding.textDescription.text = avviso.testo
        }
    }
}
