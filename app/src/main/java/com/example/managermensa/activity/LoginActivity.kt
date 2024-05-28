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
import com.example.managermensa.R
import com.example.managermensa.activity.retrofit.Client
import com.example.managermensa.databinding.ActivityLoginBinding
import com.example.managermensa.databinding.ActivityRegistrazioneBinding
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
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

                viewModel.getUtente(this, binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString())




                //Caricamento animazione al click del Button
                val scaleAnimation = AnimationUtils.loadAnimation(
                    this.binding.buttonAccedi.context,
                    R.anim.button_scale
                )
                binding.buttonAccedi.startAnimation(scaleAnimation)


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