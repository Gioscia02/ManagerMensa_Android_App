package com.example.managermensa.activity

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.managermensa.SecurePreferencesManager.saveCredentials
import com.example.managermensa.activity.retrofit.Client
import com.example.managermensa.databinding.ActivityPrenotazioniBinding
import com.example.managermensa.databinding.ActivityRegistrazioneBinding
import com.example.managermensa.databinding.ActivitySegnalazioniBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val _itemList = MutableLiveData<ArrayList<Utente>>().apply { value = ArrayList() }
    val itemList: LiveData<ArrayList<Utente>> get() = _itemList

    private val _utenteSelezionato = MutableLiveData<Utente?>().apply { value = null }
    val utenteSelezionato: LiveData<Utente?> get() = _utenteSelezionato

    private val _position = MutableLiveData<Int>().apply { value = -1 }
    val position: LiveData<Int> get() = _position

    private val _success = MutableLiveData<Boolean>().apply { value = false }
    val success: LiveData<Boolean> get() = _success

    init {
        getListaUtenti()
    }

    fun addItem(item: Utente) {
        _itemList.value?.apply {
            add(item)
            _itemList.postValue(this)
        }
    }

    fun removeItem(item: Utente) {
        _itemList.value?.apply {
            remove(item)
            _itemList.postValue(this)
        }
    }

    fun getUtente(context: Context, email: String, password: String) {
        Client.retrofit.findUtente(email, password).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val risposta: JsonObject? = response.body()
                    Log.d("risposta", risposta.toString())

                    if (risposta != null) {
                        val oggi = java.util.Date()
                        val sqlDate = Date(oggi.time)


                        showToast(context, "Accesso effettuato")

                        saveCredentials(context, email, password)


                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        showToast(context, "Accesso negato")
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("getUtente", "Failed to get user", t)
            }
        })
    }

    fun getListaUtenti() {
        Client.retrofit.getUtenti().enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    val risposta: JsonArray? = response.body()
                    Log.d("risposta", risposta.toString())
                    _itemList.value = risposta?.let { parseJsonToModel(it) }
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("getListaUtenti", "Failed to get users list", t)
            }
        })
    }

    fun deleteItem(item: Utente) {
        Log.v("delete", item.toString())
        Client.retrofit.deleteUtente(item.idutente).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _itemList.value?.apply {
                        remove(item)
                        _itemList.postValue(this)
                    }
                    _success.value = true
                } else {
                    _success.value = false
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
            }
        })
    }

    fun updateItem(item: Utente, position: Int) {
        val gson = Gson()
        Log.d("item", item.toString())
        val json = gson.toJsonTree(item).asJsonObject
        Client.retrofit.updateUtente(item.idutente, json).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _itemList.value?.apply {
                        set(position, item)
                        _itemList.postValue(this)
                    }
                    _utenteSelezionato.value = item
                    _success.value = true
                } else {
                    _success.value = false
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
            }
        })
    }

    fun insertItem(context: Context?, nome: String, cognome: String, nascita: String, email: String, password: String) {
        val gson = Gson()
        val string  =
            "{\"nome\": \"$nome\", \"cognome\": \"$cognome\", \"nascita\": \"$nascita\", \"email\": \"$email\", \"password\": \"$password\"}"

        val json = gson.fromJson(string, JsonObject::class.java)
        Client.retrofit.insertUtente(json).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val risposta = JsonArray()
                    risposta.add(response.body())
                    Log.v("risposta", risposta.toString())
                    addItem(parseJsonToModel(risposta)[0])
                    _success.value = true

                    if (context != null) {
                        saveCredentials(context, email, password)
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        Log.e("insertItem", "Context is null, cannot save credentials")
                    }


                } else {
                    _success.value = false
                    Log.e("insertItem", "Error in insertion")
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
            }
        })
    }

    //Invio segnalazione
    fun insertSegnalazione(email: String?, binding: ActivitySegnalazioniBinding, argomento: String, testo: String) {
        val gson = Gson()
        val string  =
            "{\"email\": \"$email\", \"argomento\": \"$argomento\", \"testo\": \"$testo\"}"

        val json = gson.fromJson(string, JsonObject::class.java)
        Client.retrofit.insertSegnalazione(json).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _success.value = true


                    // Pulizia campi
                    binding.editTextSegnalazione.text.clear()
                    binding.argomentiSegnalazione.clearCheck()

                    showToast(binding.root.context, "Segnalazione inviata")
                } else {
                    _success.value = false
                    Log.e("insertSegnalazione", "Error in insertion")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
                Log.e("insertSegnalazione", "Failed to send segnalazione", t)
            }
        })
    }


    // Inserimento della nuova prenotazione
    fun insertPrenotazione(binding: ActivityPrenotazioniBinding, email: String?) {

        val gson = Gson()
        val string  =
            "{\"email\": \"$email\"}"

        val json = gson.fromJson(string, JsonObject::class.java)
        // Invio della richiesta di inserimento della prenotazione tramite Retrofit
        Client.retrofit.insertPrenotazione(json).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _success.value = true


                    showToast(binding.root.context, "Prenotazione effettuata")

                    //Pulizia campo
                    binding.selectedTimeTextViewPranzo.text = ""
                    binding.selectedTimeTextViewCena.text = ""

                    // Ottieni l'orario corrente
                    val currentTime = Calendar.getInstance()
                    val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentTime.time)

                    showToast(binding.root.context, "Prenotazione effettuata")
                    // Imposta l'orario della prenotazione nella TextView

                    binding.textPrenotazioniOggi.text = "${binding.textPrenotazioniOggi.text}    ${formattedTime}"



                } else {
                    _success.value = false
                    Log.e("insertPrenotazione", "Error in insertion")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
                Log.e("insertPrenotazione", "Failed to send prenotazione", t)
            }
        })
    }



//
//    fun conferma(nome: String, cognome: String, nascita: String, email: String, password: String) {
//        val idutente = _utenteSelezionato.value?.idutente ?: 0
//        if (idutente != 0) {
//            updateItem(Utente(nome, cognome, email, Date.valueOf(nascita), idutente), _position.value!!)
//        } else {
//            insertItem(nome, cognome, nascita, email, password)
//        }
//    }

    fun reset() {
        _position.value = -1
        _utenteSelezionato.value = null
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun parseJsonToModel(jsonString: JsonArray): ArrayList<Utente> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        return gson.fromJson(jsonString, object : TypeToken<ArrayList<Utente>>() {}.type)
    }
}
