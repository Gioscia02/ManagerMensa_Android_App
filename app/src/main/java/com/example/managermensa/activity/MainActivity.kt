package com.example.managermensa.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.managermensa.R
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.databinding.ActivityAccessoBinding
import com.example.managermensa.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //Controllo se l'utente è rimasto loggato nel dispositivo
        GlobalScope.launch(Dispatchers.IO) {

            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "MensaDatabase"
            ).build()
            val userDao = db.userDao()

            //Applico la selezione al MensaDatabase
            val user_ = userDao.SelectUsers()


            if (user_ != null) {

                // L'utente è già loggato, naviga alla HomeActivity
                val intent = Intent(applicationContext, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // L'utente non è loggato, rimani nella MainActivity
            }
        }



        binding.buttonLogin.setOnClickListener{

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonLogin.context,
                R.anim.button_scale
            )
            binding.buttonLogin.startAnimation(scaleAnimation)

            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)



        }

        binding.buttonRegistrazione.setOnClickListener{

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonRegistrazione.context,
                R.anim.button_scale
            )
            binding.buttonRegistrazione.startAnimation(scaleAnimation)

            val intent = Intent(this,RegistrazioneActivity::class.java)
            startActivity(intent)

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}