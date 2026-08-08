# Build Convention

이 문서는 Gradle Convention Plugin의 책임과 모듈별 capability 적용 계약을 정의한다. 모듈
경계는 [`module-system.md`](module-system.md)를 함께 따른다.

## 1. 공통 원칙

- 공유 build configuration은 `build-logic`의 Convention Plugin으로 관리한다.
- Base Plugin은 platform과 compiler default만 소유한다.
- Capability Plugin은 Compose, Preview, resources, Hilt, Navigation과 testing 같은 한 가지
  기능을 소유한다.
- Quality leaf Plugin은 한 도구만 소유하고 bundle Plugin은 leaf Plugin 조합과 task ordering만
  담당한다.
- Composite Plugin은 child Plugin을 조합하고 child가 소유한 DSL이나 dependency를 반복하지
  않는다.
- 같은 설정이 다른 모듈에서 반복되기 전에는 성급하게 capability Plugin으로 추출하지 않는다.
- 편의를 위해 architecture boundary를 약화하는 dependency를 추가하지 않는다.

## 2. Plugin 책임

| 분류 | Plugin ID | 책임 및 적용 대상 |
|---|---|---|
| Base | `dminus14.android.application` | `:app` Application, SDK, JVM과 배포 설정 |
| Base | `dminus14.android.library` | Android Library, SDK와 JVM 설정 |
| Base | `dminus14.jvm.library` | 순수 Kotlin/JVM Library 설정 |
| Base | `dminus14.kotlin.multiplatform.library` | Compose 없는 JVM/Wasm Kotlin Multiplatform Library |
| Base | `dminus14.compose.multiplatform` | Kotlin Multiplatform와 Compose compiler 기반 |
| Base | `dminus14.compose.multiplatform.library` | Android/Wasm CMP Library target |
| Base | `dminus14.compose.multiplatform.ui-library` | `designsystem` common UI 환경 |
| Base | `dminus14.compose.multiplatform.wasm-application` | `catalog` executable Wasm UI 환경 |
| Composite | `dminus14.android.feature` | 모든 `feature:*:impl`의 표준 capability 조합 |
| Composite | `dminus14.jvm.feature-api` | 모든 `feature:*:api`의 JVM, route와 quality capability 조합 |
| Capability | `dminus14.android.compose` | Android Compose 제품 UI |
| Capability | `dminus14.compose.preview` | Android/CMP Preview annotation과 tooling |
| Capability | `dminus14.compose.resources` | 허용된 UI consumer의 `core:resources` 의존 |
| Capability | `dminus14.android.hilt` | Hilt, KSP, runtime과 compiler |
| Capability | `dminus14.android.navigation3` | Navigation 3 Android dependency |
| Capability | `dminus14.kotlin.navigation-route` | route 계약용 Kotlin Serialization Plugin과 Navigation 3 runtime dependency |
| Capability | `dminus14.android.test` | Android 기본 unit/instrumentation test stack |
| Capability | `dminus14.android.compose.test` | Android Compose UI test stack |
| Capability | `dminus14.android.room` | Room과 KSP dependency |
| Capability | `dminus14.android.network` | Retrofit, Gson converter와 logging interceptor |
| Capability | `dminus14.android.datastore` | Preferences DataStore |
| Quality | `dminus14.spotless` | Kotlin과 Gradle Kotlin DSL formatting |
| Quality | `dminus14.detekt` | Kotlin static analysis |
| Quality | `dminus14.kotlin.quality` | Spotless와 Detekt 조합 |
| Quality | `dminus14.android.lint` | Android Lint |
| Quality | `dminus14.android.compose.lint` | Android Compose lint checks |
| Quality | `dminus14.android.quality` | Kotlin quality와 Android Lint 조합 |

모든 repository module은 platform에 맞는 Kotlin 또는 Android Quality Plugin을 적용한다.

## 3. Feature composite

`dminus14.android.feature`는 `feature:*:impl`에만 적용하는 표준 composite Plugin이다. Feature
implementation이 공통으로 요구하는 Android Library, Compose, Preview, shared resources,
Hilt, Navigation과 quality capability를 조합한다. Child Plugin이 소유하는 DSL과 dependency를
composite에서 다시 선언하지 않는다.

Feature `api` 모듈에는 이 composite를 적용하지 않는다.

### 3.1 Feature API composite

`dminus14.jvm.feature-api`는 모든 `:feature:*:api`에 적용하는 표준 composite Plugin이다. JVM
Library, Navigation route와 Kotlin quality capability를 조합한다. Route capability는 Kotlin
Serialization Plugin과 Navigation 3 runtime dependency를 제공한다.

각 `:feature:*:api` build script는 개별 Kotlin Serialization Plugin이나 Navigation 3 runtime
dependency를 반복 선언하지 않고 이 composite만 적용한다.

## 4. Preview capability

`dminus14.compose.preview`는 다음 모듈에만 적용한다.

- `:app`
- `:feature:*:impl`
- `:designsystem`

Preview는 ViewModel-free UI만 렌더링하며 Hilt, Lifecycle, Navigation, network, file access와
실제 사용자 데이터를 요구하지 않는다. `:catalog`, `:core:resources`와 platform launcher를
소유한 `:core:permission`에는 적용하지 않는다.

## 5. Shared resources capability

`dminus14.compose.resources`는 다음 모듈에만 적용한다.

- `:app`
- `:feature:*:impl`
- `:designsystem`

`:catalog`와 `:core:resources`에는 적용하지 않는다. `:catalog`는 Catalog 전용 리소스를 자체
소유하며 `:core:resources`에 직접 의존하지 않는다.

## 6. Test capability

- 일반 JUnit과 AndroidX test stack은 `dminus14.android.test`를 사용한다.
- Compose UI test가 있는 모듈만 `dminus14.android.compose.test`를 추가한다.
- Compose test Plugin은 Android Compose와 일반 Android test capability를 전제로 한다.

## 7. Kotlin Multiplatform

`dminus14.kotlin.multiplatform.library`는 Kotlin Multiplatform Plugin, JVM/Wasm library target과
공통 Kotlin compiler 설정만 소유한다. Compose compiler, Compose Multiplatform, Android/KMP
Android Plugin과 Compose dependency를 적용하지 않는다. `catalog:annotations` 같은 순수
플랫폼 독립 계약 모듈에 사용한다.

`dminus14.compose.multiplatform*` Plugin은 실제 Compose 환경을 구성하므로 Compose 책임과 이름을
유지한다.

## 8. Catalog KSP

Catalog Controls의 KSP Plugin 적용, `catalog:annotations` dependency와 `kspWasmJs` processor
연결은 현재 `catalog/build.gradle.kts`가 직접 소유한다. 동일 구성이 다른 모듈에 반복되기 전에는
별도 Catalog capability Plugin으로 추출하지 않는다.

`catalog:processor`의 `kotlinpoet-ksp`는 build-time source generation 전용이며 제품 app,
`designsystem` 또는 Catalog Wasm runtime에 포함하지 않는다.

## 9. 공통 Build 값

Android SDK, JVM과 application version 같은 공통 값은
`build-logic/.../extension/BuildConfig.kt`에서 관리한다. Convention Plugin과 extension은 이
값을 사용하며 개별 모듈에 중복 정의하지 않는다.

## 10. Dependency 정당화

architecture boundary, 사용자 데이터, media processing, AI integration, networking, logging,
analytics 또는 storage에 영향을 주는 dependency는 완료 보고에 도입 이유와 경계 준수 방식을
명시한다.
