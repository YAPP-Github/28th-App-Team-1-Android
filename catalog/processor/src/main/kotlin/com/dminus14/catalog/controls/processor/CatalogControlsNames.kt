package com.dminus14.catalog.controls.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

/**
 * processor가 탐색하거나 generated source에서 참조하는 정규 이름을 한곳에서 관리한다.
 *
 * processor 모듈은 Compose와 `:catalog` Runtime에 직접 의존하지 않는다. 따라서 해당 API를 일반
 * import로 참조하는 대신 KotlinPoet의 [ClassName]과 [MemberName]으로 표현한다. Runtime 함수명을
 * 변경할 때는 이 계약과 Runtime 구현, compile-test fixture를 같은 변경 단위에서 갱신해야 한다.
 */
internal object CatalogControlsNames {
    const val CATALOG_CONTROLS_ANNOTATION = "com.dminus14.catalog.controls.CatalogControls"
    const val COMPOSABLE_ANNOTATION = "androidx.compose.runtime.Composable"

    val composable = ClassName("androidx.compose.runtime", "Composable")
    val remember = MemberName("androidx.compose.runtime", "remember")
    val mutableStateOf = MemberName("androidx.compose.runtime", "mutableStateOf")

    val controlledStoryLayout =
        MemberName("catalog.controls.runtime", "CatalogControlledStoryLayout")
    val textControl = MemberName("catalog.controls.runtime", "CatalogTextControl")
    val booleanControl = MemberName("catalog.controls.runtime", "CatalogBooleanControl")
    val numberControl = MemberName("catalog.controls.runtime", "CatalogNumberControl")
    val enumControl = MemberName("catalog.controls.runtime", "CatalogEnumControl")
    val controlsError = MemberName("catalog.controls.runtime", "CatalogControlsError")
    val previewUnavailable =
        MemberName("catalog.controls.runtime", "CatalogPreviewUnavailable")
}
