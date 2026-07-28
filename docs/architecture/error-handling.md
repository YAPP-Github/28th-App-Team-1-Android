# Error Handling

이 문서는 오류 분류, 레이어별 전달 책임과 UI 처리 정책을 정의한다. Feature의 State와 Effect는
[`feature-ui.md`](feature-ui.md), 앱 전역 Dialog는 [`global-dialog.md`](global-dialog.md)를 함께
따른다.

## 1. 오류 분류

| 타입 | 예시 | UI 처리 | 전달 방식 |
|---|---|---|---|
| `NetworkUnavailable` | 인터넷 연결 없음, timeout, DNS 실패 | 재시도 또는 앱 종료 Dialog | Global Event |
| `ServerError` | HTTP 500, 502, 503 | 앱 종료 Dialog | Global Event |
| `ClientError` | HTTP 400, 401, 404 | 화면별 기획에 따른 UI | Feature State 또는 Effect |
| `Unknown` | 분류할 수 없는 Exception | Toast | Global Event |

## 2. 처리 위치 선택

- 화면별 message나 UI가 다르면 Feature State로 처리한다.
- 한 번만 표시할 화면별 알림은 Feature Effect로 처리한다.
- 앱 전체에서 동일한 Dialog, Toast 또는 Snackbar로 처리하면 Global Event를 사용한다.
- 재시도나 앱 종료처럼 app-level action이 필요하면 Global Event를 사용한다.
- Client Error를 기본 Global Event로 변환하지 않는다.

## 3. 레이어별 책임

| 레이어 | 책임 |
|---|---|
| `data` | 외부 Exception을 프로젝트 error type으로 변환하거나 상위로 전파 |
| `domain` | UseCase 결과와 UI 독립 error를 전달하고 UI 표현은 결정하지 않음 |
| `feature:*` | Client Error를 State/Effect로 처리하고 Network/Server/Unknown을 Global Event로 전달 |
| `core:common` | 플랫폼 독립 공통 Error, `GlobalAppEvent`, `GlobalErrorHandler` 계약 |
| `app` | Global Event를 collect하고 Dialog, Toast 또는 Snackbar 렌더링 |

새로운 오류 처리 mechanism을 임의로 만들지 않고 기존 Global Event 정책을 사용한다.

## 4. 공통 오류와 Event

공통 타입은 Android에 의존하지 않는다. 다음 코드는 계약의 핵심만 보여주는 개념 예시다.

```kotlin
open class CustomException(
    val errCode: Int,
    override val message: String,
) : Exception(message)

class NetworkUnavailableException(...) : CustomException(...)
class ServerException(...) : CustomException(...)
class ClientException(...) : CustomException(...)
class UnknownException(...) : CustomException(...)
```

```kotlin
sealed interface GlobalAppEvent {
    data object ShowNetworkDialog : GlobalAppEvent
    data object ShowServerErrorDialog : GlobalAppEvent
    data class ShowToast(val message: String) : GlobalAppEvent
}
```

`GlobalErrorHandler`는 project error를 app-level event로 변환한다.

```kotlin
suspend fun emit(error: CustomException) {
    when (error) {
        is NetworkUnavailableException -> emit(ShowNetworkDialog)
        is ServerException -> emit(ShowServerErrorDialog)
        is UnknownException -> emit(ShowToast(error.message))
        is ClientException -> Unit
        else -> emit(ShowToast(error.message))
    }
}
```

SharedFlow를 사용하는 구현은 collect 이전 event를 위한 `extraBufferCapacity = 1`과 연속 실패 시
최신 event를 우선하는 `DROP_OLDEST` 정책을 사용한다.

## 5. ViewModel 처리

ViewModel은 loading State를 복구한 뒤 error type에 따라 화면 또는 전역 경로를 선택한다.

```kotlin
when (throwable) {
    is ClientException -> reduce {
        copy(isLoading = false, errorMessage = throwable.message)
    }

    is NetworkUnavailableException,
    is ServerException,
    is UnknownException,
        -> GlobalErrorHandler.emit(throwable)

    else -> GlobalErrorHandler.emit(ShowToast(...))
}
```

ViewModel은 Android UI를 직접 표시하지 않는다.

## 6. 앱 루트 처리

`app`의 Root Composable은 Global Event를 한 곳에서 collect한다.

```kotlin
LaunchedEffect(Unit) {
    GlobalErrorHandler.events.collect { event ->
        when (event) {
            ShowNetworkDialog -> ...
            ShowServerErrorDialog -> ...
            is ShowToast -> ...
        }
    }
}
```

- Network와 Server Event는 app-level Dialog 정책에 연결한다.
- Unknown Event는 app-level Toast 또는 Snackbar에 연결한다.
- UI component는 `designsystem`, Android action과 root rendering은 `app`이 소유한다.
- 재시도 action은 현재 화면 또는 Feature 정책으로 다시 전달한다.

## 7. 금지 사항

- `domain`에서 Dialog, Toast, Snackbar 문구나 UI 유형 결정
- `data`에서 UI event 발행 또는 UI 정책 encoding
- Feature에서 Network/Server/Unknown Error를 위한 별도 전역 mechanism 도입
- ViewModel에서 Android Toast나 Dialog 직접 표시
- 민감한 error payload나 사용자 데이터를 plaintext log에 기록
