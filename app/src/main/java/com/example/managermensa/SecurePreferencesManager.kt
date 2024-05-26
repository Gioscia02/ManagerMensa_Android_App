package com.example.managermensa
import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SecurePreferencesManager {

    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"

    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveCredentials(context: Context, username: String, password: String) {
        val sharedPreferences = getEncryptedSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    fun getCredentials(context: Context): Pair<String?, String?> {
        val sharedPreferences = getEncryptedSharedPreferences(context)
        val username = sharedPreferences.getString(KEY_USERNAME, null)
        val password = sharedPreferences.getString(KEY_PASSWORD, null)
        return Pair(username, password)
    }

    fun getEmail(context: Context): String? {
        val sharedPreferences = getEncryptedSharedPreferences(context)
        return sharedPreferences.getString(KEY_USERNAME, null)
    }


    fun clearCredentials(context: Context) {
        val sharedPreferences = getEncryptedSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(KEY_USERNAME)
            remove(KEY_PASSWORD)
            apply()
        }
    }
}
