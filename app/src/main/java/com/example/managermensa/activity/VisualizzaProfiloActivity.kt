package com.example.managermensa.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.managermensa.R
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.databinding.ActivityVisualizzaProfiloBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VisualizzaProfiloActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisualizzaProfiloBinding

    val viewModel : SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityVisualizzaProfiloBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Associo il viewmodel con quello nell'XML per aggiornare i campi text in automatico
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this


        //prendo il riferimento alla toolbar
        val toolbar = binding.toolbarVisualizzaProfilo
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante "<-" viene cliccato
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }


        val editTextNome = binding.editTextNome
        val editTextCognome = binding.editTextCognome
        val editTextEmail = binding.editTextEmail
        val editTextNascita = binding.editTextNascita
        val editTextPassword = binding.editTextPassword

        lifecycleScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "MensaDatabase"
            ).build()
            val userDao = db.userDao()
            val user = userDao.SelectUsers()






            if (user != null) {
                withContext(Dispatchers.Main) {

//                    //Carico i campi
                    editTextNome.setText(user.nome)
                    editTextCognome.setText(user.cognome)
                    editTextEmail.setText(user.email)
                    editTextNascita.setText(user.nascita)
                    editTextPassword.setText(user.password)
                }
            }
        }





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}