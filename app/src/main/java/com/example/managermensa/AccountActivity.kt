package com.example.managermensa

import android.os.Bundle
import android.view.MenuItem

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


import androidx.appcompat.widget.Toolbar
import com.example.managermensa.databinding.ActivityAccountBinding
import com.example.managermensa.databinding.ActivityHomeBinding


class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {


        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Collego la toolbar
        val toolbar = binding.toolbarAccount // Sostituisci R.id.toolbar con l'ID effettivo della tua toolbar
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante back <- viene cliccato
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