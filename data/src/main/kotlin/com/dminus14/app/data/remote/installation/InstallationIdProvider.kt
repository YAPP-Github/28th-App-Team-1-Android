package com.dminus14.app.data.remote.installation

import com.dminus14.app.data.local.installation.InstallationIdStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

/**
 * 비회원 참여자를 가능한 한 안정적으로 구분하기 위한 UUID를 제공한다.
 *
 * 기존 이름은 호환성을 위해 유지한다. 값은 앱 전용 DataStore에 저장되며 Android 기본 cloud
 * backup과 device transfer로 복원될 수 있으므로 단일 설치 수명에만 한정되지 않는다. 복원된
 * 유효한 UUID가 없을 때만 새 값을 생성한다.
 */
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
                runBlocking {
                    withTimeout(INSTALLATION_ID_TIMEOUT_MS.milliseconds) {
                        readOrCreateInstallationId()
                    }
                }
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
            const val INSTALLATION_ID_TIMEOUT_MS = 3_000L
        }
    }
