package com.dminus14.catalog.controls

/**
 * Catalog Story에서 편집할 매개변수를 기준으로 타입 안전한 Args와 Controls를 생성하도록 표시한다.
 *
 * 이 애너테이션은 `designsystem`의 실제 Composable이 아니라 `catalog` 모듈이 소유하는 Story
 * 어댑터 Composable에 사용한다. 어댑터는 사용자가 조작할 값만 매개변수로 노출하고 callback,
 * `Modifier`처럼 고정할 값은 함수 본문에서 실제 Composable에 전달해야 한다.
 *
 * 사용 예시는 다음과 같다.
 *
 * ```kotlin
 * @CatalogControls
 * @Composable
 * internal fun SampleButtonCatalogAdapter(
 *     text: String,
 *     enabled: Boolean,
 * ) {
 *     SampleButton(
 *         text = text,
 *         enabled = enabled,
 *         onClick = {},
 *     )
 * }
 * ```
 *
 * 위 선언에서는 같은 package에 `SampleButtonCatalogAdapterArgs` data class와
 * `SampleButtonCatalogAdapterControls(initialArgs)` Composable이 생성된다. Story 메타데이터와
 * 초기 Args, Registry 등록은 생성되지 않으므로 Story 작성자가 직접 정의해야 한다.
 *
 * 초기 지원 타입은 non-null `String`, `Boolean`, `Byte`, `Short`, `Int`, `Long`, `Float`,
 * `Double`과 항목이 하나 이상인 enum이다. 어댑터는 non-local top-level `internal` 함수여야 하며
 * `@Composable`과 함께 선언하고 `Unit`을 반환해야 한다. member, local, extension, generic,
 * overloaded 함수와 `vararg`는 지원하지 않으며 processor가 컴파일 오류를 보고한다.
 *
 * 이 애너테이션은 source retention이므로 런타임 동작이나 리플렉션 메타데이터를 제공하지 않는다.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class CatalogControls
