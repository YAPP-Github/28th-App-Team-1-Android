# 면접 준비 UI 최소 노출 시간 구현 계획

## 1. 목표와 현재 원인

면접 준비 화면의 다음 두 시각 상태를 각각 최소 3초 동안 노출한다. 모든 준비가 즉시 끝나는 최단 경로에서도 두 상태를 합쳐 최소 약 6초가 지난 후 `START_GUIDE`로 전환한다.

1. 준비 중: `InterviewScreenPreparingLayerPreview`와 같은 회색 중앙 문구
2. 준비 완료: `InterviewScreenPrepareLayerAlmostPreparedPreview`와 같은 흰색 중앙 문구

현재 `InterviewViewModel.updatePreparationGate()`는 장치, 권한, 서버 질문, 저장 공간 준비가 모두 끝나는 즉시 `START_GUIDE`로 전환한다. 서버 준비 완료 시 `QUESTION_PREPARING`을 설정한 직후 같은 호출 흐름에서 `START_GUIDE`가 설정될 수 있어, 준비 완료 상태가 한 프레임도 안정적으로 표시되지 않을 수 있다.

구현은 Composable 내부에서 실제 준비 상태를 늦추지 않고, `InterviewViewModel`이 화면 단계 전환 시점을 관리하도록 한다. 실제 준비 조건과 사용자에게 보여 주는 단계는 구분하되, 기존 MVI State와 화면 단계 계약을 유지하고 새 모듈·의존성·추상화는 추가하지 않는다.

## 2. 구현 계획

1. 준비 단계의 최소 노출 시간인 3,000ms를 Feature 상수로 정의한다.
2. `LoadInterview`로 준비 흐름이 시작될 때 준비 중 단계의 최소 시간 측정을 시작한다. 구현 단순성을 위해 앱이 백그라운드에 있는 시간도 경과 시간에 포함하며, lifecycle에 따라 측정을 일시 정지하거나 재개하지 않는다.
3. 실제 준비 조건인 `InterviewState.isReadyToStart`가 먼저 충족되더라도 준비 중 단계의 3초가 끝나기 전에는 `DEVICE_CHECK`를 유지한다.
4. 준비 중 최소 시간과 실제 준비 조건이 모두 충족되면 `QUESTION_PREPARING`으로 전환한다. 이 단계에서는 중앙 문구가 흰색으로 표시되도록 렌더링 조건을 화면 단계와 맞춘다.
5. `QUESTION_PREPARING` 진입 시 별도의 3초 측정을 시작하고, 시간이 끝난 뒤에만 `START_GUIDE`로 전환한다.
6. 권한 거부, 장치 실패, 저장 공간 부족처럼 실제 준비 조건이 충족되지 않은 경우에는 시간 경과만으로 다음 단계로 넘어가지 않는다. 뒤로 가기, 설정 열기, 오류 처리 같은 기존 사용자 동작도 지연하지 않는다.
7. 기존 준비 폴링 작업과 최소 노출 시간 작업의 생명주기를 분리하고, ViewModel이 제거될 때 남은 작업이 중복 상태 전환을 만들지 않게 한다. 프로세스 재생성 등으로 새 `InterviewViewModel`이 만들어지면 이전 인스턴스의 경과 시간을 복원하지 않고 두 단계의 시간을 처음부터 측정한다.
8. `kotlinx-coroutines-test`의 가상 시간을 사용해 경계 직전과 경계 시점의 상태를 단위 테스트한다.

## 3. 수정 예정 파일

| 파일 | 수정 내용 |
|---|---|
| `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/InterviewConstants.kt` | 준비 UI 한 단계의 최소 노출 시간 3,000ms 상수 추가 |
| `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/interview/InterviewViewModel.kt` | 준비 중 최소 시간, 실제 준비 조건, 준비 완료 최소 시간을 순서대로 만족해야 `DEVICE_CHECK` → `QUESTION_PREPARING` → `START_GUIDE`로 전환되도록 준비 게이트와 coroutine Job 관리 수정 |
| `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/interview/layer/InterviewScreenPrepareLayer.kt` | 준비 인디케이터의 흰색 상태가 `QUESTION_PREPARING` 단계에서만 표시되도록 실제 준비 값과 화면 단계를 함께 반영해, `DEVICE_CHECK`의 최소 노출 중 순간적으로 흰색이 보이지 않게 보강 |
| `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/interview/InterviewViewModelPreparationTest.kt` | 모든 준비 조건이 즉시 충족되어도 준비 중과 준비 완료가 각각 3초 미만에는 전환되지 않고, 정확히 3초가 지나면 다음 단계로 전환되는지 검증; 준비 조건이 늦게 충족되는 경우 불필요하게 준비 중 시간을 다시 기다리지 않고 준비 완료 단계만 3초 유지하는지 검증; 새 ViewModel에서는 이전 경과 시간이 이어지지 않는지 검증 |

`InterviewReadinessIndicator.kt`의 색상·애니메이션 계약 자체는 이미 `isReady=false`일 때 회색, `true`일 때 흰색이므로 수정하지 않는다. `InterviewContract.kt`에도 새 State 필드를 추가하지 않고 기존 `screenState`와 내부 시간 게이트로 처리하는 방안을 우선한다.

## 4. 수락 조건

- 모든 준비 조건이 화면 진입 직후 완료되어도 회색 중앙 문구가 표시되는 준비 중 UI가 최소 3초 유지된다.
- 준비 중 UI가 3초 이상 노출되고 실제 준비 조건도 모두 충족된 후에만 흰색 중앙 문구가 표시되는 `QUESTION_PREPARING`으로 전환된다.
- `QUESTION_PREPARING` UI가 최소 3초 유지된 후에만 `START_GUIDE`와 면접 시작 버튼이 표시된다.
- 모든 준비 조건이 즉시 충족되는 최단 경로에서도 `START_GUIDE`는 준비 흐름 시작 후 최소 약 6초가 지나야 표시된다.
- 준비 조건이 늦게 충족되면 이미 경과한 준비 중 시간을 인정한다. 즉, 실제 준비 완료 뒤 준비 중 UI를 다시 3초 추가 노출하지 않는다.
- 권한, 카메라, 마이크, 서버 질문, 저장 공간 중 하나라도 준비되지 않으면 시간만 경과해도 준비 완료 또는 시작 안내로 전환되지 않는다.
- `DEVICE_CHECK` 최소 노출 중 실제 준비 값이 먼저 `true`가 되더라도 중앙 문구가 순간적으로 흰색으로 바뀌지 않는다.
- 준비 폴링, 권한·장치 결과, 저장 공간 결과가 어떤 순서로 도착해도 각 화면 단계는 한 번만 순서대로 전환된다.
- 앱이 백그라운드에 있는 동안에도 최소 노출 시간은 계속 경과하며, foreground 복귀를 이유로 시간을 다시 시작하거나 남은 시간을 추가하지 않는다.
- 프로세스 재생성이나 새 `InterviewViewModel` 생성 후 준비 화면에 다시 진입하면 이전 경과 시간을 복원하지 않고 준비 중과 준비 완료 시간을 각각 처음부터 측정한다.
- 기존 권한 거부 뒤로 가기, 설정 열기, 저장 공간 부족 표시와 오류 흐름은 최소 노출 시간 때문에 차단되거나 지연되지 않는다.
- 관련 단위 테스트의 함수명은 기대 동작을 설명하는 한국어 문장으로 작성하고 모두 통과한다.
- 전체 CI 검증 명령이 통과한다. 실행이 불가능하거나 범위 밖 실패가 있으면 명령, 원인, 남은 위험을 별도로 보고한다.

## 5. 검증 계획

우선 다음 대상 단위 테스트를 실행한다.

```text
./gradlew :feature:interview:impl:testDebugUnitTest --tests "com.dminus14.app.feature.interview.interview.InterviewViewModelPreparationTest"
```

그다음 저장소 전체 CI 검증을 실행한다.

```text
./gradlew --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug
```

수동 확인에서는 서버와 장치 준비가 매우 빠른 조건을 만들어 회색 상태 약 3초, 흰색 상태 약 3초, 시작 안내 순서로 보이는지 확인한다. 또한 각 단계 도중 앱을 백그라운드로 보낸 뒤 복귀해 백그라운드 시간이 경과 시간에 포함되는지 확인한다. 이번 변경은 Design System이나 Catalog Web/WASM 출력을 바꾸지 않으므로 `:catalog:wasmJsBrowserDistribution`은 실행하지 않는다.

## 6. 모호점과 결정 필요 사항

현재 갱신된 계획에서 추가로 사람의 결정이 필요한 모호점은 없다.
