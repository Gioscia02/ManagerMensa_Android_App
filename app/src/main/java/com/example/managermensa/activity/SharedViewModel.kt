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
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.activity.localdatabase.Prezzi
import com.example.managermensa.activity.localdatabase.User
import com.example.managermensa.activity.retrofit.Client
import com.example.managermensa.databinding.ActivityPortafoglioBinding
import com.example.managermensa.databinding.ActivityPrenotazioniBinding
import com.example.managermensa.databinding.ActivityRegistrazioneBinding
import com.example.managermensa.databinding.ActivitySegnalazioniBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val _prezzi = MutableLiveData<Prezzi?>().apply { value = null }
    val prezzi: LiveData<Prezzi?> get() = _prezzi

    private val _saldo = MutableLiveData<Float?>().apply { value = null }
    val saldo: LiveData<Float?> get() = _saldo

    private val _itemList = MutableLiveData<ArrayList<Utente>>().apply { value = ArrayList() }
    val itemList: LiveData<ArrayList<Utente>> get() = _itemList

    private val _utenteSelezionato = MutableLiveData<User?>().apply { value = null }
    val utenteSelezionato: LiveData<User?> get() = _utenteSelezionato

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

                        //Raccolgo i dati della risposta json
                        val nome_ = risposta.get("nome")?.asString ?: ""
                        val cognome_ = risposta.get("cognome")?.asString ?: ""
                        val email_ = risposta.get("email")?.asString ?: ""
                        val nascita_ = risposta.get("nascita")?.asString ?: ""
                        val password_ = risposta.get("password")?.asString ?: ""


                        //Prendo l'utente usando i dati
                        val user_ = User(email_,password_,nome_, cognome_, nascita_)

                        //Passo l'utente al LiveData
                        _utenteSelezionato.postValue(user_)

                        showToast(context, "Accesso effettuato")

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


    fun getPrezzi(binding: ActivityPortafoglioBinding,context : Context) {
        Client.retrofit.getPrezzi().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val risposta: JsonObject? = response.body()
                    Log.d("risposta", risposta.toString())

                    if(risposta != null){


                        val prezzo_pranzo_completo = risposta.get("prezzo_pranzo_completo")?.asFloat


                        val prezzo_cena_completa = risposta.get("prezzo_cena_completa")?.asFloat
//                        val prezzo_cena_completaInt = prezzo_cena_completaString?.toIntOrNull()
                        val prezzo_primo = risposta.get("prezzo_primo")?.asFloat
//                        val prezzo_primoInt = prezzo_primoString?.toIntOrNull()
                        val prezzo_secondo = risposta.get("prezzo_secondo")?.asFloat
//                        val prezzo_secondoInt = prezzo_secondoString?.toIntOrNull()
                        val prezzo_contorno = risposta.get("prezzo_contorno")?.asFloat
//                        val prezzo_contornoInt = prezzo_contornoString?.toIntOrNull()

                        val prezzo = Prezzi( prezzo_pranzo_completo= prezzo_pranzo_completo?.toInt(), prezzo_primo = prezzo_primo?.toInt(), prezzo_secondo = prezzo_secondo?.toInt(), prezzo_contorno = prezzo_contorno?.toInt(), prezzo_cena_completa = prezzo_cena_completa?.toInt(), id = 1)
//
                        Log.d("PREZOOOOOO", prezzo_primo.toString())
                        _prezzi.postValue(prezzo)


//                        // Utilizzo le coroutine per eseguire l'operazione del database su un thread di background
//                        viewModelScope.launch(Dispatchers.IO) {
//
//                            val db = Room.databaseBuilder(
//                                context,
//                                AppDatabase::class.java,
//                                "MensaDatabase"
//                            ).build()
//                            val prezziDao = db.userDao()
//                            val prezzi_db_remoto = Prezzi( 1,
//                                risposta?.get("prezzo_pranzo_completo")?.asString?.toIntOrNull(),
//                                 risposta?.get("prezzo_cena_completa")?.asString?.toIntOrNull(),
//                                risposta?.get("prezzo_primo")?.asString?.toIntOrNull(),
//                                risposta?.get("prezzo_secondo")?.asString?.toIntOrNull(),
//                                risposta?.get("prezzo_contorno")?.asString?.toIntOrNull() )
//
//
////                            if(prezziDao.Count()==0 || prezziDao.Uguali(prezzo_pranzo_completo,prezzo_cena_completa,prezzo_primo,prezzo_secondo,prezzo_contorno)== null) {
////
////                                Log.d("entrato", "SIIIIIIIIIIIIIIIIIIIII ")
////                                prezziDao.deleteAllPrezzi()
////                                prezziDao.InsertPrezzi(prezzi_db_remoto)
////
////                            }
//                            //Controllo se l'utente è già presente nel Database locale
//                            val prezzi = prezziDao.GetPrezzi()
//                            withContext(Dispatchers.Main) {
//                                // Torna al thread principale per aggiornare le viste UI
//
//                                if(prezzi != null) {
//                                    Log.d("entrat22222222", prezzi.toString())
//                                    binding.textCostoPranzoValore.text = prezzi.prezzo_pranzo_completo.toString()
//                                    binding.textCostoCenaValore.text = prezzi.prezzo_cena_completa.toString()
//                                    binding.textPrezzoPrimoValore.text = prezzi.prezzo_primo.toString()
//                                    binding.textPrezzoSecondoValore.text = prezzi.prezzo_secondo.toString()
//                                    binding.textPrezzoContornoValore.text = prezzi.prezzo_contorno.toString()
//                                }
//                            }
//                        }



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





    fun getPrenotazioni(context: Context, binding: ActivityPrenotazioniBinding) {
        Client.retrofit.getPrenotazioni().enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                val oggi = LocalDate.now()
                if (response.isSuccessful) {
                    val prenotazioni: JsonArray? = response.body()
                    if (prenotazioni != null) {
                        val orariPrenotazioni = mutableListOf<String>()

                        // Avvia una coroutine sul contesto IO
                        GlobalScope.launch(Dispatchers.IO) {
                            // Ottieni il database e il DAO
                            val db = Room.databaseBuilder(
                                context,
                                AppDatabase::class.java,
                                "MensaDatabase"
                            ).build()
                            val userDao = db.userDao()

                            // Seleziona l'utente dal database
                            val user_ = userDao.SelectUsers()
                            val email_ = user_.email

                            // Itera su ogni elemento della JsonArray
                            for (i in 0 until prenotazioni.size()) {
                                val prenotazione = prenotazioni[i].asJsonObject
                                val orario = prenotazione.get("orario")?.asString
                                val giorno = prenotazione.get("giorno")?.asString
                                val email = prenotazione.get("email")?.asString

                                if (orario != null && giorno == oggi.toString() && email_ == email) {
                                    // Aggiungi l'orario alla lista degli orari delle prenotazioni
                                    orariPrenotazioni.add(orario.dropLast(2))
                                }
                            }

                            // Torna al thread principale per aggiornare la UI
                            withContext(Dispatchers.Main) {
                                // Ora puoi utilizzare la lista degli orari per visualizzarli o eseguire altre operazioni
                                // Ad esempio, puoi impostare il testo della textView come concatenazione degli orari
                                binding.textPrenotazioniOggi.text = orariPrenotazioni.joinToString(", ")
                            }
                        }
                    } else {
                        showToast(context, "La risposta del server è vuota")
                    }
                } else {
                    showToast(context, "Accesso negato: risposta non valida")
                    Log.e("getPrenotazioni", "Risposta non valida dal server")
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("getPrenotazioni", "Failed to get prenotazioni", t)
                showToast(context, "Accesso negato: errore di connessione")
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

    fun insertPrenotazione(context: Context, binding: ActivityPrenotazioniBinding,orario:String, email: String?, pasto: String?) {
       val localdate= LocalDate.now()
        val gson = Gson()
        val string = "{\"email\": \"$email\", \"giorno\": \"$localdate\", \"orario\": \"$orario\", \"pasto\": \"$pasto\"}"
        val json = gson.fromJson(string, JsonObject::class.java)

        // Invio della richiesta di inserimento della prenotazione tramite Retrofit
        Client.retrofit.insertPrenotazione(json).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _success.value = true

                    val risposta: JsonObject? = response.body()
                    Log.d("risposta", risposta.toString())

                    if (risposta != null) {

                        if (context != null) {


                            Log.d("gtrggrgg", orario)


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

                    showToast(context,"Prenotazione Non effettuata")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
                Log.e("insertPrenotazione", "Failed to send prenotazione", t)
            }
        })
    }


    fun insertTransazione(binding: ActivityPortafoglioBinding, email: String?,  tipo: String, quantita: Int) {
        val gson = Gson()
        val string  =
            "{\"email\": \"$email\", \"tipo\": \"$tipo\", \"quantita\": \"$quantita\"}"

        val json = gson.fromJson(string, JsonObject::class.java)
        Client.retrofit.insertTransizione(json).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _success.value = true






                    showToast(binding.root.context, "Transazione inviata")
                    } else {
                        _success.value = false
                        Log.e("insertTransazione", "Error in transazione")
                    }
                }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
                Log.e("insertTransazione", "Failed to send transazione", t)
            }
        })
    }


    fun getSaldo(binding: ActivityPortafoglioBinding, email: String?) {
        val gson = Gson()
        val string  =
            "{\"email\": \"$email\"}"

        val json = gson.fromJson(string, JsonObject::class.java)

        Client.retrofit.getSaldo(email).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _success.value = true

                    val risposta: JsonObject? = response.body()

//                    val prezzo_pranzo_completo = risposta.get("prezzo_pranzo_completo")?.asString

                    if (risposta != null) {

                        val saldoo = risposta.asJsonObject

                        val nuovo_saldo = risposta.get("saldo_totale").asFloat

                        _saldo.postValue(nuovo_saldo)

//                        binding.textSaldoValore.text = saldoo.get("saldo_totale")?.asString


//                        for (i in 0 until risposta.size()) {
//                            val transazione = risposta[i].asJsonObject
//                            val tipo = transazione.get("tipo")?.asString
//                            val quantita = transazione.get("quantita")?.asString
//
//
//                        }


                    } else {
                        _success.value = false
                        Log.e("getSaldo", "Error in getSaldo")
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
                Log.e("getSaldo", "Failed to getSaldo", t)
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
