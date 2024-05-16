package com.example.managermensa

import android.os.Bundle
import android.view.MenuItem

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


import androidx.appcompat.widget.Toolbar


class AccountActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        //Collego la toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar_account) // Sostituisci R.id.toolbar con l'ID effettivo della tua toolbar
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