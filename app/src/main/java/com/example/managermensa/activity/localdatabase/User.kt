package com.example.managermensa.activity.localdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(

            @PrimaryKey() val email : String,
            val password : String,
            val nome : String?,
            val cognome : String?,
            val nascita : String?
)
