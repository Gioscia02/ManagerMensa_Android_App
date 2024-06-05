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

class PrenotazioniActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrenotazioniBinding
    private val client = OkHttpClient()

    private var selectedTimePranzo: LocalTime? = null
    private var selectedTimeCena: LocalTime? = null

    val viewModel : SharedViewModel by viewModels()

    var pasto: String = ""


    var email_ = ""

    var prenotazioneResult: Boolean? = null
    var orarioPrenotazioneResult: String? = null


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

//        val dbManager = UserDatabaseManager(this)


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


            if (now.isAfter(LocalTime.of(0, 0)) ) {
                if (selectedTimePranzo != null) {

                    //Invio prenotazione
                    viewModel.insertPrenotazione(selectedTimePranzo.toString(), email_, pasto)
                    scheduleNotification(11,30)

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
            if (now.isAfter(LocalTime.of(0, 0)) ) {
                if (selectedTimeCena != null) {

                    //Invio prenotazione
                    viewModel.insertPrenotazione(selectedTimeCena.toString(), email_, pasto)
                    scheduleNotification(18,30)


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
                checkResults()
            }
        }

        //Osservo l'orario della prenotazione effettuata
        viewModel.orarioprenotazione.observe(this) { result2 ->
            if (result2 != null) {
                binding.textPrenotazioniOggi.text = result2
                orarioPrenotazioneResult = result2
                Log.d("ORARIOO", result2.toString())
                checkResults()
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


    //Funzione che verificando che l'invio di prenotazione è andato a buon fine e che ci sia un orario per poi comunicare all'utente il risultato
    fun checkResults() {
//        if (prenotazioneResult != null && orarioPrenotazioneResult != null) {
//            // Entrambi i risultati sono disponibili, esegui il blocco di codice
//            val result = prenotazioneResult
//            val result2 = orarioPrenotazioneResult
//
//            // Pulizia campo
//            binding.selectedTimeTextViewPranzo.text = ""
//            binding.selectedTimeTextViewCena.text = ""
//
//            // Imposta l'orario della prenotazione nella TextView
//            binding.textPrenotazioniOggi.text =
//                "${binding.textPrenotazioniOggi.text}    $result2"
//        }
//        else{
//            showToast("Non è possibile prenotare")
//        }
    }




    private fun scheduleNotification(ora:Int,minuti:Int) {
        val intent = Intent(applicationContext, NotificationReceiver::class.java).apply {
            action = "com.example.managermensa.NOTIFICATION"
        }
        val title = "Notifica per la prenotazione impostata per:"
        intent.putExtra("titleExtra", title)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d("ORARIOOOOOOOOO", orarioPrenotazioneResult.toString())
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, ora)
            set(Calendar.MINUTE, minuti)
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

    private fun parseJsonToModel(jsonString: JsonArray): ArrayList<Utente> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        return gson.fromJson(jsonString, object : TypeToken<ArrayList<Utente>>() {}.type)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
