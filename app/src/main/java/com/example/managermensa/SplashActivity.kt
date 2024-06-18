package com.example.managermensa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.managermensa.R
import com.example.managermensa.activity.MainActivity

class SplashActivity : AppCompatActivity() {
    private val Durata_Splash = 2000 // Durata in millisecondi della schermata

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            // Start MainActivity
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish() // Chiudi la SplashActivity
        }, Durata_Splash.toLong())
    }
}
