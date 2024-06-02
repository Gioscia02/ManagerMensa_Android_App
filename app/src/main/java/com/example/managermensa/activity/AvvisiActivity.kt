package com.example.managermensa.activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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

    val viewModel : SharedViewModel by viewModels()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAvvisiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getAvvisi(this)

        viewModel.avvisi.observe(this) { result ->

            if (result != null) {
                adapter = AvvisiAdapter(result)
                binding.recyclerView.layoutManager = LinearLayoutManager(this).apply {
                    reverseLayout = true
                    stackFromEnd = true
                }
                binding.recyclerView.adapter = adapter

            }
        }

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





    private fun parseJsonToModel(jsonString: JsonArray): ArrayList<Avviso> {
        val gson = GsonBuilder().setDateFormat("dd-MM-YYYY").create()
        return gson.fromJson(jsonString, object : TypeToken<ArrayList<Avviso>>() {}.type)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}



