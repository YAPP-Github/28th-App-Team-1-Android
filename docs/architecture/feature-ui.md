# Feature UI와 MVI

이 문서는 Feature 화면의 MVI Contract, ViewModel, Screen, Content와 Preview 작성 계약을
정의한다. 모듈 배치는 [`module-system.md`](module-system.md), 화면 이동은
[`navigation.md`](navigation.md), 오류 분기는 [`error-handling.md`](error-handling.md)를 함께
따른다.

## 1. 화면 구성

각 화면은 다음 책임을 분리한다.

| 구성 요소 | 책임 |
|---|---|
| `Intent` | 사용자 액션 또는 UI lifecycle-triggered event |
| `State` | 화면에 지속적으로 표시되는 데이터 |
| `Effect` | 한 번만 소비되는 Navigation, Toast, Snackbar, Modal 요청 |
| `ViewModel` | Intent 처리, UseCase 호출, State 갱신, Effect 발행 |
| `Screen` | ViewModel 연결, State 구독, Intent 전달, Effect 수집 |
| `Content` | ViewModel이 없는 순수 UI 렌더링 |

## 2. Feature 파일 구조

route 계약을 외부에 공개하지 않는 단일 모듈 Feature의 기본 구조는 다음과 같다.

```text
feature/{name}/
├── component/
├── extension/
├── navigation/
├── di/
├── {Name}Contract.kt
├── {Name}ViewModel.kt
└── {Name}Screen.kt
```

다른 모듈에 route 계약을 공개하면 `api`와 `impl`로 분리한다.

```text
feature/{name}/
├── api/
│   └── .../{Name}Route.kt
└── impl/
    └── ...
        ├── component/
        ├── extension/
        ├── navigation/
        ├── di/
        ├── {Name}Contract.kt
        ├── {Name}ViewModel.kt
        └── {Name}Screen.kt
```

`component`와 `extension`은 해당 Feature 내부에서만 사용한다. 재사용 범위가 넓어지면
[`module-system.md`](module-system.md)의 공통 모듈 이동 기준을 적용한다.

## 3. Contract

### 3.1 Intent

Intent는 사용자 행동, UI lifecycle event 또는 Screen이 요청한 Android 작업의 결과 callback을
표현하고 ViewModel의 외부 event 진입점을 `onIntent()` 하나로 통일한다.

```kotlin
sealed interface SampleIntent {
    data object Load : SampleIntent
    data object Refresh : SampleIntent
    data class ClickItem(val id: Long) : SampleIntent
    data class ReportPermissionResult(val isGranted: Boolean) : SampleIntent
}
```

- Intent 구현 시, 네이밍 컨벤션은 "V + O"(동사 + 목적어, 예: `ClickClose`, `ClickItem`, `ToggleReport`)의 형태로 작성한다.
- State 전체를 Intent에 담지 않는다.
- 처리에 필요한 최소 값만 전달한다.
- 이름은 사용자의 행동, lifecycle event 또는 Android 작업 결과가 드러나게 작성한다.
- Android 작업 결과 callback은 `Report...`처럼 결과 보고임을 드러내고 Android Framework,
  CameraX, Media3, WorkManager 객체나 실제 사용자 미디어를 Intent에 담지 않는다.
- 최초 로드도 필요한 경우 명시적 Intent로 표현한다.

### 3.2 State

State는 화면에 지속적으로 표시할 값을 하나의 immutable `data class`로 표현한다.

```kotlin
data class SampleState(
    val isLoading: Boolean = false,
    val items: List<SampleUiModel> = emptyList(),
    val errorMessage: String? = null,
)
```

- 상태 변경은 `copy()`와 `_state.update { ... }` 또는 `reduce { ... }`로 수행한다.
- Boolean은 `is`, `has`, `can`, `should`처럼 의미 있는 접두어를 사용한다.
- 한 번 실행하고 사라져야 하는 동작은 State에 넣지 않는다.

### 3.3 Effect

Effect는 한 번만 소비되어야 하는 사건을 표현한다.

```kotlin
sealed interface SampleEffect {
    data object SubmissionSucceeded : SampleEffect
    data class ShowToast(val message: String) : SampleEffect
}
```

대표적인 Effect는 Navigation을 유발하는 Feature 결과, Toast, Snackbar와 일회성 Modal
요청이다. recomposition으로 반복 실행되면 안 되는 동작은 State가 아니라 Effect로 처리한다.

### 3.4 State와 Effect 선택

| 질문 | 선택 |
|---|---|
| 화면에 계속 남아야 하는가? | State |
| 한 번 소비되면 사라져야 하는가? | Effect |
| recomposition으로 반복 실행되면 안 되는가? | Effect |
| 화면별 오류 UI로 지속되어야 하는가? | State |

### 3.5 Feature 책임을 드러내는 이름

Intent와 Effect 이름은 다른 Feature의 목적지가 아니라 현재 Feature에서 발생한 사건을
표현한다. 예를 들어 Login Feature는 `NavigateToHome`보다 `LoginSucceeded`를 발행한다. Screen은
이를 상위 callback으로 전달하고 실제 이동 목적지는 `app` 계층이 결정한다.

## 4. ViewModel

ViewModel은 `onIntent()`로 event를 받고 Intent별 처리를 내부 함수로 분리한다.

```kotlin
class SampleViewModel(...) : ViewModel() {
    private val _state = MutableStateFlow(SampleState())
    val state = _state.asStateFlow()

    private val _effect = Channel<SampleEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: SampleIntent) {
        when (intent) {
            SampleIntent.Load -> load()
            SampleIntent.Refresh -> load()
            is SampleIntent.ClickItem -> select(intent.id)
        }
    }

    private inline fun reduce(block: SampleState.() -> SampleState) {
        _state.update(block)
    }
}
```

ViewModel 규칙은 다음과 같다.

- `domain`의 UseCase를 호출하고 Repository 구현체에 직접 접근하지 않는다.
- 화면에 필요한 경우 Domain Model을 UiModel로 변환한다.
- State는 immutable copy semantics로 갱신한다.
- Effect는 `Channel` 또는 프로젝트 공통 일회성 event utility로 발행한다. `core:common`의
  `MviViewModel`을 상속하면 `reduce()`와 `sendEffect()`를 공통으로 제공받는다.
- `sendEffect()`는 `Channel.trySend()`로 즉시(non-blocking) 발행해 별도 코루틴 생성 없이
  호출 순서와 채널 진입 순서를 일치시킨다. 동시 발행량이 `Channel.BUFFERED` 용량을 넘거나
  채널이 닫혀 발행이 실패하면 Effect는 유실되며, 경고 로그만 남기고 앱을 크래시시키지
  않는다.
- Navigation을 직접 실행하지 않고 Feature 결과 Effect를 발행한다.
- 오류 유형에 따른 처리는 [`error-handling.md`](error-handling.md)를 따른다.

## 5. Screen과 Content

Screen은 ViewModel을 연결하고 실제 UI는 ViewModel-free Content로 분리한다.

```kotlin
@Composable
fun SampleScreen(
    onCompleted: () -> Unit,
    viewModel: SampleViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(SampleIntent.Load)
        viewModel.effect.collect { effect ->
            when (effect) {
                SampleEffect.SubmissionSucceeded -> onCompleted()
                is SampleEffect.ShowToast -> ...
            }
        }
    }

    SampleContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun SampleContent(
    state: SampleState,
    onIntent: (SampleIntent) -> Unit,
) {
    ...
}
```

- Screen은 State 구독, Intent 전달과 Effect 수집을 담당한다.
- Content는 전달된 값과 callback만으로 UI를 렌더링한다.
- Figma MCP 서버를 통해 참조/가져온 디자인을 바탕으로 Composable UI(Screen, Content, Component)를 구현할 경우, KDoc 최상단에 해당 Figma 노드 ID/번호(예: `Figma Node: 1234:5678`)를 반드시 병기한다.
- 필요한 최초 로드는 `LaunchedEffect(Unit)`에서 `Load` Intent로 전달한다.
- Content는 Hilt, Android Lifecycle, Navigation, network, file access와 실제 사용자 데이터에
  의존하지 않는다.

## 6. Preview와 Catalog

Preview와 Catalog는 ViewModel-free UI만 렌더링한다.

| 모듈 | Preview 대상 |
|---|---|
| `app` | 앱 Theme과 ViewModel-free 전역 UI |
| `feature:*:impl` | ViewModel-free Content UI |
| `designsystem` | `commonMain` 공용 component와 Theme |

- Android와 Compose Multiplatform `commonMain`은
  `androidx.compose.ui.tooling.preview.Preview` annotation을 사용한다.
- `core:permission` platform launcher, `core:resources` accessor와 `catalog` Wasm UI에는 Preview
  Convention Plugin을 적용하지 않는다.
- Catalog 검수는 Web/WASM Story를 사용한다.
- Preview와 Catalog에 실제 사용자 데이터나 제품 runtime dependency를 포함하지 않는다.
- Plugin 적용 대상은 [`build-conventions.md`](build-conventions.md)를 따른다.
