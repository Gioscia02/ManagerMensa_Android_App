package com.example.managermensa.activity

import android.content.Context
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.managermensa.R
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.databinding.ActivityRicaricasaldoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RicaricasaldoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRicaricasaldoBinding

    val viewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRicaricasaldoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //prendo il riferimento alla toolbar
        val toolbar = binding.toolbarRicaricaSaldo
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante "<-" viene cliccato
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }


        binding.buttonRicaricaImporto.setOnClickListener{

            val scaleAnimation = AnimationUtils.loadAnimation(this.binding.buttonRicaricaImporto.context,R.anim.button_scale)
            binding.buttonRicaricaImporto.startAnimation(scaleAnimation)

            val importoText = binding.editTextImporto.text.toString()

            if (importoText.toDoubleOrNull() != null) {
                //E' un numero

                //Courutine per accedere al DB interno
                lifecycleScope.launch(Dispatchers.IO) {

                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "MensaDatabase"
                    ).build()
                    val userDao = db.userDao()

                    val user = userDao.SelectUsers()

                    val email = user.email

                    viewModel.insertTransazione(email,"Ricarica",importoText.toInt())

                    // Aspetta mezzo secondo
                    lifecycleScope.launch {
                        //Attendi un pò per essere sicuri che la transazione sia avvenuta prima di aggiornare il saldo
                        delay(500)


                        finish()

                    }



                }
                // Mostra il Toast sul thread principale
                showToast(this,"Pagamento riuscito")

            } else {
                //Non è un numero

                showToast(this,"Non è un numero")
            }


        }


        viewModel.gettransazione.observe(this) { result ->

            if (result != null) {
                if (result) {
                    showToast(this, "Transazione riuscita")
                    binding.editTextImporto.setText("")
                } else {
                    showToast(this, "Transazione NON riuscita")
                }
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}