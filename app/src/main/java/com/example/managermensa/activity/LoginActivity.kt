package com.example.managermensa.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.managermensa.R
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.activity.retrofit.Client
import com.example.managermensa.databinding.ActivityLoginBinding
import com.example.managermensa.databinding.ActivityRegistrazioneBinding
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.Date

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    val viewModel : SharedViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //prendo il riferimento alla toolbar
        val toolbar = binding.toolbarLogin
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante "<-" viene cliccato
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }



        binding.buttonAccedi.setOnClickListener{

            if(binding.editTextEmail.text.isEmpty()||binding.editTextPassword.text.isEmpty()){

                showToast("Inserisci tutti i campi")

            }

            else {


                //Chiamo la funzione getUtente connessa al DB esterno
                viewModel.getUtente(this, binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString())


                //Osservo la risposta di getUtente
                viewModel.utenteSelezionato.observe(this) { utente ->

                    if(utente!=null) {

                        // Utilizzo le coroutine per eseguire l'operazione del database su un thread di background
                        GlobalScope.launch(Dispatchers.IO) {
                            val db = Room.databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                "MensaDatabase"
                            ).build()
                            val userDao = db.userDao()

                            //Controllo se l'utente è già presente nel Database locale
                            val existingUser = userDao.SelectUser(utente.email)
                            if (existingUser == null) {

                                // L'utente non esiste ancora nel database, quindi è sicuro inserirlo
                                userDao.InsertUser(utente)

                            } else {

                                // L'utente con questa email esiste già nel database locale

                            }

                        }


                    }
                    showToast("Accesso effettuato")

                }


                //Caricamento animazione al click del Button
                val scaleAnimation = AnimationUtils.loadAnimation(
                    this.binding.buttonAccedi.context,
                    R.anim.button_scale
                )
                binding.buttonAccedi.startAnimation(scaleAnimation)

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()



            }
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