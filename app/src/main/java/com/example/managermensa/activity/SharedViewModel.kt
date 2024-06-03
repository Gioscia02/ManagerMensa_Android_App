package com.example.managermensa.activity

import SecurePreferencesManager
import UserDatabaseManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.managermensa.activity.localdatabase.AppDatabase
import com.example.managermensa.activity.localdatabase.LocalViewModel
import com.example.managermensa.activity.localdatabase.Prezzi
import com.example.managermensa.activity.localdatabase.User
import com.example.managermensa.activity.retrofit.Client
import com.example.managermensa.data.Avviso
import com.example.managermensa.data.Pasto
import com.example.managermensa.data.Transazione
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
import java.util.Date
import java.util.Locale

class SharedViewModel(application: Application) : AndroidViewModel(application) {


    private val _pastiprimi = MutableLiveData<List<Pasto>>().apply { value = null }
    val pastiprimi: LiveData<List<Pasto>> get() = _pastiprimi

    private val _pastisecondi = MutableLiveData<List<Pasto>>().apply { value = null }
    val pastisecondi: LiveData<List<Pasto>> get() = _pastisecondi


    private val _pasticontorni = MutableLiveData<List<Pasto>>().apply { value = null }
    val pasticontorni: LiveData<List<Pasto>> get() = _pasticontorni

    private val _prezzi = MutableLiveData<Prezzi?>().apply { value = null }
    val prezzi: LiveData<Prezzi?> get() = _prezzi

    private val _saldo = MutableLiveData<Float?>().apply { value = null }
    val saldo: LiveData<Float?> get() = _saldo

    //Flag se la transazione è andata a buon fine o no
    private val _gettransazione = MutableLiveData<Boolean>().apply { value = null }
    val gettransazione: LiveData<Boolean> get() = _gettransazione


    //Tutte le transazioni
    private val _transazioni = MutableLiveData<List<Transazione>>().apply { value = null }
    val transazioni: LiveData<List<Transazione>> get() = _transazioni


    //Nuovo Utente
    private val _user = MutableLiveData<User>().apply { value = null }
    val user: LiveData<User> get() = _user

    //Aggiornamento Utente
    private val _update = MutableLiveData<Boolean>().apply { value = null }
    val update: LiveData<Boolean> get() = _update


    //Tutti gli avvisi
    private val _avvisi = MutableLiveData<List<Avviso>>().apply { value = null }
    val avvisi: LiveData<List<Avviso>> get() = _avvisi

    //L'ultimo avviso
    private val _ultimoavviso = MutableLiveData<Avviso>().apply { value = null }
    val ultimoavviso: LiveData<Avviso> get() = _ultimoavviso

    //Avvenuta  Prenotazione
    private val _prenotazione = MutableLiveData<Boolean>().apply { value = null }
    val prenotazione: LiveData<Boolean> get() = _prenotazione

    //Orario Prenotazione
    private val _orarioprenotazione = MutableLiveData<String>().apply { value = null }
    val orarioprenotazione: LiveData<String> get() = _orarioprenotazione

    private val _itemList = MutableLiveData<ArrayList<User>>().apply { value = ArrayList() }
    val itemList: LiveData<ArrayList<User>> get() = _itemList

    private val _utenteSelezionato = MutableLiveData<User?>().apply { value = null }
    val utenteSelezionato: LiveData<User?> get() = _utenteSelezionato

    private val _position = MutableLiveData<Int>().apply { value = -1 }
    val position: LiveData<Int> get() = _position

    private val _success = MutableLiveData<Boolean>().apply { value = false }
    val success: LiveData<Boolean> get() = _success

    init {
        getListaUtenti()
    }

    fun addItem(item: User) {
        _itemList.value?.apply {
            add(item)
            _itemList.postValue(this)
        }
    }

//    fun removeItem(item: Utente) {
//        _itemList.value?.apply {
//            remove(item)
//            _itemList.postValue(this)
//        }
//    }

    fun getUtente(email: String, password: String) {
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



                    } else {

                        Log.e("getUtente", "Risposta nulla dal server")
                    }
                } else {

                    Log.e("getUtente", "Risposta non valida dal server")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("getUtente", "Failed to get user", t)
            }
        })


    }


    fun getPrezzi(context : Context) {
        Client.retrofit.getPrezzi().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val risposta: JsonObject? = response.body()
                    Log.d("risposta", risposta.toString())

                    if(risposta != null){


                        //Prendo i dati richiesti
                        val prezzo_pranzo_completo = risposta.get("prezzo_pranzo_completo")?.asFloat
                        val prezzo_cena_completa = risposta.get("prezzo_cena_completa")?.asFloat
                        val prezzo_primo = risposta.get("prezzo_primo")?.asFloat
                        val prezzo_secondo = risposta.get("prezzo_secondo")?.asFloat
                        val prezzo_contorno = risposta.get("prezzo_contorno")?.asFloat

                        val prezzo = Prezzi( prezzo_pranzo_completo= prezzo_pranzo_completo, prezzo_primo = prezzo_primo, prezzo_secondo = prezzo_secondo, prezzo_contorno = prezzo_contorno, prezzo_cena_completa = prezzo_cena_completa, id = 1)
//
                        Log.d("PREZOOOOOO", prezzo_primo.toString())

                        //Assegno il prezzo al LiveData
                        _prezzi.postValue(prezzo)

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
//
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


    fun getPrenotazioni(context: Context) {
        Client.retrofit.getPrenotazioni().enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                val oggi = LocalDate.now()
                if (response.isSuccessful) {
                    val prenotazioni: JsonArray? = response.body()
                    if (prenotazioni != null) {
                        //Lista per contenere le prenotazioni di oggi
                        val orariPrenotazioni = mutableListOf<String>()

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

                            // Itera su ogni elemento della JsonArray per prendere i dati che abbiamoo richiesto
                            for (i in 0 until prenotazioni.size()) {
                                val prenotazione = prenotazioni[i].asJsonObject
                                val orario = prenotazione.get("orario")?.asString
                                val giorno = prenotazione.get("giorno")?.asString
                                val email = prenotazione.get("email")?.asString

                                if (orario != null && giorno == oggi.toString() && email_ == email) {
                                    // Aggiungi l'orario alla lista degli orari delle prenotazioni
                                    orariPrenotazioni.add(orario)
                                }
                            }

                            //Memorizzo gli orari delle prenotazioni di oggi, comverto da mutablelist a string usando joinToString
                            _orarioprenotazione.postValue(orariPrenotazioni.joinToString(", "))
                        }
                    }
                } else {
                    Log.e("getPrenotazioni", "Risposta non valida dal server")
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("getPrenotazioni", "Failed to get prenotazioni", t)

            }
        })
    }




    fun getAvvisi(context: Context) {
        Client.retrofit.getAvvisi().enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                val oggi = LocalDate.now()
                if (response.isSuccessful) {
                    val avvisi: JsonArray? = response.body()
                    if (avvisi != null) {
                        //Lista per contenere gli ultimi 3 avvisi
                        val avvisi_lista = mutableListOf<Avviso>()

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

                            // Itera su ogni elemento della JsonArray per prendere i dati che abbiamoo richiesto
                            for (i in 0 until avvisi.size()) {
                                val avviso = avvisi[i].asJsonObject
                                val titolo = avviso.get("titolo").asString
                                val testo = avviso.get("testo").asString

                                // Supponiamo che avviso.get("data") restituisca un timestamp
                                val timestamp = avviso.get("data").asString  // Converti il timestamp in Long

                                // Creazione di un oggetto SimpleDateFormat per formattare la data
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy")

                                // Creazione di un oggetto Date dal timestamp
                                val date = Date(timestamp)

                                // Formattazione della data nel formato desiderato
                                val data_formattata = dateFormat.format(date)
//
//                                val data = avviso.get("data").asString

                                avvisi_lista.add(Avviso(titolo,data_formattata,testo))


                            }

                            //Memorizzo gli orari delle prenotazioni di oggi, comverto da mutablelist a string usando joinToString
                            _avvisi.postValue(avvisi_lista)
                        }
                    }
                } else {
                    Log.e("getPrenotazioni", "Risposta non valida dal server")
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e("getPrenotazioni", "Failed to get prenotazioni", t)

            }
        })
    }


    fun getUltimoavviso() {
        Client.retrofit.getUltimoavviso().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val risposta: JsonObject? = response.body()

                    if (risposta != null) {
                        // Prendi l'ultimo avviso dalla risposta
                        val ultimoAvviso = risposta.asJsonObject

                        if (ultimoAvviso != null) {
                            // Puoi ora manipolare l'ultimo avviso come JsonObject
                            // Ad esempio, se vuoi accedere ai singoli campi dell'avviso, puoi farlo in questo modo:
                            val titolo = ultimoAvviso.getAsJsonPrimitive("titolo").asString
                            val testo = ultimoAvviso.getAsJsonPrimitive("testo").asString
//                            val timestamp = ultimoAvviso.getAsJsonPrimitive("timestamp").asString
                            // Esegui altre operazioni necessarie con l'ultimo avviso

                            // Supponiamo che avviso.get("data") restituisca un timestamp
                            val timestamp = ultimoAvviso.get("data").asString  // Converti il timestamp in Long

                            // Creazione di un oggetto SimpleDateFormat per formattare la data
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy")

                            // Creazione di un oggetto Date dal timestamp
                            val date = Date(timestamp)

                            // Formattazione della data nel formato desiderato
                            val data_formattata = dateFormat.format(date)


                            _ultimoavviso.postValue(Avviso(titolo,data_formattata,testo))


                        } else {
                            Log.e("getUltimoavviso", "La risposta non contiene avvisi")
                        }
                    } else {
                        Log.e("getUltimoavviso", "Risposta nulla")
                    }
                } else {
                    Log.e("getUltimoavviso", "Errore nella risposta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("getUltimoavviso", "Errore nella chiamata: ${t.message}")
            }
        })
    }


    fun updateUtente(emailattuale: String?,emailnuova: String?,password: String?, nome: String?, cognome: String?,nascita: String? ) {
        val gson = Gson()
        val string  =
            "{\"emailattuale\": \"$emailattuale\", \"emailnuova\": \"$emailnuova\", \"password\": \"$password\", \"nome\": \"$nome\", \"cognome\": \"$cognome\", \"nascita\": \"$nascita\"}"

        Log.d("Password", password.toString())
        val json = gson.fromJson(string, JsonObject::class.java)
        Client.retrofit.updateUtente(json).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _success.value = true
                    val risposta = response.body()

                    if(risposta!=null){

                        _update.postValue(true)

                        Log.d("TAG", risposta.toString())
                    }

                } else {
                    _success.value = false
                    Log.d("updateUtente", "Errore aggiornamento Utente")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
                Log.d("updateUtente", "Errore aggiornamento Utente", t)
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
                    val risposta = response.body()


                    if (risposta != null) {

//                    risposta.add(response.body())
                        Log.v("risposta", risposta.toString())
//                    addItem(parseJsonToModel(risposta)[0])

                        //Raccolgo i dati della risposta json
                        val nome_ = risposta.get("nome")?.asString ?: ""
                        val cognome_ = risposta.get("cognome")?.asString ?: ""
                        val email_ = risposta.get("email")?.asString ?: ""
                        val nascita_ = risposta.get("nascita")?.asString ?: ""
                        val password_ = risposta.get("password")?.asString ?: ""

                        val new_user = User(email_,password_,nome,cognome_,nascita_)

                        Log.d("Nuovo Utente", new_user.toString())

                        _user.postValue(new_user)

                        _success.value = true

                    } else {
                        _success.value = false
                        Log.e("insertItem", "Error in insertion")
                    }
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

    fun insertPrenotazione(orario:String, email: String?, pasto: String?) {
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

                    if (risposta != null) {
                        Log.d("risposta", risposta.toString())

                        if (risposta.get("success").asBoolean == true) {

                            Log.d("gtrggrgg", orario)
                            _prenotazione.postValue(true)
                            _orarioprenotazione.postValue( orario +", " + _orarioprenotazione.value.toString())

                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
                Log.e("insertPrenotazione", "Failed to send prenotazione", t)
            }
        })
    }


    fun insertTransazione(email: String?,  tipo: String, quantita: Int) {
        val gson = Gson()
        val string  =
            "{\"email\": \"$email\", \"tipo\": \"$tipo\", \"quantita\": \"$quantita\"}"

        val json = gson.fromJson(string, JsonObject::class.java)
        Client.retrofit.insertTransizione(json).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _success.value = true

                    //Salvo che la transazione è andata a buon fine
                    _gettransazione.postValue(true)

                    Log.d("insertTransazione", "Transazione riuscita")

                    } else {
                        _success.value = false

                    //Salvo che la transazione NON è andata a buon fine
                    _gettransazione.postValue(false)

                        Log.d("insertTransazione", "Errore nella transazione")
                    }
                }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
                //Salvo che la transazione NON è andata a buon fine
                _gettransazione.postValue(false)
                Log.e("insertTransazione", "Failed to send transazione", t)
            }
        })
    }


    fun getSaldo(email: String?) {
        val gson = Gson()
        val string = "{\"email\": \"$email\"}"
        val json = gson.fromJson(string, JsonObject::class.java)

        Client.retrofit.getSaldo(email).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    _success.value = true
                    val risposta: JsonObject? = response.body()

                    if (risposta != null) {
                        if (risposta.has("saldo_totale")) {
                            val saldo = risposta.get("saldo_totale").asFloat
                            _saldo.postValue(saldo)
                        } else {
                            _success.value = false
                            Log.e("getSaldo", "Response does not contain 'saldo_totale' field")
                        }
                    } else {
                        _success.value = false
                        Log.e("getSaldo", "Response body is null")
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                _success.value = false
                Log.e("getSaldo", "Failed to getSaldo", t)
            }
        })
    }



    fun getTransazioni(context: Context, email: String?) {
        val gson = Gson()
        val string  =
            "{\"email\": \"$email\"}"

        val json = gson.fromJson(string, JsonObject::class.java)

        Client.retrofit.getTransazioni(email).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    _success.value = true

                    val transazioni_array_json: JsonArray? = response.body()
                    var transazioni_array =  mutableListOf<Transazione>()

                    if (transazioni_array_json != null) {

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

                            // Itera su ogni elemento della JsonArray per prendere i dati che abbiamoo richiesto
                            for (i in 0 until transazioni_array_json.size()) {
                                val transizione_presa = transazioni_array_json[i].asJsonObject
                                val tipo = transizione_presa.get("tipo").asString
                                val quantita = transizione_presa.get("quantita").asString

                                // Supponiamo che avviso.get("data") restituisca un timestamp
                                val timestamp = transizione_presa.get("data").asString  // Converti il timestamp in Long
                                val timestampFormattato = timestamp
//                                replace("GMT", "").replace(Regex("[a-zA-Z]+,"), "")


                                transazioni_array.add(Transazione(email_,timestampFormattato,tipo,quantita))


                            }

                            //Memorizzo gli orari delle prenotazioni di oggi, comverto da mutablelist a string usando joinToString
                            _transazioni.postValue(transazioni_array)
                        }



                    } else {
                        _success.value = false
                        Log.e("getSaldo", "Error in getSaldo")
                    }
                }
            }
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                _success.value = false
                Log.e("getSaldo", "Failed to getSaldo", t)
            }
        })
    }





    fun getPastiPrimi(giorno: String) {
        val gson = Gson()
        val string  = ""
        val json = gson.fromJson(string, JsonObject::class.java)

        Client.retrofit.getPastiPrimi(giorno).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    _success.value = true

                    val pasti_array_json: JsonArray? = response.body()
                    var pasti_array =  mutableListOf<Pasto>()

                    if(pasti_array_json!=null) {


                        for (i in 0 until pasti_array_json.size()) {

                            val pasto_preso = pasti_array_json[i].asJsonObject
                            val nome =pasto_preso.get("nome").asString
                            val allergieString = pasto_preso.get("allergie").asString
                            val tipo =pasto_preso.get("tipo").asString

                            val allergieArray = allergieString.split(", ").toList()

                            pasti_array.add(Pasto(nome,tipo,allergieArray))
                        }

                        _pastiprimi.postValue(pasti_array)
                    }
                    else {
                        _success.value = false
                        Log.e("getSaldo", "Error in getSaldo")
                    }
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                _success.value = false
                Log.e("getSaldo", "Failed to getSaldo", t)
            }
        })
    }


    fun getPastiSecondi(giorno: String) {
        val gson = Gson()
        val string  = ""
        val json = gson.fromJson(string, JsonObject::class.java)

        Client.retrofit.getPastiSecondi(giorno).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    _success.value = true

                    val pasti_array_json: JsonArray? = response.body()
                    var pasti_array =  mutableListOf<Pasto>()

                    if(pasti_array_json!=null) {


                        for (i in 0 until pasti_array_json.size()) {

                            val pasto_preso = pasti_array_json[i].asJsonObject
                            val nome =pasto_preso.get("nome").asString
                            val allergieString = pasto_preso.get("allergie").asString
                            val tipo =pasto_preso.get("tipo").asString

                            val allergieArray = allergieString.split(", ").toList()

                            pasti_array.add(Pasto(nome,tipo,allergieArray))
                        }

                        _pastisecondi.postValue(pasti_array)
                    }
                    else {
                        _success.value = false
                        Log.e("getSaldo", "Error in getSaldo")
                    }
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                _success.value = false
                Log.e("getSaldo", "Failed to getSaldo", t)
            }
        })
    }


    fun getPastiContorni(giorno: String) {
        val gson = Gson()
        val string  = ""
        val json = gson.fromJson(string, JsonObject::class.java)

        Client.retrofit.getPastiContorni(giorno).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful) {
                    _success.value = true

                    val pasti_array_json: JsonArray? = response.body()
                    var pasti_array =  mutableListOf<Pasto>()

                    if(pasti_array_json!=null) {


                        for (i in 0 until pasti_array_json.size()) {

                            val pasto_preso = pasti_array_json[i].asJsonObject
                            val nome =pasto_preso.get("nome").asString
                            val allergieString = pasto_preso.get("allergie").asString
                            val tipo =pasto_preso.get("tipo").asString

                            val allergieArray = allergieString.split(", ").toList()

                            pasti_array.add(Pasto(nome,tipo,allergieArray))
                        }

                        _pasticontorni.postValue(pasti_array)
                    }
                    else {
                        _success.value = false
                        Log.e("getSaldo", "Error in getSaldo")
                    }
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
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

    private fun parseJsonToModel(jsonArray: JsonArray): ArrayList<User> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        return gson.fromJson(jsonArray, object : TypeToken<ArrayList<User>>() {}.type)
    }
}
