# Global Modal

이 문서는 여러 Feature와 공통 오류 처리에서 사용하는 앱 전역 Modal 요청·결과·queue 계약을
정의한다. UI 상태 책임은 [`feature-ui.md`](feature-ui.md), 공용 Modal UI는
[`design-system.md`](design-system.md)를 함께 따른다.

## 1. 단일 진입점

Feature 또는 공통 오류 처리 코드에서 앱 전역 Modal이 필요하면 `core:common`의
`showGlobalModal(...)`만 직접 호출한다.

```text
Feature 또는 공통 오류 처리
    → core:common showGlobalModal(...)
    → core:common SharedFlow
    → app GlobalModalManager와 GlobalModalHost
    → designsystem HilitModal
    → GlobalModalResult
```

호출자는 event Flow, queue, Manager와 Host를 알 필요가 없다.

## 2. 요청과 결과

`showGlobalModal(...)`은 사용자 선택 또는 overflow 결과가 결정될 때까지 suspend된다.

```kotlin
val result = showGlobalModal(
    GlobalModalRequest(
        title = "작업을 종료할까요?",
        message = "저장하지 않은 변경 사항은 사라집니다.",
        confirmText = "종료",
        cancelText = "취소",
        dismissible = false,
    ),
)

when (result) {
    Confirm -> finishWork()
    Cancel, Dismiss, DroppedByOverflow -> Unit
}
```

| 결과 | 의미 |
|---|---|
| `Confirm` | 사용자가 확인 버튼을 선택함 |
| `Cancel` | 사용자가 선택적 취소 버튼을 선택함 |
| `Dismiss` | 허용된 Back 또는 바깥 영역 터치로 닫힘 |
| `DroppedByOverflow` | queue overflow 정책으로 요청이 제거되거나 거절됨 |

호출 coroutine이 취소되면 현재 또는 대기 중 요청도 제거한다. 취소를 별도
`GlobalModalResult`로 변환하지 않는다.

## 3. ViewModel에서 호출

ViewModel에서 호출하면 MVI Intent 처리 coroutine 안에서 실행하고 결과에 따른 후속 작업을
State 변경 또는 Effect 발행으로 연결한다. Modal UI나 Manager를 직접 참조하지 않는다.

## 4. `dismissible`

- `true`면 Back과 Modal 바깥 영역 터치를 허용하고 `Dismiss`를 반환한다.
- `false`면 Back과 바깥 영역 터치를 무시하고 overflow 제거 대상에서도 보호한다.
- confirm과 선택적 cancel button은 명시적 사용자 선택이므로 값과 관계없이 동작한다.
- 이 값은 system dismiss 허용 여부와 overflow 보호 여부를 함께 나타내며 시각 속성이 아니다.

## 5. Queue와 수명

- 한 번에 하나의 Modal만 표시한다.
- 표시 중 요청을 제외한 대기 queue는 FIFO 최대 10건이다.
- queue가 가득 차면 가장 오래된 dismissible 대기 요청을 제거하고 새 요청을 추가한다.
- 제거 가능한 요청이 없으면 새 dismissible 요청을 `DroppedByOverflow`로 완료한다.
- 제거 가능한 요청이 없을 때 새 non-dismissible 요청은 공간이 생길 때까지 backpressure를
  받는다.
- Manager는 app process lifetime으로 동작한다.
- Activity 재생성 또는 Host 부재 중에도 현재 요청과 대기 순서를 유지한다.
- process 종료 후 요청, 결과 대기와 Modal UI는 영속 복원하지 않는다.

## 6. 모듈별 책임

| 모듈 | 책임 |
|---|---|
| `feature:*` | Modal 필요 여부 결정, 호출 결과에 따른 후속 동작 |
| `core:common` | 플랫폼 독립 요청·결과, 단일 SharedFlow와 호출 함수 |
| `app` | process lifetime 수집, FIFO·overflow·취소 처리, 최상단 렌더링 |
| `designsystem` | stateless `HilitModal` UI와 callback |
| `catalog` | 제품 runtime 없이 Modal 주요 시각 상태를 Story로 노출 |

## 7. 금지 사항

- 호출자가 `globalModalEvents`, `GlobalModalManager` 또는 `GlobalModalHost`를 직접 참조
- Feature가 전역 Modal을 위해 `HilitModal`을 직접 렌더링
- Request model에 color, spacing, shape, typography 같은 표현 정보 추가
- Request model에 호출자 callback 추가
- Modal title과 message를 log, analytics 또는 crash report에 기록
