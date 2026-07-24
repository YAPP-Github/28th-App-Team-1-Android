package catalog.controls.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CatalogNumberParserTest {
    @Test
    fun `정수 경계값을 변환한다`() {
        assertEquals(Byte.MIN_VALUE, parseCatalogByte(Byte.MIN_VALUE.toString()).value)
        assertEquals(Byte.MAX_VALUE, parseCatalogByte(Byte.MAX_VALUE.toString()).value)
        assertEquals(Short.MIN_VALUE, parseCatalogShort(Short.MIN_VALUE.toString()).value)
        assertEquals(Short.MAX_VALUE, parseCatalogShort(Short.MAX_VALUE.toString()).value)
        assertEquals(Int.MIN_VALUE, parseCatalogInt(Int.MIN_VALUE.toString()).value)
        assertEquals(Int.MAX_VALUE, parseCatalogInt(Int.MAX_VALUE.toString()).value)
        assertEquals(Long.MIN_VALUE, parseCatalogLong(Long.MIN_VALUE.toString()).value)
        assertEquals(Long.MAX_VALUE, parseCatalogLong(Long.MAX_VALUE.toString()).value)
    }

    @Test
    fun `중간 입력과 범위를 벗어난 정수를 거부한다`() {
        listOf("", "+", "-").forEach { rawValue ->
            assertNull(parseCatalogInt(rawValue).value)
        }
        assertNull(parseCatalogByte("128").value)
        assertNull(parseCatalogByte("-129").value)
        assertNull(parseCatalogShort("32768").value)
        assertNull(parseCatalogInt("2147483648").value)
        assertNull(parseCatalogLong("9223372036854775808").value)
    }

    @Test
    fun `유한한 부동 소수점 입력을 변환한다`() {
        assertEquals(1.25f, parseCatalogFloat("1.25").value)
        assertEquals(-3.5, parseCatalogDouble("-3.5").value)
    }

    @Test
    fun `유한하지 않거나 잘못된 부동 소수점 입력을 거부한다`() {
        listOf("", "+", "-", "NaN", "Infinity", "-Infinity", "value").forEach { rawValue ->
            assertNull(parseCatalogFloat(rawValue).value)
            assertNull(parseCatalogDouble(rawValue).value)
        }
    }
}
