package com.example.managermensa.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.managermensa.R
import com.example.managermensa.databinding.ActivityRegistrazioneBinding
import java.util.Calendar


class RegistrazioneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrazioneBinding

    val viewModel : SharedViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistrazioneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbarRegistrazione
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        binding.buttonRegister.setOnClickListener {
            if (validateInput()) {
                if (Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmail.text).matches()) {
                    viewModel.insertItem( this,
                        binding.editTextName.text.toString(),
                        binding.editTextSurname.text.toString(),
                        binding.editTextDate.text.toString(),
                        binding.editTextEmail.text.toString(),
                        binding.editTextPassword.text.toString()
                    )

                } else {
                    showToast("Inserisci una email valida")
                }
            } else {
                showToast("Inserisci tutti i campi")
            }
        }

        binding.editTextDate.setOnClickListener {
            showDatePickerDialog(this)
        }

        binding.buttonFotoSelezionata.setOnClickListener {
            openGalleryForImage()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validateInput(): Boolean {
        return binding.editTextName.text.isNotEmpty() &&
                binding.editTextSurname.text.isNotEmpty() &&
                binding.editTextDate.text.isNotEmpty() &&
                binding.editTextEmail.text.isNotEmpty() &&
                binding.editTextPassword.text.isNotEmpty()
    }

    private fun insetItem(nome: String, cognome: String, nascita: String, email: String, password: String) {
        // Codice per inserire l'utente nel backend
    }

    private fun showDatePickerDialog(context: Context) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // data selezionata
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                val formattedDate = DateFormat.format("dd/MM/yyyy", selectedDate)
                Log.d("SelectedDate", formattedDate.toString())
                binding.editTextDate.setText(formattedDate.toString())
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // Imposta il limite massimo della data selezionabile a 10 anni fa rispetto alla data corrente
        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.add(Calendar.YEAR, -10)

        //Imposto la data massima selezionabile
        datePickerDialog.datePicker.maxDate = maxDateCalendar.timeInMillis

        //Mostro la schermata
        datePickerDialog.show()
    }




    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            // Codice per gestire l'immagine selezionata
            binding.imageViewPhoto.setImageURI(selectedImageUri)
        }
    }

    private fun openGalleryForImage() {
        val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }

}