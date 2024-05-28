package com.example.managermensa.activity

import SecurePreferencesManager
import UserDatabaseManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import java.text.SimpleDateFormat
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
                        val nome_ = risposta.get("nome")?.asString ?: ""
                        val cognome_ = risposta.get("cognome")?.asString ?: ""
                        val email_ = risposta.get("email")?.asString ?: ""
                        val nascita_ = risposta.get("nascita")?.asString ?: ""
                        val password_ = risposta.get("password")?.asString ?: ""


                            val user = Utente(nome_, cognome_, email_, password_, nascita_)
                            val dbManager = UserDatabaseManager(context)

                            // Inserire un nuovo utente nel DB locale
                            val isInserted = dbManager.insertUser(user)
                            if (isInserted) {
                                Log.d("DB", "User inserted successfully")
                            } else {
                                Log.e("DB", "Error inserting user")
                            }


                            showToast(context, "Accesso effettuato")
                            SecurePreferencesManager.saveUser(context, user)


                        // Verifica se l'activity corrente è l'activity di login
                        if (context is LoginActivity) {
                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                            (context as LoginActivity).finish()
                        }



                    } else {
                        showToast(context, "Accesso negato: risposta nulla")
                        Log.e("getUtente", "Risposta nulla dal server")
                    }
                } else {
                    showToast(context, "Accesso negato: risposta non valida")
                    Log.e("getUtente", "Risposta non valida dal server")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("getUtente", "Failed to get user", t)
                showToast(context, "Accesso negato: errore di connessione")
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

    fun updateUtente(emailattuale: String?,emailnuova: String?,password: String?, nome: String?, cognome: String?,nascita: String? ) {
        val gson = Gson()
        val string  =
            "{\"emailattuale\": \"$emailattuale\", \"emailnuova\": \"$emailnuova\", \"password\": \"$password\", \"nome\": \"$nome\", \"cognome\": \"$cognome\", \"nascita\": \"$nascita\"}"

        val json = gson.fromJson(string, JsonObject::class.java)
        Client.retrofit.updateUtente(json).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _success.value = true
                } else {
                    _success.value = false
                    Log.e("updateUtente", "Errore aggiornamento Utente")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
                Log.e("updateUtente", "Errore aggiornamento Utente", t)
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
                        val dbManager = UserDatabaseManager(context)

                        // Inserire un nuovo utente nel DB locale
                        val user = Utente(nome, cognome, email, password, nascita)
                        val isInserted = dbManager.insertUser(user)
                        if (isInserted) {
                            Log.d("DB", "User inserted successfully")
                        } else {
                            Log.e("DB", "Error inserting user")
                        }

                        // Recuperare un utente per email
                        val retrievedUser = dbManager.getUserByEmail(email)
                        if (retrievedUser != null) {
                            Log.d("DB", "User found: $retrievedUser")
                        } else {
                            Log.e("DB", "User not found")
                        }

                        // Memorizza l'account loggato localmente
                        SecurePreferencesManager.saveUser(context, user)
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
                Log.e("insertItem", "Failed to insert item", t)
            }
        })
    }

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

    fun insertPrenotazione(context: Context, binding: ActivityPrenotazioniBinding,orario:String, email: String?) {
        val gson = Gson()
        val string = "{\"email\": \"$email\"}"
        val json = gson.fromJson(string, JsonObject::class.java)

        // Invio della richiesta di inserimento della prenotazione tramite Retrofit
        Client.retrofit.insertPrenotazione(json).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _success.value = true

                    if (context != null) {

                        val dbManager = UserDatabaseManager(context)

                        // Ottieni la data e l'orario corrente
                        val currentTime = Calendar.getInstance()
                        val formattedDate = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(currentTime.time)
//                        val formattedTime =
//                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentTime.time)
                        val dateTime = formattedDate

                        Log.d("gtrggrgg", orario)
                        // Inserisci la prenotazione nel database locale
                        val isInserted = dbManager.insertPrenotazione(email, orario, dateTime.toString())
                        if (isInserted) {
                            Log.d("DB", "Prenotazione inserita correttamente")
                        } else {
                            Log.e("DB", "Errore nell'inserimento della prenotazione")
                        }

                        showToast(binding.root.context, "Prenotazione effettuata")

                        // Pulizia campo
                        binding.selectedTimeTextViewPranzo.text = ""
                        binding.selectedTimeTextViewCena.text = ""

                        // Imposta l'orario della prenotazione nella TextView
                        binding.textPrenotazioniOggi.text =
                            "${binding.textPrenotazioniOggi.text}    $orario"
                    } else {
                        _success.value = false
                        Log.e("insertPrenotazione", "Error in insertion")
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
                Log.e("insertPrenotazione", "Failed to send prenotazione", t)
            }
        })
    }


    fun reset() {
        _position.value = -1
        _utenteSelezionato.value = null
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun parseJsonToModel(jsonArray: JsonArray): ArrayList<Utente> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        return gson.fromJson(jsonArray, object : TypeToken<ArrayList<Utente>>() {}.type)
    }
}
