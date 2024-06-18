package com.example.managermensa.activity

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
import com.example.managermensa.databinding.ActivityAcquistapastoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AcquistapastoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAcquistapastoBinding

    val viewModel: SharedViewModel by viewModels()


    var email_globale: String = ""

    var transazioni_counter: Int = 0

    // Ottiene la data di oggi
    val oggi = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAcquistapastoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Prendo il riferimento alla toolbar
        val toolbar = binding.toolbarAcquistaPasto
        setSupportActionBar(toolbar)

        // Controlla quando il pulsante "<-" viene cliccato
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        //Associo il viewmodel con quello nell'XML per aggiornare i campi text in automatico
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this


        viewModel.getTransazioni(this, email_globale)

        lifecycleScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "MensaDatabase"
            ).build()
            val userDao = db.userDao()
            val user = userDao.SelectUsers()


            val email = user.email


            viewModel.getSaldo(email)
            email_globale = email


            if (user != null) {
                withContext(Dispatchers.Main) {

                }
            }
        }

        // Richiedo i prezzi al DB esterno
        viewModel.getPrezzi(this)

    
        // Button acquista Pranzo completo
        binding.costoPranzoContainer.setOnClickListener {
            // Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(
                this.binding.costoPranzoContainer.context,
                R.anim.button_scale
            )
            binding.costoPranzoContainer.startAnimation(scaleAnimation)
            viewModel.getTransazioni(this, email_globale)

            //Controllo se si possono fare operazioni con il costo senza causare errori
            //quindi se si è riusciti a prendere il costo dal DB esterno
            if(binding.textCostoPranzoValore.text.isNotEmpty()){

            //Controllo se la transazione può essere eseguita
            if (controlloPossibilitaTransazione(
                    binding.textCostoPranzoValore.text.toString().toFloat()
                ) == true
            ) {
                // Esecuzione della transazione del pagamento
                viewModel.insertTransazione(
                    email_globale,
                    "Pagamento",
                    -binding.textCostoPranzoValore.text.toString().toFloat().toInt()
                )
                // Aspetta mezzo secondo
                lifecycleScope.launch {
                    //Attendi un pò per essere sicuri che la transazione sia avvenuta prima di aggiornare il saldo
                    delay(500)
                    // Aggiorna il saldo dopo aver inserito la transazione
                    viewModel.getSaldo(email_globale)

                    showToast("Pagamento riuscito")
                }
            } else {
                showToast("Pagamento NON riuscito")

            }
        }else{

                showToast("Errore nel collegamento con il server")

        }
}

        // Button acquista Pranzo completo
        binding.costoCenaContainer.setOnClickListener {
            // Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(
                this.binding.costoCenaContainer.context,
                R.anim.button_scale
            )
            binding.costoCenaContainer.startAnimation(scaleAnimation)

            //Controllo se si possono fare operazioni con il costo senza causare errori
            //quindi se si è riusciti a prendere il costo dal DB esterno
            if (binding.textCostoCenaValore.text.isNotEmpty()) {

                //Controllo se la transazione può essere eseguita
                if (controlloPossibilitaTransazione(
                        binding.textCostoCenaValore.text.toString().toFloat()
                    )
                ) {
                    // Esecuzione della transazione del pagamento
                    viewModel.insertTransazione(
                        email_globale,
                        "Pagamento",
                        -binding.textCostoCenaValore.text.toString().toFloat().toInt()
                    )
                    // Aspetta mezzo secondo
                    lifecycleScope.launch {
                        //Attendi un pò per essere sicuri che la transazione sia avvenuta prima di aggiornare il saldo
                        delay(500)
                        // Aggiorna il saldo dopo aver inserito la transazione
                        viewModel.getSaldo(email_globale)

                        // Mostra il Toast sul thread principale
                        showToast("Pagamento riuscito")
                    }
                } else {
                    showToast("Pagamento NON riuscito")
                }
            }else{

                showToast("Errore nel collegamento con il server")

            }
        }

        // Button acquista Pranzo completo
        binding.costoPrimoContainer.setOnClickListener {
            // Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(
                this.binding.costoPrimoContainer.context,
                R.anim.button_scale
            )
            binding.costoPrimoContainer.startAnimation(scaleAnimation)

            //Controllo se si possono fare operazioni con il costo senza causare errori
            //quindi se si è riusciti a prendere il costo dal DB esterno
            if (binding.textCostoPrimoValore.text.isNotEmpty()) {

                //Controllo se la transazione può essere eseguita
                if (controlloPossibilitaTransazione(
                        binding.textCostoPrimoValore.text.toString().toFloat()
                    )
                ) {

                    // Esecuzione della transazione del pagamento
                    viewModel.insertTransazione(
                        email_globale,
                        "Pagamento",
                        -binding.textCostoPrimoValore.text.toString().toFloat().toInt()
                    )
                    // Aspetta mezzo secondo
                    lifecycleScope.launch {
                        //Attendi un pò per essere sicuri che la transazione sia avvenuta prima di aggiornare il saldo
                        delay(500)
                        // Aggiorna il saldo dopo aver inserito la transazione
                        viewModel.getSaldo(email_globale)

                        // Mostra il Toast sul thread principale
                        showToast("Pagamento riuscito")
                    }
                } else {
                    showToast("Pagamento NON riuscito")
                }
            }else{

                showToast("Errore nel collegamento con il server")

            }
        }

        // Button acquista Pranzo completo
        binding.costoSecondoContainer.setOnClickListener {
            // Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(
                this.binding.costoSecondoContainer.context,
                R.anim.button_scale
            )
            binding.costoSecondoContainer.startAnimation(scaleAnimation)


            //Controllo se si possono fare operazioni con il costo senza causare errori
            //quindi se si è riusciti a prendere il costo dal DB esterno
            if (binding.textCostoSecondoValore.text.isNotEmpty()) {

                //Controllo se la transazione può essere eseguita
                if (controlloPossibilitaTransazione(
                        binding.textCostoSecondoValore.text.toString().toFloat()
                    )
                ) {

                    // Esecuzione della transazione del pagamento
                    viewModel.insertTransazione(
                        email_globale,
                        "Pagamento",
                        -binding.textCostoSecondoValore.text.toString().toFloat().toInt()
                    )
                    // Aspetta mezzo secondo
                    lifecycleScope.launch {
                        //Attendi un pò per essere sicuri che la transazione sia avvenuta prima di aggiornare il saldo
                        delay(500)
                        // Aggiorna il saldo dopo aver inserito la transazione
                        viewModel.getSaldo(email_globale)

                        // Mostra il Toast sul thread principale
                        showToast("Pagamento riuscito")
                    }
                } else {
                    showToast("Pagamento NON riuscito")
                }
            }else{

                showToast("Errore nel collegamento con il server")

            }
        }

        // Button acquista Pranzo completo
        binding.costoContornoContainer.setOnClickListener {
            // Caricamento animazione al click del Button
            val scaleAnimation = AnimationUtils.loadAnimation(
                this.binding.costoContornoContainer.context,
                R.anim.button_scale
            )
            binding.costoContornoContainer.startAnimation(scaleAnimation)

            //Controllo se si possono fare operazioni con il costo senza causare errori
            //quindi se si è riusciti a prendere il costo dal DB esterno
            if (binding.textCostoContornoValore.text.isNotEmpty()) {

                //Controllo se la transazione può essere eseguita
                if (controlloPossibilitaTransazione(
                        binding.textCostoContornoValore.text.toString().toFloat()
                    )
                ) {

                    // Esecuzione della transazione del pagamento
                    viewModel.insertTransazione(
                        email_globale,
                        "Pagamento",
                        -binding.textCostoContornoValore.text.toString().toFloat().toInt()
                    )
                    // Aspetta mezzo secondo
                    lifecycleScope.launch {
                        //Attendi un pò per essere sicuri che la transazione sia avvenuta prima di aggiornare il saldo
                        delay(500)
                        // Aggiorna il saldo dopo aver inserito la transazione
                        viewModel.getSaldo(email_globale)

                        // Mostra il Toast sul thread principale
                        showToast("Pagamento riuscito")
                    }
                } else {
                    showToast("Pagamento NON riuscito")
                }
            }else{

                showToast("Errore nel collegamento con il server")

            }
        }

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
    }

    //Controllo se il saldo disponibile è abbastanza per acquistare la scelta selezionata
    private fun controlloPossibilitaTransazione(pagamento:Float): Boolean{


        if(binding.textSaldoValore.text.toString().toFloat() >= pagamento && transazioni_counter < 2 ) {

            return true
        }
        else{

            return false
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
