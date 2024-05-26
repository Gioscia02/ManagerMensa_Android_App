package com.example.managermensa.activity

import android.app.TimePickerDialog
import android.os.Bundle
import android.provider.ContactsContract.Contacts.Data
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.managermensa.R
import com.example.managermensa.SecurePreferencesManager
import com.example.managermensa.activity.retrofit.Client
import com.example.managermensa.databinding.ActivityPrenotazioniBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PrenotazioniActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrenotazioniBinding
    private val client = OkHttpClient()

    private var selectedTimePranzo: LocalTime? = null
    private var selectedTimeCena: LocalTime? = null

    val viewModel : SharedViewModel by viewModels()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrenotazioniBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        sendReservation()

        val toolbar = binding.toolbarPrenotazioni
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            insets
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.selectTimeButtonPranzo.setOnClickListener {
            showTimePickerDialog(true)
        }

        binding.selectTimeButtonCena.setOnClickListener {
            showTimePickerDialog(false)
        }

        binding.buttonPrenotaPranzo.setOnClickListener {
            val now = LocalTime.now()


            if (now.isAfter(LocalTime.of(0, 0)) ) {
                if (selectedTimePranzo != null) {
                    val email = SecurePreferencesManager.getEmail(this)
                    viewModel.insertPrenotazione(binding, email)

                } else {
                    showToast("Seleziona un orario per il pranzo prima di prenotare")
                }
            } else {
                showToast("Prenotazione non effettuata")
            }
        }

        binding.buttonPrenotaCena.setOnClickListener {
            val now = LocalTime.now()
            if (now.isAfter(LocalTime.of(0, 0)) ) {
                if (selectedTimeCena != null) {

                    val email = SecurePreferencesManager.getEmail(this)
                    viewModel.insertPrenotazione(binding, email)
                } else {
                    showToast("Seleziona un orario per la cena prima di prenotare")
                }
            } else {
                showToast("Non puoi prenotare adesso")
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showTimePickerDialog(isPranzo: Boolean) {
        val currentTime = LocalTime.now()
        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                val selectedLocalTime = LocalTime.of(hourOfDay, minute)

                if (true) {
                    if (selectedLocalTime in LocalTime.of(12, 0)..LocalTime.of(14, 30) && isPranzo) {
                        selectedTimePranzo = selectedLocalTime
                        binding.selectedTimeTextViewPranzo.text =
                            "Orario selezionato per il pranzo: $selectedTimePranzo"
                    } else if (isPranzo && !(selectedLocalTime in LocalTime.of(12, 0)..LocalTime.of(14, 30))) {
                        showToast("Seleziona un orario compreso tra le 12 e le 14:30")
                    } else if (selectedLocalTime in LocalTime.of(19, 0)..LocalTime.of(21, 30) && !isPranzo) {
                        selectedTimeCena = selectedLocalTime
                        binding.selectedTimeTextViewCena.text = "Orario selezionato per la cena: $selectedTimeCena"
                    } else if (!(selectedLocalTime in LocalTime.of(19, 0)..LocalTime.of(21, 30)) && !isPranzo) {
                        showToast("Seleziona un orario compreso tra le 19 e le 21:30")
                    }
                } else {
                    showToast("Seleziona un orario accettabile")
                }

            },
            currentTime.hour,
            currentTime.minute,
            true
        )
        timePickerDialog.updateTime(12, 0)
        timePickerDialog.show()
    }








    private fun parseJsonToModel(jsonString: JsonArray): ArrayList<Utente> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        return gson.fromJson(jsonString, object : TypeToken<ArrayList<Utente>>() {}.type)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}