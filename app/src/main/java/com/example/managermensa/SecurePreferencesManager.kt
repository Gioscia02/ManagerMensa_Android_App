import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.managermensa.activity.Utente

object SecurePreferencesManager {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_PASSWORD = "password"
    private const val KEY_NOME = "nome"
    private const val KEY_COGNOME = "cognome"
    private const val KEY_NASCITA = "nascita"


    fun saveUser(context: Context, user: Utente) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString(KEY_EMAIL, user.email)
            putString(KEY_PASSWORD, user.password)
            putString(KEY_NOME, user.nome)
            putString(KEY_COGNOME, user.cognome)
            putString(KEY_NASCITA, user.nascita)
            apply()
        }
    }

    fun getUser(context: Context): Utente? {


        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val email = sharedPrefs.getString(KEY_EMAIL, null)
        val password = sharedPrefs.getString(KEY_PASSWORD, null)
        val nome = sharedPrefs.getString(KEY_NOME, null)
        val cognome = sharedPrefs.getString(KEY_COGNOME, null)
        val nascita = sharedPrefs.getString(KEY_NASCITA, null)

        return if (email != null && password != null && nome != null && cognome != null && nascita != null) {
            Utente(nome, cognome, email, password, nascita)
        } else {
            null
        }

        null
    }

    fun clearUser(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            clear()
            apply()
        }
    }

    // Metodo per ottenere un'istanza di SharedPreferences crittografate
    fun getSecurePrefs(context: Context): SharedPreferences {
        // Genera una chiave master per crittografare le preferenze condivise
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Crea un'istanza di EncryptedSharedPreferences
        return EncryptedSharedPreferences.create(
            PREFS_NAME,
            masterKeyAlias,
            context.applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

}

