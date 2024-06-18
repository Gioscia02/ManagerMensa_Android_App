package com.example.managermensa.activity.localdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.managermensa.data.Allergia

@Database( entities = [User :: class, Prezzi :: class, Allergia :: class], version =4)
abstract class AppDatabase : RoomDatabase() {
    abstract  fun userDao() : UserDao


}