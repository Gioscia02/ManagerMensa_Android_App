package com.example.managermensa.activity

import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.managermensa.R
import com.example.managermensa.activity.retrofit.Client
import com.example.managermensa.databinding.ActivitySegnalazioniBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SegnalazioniActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySegnalazioniBinding

    private val itemList = MutableLiveData<ArrayList<Utente>>()
    init {
        itemList.value = ArrayList()
    }

    val utenteSelezionato = MutableLiveData<Utente?>()
    init {
        utenteSelezionato.value = null
    }

    val position = MutableLiveData<Int>()
    init {
        position.value = -1
    }

    val success = MutableLiveData<Boolean>()
    init {
        success.value = false
    }

    fun getItemList(): LiveData<ArrayList<Utente>> {
        return itemList
    }

    fun addItem(item: Utente) {
        itemList.value?.add(item)
    }

    fun removeItem(item: Utente) {
        itemList.value?.remove(item)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_segnalazioni)

        binding = ActivitySegnalazioniBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        //Attivazione della Toolbar
        setSupportActionBar(binding.toolbarSegnalazioni)

        //Controlla quando il pulsante back <- viene cliccato
        binding.toolbarSegnalazioni.setNavigationOnClickListener {

            onBackPressed()
        }


        binding.inviaSegnalazione.setOnClickListener {
            var id_scelto = binding.argomentiSegnalazione.checkedRadioButtonId

            //Controllo sull'inserimento dei campi
            if (id_scelto != -1 || binding.editTextSegnalazione.text.isEmpty()) {


                val selectedRadioButton = findViewById<RadioButton>(id_scelto)
                val selectedText = selectedRadioButton.text


                insetItem(selectedText.toString(), binding.editTextSegnalazione.text.toString())

            } else {
                showToast("Compila tutti i campi")
            }
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    //Invio segnalazione
    fun insetItem(argomento: String, testo: String) {
        val gson = Gson()
        val string  =
            "{\"argomento\": \"$argomento\", \"testo\": \"$testo\"}"

        val json = gson.fromJson(string, JsonObject::class.java)
        Client.retrofit.insertSegnalazione(json).enqueue(
            object : Callback<JsonObject> {
                override fun onResponse(p0: Call<JsonObject>, p1: Response<JsonObject>) {
                    if (p1.isSuccessful) {
                        val risposta = JsonArray()
                        risposta.add(p1.body())
                        Log.v("risposta", risposta.toString())
                        addItem(parseJsonToModel(risposta)[0])
                        success.value = true

                        //Pulizia dei campi dopo invio segnalazione
                        binding.editTextSegnalazione.text.clear()
                        binding.argomentiSegnalazione.clearCheck()

                        showToast("Segnalazione inviata")
                    }
                    else{
                        success.value = false
                        println("errato")
                    }
                }
                override fun onFailure(p0: Call<JsonObject>, p1: Throwable) {
                    success.value = false
                }
            }
        )
    }

    private fun parseJsonToModel(jsonString: JsonArray): ArrayList<Utente> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        return gson.fromJson(jsonString, object : TypeToken<ArrayList<Utente>>() {}.type)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}