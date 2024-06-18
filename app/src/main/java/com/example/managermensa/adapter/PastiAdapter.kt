package com.example.managermensa.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.managermensa.R
import com.example.managermensa.data.Allergia
import com.example.managermensa.data.Avviso
import com.example.managermensa.data.Pasto
import com.example.managermensa.databinding.ItemAvvisoBinding
import com.example.managermensa.databinding.ItemPastoBinding

class PastiAdapter(private val avvisi: List<Pasto>, private val allergie: List<Allergia>) : RecyclerView.Adapter<PastiAdapter.PastoViewHolder>(){

     var allergia_pasto: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastoViewHolder {
        val binding = ItemPastoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PastoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PastoViewHolder, position: Int) {
        val avviso = avvisi[position]
        holder.bind(avviso,allergie)
    }

    override fun getItemCount(): Int = avvisi.size

    // ViewHolder per l'avviso
    inner class PastoViewHolder(private val binding: ItemPastoBinding) : RecyclerView.ViewHolder(binding.root) {

        //Prendo il contesto
        val context = binding.root.context

        //Con il contesto prendo il colore verde
        val ColoreRosso = ContextCompat.getColor(context, R.color.red)

        fun bind(pasto: Pasto,allergie: List<Allergia>) {

            for(i in allergie){
                for(n in pasto.allergie) {
                    if (i.nome == n){
                        binding.textAvvertenza.text = "ATTENZIONE PUO' ESSERE PERICOLOSO PER LA TUA SALUTE"
                        allergia_pasto = i.nome
                    }
                }
            }

            binding.textNome.text = pasto.nome
            binding.textRischioAllergie.text = pasto.allergie.joinToString(",")
        }
    }
}