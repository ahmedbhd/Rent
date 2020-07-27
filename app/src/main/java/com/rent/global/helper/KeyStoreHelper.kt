package com.rent.global.helper

import android.annotation.TargetApi
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.rent.global.utils.TAG
import java.security.*
import java.security.cert.CertificateEncodingException
import java.security.spec.RSAKeyGenParameterSpec
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException

object KeyStoreHelper {

    private val cipher: Cipher
        @Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class)
        get() = Cipher.getInstance(
            String.format(
                "%s/%s/%s",
                SecurityConstants.TYPE_RSA,
                SecurityConstants.BLOCKING_MODE,
                SecurityConstants.PADDING_TYPE
            )
        )

    /**
     * Creates a public and private key1 and stores it using the Android Key
     * Store, so that only this application will be able to access the keys.
     */
    @Throws(
        NoSuchProviderException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class
    )
    fun createKeys(alias: String) {
        if (!isSigningKey(alias)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                createKeysM(alias, false)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    internal fun createKeysM(alias: String, requireAuth: Boolean) {
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE
            )
            keyPairGenerator.initialize(
                KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setAlgorithmParameterSpec(
                        RSAKeyGenParameterSpec(
                            1024,
                            RSAKeyGenParameterSpec.F4
                        )
                    )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setDigests(
                        KeyProperties.DIGEST_SHA256,
                        KeyProperties.DIGEST_SHA384,
                        KeyProperties.DIGEST_SHA512
                    )
                    // Only permit the private key1 to be used if the user authenticated
                    // within the last five minutes.
                    .setUserAuthenticationRequired(requireAuth)
                    .build()
            )
            val keyPair = keyPairGenerator.generateKeyPair()
            Log.d(TAG, "Public Key is: " + keyPair.public.toString())

        } catch (e: NoSuchProviderException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        }

    }

    /**
     * JBMR2+ If Key with the default alias exists, returns true, else false.
     * on pre-JBMR2 returns true always.
     */
    private fun isSigningKey(alias: String): Boolean {
        return try {
            val keyStore =
                KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
            keyStore.load(null)
            keyStore.containsAlias(alias)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            false
        }

    }

    /**
     * Returns the private key1 signature on JBMR2+ or else null.
     */
    @Throws(CertificateEncodingException::class)
    fun getSigningKey(alias: String): String? {
        val cert = getPrivateKeyEntry(alias)!!.certificate ?: return null
        return Base64.encodeToString(cert.encoded, Base64.NO_WRAP)
    }

    private fun getPrivateKeyEntry(alias: String): KeyStore.PrivateKeyEntry? {
        try {
            val ks = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
            ks.load(null)
            val entry = ks.getEntry(alias, null)

            if (entry == null) {
                Log.w(TAG, "No key1 found under alias: $alias")
                Log.w(TAG, "Exiting signData()...")
                return null
            }

            if (entry !is KeyStore.PrivateKeyEntry) {
                Log.w(TAG, "Not an instance of a PrivateKeyEntry")
                Log.w(TAG, "Exiting signData()...")
                return null
            }
            return entry
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            return null
        }

    }

    fun encrypt(alias: String, plaintext: String): String {
        try {
            val publicKey = getPrivateKeyEntry(alias)!!.certificate.publicKey
            val cipher = cipher
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            return Base64.encodeToString(cipher.doFinal(plaintext.toByteArray()), Base64.NO_WRAP)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    fun decrypt(alias: String, ciphertext: String): String {
        try {
            val privateKey = getPrivateKeyEntry(alias)!!.privateKey
            val cipher = cipher
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            return String(cipher.doFinal(Base64.decode(ciphertext, Base64.NO_WRAP)))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    interface SecurityConstants {
        companion object {
            val KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore"
            val TYPE_RSA = "RSA"
            val PADDING_TYPE = "PKCS1Padding"
            val BLOCKING_MODE = "NONE"

            val SIGNATURE_SHA256withRSA = "SHA256withRSA"
            val SIGNATURE_SHA512withRSA = "SHA512withRSA"
        }
    }
}