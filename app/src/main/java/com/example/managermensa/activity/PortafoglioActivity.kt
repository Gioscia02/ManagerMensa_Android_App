package com.example.managermensa.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.managermensa.R
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.databinding.ActivityLoginBinding
import com.example.managermensa.databinding.ActivityPortafoglioBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PortafoglioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPortafoglioBinding
    val viewModel : SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPortafoglioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Associo il viewmodel con quello nell'XML per aggiornare i campi text in automatico
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        enableEdgeToEdge()

        //prendo il riferimento alla toolbar
        val toolbar = binding.toolbarPortafoglio
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante "<-" viene cliccato
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

        //Richiedo i prezzi al DB esterno
        viewModel.getPrezzi(this)




        //Osservo il Saldo del DB esterno
//        viewModel.saldo.observe(this) { result ->
//
//            //Controllo se il result non è nullo prima di fare qualunque azione con esso
//            if (result != null) {
//                binding.textSaldoValore.text = result.toString()
//            }
//        }


        binding.buttonRicaricaSaldo.setOnClickListener{

            //Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonRicaricaSaldo.context,
                R.anim.button_scale
            )
            binding.buttonRicaricaSaldo.startAnimation(scaleAnimation)



            val intent = Intent(this,RicaricasaldoActivity::class.java)
            startActivity(intent)


        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()

        //Courutine per accedere al DB interno e aggiornare il saldo
        lifecycleScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "MensaDatabase"
            ).build()
            val userDao = db.userDao()
            val users = userDao.SelectUsers()

            // Aggiorna il saldo dopo aver inserito la transazione
            viewModel.getSaldo(users.email)
        }
    }


}