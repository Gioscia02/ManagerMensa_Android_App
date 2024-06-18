package com.example.managermensa.activity

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.managermensa.R
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.activity.localdatabase.User
import com.example.managermensa.databinding.ActivityModificaCredenzialiBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModificaCredenzialiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModificaCredenzialiBinding
    val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModificaCredenzialiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        val toolbar = binding.toolbarModificaCredenziali
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val editTextNome = binding.editTextNome
        val editTextCognome = binding.editTextCognome
        val editTextEmail = binding.editTextEmail
        val editTextNascita = binding.editTextNascita
        val editTextPassword = binding.editTextPassword
        val buttonSalva = binding.buttonSalva
        val buttonAnnulla = binding.buttonAnnulla

        lifecycleScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "MensaDatabase"
            ).build()
            val userDao = db.userDao()
            val user = userDao.SelectUsers()

            Log.d("LOCALDATABASEUSER", user.toString())

            if (user != null) {
                withContext(Dispatchers.Main) {
                    //Prendo l'utente nel DB esterno
                    viewModel.getUtente(user.email, user.password)

                    //Carico i campi
                    editTextNome.setText(user.nome)
                    editTextCognome.setText(user.cognome)
                    editTextEmail.setText(user.email)
                    editTextNascita.setText(user.nascita)
                    editTextPassword.setText(user.password)
                }
            }
        }

        buttonSalva.setOnClickListener {
            val nome = editTextNome.text.toString()
            val cognome = editTextCognome.text.toString()
            val emailnuova = editTextEmail.text.toString()
            val nascita = editTextNascita.text.toString()
            val password = editTextPassword.text.toString()

            //Controllo i cambiamenti dell'utente richiesto in precedenza
            viewModel.utenteSelezionato.observe(this) { result ->

                //Controllo se è possibile caricare gli aggiornamenti
                if (nome.isNotBlank() && cognome.isNotBlank() && emailnuova.isNotBlank() && nascita.isNotBlank() && password.isNotBlank() && result != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val db = Room.databaseBuilder(
                            applicationContext,
                            AppDatabase::class.java,
                            "MensaDatabase"
                        ).build()
                        val userDao = db.userDao()
                        val user_attuale = userDao.SelectUsers()

                        if (user_attuale != null) {
                            //Definisco l'utente con i nuovi dati
                            val updateUser = User(emailnuova, password, nome, cognome, nascita)

                            //Carico gli aggiornamenti anche nel DB locale
                            val user_aggiornato = userDao.UpdateUser(updateUser)

                            Log.d("USERRRRRR", updateUser.toString())

                            withContext(Dispatchers.Main) {

                                //Carico gli aggiornamenti dell'utente al DB esterno
                                viewModel.updateUtente(
                                    user_attuale.email,
                                    emailnuova,
                                    password,
                                    nome,
                                    cognome,
                                    nascita
                                )

                            }
                        }


                    }
                    Toast.makeText(
                        applicationContext,
                        "Credenziali aggiornate con successo",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()

                } else {
                    Toast.makeText(
                        applicationContext,
                        "Errore nel caricamento dei campi",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            buttonAnnulla.setOnClickListener {
                finish()
            }
        }
    }
}
