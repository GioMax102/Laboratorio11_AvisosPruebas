package mx.tec.avisos.data.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

/**
 * Cifra y descifra texto con una llave que vive en el Android Keystore.
 *
 * Lo importante no es el AES: es DÓNDE está la llave. El Keystore la genera
 * y la guarda en hardware (o en un proceso aparte del sistema), y nunca la
 * entrega: tu app le pide "cifra esto" y recibe el resultado. Ni tu propio
 * código puede leer los bytes de la llave — y por eso tampoco puede leerlos
 * quien se robe el archivo de datos.
 *
 * Es, en 40 líneas, lo que hacía `EncryptedSharedPreferences` antes de que
 * Google la retirara.
 */
class Cifrador @Inject constructor() {

    private val alias = "sesion"

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    /** La llave se crea una sola vez, la primera vez que hace falta. */
    private fun llave(): SecretKey = synchronized(this) {
        (keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry)?.secretKey?.let { return it }

        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            .apply { init(spec) }
            .generateKey()
    }

    /** Texto → base64 de (iv + cifrado). El iv cambia en cada llamada; va pegado al frente. */
    fun cifrar(texto: String): String {
        val cipher = Cipher.getInstance(TRANSFORMACION)
        cipher.init(Cipher.ENCRYPT_MODE, llave())
        val cifrado = cipher.doFinal(texto.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(cipher.iv + cifrado, Base64.NO_WRAP)
    }

    /**
     * El camino inverso. Devuelve null si no se puede: el texto no era nuestro,
     * fue alterado, o la llave ya no es la misma (se restauró un respaldo en
     * otro teléfono, por ejemplo). En todos esos casos la respuesta correcta es
     * "no hay sesión", no tronar.
     */
    fun descifrar(base64: String): String? = try {
        val bytes = Base64.decode(base64, Base64.NO_WRAP)
        val iv = bytes.copyOfRange(0, IV_BYTES)
        val cifrado = bytes.copyOfRange(IV_BYTES, bytes.size)
        val cipher = Cipher.getInstance(TRANSFORMACION)
        cipher.init(Cipher.DECRYPT_MODE, llave(), GCMParameterSpec(TAG_BITS, iv))
        String(cipher.doFinal(cifrado), Charsets.UTF_8)
    } catch (e: GeneralSecurityException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    }

    private companion object {
        const val TRANSFORMACION = "AES/GCM/NoPadding"
        const val IV_BYTES = 12
        const val TAG_BITS = 128
    }
}
