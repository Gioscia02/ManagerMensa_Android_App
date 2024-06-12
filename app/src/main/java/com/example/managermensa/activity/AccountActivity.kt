package com.example.managermensa.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room


import com.example.managermensa.R
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.databinding.ActivityAccountBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {


        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //prendo il riferimento alla toolbar
        val toolbar = binding.toolbarAccount
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante "<-" viene cliccato
        toolbar.setNavigationOnClickListener {

        onBackPressed()

        }




        //Button del portafoglio
        binding.buttonPortafoglio.setOnClickListener{

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonPortafoglio.context,
                R.anim.button_scale
            )
            binding.buttonPortafoglio.startAnimation(scaleAnimation)

            //Cambio Activity
            val intent = Intent(this, PortafoglioActivity::class.java)
            startActivity(intent)

        }

        //Button di modifica credenziali
        binding.buttonModificaCredenziali.setOnClickListener{

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonModificaCredenziali.context,
                R.anim.button_scale
            )
            binding.buttonModificaCredenziali.startAnimation(scaleAnimation)

            //Cambio Activity
            val intent = Intent(this, ModificaCredenzialiActivity::class.java)
            startActivity(intent)

        }

        binding.buttonStoricoSpese.setOnClickListener{


            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonStoricoSpese.context,
                R.anim.button_scale
            )
            binding.buttonStoricoSpese.startAnimation(scaleAnimation)


            val intent = Intent(this,StoricospeseActivity::class.java)
            startActivity(intent)

        }


        //button di Logout
        binding.buttonLogout.setOnClickListener{


            //Cancella le credenziali
            SecurePreferencesManager.clearUser(this)

            GlobalScope.launch(Dispatchers.IO) {

                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "MensaDatabase"
                ).build()
                val userDao = db.userDao()

                val users = userDao.SelectUsers()

                //Applico la selezione al MensaDatabase
                val user_ = userDao.DeleteUser(users)

                val allergie = userDao.DeleteAllergie()

            }

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonLogout.context,
                R.anim.button_scale
            )
            binding.buttonLogout.startAnimation(scaleAnimation)

            showToast("Logout effettuato")

            //Torna alla schermata di accesso
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)



        }

        binding.buttonVisualizzaProfilo.setOnClickListener{

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonVisualizzaProfilo.context,
                R.anim.button_scale
            )
            binding.buttonVisualizzaProfilo.startAnimation(scaleAnimation)


            //Torna alla schermata di accesso
            val intent = Intent(this, VisualizzaProfiloActivity::class.java)

            startActivity(intent)




        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }





    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}