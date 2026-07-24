package com.dminus14.app.data.remote.installation

import com.dminus14.app.data.local.installation.InstallationIdStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class InstallationIdProviderTest {
    @Test
    fun `저장된 UUID를 표준 형식으로 정규화해 재사용한다`() {
        val storedValue = "123E4567-E89B-12D3-A456-426614174000"
        val store = FakeInstallationIdStore(initialValue = storedValue)

        val actual = InstallationIdProvider(store).get()

        assertEquals(storedValue.lowercase(), actual)
        assertEquals(storedValue.lowercase(), store.value)
        assertEquals(1, store.getCount)
        assertEquals(1, store.setCount)
    }

    @Test
    fun `저장값이 없으면 UUID를 생성해 저장한다`() {
        val store = FakeInstallationIdStore()

        val actual = InstallationIdProvider(store).get()

        assertEquals(UUID.fromString(actual).toString(), actual)
        assertEquals(actual, store.value)
        assertEquals(1, store.setCount)
    }

    @Test
    fun `잘못된 저장값은 새 UUID로 교체한다`() {
        val invalidValue = "invalid-installation-id"
        val store = FakeInstallationIdStore(initialValue = invalidValue)

        val actual = InstallationIdProvider(store).get()

        assertNotEquals(invalidValue, actual)
        assertEquals(UUID.fromString(actual).toString(), actual)
        assertEquals(actual, store.value)
    }

    @Test
    fun `반복 호출은 메모리 캐시를 사용한다`() {
        val storedValue = "123e4567-e89b-12d3-a456-426614174000"
        val store = FakeInstallationIdStore(initialValue = storedValue)
        val provider = InstallationIdProvider(store)

        assertEquals(storedValue, provider.get())
        assertEquals(storedValue, provider.get())
        assertEquals(1, store.getCount)
        assertEquals(0, store.setCount)
    }

    @Test
    fun `동시 최초 호출에도 UUID를 한 번만 생성해 저장한다`() {
        val store = FakeInstallationIdStore()
        val provider = InstallationIdProvider(store)
        val executor = Executors.newFixedThreadPool(8)

        try {
            val results =
                executor
                    .invokeAll(
                        List(20) {
                            Callable { provider.get() }
                        },
                    ).map { future -> future.get() }

            assertEquals(1, results.toSet().size)
            assertEquals(1, store.getCount)
            assertEquals(1, store.setCount)
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun `저장소 실패는 캐시하지 않고 다음 호출에서 다시 시도한다`() {
        val store = FakeInstallationIdStore(getFailure = IOException("synthetic failure"))
        val provider = InstallationIdProvider(store)

        val firstFailure = assertThrows(IOException::class.java) { provider.get() }
        val secondFailure = assertThrows(IOException::class.java) { provider.get() }

        assertEquals("설치 식별자를 제공하지 못했습니다.", firstFailure.message)
        assertEquals("설치 식별자를 제공하지 못했습니다.", secondFailure.message)
        assertEquals(2, store.getCount)
        assertTrue(firstFailure.message?.contains("synthetic") == false)
    }

    @Test
    fun `UUID 저장 실패도 캐시하지 않고 다음 호출에서 다시 시도한다`() {
        val store = FakeInstallationIdStore(setFailure = IOException("synthetic failure"))
        val provider = InstallationIdProvider(store)

        assertThrows(IOException::class.java) { provider.get() }
        assertThrows(IOException::class.java) { provider.get() }

        assertEquals(2, store.getCount)
        assertEquals(2, store.setCount)
        assertNull(store.value)
    }
}

internal class FakeInstallationIdStore(
    initialValue: String? = null,
    private val getFailure: Exception? = null,
    private val setFailure: Exception? = null,
) : InstallationIdStore {
    private val lock = Any()

    var value: String? = initialValue
        private set

    var getCount: Int = 0
        private set

    var setCount: Int = 0
        private set

    override suspend fun get(): String? =
        synchronized(lock) {
            getCount += 1
            getFailure?.let { throw it }
            value
        }

    override suspend fun set(value: String) {
        synchronized(lock) {
            setCount += 1
            setFailure?.let { throw it }
            this.value = value
        }
    }
}
