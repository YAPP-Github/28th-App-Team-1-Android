package catalog.controls.runtime

internal data class CatalogNumberParseResult<T : Number>(
    val value: T?,
    val errorMessage: String?,
)

internal fun parseCatalogByte(rawValue: String): CatalogNumberParseResult<Byte> =
    rawValue.toByteOrNull().toCatalogNumberParseResult("Byte")

internal fun parseCatalogShort(rawValue: String): CatalogNumberParseResult<Short> =
    rawValue.toShortOrNull().toCatalogNumberParseResult("Short")

internal fun parseCatalogInt(rawValue: String): CatalogNumberParseResult<Int> =
    rawValue.toIntOrNull().toCatalogNumberParseResult("Int")

internal fun parseCatalogLong(rawValue: String): CatalogNumberParseResult<Long> =
    rawValue.toLongOrNull().toCatalogNumberParseResult("Long")

internal fun parseCatalogFloat(rawValue: String): CatalogNumberParseResult<Float> =
    rawValue
        .toFloatOrNull()
        ?.takeIf(Float::isFinite)
        .toCatalogNumberParseResult("Float")

internal fun parseCatalogDouble(rawValue: String): CatalogNumberParseResult<Double> =
    rawValue
        .toDoubleOrNull()
        ?.takeIf(Double::isFinite)
        .toCatalogNumberParseResult("Double")

private fun <T : Number> T?.toCatalogNumberParseResult(
    typeName: String,
): CatalogNumberParseResult<T> =
    this?.let {
        CatalogNumberParseResult(
            value = it,
            errorMessage = null,
        )
    } ?: CatalogNumberParseResult(
        value = null,
        errorMessage = "유효한 $typeName 값을 입력해 주세요.",
    )
