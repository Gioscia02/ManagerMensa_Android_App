package com.example.managermensa.activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managermensa.adapter.AvvisiAdapter
import com.example.managermensa.data.Avviso
import com.example.managermensa.databinding.ActivityAvvisiBinding
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import java.time.LocalDate


class AvvisiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAvvisiBinding
    private lateinit var adapter: AvvisiAdapter // Aggiungo l'adapter per la RecyclerView
    val today = LocalDate.now() // Data odierna



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
        enableEdgeToEdge()
        binding = ActivityAvvisiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inizializza l'adapter e imposta la RecyclerView
        adapter = AvvisiAdapter(getSampleAvvisi()) // Implementa la funzione getSampleAvvisi() per ottenere i dati degli avvisi
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        //prendo il riferimento alla toolbar
        val toolbar = binding.toolbarAvvisi
        setSupportActionBar(toolbar)

        //Controlla quando il pulsante "<-" viene cliccato
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

        // Applica il padding per i system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Esempio di dati di avvisi di prova
    private fun getSampleAvvisi(): List<Avviso> {
        return listOf(
            Avviso("Titolo Avviso 1",today, "Descrizione Avviso 1"),
            Avviso("Titolo Avviso 2",today, "Descrizione Avviso 2"),
            Avviso("Titolo Avviso 3",today, "Descrizione Avviso 3")
            // Aggiungi altri avvisi se necessario
        )
    }



    private fun parseJsonToModel(jsonString: JsonArray): ArrayList<Avviso> {
        val gson = GsonBuilder().setDateFormat("dd-MM-YYYY").create()
        return gson.fromJson(jsonString, object : TypeToken<ArrayList<Avviso>>() {}.type)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}



