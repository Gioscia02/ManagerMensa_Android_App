package com.example.managermensa.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.managermensa.R
import com.example.managermensa.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()


        //Controllo click sull'AccountButton
        binding.accountButton.setOnClickListener(){

            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.accountButton.context,
                R.anim.button_scale
            )
            binding.accountButton.startAnimation(scaleAnimation)

            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)

        }


        //Controllo click sull'Info mensa Button
        binding.buttonInfoMensa.setOnClickListener(){

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonInfoMensa.context,
                R.anim.button_scale
            )
            binding.buttonInfoMensa.startAnimation(scaleAnimation)

            //Cambio Activity
            val intent = Intent(this, InfomensaActivity::class.java)
            startActivity(intent)
        }


        //Controllo click sull Segnalazioni Button
        binding.buttonSegnalazioni.setOnClickListener(){

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonSegnalazioni.context,
                R.anim.button_scale
            )
            binding.buttonSegnalazioni.startAnimation(scaleAnimation)

            //Cambio Activity
            val intent = Intent(this, SegnalazioniActivity::class.java)
            startActivity(intent)
        }


        //Controllo click sull'Avvisi Button
        binding.buttonAvvisi.setOnClickListener(){

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonAvvisi.context,
                R.anim.button_scale
            )
            binding.buttonAvvisi.startAnimation(scaleAnimation)

            //Cambio Activity
            val intent = Intent(this, AvvisiActivity::class.java)
            startActivity(intent)
        }


        //Controllo click sull'ultimo Avviso
        binding.avvisiTopbar.setOnClickListener(){

            //Caricamento animazione al click del Button
//            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonAvvisi.context,
//                R.anim.button_scale
//            )
//            binding.buttonAvvisi.startAnimation(scaleAnimation)

            //Cambio Activity
            val intent = Intent(this, AvvisiActivity::class.java)
            startActivity(intent)
        }


        //Controllo click sul Button prenotazioni
        binding.buttonPrenotazioni.setOnClickListener(){

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonPrenotazioni.context,
                R.anim.button_scale
            )
            binding.buttonPrenotazioni.startAnimation(scaleAnimation)

            //Cambio Activity
            val intent = Intent(this, PrenotazioniActivity::class.java)
            startActivity(intent)
        }




        }


//            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//                insets
//            }
        }


