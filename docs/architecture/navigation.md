# Navigation 3

이 문서는 D-14의 Navigation 3 소유권과 route, entry, back stack 조립 계약을 정의한다.
[`module-system.md`](module-system.md)와 [`feature-ui.md`](feature-ui.md)를 함께 적용한다.

## 1. 소유권

- 앱 최상위 Navigation 3 조립은 `app`이 담당한다.
- 별도 `:navigation` 또는 `:feature:navigator` Gradle 모듈을 만들지 않는다.
- `Navigator`라는 이름의 back stack helper는 `app`의 navigation package에 둘 수 있다.
- Feature는 자신이 제공하는 route 또는 entry 계약을 정의해 상위 앱 계층에 제공한다.
- Feature는 app-level navigation assembly를 수행하지 않는다.
- ViewModel은 Navigation API나 `Navigator`를 직접 호출하지 않는다.

## 2. 계층별 책임

| 계층 | 책임 |
|---|---|
| ViewModel | Feature 책임에 해당하는 Navigation Effect 발행 |
| Screen | Effect를 수집하고 상위 callback을 호출 |
| `app` | callback을 route 결정과 `Navigator` 호출에 연결 |
| `Navigator` | back stack 변경 |
| `NavDisplay` | 현재 destination Composable 렌더링 |

Screen은 Effect를 수집해 상위 callback만 호출하고 실제 이동 목적지는 `app` 계층에서
결정한다. ViewModel, Contract와 Screen은 다른 Feature의 화면 이름이나 route 정책을 알 필요가
없다.

## 3. `app` 구성 요소

| 구성 요소 | 위치 | 책임 |
|---|---|---|
| `Navigator` | `app/.../navigation/Navigator.kt` | `SnapshotStateList` 기반 back stack과 `goTo`, `goBack` 관리 |
| `AppNavigationState` | `app/.../navigation/AppNavigationState.kt` | `Navigator`와 entry installer 집합을 `ActivityRetainedScoped`로 제공 |
| `EntryProviderInstaller` | `app/.../navigation/EntryProviderInstaller.kt` | `EntryProviderScope<Any>.() -> Unit` 계약 |
| `NavigatorModule` | `app/.../navigation/di/NavigatorModule.kt` | 시작 destination 제공 |
| 앱 Root 또는 `MainActivity` | `app` | `NavDisplay`, `entryProvider`, back 처리 조립 |

Application과 Activity 같은 Manifest entry 등록도 `app`에서 일원화한다. Feature가 독립적으로
Manifest entry를 추가하는 방식은 피한다.

### 3.1 Navigation 상태 수명

`AppNavigationState`는 `Navigator`와 Feature entry installer 집합을
`ActivityRetainedScoped` 수명으로 제공한다. 이 scope는 Activity 재생성 사이에서 앱 Navigation
상태와 조립 정보를 유지하기 위한 계약이다.

이 수명은 process 종료 후 back stack이나 entry 조립 정보를 영속 복원한다고 보장하지 않는다.
Process 종료 이후의 복원 정책으로 확대 해석하지 않는다.

## 4. Feature route와 entry

다른 모듈에 route를 공개해야 하면 Feature를 `api`와 `impl`로 나눈다.

| 모듈 | 책임 |
|---|---|
| `feature:{name}:api` | route key와 args 같은 Navigation 계약 |
| `feature:{name}:impl` | Screen, ViewModel, entry builder와 Hilt binding |

Feature `impl`은 다른 Feature의 `api`만 참조할 수 있다. 다른 Feature의 `impl`, Screen,
ViewModel과 entry builder를 직접 참조하지 않는다.

모든 `:feature:*:api` 모듈은 `dminus14.jvm.feature-api` Convention Plugin을 적용한다. API
모듈에 정의하는 모든 route는 Kotlin Serialization의 `@Serializable`을 적용하고 Navigation
3의 `NavKey`를 구현해야 한다.

Feature entry builder는 route key와 Composable entry를 연결한다.

```kotlin
@Serializable
data object SampleRoute : NavKey

fun EntryProviderScope<Any>.sampleEntryBuilder() {
    entry<SampleRoute> {
        SampleScreen(...)
    }
}
```

Hilt multibinding은 Feature entry installer를 상위에 제공한다.

```kotlin
@IntoSet
@Provides
fun provideSampleEntryInstaller(): EntryProviderScope<Any>.() -> Unit = {
    sampleEntryBuilder()
}
```

예시는 핵심 계약만 나타내며 완전한 컴파일 단위를 의도하지 않는다.

## 5. 앱 루트 조립

`app`은 주입된 entry installer를 `entryProvider`에 설치하고 `Navigator` back stack을
`NavDisplay`에 연결한다.

```kotlin
HilitTheme {
    NavDisplay(
        backStack = navigationState.navigator.backStack,
        onBack = navigationState.navigator::goBack,
        entryProvider = entryProvider {
            navigationState.entryInstallers.forEach { installer -> installer() }
        },
    )
}
```

앱 Root Composable을 도입하면 Navigation 조립과 전역 UI event rendering을 `app`의 해당
Root에 모은다.

## 6. Effect 책임 경계

Intent와 Effect는 해당 Feature 안에서 일어난 일을 Feature의 용어로 표현한다.

```kotlin
// 지양: 다른 Feature 목적지를 직접 규정한다.
data object NavigateToHome : LoginEffect

// 권장: 현재 Feature의 결과를 표현한다.
data object LoginSucceeded : LoginEffect
```

- 다른 Feature 또는 화면 이름을 Effect에 직접 노출하지 않는다.
- Screen은 `LoginSucceeded`를 상위 callback으로 전달하고 `app` 계층이 실제 route로 해석한다.
- 임시 bootstrap 구현이 이 원칙과 다르더라도 승인된 목표 구조는 이 계약을 따른다.

## 7. 금지 사항

- ViewModel에서 `Navigator`, `NavDisplay` 또는 Android Navigation API 직접 호출
- Feature에서 app-level `NavDisplay` 또는 전체 entry set 조립
- Feature가 `app`에 의존해 `Navigator`를 가져오는 구조
- Feature `impl` 간 Navigation 구현 공유
- 별도 Navigation Gradle 모듈 도입
