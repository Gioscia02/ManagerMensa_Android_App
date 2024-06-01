package com.example.managermensa.data

import java.sql.Date

data class Utente(val nome: String, val cognome: String,
                  val email: String, val password: String, val nascita: String){}
