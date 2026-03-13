package ca.boomerconx.core.security

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

private val Context.pinDataStore by preferencesDataStore(name = "pin_store")

@Singleton
class PinManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val pinHashKey = stringPreferencesKey("pin_hash")
    private val pinSaltKey = stringPreferencesKey("pin_salt")

    suspend fun setPin(pin: String) {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        context.pinDataStore.edit { prefs ->
            prefs[pinSaltKey] = salt
            prefs[pinHashKey] = hash
        }
    }

    suspend fun verifyPin(pin: String): Boolean {
        val prefs = context.pinDataStore.data.first()
        val storedHash = prefs[pinHashKey] ?: return false
        val salt = prefs[pinSaltKey] ?: return false
        return hashPin(pin, salt) == storedHash
    }

    suspend fun hasPin(): Boolean {
        return context.pinDataStore.data.map { it[pinHashKey] != null }.first()
    }

    private fun hashPin(pin: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val input = "$salt:$pin".toByteArray(Charsets.UTF_8)
        return digest.digest(input).joinToString("") { "%02x".format(it) }
    }

    private fun generateSalt(): String {
        val bytes = ByteArray(16)
        java.security.SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
