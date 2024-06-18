package com.example.managermensa.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.managermensa.R
import com.example.managermensa.data.Avviso
import com.example.managermensa.data.Transazione
import com.example.managermensa.databinding.ItemAvvisoBinding
import com.example.managermensa.databinding.ItemStoricoBinding

class StoricoAdapter(private val transazioneList: List<Transazione>) : RecyclerView.Adapter<StoricoAdapter.TransazioneViewHolder>() {

    // Definisci il ViewHolder
    class TransazioneViewHolder(private val binding: ItemStoricoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transazione: Transazione) {
            binding.textData.text = transazione.data
            binding.textTipoOperazione.text = transazione.tipoOperazione
            binding.textQuantita.text = transazione.quantita.toString()

            //Prendo il contesto
            val context = binding.root.context

            //Con il contesto prendo il colore verde
            val ColoreVerde = ContextCompat.getColor(context, R.color.green)

            //Con il contesto prendo il colore verde
            val ColoreRosso = ContextCompat.getColor(context, R.color.red)



            if(transazione.tipoOperazione=="Ricarica"){

                binding.textTipoOperazione.setTextColor(ColoreVerde)
            }
            else{
                binding.textTipoOperazione.setTextColor(ColoreRosso)

            }
        }
    }

    // Inflata il layout dell'elemento di lista e crea il ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransazioneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_avviso, parent, false)
        val binding = ItemStoricoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransazioneViewHolder(binding)
    }

    // Associa i dati dell'elemento al ViewHolder
    override fun onBindViewHolder(holder: TransazioneViewHolder, position: Int) {
        val transazione = transazioneList[position]
        holder.bind(transazione)
    }

    // Restituisce il numero totale degli elementi nella lista
    override fun getItemCount(): Int {
        return transazioneList.size
    }
}
