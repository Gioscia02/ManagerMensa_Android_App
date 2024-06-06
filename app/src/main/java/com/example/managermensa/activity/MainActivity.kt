package com.example.managermensa.activity

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.example.managermensa.NotificationReceiver
import com.example.managermensa.R
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

const val channelID = "com.example.managermensa.channel"
const val notificationID = 1

class MainActivity : AppCompatActivity() {

    val viewModel: SharedViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            checkExactAlarmPermissionAndSchedule()
        } else {
            showPermissionDeniedDialog("Il permesso di notifiche è necessario")
        }
    }

    private val requestScheduleExactAlarmPermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkExactAlarmPermissionAndSchedule()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                checkExactAlarmPermissionAndSchedule()
            }
        } else {
            checkExactAlarmPermissionAndSchedule()
        }

        // Controllo se l'utente è rimasto loggato nel dispositivo
        GlobalScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "MensaDatabase"
            ).fallbackToDestructiveMigration().build()
            val userDao = db.userDao()

            // Applico la selezione al MensaDatabase
            val user_ = userDao.SelectUsers()

            if (user_ != null) {
                // L'utente è già loggato, naviga alla HomeActivity
                val intent = Intent(applicationContext, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.buttonLogin.setOnClickListener {
            // Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(
                binding.buttonLogin.context,
                R.anim.button_scale
            )
            binding.buttonLogin.startAnimation(scaleAnimation)

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.buttonRegistrazione.setOnClickListener {
            // Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(
                this.binding.buttonRegistrazione.context,
                R.anim.button_scale
            )
            binding.buttonRegistrazione.startAnimation(scaleAnimation)

            val intent = Intent(this, RegistrazioneActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkExactAlarmPermissionAndSchedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                requestScheduleExactAlarmPermission.launch(intent)
            } else {
            }
        } else {
        }
    }




    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notifica prenotazione"
            val descriptionText = "Canale per notifiche per prenotazioni"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showPermissionDeniedDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Permesso rifiutato")
            .setMessage(message)
            .setPositiveButton("ok") { _, _ -> }
            .show()
    }
}
