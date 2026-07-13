package com.dminus14.app.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android Keystore 기반 AES-GCM 암/복호화 유틸.
 *
 * 키는 기기 Keystore에 alias로 보관되며, 앱 밖으로 노출되지 않는다.
 * 암호화 결과는 Preferences DataStore 등에 저장하기 쉬운
 * `"base64(iv)|base64(ciphertext)"` 문자열로 반환한다.
 *
 * 평문·암호문을 로그에 남기지 않는다. 호출 측에서도 동일하게 취급해야 한다.
 */
@Singleton
class CryptoManager
    @Inject
    constructor() {
        /** 기기 Android Keystore 인스턴스. 최초 사용 시 한 번만 로드한다. */
        private val keyStore by lazy {
            try {
                KeyStore.getInstance(ANDROID_KEYSTORE).apply {
                    load(null)
                }
            } catch (e: GeneralSecurityException) {
                throw IllegalStateException("Failed to load Android KeyStore", e)
            } catch (e: IOException) {
                throw IllegalStateException("Failed to load Android KeyStore", e)
            }
        }

        /**
         * Android Keystore에서 AES 키를 가져오거나, 없으면 새로 생성한다.
         *
         * 키가 이미 있으면 재사용하고, 없으면 AES-256 / GCM 용도로 생성해 Keystore에 저장한다.
         */
        private fun getOrCreateKey(): SecretKey =
            synchronized(keyLock) {
                val existingKey = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
                if (existingKey != null) {
                    return@synchronized existingKey.secretKey
                }

                val paramsBuilder =
                    KeyGenParameterSpec
                        .Builder(
                            KEY_ALIAS,
                            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                        ).apply {
                            setBlockModes(BLOCK_MODE)
                            setEncryptionPaddings(PADDING)
                            setKeySize(KEY_SIZE)
                            setRandomizedEncryptionRequired(true)
                        }

                val keyGenerator = KeyGenerator.getInstance(ALGORITHM, ANDROID_KEYSTORE)
                keyGenerator.init(paramsBuilder.build())
                keyGenerator.generateKey()
            }

        /**
         * 바이트 배열을 AES-GCM으로 암호화한다.
         *
         * @param plainText 암호화할 평문. 비어 있으면 안 된다.
         * @return `"base64(iv)|base64(ciphertext)"` 형식의 문자열.
         * 복호화 시 [decrypt]에 그대로 넘긴다.
         * @throws IllegalStateException 암호화에 실패한 경우
         */
        fun encrypt(plainText: ByteArray): String {
            require(plainText.isNotEmpty()) { "Plain text cannot be empty" }

            try {
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
                val ciphertext = cipher.doFinal(plainText)
                val iv = cipher.iv
                return buildPayload(iv, ciphertext)
            } catch (e: GeneralSecurityException) {
                throw IllegalStateException("Failed to encrypt data", e)
            }
        }

        /**
         * [encrypt]가 만든 `"iv|ciphertext"` 문자열을 복호화한다.
         *
         * @param encrypted `"base64(iv)|base64(ciphertext)"` 형식의 문자열
         * @return 복호화된 평문 바이트
         * @throws SecurityException 암호문·IV가 위변조되었거나 키가 맞지 않는 경우
         * @throws IllegalStateException 그 외 복호화 실패
         * @throws IllegalArgumentException 형식이 잘못된 경우
         */
        fun decrypt(encrypted: String): ByteArray {
            val (iv, ciphertext) = parsePayload(encrypted)

            try {
                val cipher = Cipher.getInstance(TRANSFORMATION)
                val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
                cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)
                return cipher.doFinal(ciphertext)
            } catch (_: AEADBadTagException) {
                throw SecurityException("Decryption failed: Invalid ciphertext or IV")
            } catch (e: GeneralSecurityException) {
                throw IllegalStateException("Failed to decrypt data", e)
            }
        }

        /**
         * UTF-8 문자열을 암호화한다.
         *
         * Preferences DataStore처럼 String만 저장 가능한 저장소에 넣을 때 사용한다.
         *
         * @param plainText 암호화할 평문 문자열
         * @return `"base64(iv)|base64(ciphertext)"` 형식의 문자열
         */
        fun encryptStringToBase64(plainText: String): String =
            encrypt(plainText.toByteArray(Charsets.UTF_8))

        /**
         * [encryptStringToBase64] 결과를 복호화해 UTF-8 문자열로 반환한다.
         *
         * @param encrypted `"base64(iv)|base64(ciphertext)"` 형식의 문자열
         * @return 복호화된 평문 문자열
         */
        fun decryptStringFromBase64(encrypted: String): String =
            decrypt(encrypted).toString(Charsets.UTF_8)

        /**
         * IV와 ciphertext를 Base64로 인코딩한 뒤 `|`로 이어 하나의 문자열로 만든다.
         *
         * 형식: `"base64(iv)|base64(ciphertext)"`
         */
        private fun buildPayload(
            iv: ByteArray,
            ciphertext: ByteArray,
        ): String {
            val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            val ciphertextBase64 = Base64.encodeToString(ciphertext, Base64.NO_WRAP)
            return "$ivBase64$PAYLOAD_SEPARATOR$ciphertextBase64"
        }

        /**
         * `"iv|ciphertext"` 문자열을 파싱해 IV·암호문 바이트로 분리한다.
         *
         * @return `Pair(iv, ciphertext)`
         * @throws IllegalArgumentException 형식이 잘못되었거나 IV 길이가 유효하지 않은 경우
         */
        private fun parsePayload(encrypted: String): Pair<ByteArray, ByteArray> {
            require(encrypted.isNotEmpty()) { "Encrypted text cannot be empty" }

            val separatorIndex = encrypted.indexOf(PAYLOAD_SEPARATOR)
            require(separatorIndex > 0 && separatorIndex < encrypted.lastIndex) {
                "Encrypted text must be in 'iv|ciphertext' format"
            }

            val iv =
                Base64.decode(encrypted.substring(0, separatorIndex), Base64.NO_WRAP)
            val ciphertext =
                Base64.decode(encrypted.substring(separatorIndex + 1), Base64.NO_WRAP)

            require(iv.isNotEmpty()) { "IV cannot be empty" }
            require(iv.size == IV_SIZE) { "IV must be $IV_SIZE bytes" }
            require(ciphertext.isNotEmpty()) { "Encrypted text cannot be empty" }

            return iv to ciphertext
        }

        private companion object {
            /** Android Keystore에 저장되는 AES 키의 alias. */
            private const val KEY_ALIAS = "DMINUS14_MASTER_KEY"

            /**
             * Keystore 마스터 키 생성/조회 동시성 제어용 락.
             * alias가 프로세스 전역이므로 인스턴스가 여러 개여도 단일 락을 사용한다.
             */
            private val keyLock = Any()

            private const val ANDROID_KEYSTORE = "AndroidKeyStore"
            private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
            private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
            private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
            private const val KEY_SIZE = 256
            private const val TRANSFORMATION = "AES/GCM/NoPadding"

            /** GCM에서 사용하는 IV 길이(바이트). */
            private const val IV_SIZE = 12

            /** GCM authentication tag 길이(비트). */
            private const val GCM_TAG_LENGTH_BITS = 128

            /** 저장 문자열에서 IV와 ciphertext를 구분하는 구분자. Base64 문자와 겹치지 않는다. */
            private const val PAYLOAD_SEPARATOR = '|'
        }
    }
