package com.dminus14.catalog.controls.processor

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName

/**
 * 하나의 Catalog Controls 어댑터에서 코드 생성에 필요한 정보를 정규화한 모델이다.
 *
 * @property packageName 어댑터와 generated source가 위치할 package
 * @property simpleName package를 제외한 어댑터 함수명
 * @property parameters 선언 순서를 유지한 조작 가능 매개변수 목록
 * @property originatingFile isolating KSP output에 연결할 원본 소스 파일
 */
internal data class AdapterModel(
    val packageName: String,
    val simpleName: String,
    val parameters: List<ParameterModel>,
    val originatingFile: KSFile,
) {
    /** 생성할 타입 안전 Args data class 이름이다. */
    val argsName: String = "${simpleName}Args"

    /** 생성할 Controls Composable 함수 이름이다. */
    val controlsName: String = "${simpleName}Controls"
}

/**
 * 어댑터 매개변수 하나의 이름, Kotlin 타입과 렌더링할 Control 종류를 묶는다.
 *
 * @property name label, Args 프로퍼티와 named argument에 그대로 사용할 매개변수명
 * @property typeName KotlinPoet이 generated source에 기록할 구체 Kotlin 타입
 * @property kind 해당 타입에 대응하는 Control 생성 정책
 */
internal data class ParameterModel(
    val name: String,
    val typeName: TypeName,
    val kind: ParameterKind,
)

/** 지원 타입을 어떤 Catalog Runtime Control로 생성할지 나타내는 닫힌 분류 체계다. */
internal sealed interface ParameterKind {
    /** `String`을 single-line text Control로 생성한다. */
    data object StringKind : ParameterKind

    /** `Boolean`을 Switch Control로 생성한다. */
    data object BooleanKind : ParameterKind

    /** 숫자 타입을 원문과 마지막 유효 값을 분리하는 number Control로 생성한다. */
    data class NumberKind(
        val type: NumericType,
    ) : ParameterKind

    /**
     * enum을 단일 선택 Dropdown Control로 생성한다.
     *
     * @property entries 선언 순서를 유지한 enum 항목명 목록
     */
    data class EnumKind(
        val entries: List<String>,
    ) : ParameterKind
}

/**
 * 초기 지원 숫자 타입과 Catalog Runtime parser 함수의 대응을 정의한다.
 *
 * @property qualifiedName KSP type declaration과 비교할 Kotlin 정규 이름
 * @property parser generated Controls가 호출할 Runtime parser
 *
 * 각 enum entry 이름은 generated source가 참조하는 Runtime `CatalogControlType`의 숫자 entry와
 * 동일해야 한다.
 */
internal enum class NumericType(
    val qualifiedName: String,
    val parser: MemberName,
) {
    BYTE("kotlin.Byte", MemberName("catalog.controls.runtime", "parseCatalogByte")),
    SHORT("kotlin.Short", MemberName("catalog.controls.runtime", "parseCatalogShort")),
    INT("kotlin.Int", MemberName("catalog.controls.runtime", "parseCatalogInt")),
    LONG("kotlin.Long", MemberName("catalog.controls.runtime", "parseCatalogLong")),
    FLOAT("kotlin.Float", MemberName("catalog.controls.runtime", "parseCatalogFloat")),
    DOUBLE("kotlin.Double", MemberName("catalog.controls.runtime", "parseCatalogDouble")),
    ;

    /** 초기 Args에서도 NaN과 무한대를 차단해야 하는 부동 소수점 타입인지 나타낸다. */
    val requiresFiniteInitialValue: Boolean
        get() = this == FLOAT || this == DOUBLE

    companion object {
        /** [qualifiedName]과 정확히 일치하는 지원 숫자 타입을 반환한다. */
        fun fromQualifiedName(qualifiedName: String): NumericType? =
            entries.firstOrNull { it.qualifiedName == qualifiedName }
    }
}
