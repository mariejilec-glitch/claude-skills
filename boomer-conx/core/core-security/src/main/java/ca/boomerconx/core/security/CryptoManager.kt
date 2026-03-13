package ca.boomerconx.core.security

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AesGcmKeyManager
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val aead: Aead by lazy {
        AeadConfig.register()
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(AesGcmKeyManager.aes256GcmTemplate())
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
        keysetHandle.getPrimitive(Aead::class.java)
    }

    fun encrypt(plaintext: ByteArray, associatedData: ByteArray = byteArrayOf()): ByteArray =
        aead.encrypt(plaintext, associatedData)

    fun decrypt(ciphertext: ByteArray, associatedData: ByteArray = byteArrayOf()): ByteArray =
        aead.decrypt(ciphertext, associatedData)

    fun encryptString(plaintext: String, associatedData: ByteArray = byteArrayOf()): ByteArray =
        encrypt(plaintext.toByteArray(Charsets.UTF_8), associatedData)

    fun decryptToString(ciphertext: ByteArray, associatedData: ByteArray = byteArrayOf()): String =
        decrypt(ciphertext, associatedData).toString(Charsets.UTF_8)

    companion object {
        private const val KEYSET_NAME = "boomerconx_keyset"
        private const val PREF_FILE_NAME = "boomerconx_crypto_prefs"
        private const val MASTER_KEY_URI = "android-keystore://boomerconx_master_key"
    }
}
