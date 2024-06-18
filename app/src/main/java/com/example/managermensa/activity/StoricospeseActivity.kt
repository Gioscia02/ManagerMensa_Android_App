package com.example.managermensa.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.managermensa.R
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.adapter.AvvisiAdapter
import com.example.managermensa.adapter.StoricoAdapter
import com.example.managermensa.databinding.ActivityStoricospeseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StoricospeseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoricospeseBinding

    val viewModel : SharedViewModel by viewModels()


    private lateinit var adapter: StoricoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityStoricospeseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //prendo il riferimento alla toolbar
        val toolbar = binding.toolbarStoricoSpese
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante "<-" viene cliccato
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

        //Courutine per accedere al DB interno
        GlobalScope.launch(Dispatchers.IO) {

            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "MensaDatabase"
            ).build()
            val userDao = db.userDao()

            val users = userDao.SelectUsers()


            //Chiamo getSaldo passando la mail dell'utente
            viewModel.getTransazioni(applicationContext,users.email)

        }

        viewModel.transazioni.observe(this){result->

            if(result!=null) {

                adapter = StoricoAdapter(result)
                binding.recyclerView.layoutManager = LinearLayoutManager(this).apply {
                    reverseLayout = true
                    stackFromEnd = true
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