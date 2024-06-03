package com.example.managermensa.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managermensa.R
import com.example.managermensa.adapter.AvvisiAdapter
import com.example.managermensa.adapter.PastiAdapter
import com.example.managermensa.databinding.ActivityAllergieBinding
import com.example.managermensa.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    val viewModel: SharedViewModel by viewModels()

    private lateinit var adapter: PastiAdapter // Aggiungo l'adapter per la RecyclerView

//    private lateinit var adapter_primi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //prendo il riferimento alla toolbar
        val toolbar = binding.toolbarMenu
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante "<-" viene cliccato
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

        viewModel.getPasti()

        viewModel.pasti.observe(this){pasto->

            if(pasto!=null) {

                adapter = PastiAdapter(pasto)
                binding.recyclerView.layoutManager = LinearLayoutManager(this).apply {

                }
                binding.recyclerView.adapter = adapter
            }

        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}