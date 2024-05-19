package com.example.managermensa.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.managermensa.R
import com.example.managermensa.databinding.ActivitySegnalazioniBinding

class SegnalazioniActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySegnalazioniBinding


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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}