package com.example.managermensa.activity.localdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.managermensa.activity.localdatabase.User
import com.example.managermensa.data.Allergia

@Dao
interface UserDao {

    @Insert
    fun InsertUser(user: User)

    @Delete
    fun DeleteUser(user: User)

    @Update
    fun UpdateUser(user: User)

    @Query("SELECT * FROM User WHERE email = :email_")
    fun SelectUser(email_ : String) : User

    @Query("SELECT * FROM User")
    fun SelectUsers() : User



    @Query("DELETE FROM allergia")
    fun DeleteAllergie()

    @Delete
    fun DeleteAllergia(allergia:Allergia)

    @Query ("SELECT * FROM allergia")
    fun GetAllergie(): List<Allergia>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun InsertAllergia(allergia:Allergia)

    @Query("DELETE FROM prezzi")
    fun deleteAllPrezzi()

    @Insert
    fun InsertPrezzi(prezzi:Prezzi)

    @Query("SELECT * FROM prezzi")
    fun GetPrezzi() : Prezzi

    @Query("SELECT COUNT(*) FROM prezzi")
    fun Count(): Int

    @Query("SELECT * FROM prezzi WHERE prezzo_pranzo_completo = :pranzoCompleto AND prezzo_cena_completa = :cenaCompleta AND prezzo_primo = :primo AND prezzo_secondo = :secondo AND prezzo_contorno = :contorno")
    fun Uguali(pranzoCompleto: Int?, cenaCompleta: Int?, primo: Int?, secondo: Int?, contorno: Int?): Prezzi?


}