package com.dminus14.app.data.remote.installation

import com.dminus14.app.data.local.installation.InstallationIdStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstallationIdProvider
    @Inject
    constructor(
        private val installationIdStore: InstallationIdStore,
    ) {
        private val lock = Any()

        @Volatile
        private var cachedInstallationId: String? = null

        @Throws(IOException::class)
        fun get(): String =
            cachedInstallationId ?: synchronized(lock) {
                cachedInstallationId ?: loadInstallationId().also { installationId ->
                    cachedInstallationId = installationId
                }
            }

        private fun loadInstallationId(): String =
            try {
                runBlocking { readOrCreateInstallationId() }
            } catch (exception: IOException) {
                throw IOException(INSTALLATION_ID_FAILURE_MESSAGE, exception)
            } catch (exception: CancellationException) {
                throw IOException(INSTALLATION_ID_FAILURE_MESSAGE, exception)
            }

        private suspend fun readOrCreateInstallationId(): String {
            val storedValue = installationIdStore.get()
            val normalizedValue = storedValue?.toNormalizedUuidOrNull()

            if (normalizedValue != null) {
                if (storedValue != normalizedValue) {
                    installationIdStore.set(normalizedValue)
                }
                return normalizedValue
            }

            return UUID.randomUUID().toString().also { generatedValue ->
                installationIdStore.set(generatedValue)
            }
        }

        private fun String.toNormalizedUuidOrNull(): String? {
            val parsedValue =
                try {
                    UUID.fromString(this)
                } catch (_: IllegalArgumentException) {
                    return null
                }
            val normalizedValue = parsedValue.toString()
            return normalizedValue.takeIf { it.equals(this, ignoreCase = true) }
        }

        private companion object {
            const val INSTALLATION_ID_FAILURE_MESSAGE = "설치 식별자를 제공하지 못했습니다."
        }
    }
