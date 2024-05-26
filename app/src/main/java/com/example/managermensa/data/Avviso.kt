package com.example.managermensa.data

import android.provider.ContactsContract
import java.time.LocalDate

// Modello di dati per un avviso
data class Avviso(val titolo: String, val data: LocalDate, val testo: String)

