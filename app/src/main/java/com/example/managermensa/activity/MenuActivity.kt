package com.example.managermensa.activity

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        viewModel.getPastiPrimi("Lunedi")
        viewModel.getPastiSecondi("Lunedi")
        viewModel.getPastiContorni("Lunedi")

        // Imposta il colore del testo del pulsante
        binding.buttonLunedi.setTextColor(ContextCompat.getColor(this, R.color.green))

        viewModel.pastiprimi.observe(this){pasto->

            if(pasto!=null) {

                adapter = PastiAdapter(pasto)
                binding.recyclerViewPrimi.layoutManager = LinearLayoutManager(this).apply {

                }
                binding.recyclerViewPrimi.adapter = adapter
            }

        }

        viewModel.pastisecondi.observe(this){pasto->

            if(pasto!=null) {

                adapter = PastiAdapter(pasto)
                binding.recyclerViewSecondi.layoutManager = LinearLayoutManager(this).apply {

                }
                binding.recyclerViewSecondi.adapter = adapter
            }

        }

        viewModel.pasticontorni.observe(this){pasto->

            if(pasto!=null) {

                adapter = PastiAdapter(pasto)
                binding.recyclerViewContorni.layoutManager = LinearLayoutManager(this).apply {

                }
                binding.recyclerViewContorni.adapter = adapter
            }

        }


        binding.buttonLunedi.setOnClickListener{

            viewModel.getPastiPrimi("Lunedi")
            viewModel.getPastiSecondi("Lunedi")
            viewModel.getPastiContorni("Lunedi")

            // Imposta il colore del testo del pulsante
            binding.buttonLunedi.setTextColor(ContextCompat.getColor(this, R.color.green))

            binding.buttonMartedi.setTextColor(Color.BLACK)
            binding.buttonMercoledi.setTextColor(Color.BLACK)
            binding.buttonGiovedi.setTextColor(Color.BLACK)
            binding.buttonVenerdi.setTextColor(Color.BLACK)
            binding.buttonSabato.setTextColor(Color.BLACK)
            binding.buttonDomenica.setTextColor(Color.BLACK)


        }



        binding.buttonMartedi.setOnClickListener{


            viewModel.getPastiPrimi("Martedi")
            viewModel.getPastiSecondi("Martedi")
            viewModel.getPastiContorni("Martedi")

            // Imposta il colore del testo del pulsante
            binding.buttonMartedi.setTextColor(ContextCompat.getColor(this, R.color.green))

            binding.buttonLunedi.setTextColor(Color.BLACK)
            binding.buttonMercoledi.setTextColor(Color.BLACK)
            binding.buttonGiovedi.setTextColor(Color.BLACK)
            binding.buttonVenerdi.setTextColor(Color.BLACK)
            binding.buttonSabato.setTextColor(Color.BLACK)
            binding.buttonDomenica.setTextColor(Color.BLACK)

        }

        binding.buttonMercoledi.setOnClickListener{


            viewModel.getPastiPrimi("Mercoledi")
            viewModel.getPastiSecondi("Mercoledi")
            viewModel.getPastiContorni("Mercoledi")
            // Imposta il colore del testo del pulsante
            binding.buttonMercoledi.setTextColor(ContextCompat.getColor(this, R.color.green))

            binding.buttonLunedi.setTextColor(Color.BLACK)
            binding.buttonMartedi.setTextColor(Color.BLACK)
            binding.buttonGiovedi.setTextColor(Color.BLACK)
            binding.buttonVenerdi.setTextColor(Color.BLACK)
            binding.buttonSabato.setTextColor(Color.BLACK)
            binding.buttonDomenica.setTextColor(Color.BLACK)
        }
        binding.buttonGiovedi.setOnClickListener{


            viewModel.getPastiPrimi("Giovedi")
            viewModel.getPastiSecondi("Giovedi")
            viewModel.getPastiContorni("Giovedi")
            // Imposta il colore del testo del pulsante
            binding.buttonGiovedi.setTextColor(ContextCompat.getColor(this, R.color.green))

            binding.buttonLunedi.setTextColor(Color.BLACK)
            binding.buttonMartedi.setTextColor(Color.BLACK)
            binding.buttonMercoledi.setTextColor(Color.BLACK)
            binding.buttonVenerdi.setTextColor(Color.BLACK)
            binding.buttonSabato.setTextColor(Color.BLACK)
            binding.buttonDomenica.setTextColor(Color.BLACK)

        }
        binding.buttonVenerdi.setOnClickListener{


            viewModel.getPastiPrimi("Venerdi")
            viewModel.getPastiSecondi("Venerdi")
            viewModel.getPastiContorni("Venerdi")
            // Imposta il colore del testo del pulsante
            binding.buttonVenerdi.setTextColor(ContextCompat.getColor(this, R.color.green))

            binding.buttonLunedi.setTextColor(Color.BLACK)
            binding.buttonMartedi.setTextColor(Color.BLACK)
            binding.buttonMercoledi.setTextColor(Color.BLACK)
            binding.buttonGiovedi.setTextColor(Color.BLACK)
            binding.buttonSabato.setTextColor(Color.BLACK)
            binding.buttonDomenica.setTextColor(Color.BLACK)

        }
        binding.buttonSabato.setOnClickListener{


            viewModel.getPastiPrimi("Sabato")
            viewModel.getPastiSecondi("Sabato")
            viewModel.getPastiContorni("Sabato")

            // Imposta il colore del testo del pulsante
            binding.buttonSabato.setTextColor(ContextCompat.getColor(this, R.color.green))

            binding.buttonLunedi.setTextColor(Color.BLACK)
            binding.buttonMartedi.setTextColor(Color.BLACK)
            binding.buttonMercoledi.setTextColor(Color.BLACK)
            binding.buttonGiovedi.setTextColor(Color.BLACK)
            binding.buttonVenerdi.setTextColor(Color.BLACK)
            binding.buttonDomenica.setTextColor(Color.BLACK)

        }
        binding.buttonDomenica.setOnClickListener{


            viewModel.getPastiPrimi("Domenica")
            viewModel.getPastiSecondi("Domenica")
            viewModel.getPastiContorni("Domenica")

            // Imposta il colore del testo del pulsante
            binding.buttonDomenica.setTextColor(ContextCompat.getColor(this, R.color.green))

            binding.buttonLunedi.setTextColor(Color.BLACK)
            binding.buttonMartedi.setTextColor(Color.BLACK)
            binding.buttonMercoledi.setTextColor(Color.BLACK)
            binding.buttonGiovedi.setTextColor(Color.BLACK)
            binding.buttonVenerdi.setTextColor(Color.BLACK)
            binding.buttonSabato.setTextColor(Color.BLACK)

        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}