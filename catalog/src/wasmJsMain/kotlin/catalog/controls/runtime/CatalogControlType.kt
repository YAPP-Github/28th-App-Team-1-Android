package catalog.controls.runtime

/**
 * Catalog Control이 디자이너에게 표시할 Kotlin 타입 이름과 설명을 정의한다.
 *
 * [displayName]은 Control 카드의 타입 본문에 사용하고, [description]은 도움말 Tooltip에
 * 사용한다. 숫자 타입 설명은 Catalog 입력기가 실제로 허용하는 유한한 값의 범위를 따른다.
 */
internal enum class CatalogControlType(
    val displayName: String,
    val description: String,
) {
    STRING(
        displayName = "String",
        description = "문자열입니다.",
    ),
    BOOLEAN(
        displayName = "Boolean",
        description = "참 또는 거짓으로만 표현되는 데이터입니다.",
    ),
    BYTE(
        displayName = "Byte",
        description = "정수 숫자로 표현되는 데이터입니다. 입력 가능한 범위는 -128부터 127까지입니다.",
    ),
    SHORT(
        displayName = "Short",
        description = "정수 숫자로 표현되는 데이터입니다. 입력 가능한 범위는 -32,768부터 32,767까지입니다.",
    ),
    INT(
        displayName = "Int",
        description =
            "정수 숫자로 표현되는 데이터입니다. " +
                "입력 가능한 범위는 -2,147,483,648부터 2,147,483,647까지입니다.",
    ),
    LONG(
        displayName = "Long",
        description =
            "정수 숫자로 표현되는 데이터입니다. " +
                "입력 가능한 범위는 -9,223,372,036,854,775,808부터 " +
                "9,223,372,036,854,775,807까지입니다.",
    ),
    FLOAT(
        displayName = "Float",
        description =
            "소수점을 포함할 수 있는 숫자로 표현되는 데이터입니다. " +
                "유한한 값의 범위는 약 -3.4028235 × 10³⁸부터 3.4028235 × 10³⁸까지입니다.",
    ),
    DOUBLE(
        displayName = "Double",
        description =
            "소수점을 포함할 수 있는 숫자로 표현되는 데이터입니다. " +
                "유한한 값의 범위는 약 -1.7976931348623157 × 10³⁰⁸부터 " +
                "1.7976931348623157 × 10³⁰⁸까지입니다.",
    ),
    ENUM(
        displayName = "Enum",
        description = "여러 선택지 중 하나를 고를 수 있는 데이터입니다.",
    ),
    ;

    /** Tooltip에 표시할 타입 이름과 설명을 하나의 문장으로 반환한다. */
    val tooltipText: String
        get() = "$displayName | $description"
}
