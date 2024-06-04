package com.example.managermensa.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.managermensa.R
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.data.Allergia
import com.example.managermensa.databinding.ActivityAllergieBinding
import com.example.managermensa.databinding.ActivityAvvisiBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllergieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllergieBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAllergieBinding.inflate(layoutInflater)
        setContentView(binding.root)




        //prendo il riferimento alla toolbar
        val toolbar = binding.toolbarAllergie
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante "<-" viene cliccato
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }


        lifecycleScope.launch (Dispatchers.IO){
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "MensaDatabase"
            ).build()
            val userDao = db.userDao()

            val allergie = userDao.GetAllergie()

            for (i in allergie){

                if(i.nome == binding.allergiaGlutine.text){
                    binding.allergiaGlutine.isChecked = true
                }
                if(i.nome == binding.allergiaLatte.text){
                    binding.allergiaLatte.isChecked = true
                }
                if(i.nome == binding.allergiaUova.text){
                    binding.allergiaUova.isChecked = true
                }
                if(i.nome == binding.allergiaSoia.text){
                    binding.allergiaSoia.isChecked = true
                }

            }
        }


        binding.buttonSalvaAllergie.setOnClickListener {
            val selectedAllergies = mutableListOf<String>()

            if (binding.allergiaGlutine.isChecked) {

                lifecycleScope.launch (Dispatchers.IO){
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "MensaDatabase"
                    ).build()
                    val userDao = db.userDao()

                    val allergie = userDao.GetAllergie()

                    userDao.InsertAllergia(Allergia(binding.allergiaGlutine.text.toString()))
                }
                selectedAllergies.add("Glutine")
            }

            if (!binding.allergiaGlutine.isChecked) {

                lifecycleScope.launch (Dispatchers.IO){
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "MensaDatabase"
                    ).build()
                    val userDao = db.userDao()

                    userDao.DeleteAllergia(Allergia(binding.allergiaGlutine.text.toString()))

                }
                selectedAllergies.add("Glutine")
            }

            if (binding.allergiaLatte.isChecked) {

                lifecycleScope.launch (Dispatchers.IO){
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "MensaDatabase"
                    ).build()
                    val userDao = db.userDao()
                    userDao.InsertAllergia(Allergia(binding.allergiaLatte.text.toString()))
                }
                selectedAllergies.add("Latte")
            }
            if (!binding.allergiaLatte.isChecked) {

                lifecycleScope.launch (Dispatchers.IO){
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "MensaDatabase"
                    ).build()
                    val userDao = db.userDao()

                    userDao.DeleteAllergia(Allergia(binding.allergiaLatte.text.toString()))

                }
            }
            if (binding.allergiaUova.isChecked) {

                lifecycleScope.launch (Dispatchers.IO){
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "MensaDatabase"
                    ).build()
                    val userDao = db.userDao()
                    userDao.InsertAllergia(Allergia(binding.allergiaUova.text.toString()))
                }
                selectedAllergies.add("Uova")
            }

            if (!binding.allergiaUova.isChecked) {

                lifecycleScope.launch (Dispatchers.IO){
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "MensaDatabase"
                    ).build()
                    val userDao = db.userDao()

                    userDao.DeleteAllergia(Allergia(binding.allergiaUova.text.toString()))

                }
            }


            if (binding.allergiaSoia.isChecked) {

                lifecycleScope.launch (Dispatchers.IO){
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "MensaDatabase"
                    ).build()
                    val userDao = db.userDao()
                    userDao.InsertAllergia(Allergia(binding.allergiaSoia.text.toString()))
                }
                selectedAllergies.add("Soia")
            }

            if (!binding.allergiaSoia.isChecked) {

                lifecycleScope.launch (Dispatchers.IO){
                    val db = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "MensaDatabase"
                    ).build()
                    val userDao = db.userDao()

                    userDao.DeleteAllergia(Allergia(binding.allergiaSoia.text.toString()))

                }
            }

            lifecycleScope.launch (Dispatchers.IO) {
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "MensaDatabase"
                ).build()
                val userDao = db.userDao()
                val allergie = userDao.GetAllergie()

                withContext(Dispatchers.Main) {

                    showToast("Allergie aggiornate")
                }
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}