package com.example.managermensa.activity

import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.managermensa.R
import com.example.managermensa.activity.retrofit.Client
import com.example.managermensa.databinding.ActivitySegnalazioniBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SegnalazioniActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySegnalazioniBinding

   val viewModel : SharedViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_segnalazioni)

        binding = ActivitySegnalazioniBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        //Attivazione della Toolbar
        setSupportActionBar(binding.toolbarSegnalazioni)

        //Controlla quando il pulsante back <- viene cliccato
        binding.toolbarSegnalazioni.setNavigationOnClickListener {

            onBackPressed()
        }


        binding.inviaSegnalazione.setOnClickListener {
            var id_scelto = binding.argomentiSegnalazione.checkedRadioButtonId

            //Controllo sull'inserimento dei campi
            if (id_scelto != -1 || binding.editTextSegnalazione.text.isEmpty()) {


                val selectedRadioButton = findViewById<RadioButton>(id_scelto)
                val selectedText = selectedRadioButton.text


                viewModel.insertSegnalazione(binding,selectedText.toString(), binding.editTextSegnalazione.text.toString())

            } else {
                showToast("Compila tutti i campi")
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