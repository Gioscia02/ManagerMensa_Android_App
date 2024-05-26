package com.example.managermensa.activity

import java.sql.Date

data class Utente(val nome: String, val cognome: String,
                  val email: String, val nascita: Date, val idutente: Int){}
