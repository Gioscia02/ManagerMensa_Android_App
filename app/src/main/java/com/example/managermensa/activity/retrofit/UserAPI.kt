package com.example.managermensa.activity.retrofit

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserAPI {


    @DELETE("$USER_URI/{id}")
    fun deleteUtente(@Path("id") id: Int): Call<JsonObject>

    @PUT("$USER_URI/aggiornautente")
    fun updateUtente( @Body body: JsonObject): Call<JsonObject>

    @POST(USER_URI)
    fun insertUtente( @Body body: JsonObject): Call<JsonObject>

    @POST("$USER_URI/segnalazione")
    fun insertSegnalazione( @Body body: JsonObject): Call<JsonObject>

    @POST("$USER_URI/prenotazione")
    fun insertPrenotazione( @Body body: JsonObject): Call<JsonObject>

    @POST("$USER_URI/transazione")
    fun insertTransizione( @Body body: JsonObject): Call<JsonObject>



    @GET("$USER_URI/prenotazione")
    fun getPrenotazioni(): Call<JsonArray>


    @GET(USER_URI)
    fun getUtenti(): Call<JsonArray>


    @GET("$USER_URI/avvisi")
    fun getAvvisi(): Call<JsonArray>

    @GET("$USER_URI/prezzi")
    fun getPrezzi(): Call<JsonObject>

    @GET("$USER_URI/transazioni/{email}")
    fun getSaldo( @Path("email") email: String?): Call<JsonObject>




    @GET("$USER_URI/{email}/{password}")
    fun findUtente(@Path("email") email: String,@Path("password") password: String): Call<JsonObject>




    companion object {
        const val BASE_URL = "http://192.168.116.242:9000/"
        const val USER_URI = "pwm/utenti"

    }

}