package com.example.managermensa.activity.localdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Prezzi(

    @PrimaryKey(autoGenerate = true) val id : Int,
    var prezzo_pranzo_completo : Float?,
    var prezzo_cena_completa : Float?,
    var prezzo_primo : Float?,
    var prezzo_secondo : Float?,
    var prezzo_contorno : Float?
)
