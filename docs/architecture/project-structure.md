# Project Structure

이 문서는 현재 Gradle module 상태와 승인된 목표 구조를 구분하고, 목표 구조의 주요 파일과
directory ownership을 정의한다. 모듈 책임과 의존성은 [`module-system.md`](module-system.md),
Navigation 상태와 동작은 [`navigation.md`](navigation.md), Convention Plugin 정책은
[`build-conventions.md`](build-conventions.md)를 기준으로 해석한다.

## 1. 해석 원칙

- 현재 상태와 목표 구조를 동일하게 해석하지 않는다.
- 목표 구조는 승인된 Architecture를 나타낸다.
- repository가 bootstrap 또는 staged implementation 상태여서 일부 목표가 아직 구현되지 않아도
  그 사실만으로 문서 불일치로 판단하지 않는다.
- 현재 구현을 이유로 목표 Architecture boundary를 약화하지 않는다.
- 새 module이나 기능을 도입할 때 Constitution과 관련 Architecture 계약을 충족한다.
- 현재에만 존재하고 목표 구조에서 별도 책임이 정의되지 않은 module에는 이 문서가 새로운
  책임을 추정해 부여하지 않는다.
- 민감 데이터 기능은 정책이 완전하게 정의되기 전까지 구현을 중단한다.

## 2. 현재 Gradle module 상태

현재 `settings.gradle.kts`에는 다음 module이 포함되어 있다.

```text
:app
:domain
:data
:feature:main:api
:feature:main:impl
:feature:login:api
:feature:login:impl
:feature:feedback:api
:feature:feedback:impl
:core:common
:core:resources
:core:permission
:core:crypto
:designsystem
:catalog
:catalog:annotations
:catalog:processor
```

이 목록은 현재 build 포함 상태만 설명하며 각 module의 목표 책임을 새로 정의하지 않는다. 현재
상태가 바뀌면 `settings.gradle.kts`를 기준으로 갱신한다.

## 3. 승인된 목표 구조

아래 tree는 주요 파일과 directory의 목표 ownership을 보여주는 개념적 구조다. 실제 package와
Feature 이름은 프로젝트 확정값을 사용한다. 경로에 표시된 책임보다 상세한 정책은 각 주제
문서가 소유한다.

```text
android-project/
├── build-logic/                                   # Gradle Convention Plugin
│   ├── build.gradle.kts                           # Plugin 등록과 build-logic dependency
│   └── src/main/kotlin/.../
│       ├── convention/
│       │   ├── base/                              # Platform과 compiler default
│       │   │   ├── AndroidApplicationConventionPlugin.kt
│       │   │   ├── AndroidLibraryConventionPlugin.kt
│       │   │   ├── JvmLibraryConventionPlugin.kt
│       │   │   ├── KotlinMultiplatformLibraryConventionPlugin.kt
│       │   │   ├── ComposeMultiplatformConventionPlugin.kt
│       │   │   ├── ComposeMultiplatformLibraryConventionPlugin.kt
│       │   │   ├── ComposeMultiplatformUiLibraryConventionPlugin.kt
│       │   │   └── ComposeMultiplatformWasmApplicationConventionPlugin.kt
│       │   ├── capability/                        # 선택 가능한 단일 build capability
│       │   │   ├── AndroidComposeConventionPlugin.kt
│       │   │   ├── ComposePreviewConventionPlugin.kt
│       │   │   ├── ComposeResourcesConventionPlugin.kt
│       │   │   ├── AndroidHiltConventionPlugin.kt
│       │   │   ├── AndroidNavigation3ConventionPlugin.kt
│       │   │   ├── KotlinNavigationRouteConventionPlugin.kt
│       │   │   ├── AndroidTestConventionPlugin.kt
│       │   │   ├── AndroidComposeTestConventionPlugin.kt
│       │   │   ├── AndroidRoomConventionPlugin.kt
│       │   │   ├── AndroidNetworkConventionPlugin.kt
│       │   │   └── AndroidDataStoreConventionPlugin.kt
│       │   ├── composite/                         # Child Plugin 조합
│       │   │   ├── AndroidFeatureConventionPlugin.kt
│       │   │   └── JvmFeatureApiConventionPlugin.kt
│       │   └── quality/                           # Quality leaf와 bundle
│       │       ├── SpotlessConventionPlugin.kt
│       │       ├── DetektConventionPlugin.kt
│       │       ├── KotlinQualityConventionPlugin.kt
│       │       ├── AndroidLintConventionPlugin.kt
│       │       ├── AndroidComposeLintConventionPlugin.kt
│       │       └── AndroidQualityConventionPlugin.kt
│       └── extension/
│           ├── BuildConfig.kt                     # SDK, JVM과 app version 단일 기준
│           ├── Application.kt                     # app identifier와 배포 설정
│           ├── KotlinAndroid.kt                   # Android/JVM compiler 설정
│           ├── KotlinMultiplatform.kt             # Android/Wasm target 설정
│           ├── Compose.kt                         # Android Compose 제품 UI 설정
│           ├── ComposeMultiplatform.kt            # CMP UI dependency 설정
│           ├── ComposePreview.kt                  # Android/CMP Preview 설정
│           ├── ComposeResources.kt                # CMP shared resource 연결
│           └── ProjectExtensions.kt               # Version Catalog과 Plugin ID 접근
├── gradle/
│   └── libs.versions.toml                         # Version Catalog
├── app/                                           # Android entry와 composition root
│   └── src/main/
│       ├── AndroidManifest.xml                    # Application과 Activity 등록
│       └── java/.../
│           ├── DMinus14App.kt                     # @HiltAndroidApp Application
│           ├── MainActivity.kt                    # Single Activity와 NavDisplay 조립
│           └── navigation/
│               ├── Navigator.kt                   # Back stack 관리
│               ├── AppNavigationState.kt          # Navigator와 entry installer 집합
│               ├── EntryProviderInstaller.kt      # Entry 등록 typealias
│               └── di/
│                   └── NavigatorModule.kt          # 시작 destination 제공
├── core/
│   ├── common/                                    # 최소 공통 플랫폼 독립 계약
│   │   └── src/main/kotlin/.../
│   │       ├── model/                             # 공통 model
│   │       ├── extension/                         # 공통 extension
│   │       ├── mvi/
│   │       │   ├── MviContract.kt                 # 공통 MVI Contract
│   │       │   └── MviViewModel.kt                # State/Effect 기반 ViewModel
│   │       ├── event/
│   │       │   ├── GlobalAppEvent.kt              # 전역 UI event
│   │       │   └── GlobalErrorHandler.kt          # 전역 error event hub
│   │       ├── error/                             # 공통 Error와 Exception
│   │       ├── util/                              # 실제 공유되는 util
│   │       └── navigation/
│   │           └── NavKey.kt                      # 공통 route/key 계약
│   └── resources/                                 # Android와 Web/WASM 공용 CMP resource
│       └── src/commonMain/composeResources/
│           ├── drawable/                          # 공용 SVG drawable
│           ├── font/                              # 공용 product font
│           └── values/                            # 공용 string 등 value resource
├── designsystem/                                  # CMP 공용 UI와 Theme
│   └── src/commonMain/kotlin/.../
│       ├── component/                             # 공용 Compose component
│       └── theme/
│           ├── Color.kt                           # Product color token
│           ├── Typography.kt                      # Product text style
│           ├── Shape.kt                           # Shape primitive
│           └── Theme.kt                           # HilitTheme
├── catalog/                                       # Web/WASM Design System Catalog
│   ├── annotations/
│   │   └── src/                                   # @CatalogControls 계약
│   ├── processor/
│   │   └── src/main/
│   │       ├── kotlin/                            # KSP processor와 code generation
│   │       └── resources/META-INF/services/       # SymbolProcessorProvider 등록
│   └── src/
│       ├── commonMain/kotlin/.../
│       │   ├── story/                             # 공통 Story type과 group
│       │   └── component/                         # Catalog 전용 UI
│       └── wasmJsMain/
│           ├── kotlin/.../                        # Catalog shell과 Story Registry
│           └── kotlin/.../Main.kt                 # Web/WASM entry point
├── domain/                                        # 순수 Kotlin business rule
│   └── src/main/kotlin/.../
│       ├── model/                                 # Domain Entity와 Model
│       ├── repository/                            # Repository Interface
│       └── usecase/                               # UseCase
├── data/                                          # Repository와 data access 구현
│   └── src/main/kotlin/.../
│       ├── di/                                    # Data-layer Hilt Module
│       ├── remote/
│       │   ├── api/                               # Retrofit API Interface
│       │   ├── dto/                               # Request/Response DTO
│       │   └── datasource/                        # RemoteDataSource
│       ├── local/
│       │   ├── model/                             # Room Entity 또는 local model
│       │   ├── dao/                               # Room DAO
│       │   ├── datasource/                        # Room/DataStore abstraction
│       │   └── AppDatabase.kt                     # Room Database
│       └── repository/                            # Repository implementation
└── feature/                                       # 화면 단위 MVI Feature
    └── {featureName}/
        ├── api/                                   # 선택적 route/args 공개 계약
        │   └── src/main/kotlin/.../api/
        │       └── {Feature}Route.kt
        └── impl/                                  # UI와 Feature implementation
            └── src/main/kotlin/.../
                ├── component/                     # Feature 전용 UI
                ├── extension/                     # Feature 전용 extension
                ├── navigation/                    # Entry builder
                ├── di/                            # Hilt binding
                ├── {Feature}Contract.kt           # Intent, State와 Effect
                ├── {Feature}ViewModel.kt          # Intent 처리와 State/Effect
                └── {Feature}Screen.kt             # Screen과 Content
```

## 4. 구조별 상세 문서

목표 tree는 file ownership을 설명하고 다음 정책은 별도 상세 문서가 단일 원본으로 소유한다.

| 영역 | 상세 정책 문서 |
|---|---|
| Module 책임과 dependency | [`module-system.md`](module-system.md) |
| Navigation 조립과 상태 수명 | [`navigation.md`](navigation.md) |
| MVI와 Feature UI | [`feature-ui.md`](feature-ui.md) |
| Design System과 resource | [`design-system.md`](design-system.md) |
| Catalog와 Controls | [`catalog.md`](catalog.md) |
| Error 처리 | [`error-handling.md`](error-handling.md) |
| Global Modal | [`global-modal.md`](global-modal.md) |
| Convention Plugin | [`build-conventions.md`](build-conventions.md) |

`AppNavigationState`가 사용하는 `ActivityRetainedScoped` 수명 계약은
[`navigation.md`](navigation.md#31-navigation-상태-수명)가 소유한다. 이 문서는 해당 파일이
Navigation 상태와 entry installer 집합을 소유한다는 구조만 보여준다.

## 5. Feature 분리 기준

Feature route나 args 계약을 다른 module에 공개할 필요가 없으면 반드시 `api`와 `impl`로 나눌
필요는 없다. 분리하는 경우 `api`는 route와 args 계약만 소유하고 `impl`은 Screen, ViewModel,
entry builder와 DI 같은 implementation을 소유한다.
