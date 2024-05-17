package com.example.managermensa

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.managermensa.databinding.ActivityAccessoBinding
import com.example.managermensa.databinding.ActivityInfomensaBinding
import com.google.android.gms.maps.MapView

class InfomensaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfomensaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityInfomensaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        //Collego la toolbar
        val toolbar = binding.toolbarInfomensa
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante back <- viene cliccato
        toolbar.setNavigationOnClickListener {

            onBackPressed()
        }

        binding.mapView.onCreate(savedInstanceState)

        binding.mapView.getMapAsync { googleMap ->
            // Use the GoogleMap instance here
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }





}