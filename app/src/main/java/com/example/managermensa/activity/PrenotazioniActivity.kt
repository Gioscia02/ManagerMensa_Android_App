package com.example.managermensa.activity

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.managermensa.NotificationReceiver
import com.example.managermensa.R
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.data.Utente
import com.example.managermensa.databinding.ActivityPrenotazioniBinding
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.minutes

class PrenotazioniActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrenotazioniBinding

    //Orario per le prenotazioni selezionate tramite i pulsanti
    private var selectedTimePranzo: LocalTime? = null
    private var selectedTimeCena: LocalTime? = null

    val viewModel : SharedViewModel by viewModels()

    //Può essere 'Pranzo' o 'Cena' e verra' passato all'insertPrenotazione
    var pasto: String = ""


    //Viene salvata la mail dell'utente da passare all'insertPRenotazione
    var email_ = ""

    var prenotazioneResult: Boolean? = null
    var orarioPrenotazioneResult: String? = null

    //Orario e minuto scelti per la prenotazione da effettuare
    var oraPrenotazione:Int =0
    var minutoPrenotazione:Int =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrenotazioniBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getPrenotazioni(this)

        //Recupero email
        lifecycleScope.launch (Dispatchers.IO){
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "MensaDatabase"
            ).build()
            val userDao = db.userDao()
            val user_attuale = userDao.SelectUsers()

            val email : String = user_attuale.email

            email_ = email

            Log.d("aaaaaaaaaaaaaaaaa", user_attuale.toString())


        }



        val toolbar = binding.toolbarPrenotazioni
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            insets
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }



        //Button per la selezione dell'orario di pranzo
        binding.selectTimeButtonPranzo.setOnClickListener {
            showTimePickerDialog(true)
        }
        //Button per la selezione dell'orario di cena
        binding.selectTimeButtonCena.setOnClickListener {
            showTimePickerDialog(false)
        }

        //Button per l'invio di prenotazione per pranzo
        binding.buttonPrenotaPranzo.setOnClickListener {

            pasto = "pranzo"

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonPrenotaPranzo.context,
                R.anim.button_scale
            )
            binding.buttonPrenotaPranzo.startAnimation(scaleAnimation)

            val now = LocalTime.now()

            //Controllo se l'orario corrente è accettabile per effettuare la prenotazione
            if (now.isAfter(LocalTime.of(0, 0)) ) {

                val Prenotazione_: LocalTime? = selectedTimePranzo
                if (Prenotazione_ != null) {

                    //Invio prenotazione
                    viewModel.insertPrenotazione(selectedTimePranzo.toString(), email_, pasto)


                    oraPrenotazione = Prenotazione_.hour
                    minutoPrenotazione = Prenotazione_.minute

                    Log.d("ORARIOPRENOTA", "Ora Prenotazione: $oraPrenotazione")
                    Log.d("MINUTOPRENOTA", "Minuto Prenotazione: $minutoPrenotazione")

                } else {
                    showToast("Seleziona un orario per il pranzo prima di prenotare")
                }
            } else {
                showToast("Prenotazione non effettuata")
            }
        }


        //Button di invio prenotazione per cena
        binding.buttonPrenotaCena.setOnClickListener {

            pasto = "cena"

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonPrenotaCena.context,
                R.anim.button_scale
            )
            binding.buttonPrenotaCena.startAnimation(scaleAnimation)

            val now = LocalTime.now()

            //Controllo se l'orario corrente è accettabile per effettuare la prenotazione
            if (now.isAfter(LocalTime.of(0, 0)) ) {
                val Prenotazione_: LocalTime? = selectedTimeCena
                if (Prenotazione_ != null) {

                    //Invio prenotazione
                    viewModel.insertPrenotazione(selectedTimeCena.toString(), email_, pasto)

                    //Mi salvo l'orario della prenotazione per altre operazioni in altre funzioni di questa activity
                    oraPrenotazione = Prenotazione_.hour
                    minutoPrenotazione = Prenotazione_.minute

                } else {
                    showToast("Seleziona un orario per la cena prima di prenotare")
                }
            } else {
                showToast("Non puoi prenotare adesso")
            }
        }



        //Osservo se l'inserimento di una prenotazione è avvenuta con successo
        viewModel.prenotazione.observe(this) { result ->
            if (result != null) {
                prenotazioneResult = result

                //Chiama la funzione per notificare la avvenuta prenotazione e il promemoria impostato
                scheduleNotification("$oraPrenotazione:$minutoPrenotazione",oraPrenotazione,minutoPrenotazione)
            }
        }

        //Osservo l'orario delle prenotazione effettuate e aggiorno la Gui
        viewModel.orarioprenotazione.observe(this) { result2 ->
            if (result2 != null) {
                binding.textPrenotazioniOggi.text = result2
                orarioPrenotazioneResult = result2



            }
        }



        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    //funzione chiamata per la selezione dell'orario della prenotazione
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



    //Viene schedulata la notifica per la prenotazione
    private fun scheduleNotification(oraPrenotazione:String,ora:Int,minuto:Int) {
        val intent = Intent(applicationContext, NotificationReceiver::class.java).apply {
            action = "com.example.managermensa.NOTIFICATION"
        }

        val title = "Promemoria prenotazione:"
        val message = "$oraPrenotazione"
        intent.putExtra("titleExtra", title)
        intent.putExtra("messageExtra", message)


        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d("ORARIOOOOOOOOO", orarioPrenotazioneResult.toString())
        val calendar = Calendar.getInstance().apply {

            //Promemoria per prima dell'ora di pranzo
            if(ora <15) {
                set(Calendar.HOUR_OF_DAY, 11)
                set(Calendar.MINUTE, 30)
            }

            //Promemoria per prima dell'ora di cena
            else{
                set(Calendar.HOUR_OF_DAY, 18)
                set(Calendar.MINUTE, 30)
            }
        }

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }



        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            showAlert(calendar.timeInMillis, title)
        } catch (e: SecurityException) {
            showPermissionDeniedDialog("Exact alarm permission is required for this feature.")
        }
    }

    private fun showPermissionDeniedDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage(message)
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }


    //Viene mostrato l'allert per la avvenuta prenotazione e per il promemoria impostato
    private fun showAlert(time: Long, title: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Prenotazione effettuata")
            .setMessage(
                "$title\n${dateFormat.format(date)} ${timeFormat.format(date)}"
            )
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }



    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
