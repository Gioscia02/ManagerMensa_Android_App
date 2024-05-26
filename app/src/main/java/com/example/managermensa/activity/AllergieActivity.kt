package com.example.managermensa.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.managermensa.R
import com.example.managermensa.databinding.ActivityAllergieBinding
import com.example.managermensa.databinding.ActivityAvvisiBinding

class AllergieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllergieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAllergieBinding.inflate(layoutInflater)
        setContentView(binding.root)




        //prendo il riferimento alla toolbar
        val toolbar = binding.toolbarAllergie
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante "<-" viene cliccato
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}