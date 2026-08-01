# Guest Feedback UI·ViewModel·Navigation 구현 결과 보고서

## 1. 구현 결과 요약

Guest Feedback의 세 화면과 MVI, 메모리 flow session, Navigation 3 연결, 영상 재생, 제출 및 전역
오류 처리를 구현했다.

- `FeedbackOnboarding`에서 token으로 진입 정보를 한 번 조회하고, 작성 불가 상태를 차단한 뒤 필수
  별칭을 입력받는다.
- `Feedback`에서 지정 평가 축만 표시하고, 축별 4단계 평가와 선택 코멘트를 메모리 초안으로
  관리한다.
- `FeedbackReview`에서 작성 내용을 검토하고 코멘트를 수정한 뒤 확인 Modal을 거쳐 한 번만
  제출한다.
- `FeedbackOnboarding(token)` → `Feedback` → `FeedbackReview` route와 app 소유 back stack
  전환을 연결했다. 세 번째 route는 확정된 `FeedbackReview`를 유지한다.
- `ActivityRetainedScoped`인 `GuestFeedbackFlowSession` 하나만 두고 token, 영상 URL, 질문 경계,
  별칭과 초안을 메모리에서만 전달한다. 제출·종료·진입 실패 때 즉시 비운다.
- Media3 ExoPlayer와 `PlayerSurface`로 자동 재생, 터치 재생/일시정지, 최초 안내, 확대 표시,
  background 일시정지와 player 해제를 구현했다.
- Network/Server 오류는 종료 Modal, Unknown 오류는 고정 문구 Toast로 처리하는 최소 전역 event
  통로를 추가했다.
- 공용 `HilitModal`을 제품 Modal 구성으로 갱신하되 public API와 전역 Modal queue는 유지했다.
- 별칭 Domain 계약을 nullable에서 필수 문자열로 강화하고, 트리밍 후 1~12자·줄바꿈 금지를
  유스케이스와 테스트에 함께 적용했다.

## 2. 수락 조건 충족 여부

| 수락 조건 | 결과 | 근거 |
|---|---|---|
| 세 화면을 MVI Contract·ViewModel·Screen·Content로 분리 | 충족 | 화면별 Contract/ViewModel/Screen과 단위 테스트 추가 |
| Navigation은 Feature Effect와 app callback으로 분리 | 충족 | `FeedbackEntryBuilder`, `FeedbackNavigationModule`, `NavigatorTest` |
| `FeedbackReview` route 유지 | 충족 | `FeedbackRoute.kt`와 entry 연결에서 동일 route 사용 |
| 세 화면 사이 데이터는 한 흐름의 메모리에서만 전달 | 충족 | `GuestFeedbackFlowSession` 한 개만 추가, 영속 저장 미추가 |
| 별칭은 트리밍 후 필수 1~12자이며 줄바꿈 금지 | 충족 | UI 선행 검증과 Domain 최종 검증·경계 테스트 통과 |
| non-OPEN 및 `submissionOpen=false`는 평가 진입 차단 | 충족 | 상태별 비해제 Modal, session clear와 앱 종료 Effect 구현 |
| Onboarding 두 번 뒤로가기와 이후 Onboarding 복귀 차단 | 충족 | 2초 재입력 처리와 `replaceAll(Feedback)` 연결·테스트 |
| 지정 축만 `4→1` 순서로 평가하고 코멘트는 `다음`에서 확정 | 충족 | 축별 draft 갱신, 임시 편집값과 dismiss 복원 구현·테스트 |
| 질문 경계는 State/session에 보존하되 UI에서 미사용 | 충족 | 별도 질문 UI나 seek 기능 없이 메모리 모델에만 포함 |
| 최초 안내는 한 흐름에서 한 번만 표시 | 충족 | 2초 또는 터치 후 300ms fade, session intro 상태 유지 |
| 영상 runtime 상태는 player Composable만 소유 | 충족 | ViewModel에는 확대·안내·치명 오류만 전달 |
| 치명적 영상 오류는 평가를 막고 종료 안내 | 충족 | 오류 1회 보고와 비해제 종료 Modal 분기·단위 테스트 |
| Review에서는 코멘트만 수정하고 최종 제출은 한 번만 수행 | 충족 | 제출 확인, 중복 방지, 성공 시 session clear→Toast→종료 구현 |
| Guest·공통 오류 정책 준수 | 충족 | Client Modal과 Network/Server/Unknown 전역 event 분리 |
| 가능한 Design System Composable과 token 재사용 | 충족 | Hilit Text/Button/Field/Tab/Tag/Icon/Loading/Modal 사용 |
| 민감 데이터를 저장·캐시·로그하지 않음 | 충족 | 새 구현에서 저장소·파일·평문 로그 호출이 없음을 검색으로 확인 |
| Android 정적 검증과 단위 테스트 | 충족 | 전체 Android CI 성공, 대상 단위 테스트 45개 실패 0 |
| 실제 영상·Modal·뒤로가기 기기 동작 | 미검증 | 연결된 Android 기기가 없어 instrumented test와 수동 검증 미실행 |
| Catalog/Design System Wasm 결과 | 사용자 확인 | 사용자 요청에 따라 Wasm 빌드·테스트·distribution 재검증 제외 |

온보딩 일러스트는 확인되지 않은 Figma vector를 새 asset으로 추측해 추가하지 않고 기존
`HilitIconAsset.Profile`을 사용했다. 따라서 정확한 Figma 일러스트와의 시각 일치는 수동 확인이
필요하다.

## 3. 생성·변경된 파일

### Build, Domain과 Route

| 파일 | 구분 | 내용 |
|---|---|---|
| `gradle/libs.versions.toml` | 변경 | Media3 `1.10.1`과 ExoPlayer·UI Compose alias 추가 |
| `feature/feedback/impl/build.gradle.kts` | 변경 | Feature API, Core, Design System, Domain, Media3와 test 의존성 연결 |
| `app/build.gradle.kts` | 변경 | Feedback 구현 모듈 조립 |
| `feature/feedback/api/src/main/kotlin/com/dminus14/app/feature/feedback/api/FeedbackRoute.kt` | 변경 | token을 가진 redacted Onboarding route와 기존 Feedback·FeedbackReview route |
| `domain/src/main/kotlin/com/dminus14/app/domain/model/GuestFeedback.kt` | 변경 | 제출 별칭을 non-null 계약으로 변경 |
| `domain/src/main/kotlin/com/dminus14/app/domain/usecase/SubmitGuestFeedbackUseCase.kt` | 변경 | 별칭 트리밍·필수·줄바꿈·1~12자 검증 |
| `domain/src/test/kotlin/com/dminus14/app/domain/usecase/GuestFeedbackUseCaseTest.kt` | 변경 | 별칭 정상 경계와 잘못된 입력 테스트 |

### Core와 App 조립

| 파일 | 구분 | 내용 |
|---|---|---|
| `core/common/src/main/kotlin/com/dminus14/app/core/common/event/GlobalAppEvent.kt` | 생성 | 민감 값이 없는 두 전역 오류 event |
| `core/common/src/main/kotlin/com/dminus14/app/core/common/event/GlobalErrorHandler.kt` | 생성 | 최소 SharedFlow event 통로 |
| `core/common/src/test/kotlin/com/dminus14/app/core/common/event/GlobalErrorHandlerTest.kt` | 생성 | 전역 event 전달 테스트 |
| `app/src/main/java/com/dminus14/app/error/GlobalErrorHost.kt` | 생성 | 연결 오류 Modal과 Unknown Toast host |
| `app/src/main/java/com/dminus14/app/navigation/di/FeedbackNavigationModule.kt` | 생성 | Feedback entry callback과 back stack 정책 조립 |
| `app/src/main/java/com/dminus14/app/navigation/Navigator.kt` | 변경 | root 종료 callback 구현 |
| `app/src/main/java/com/dminus14/app/MainActivity.kt` | 변경 | 전역 오류 host와 `finishAffinity()` 연결 |
| `app/src/test/java/com/dminus14/app/navigation/NavigatorTest.kt` | 생성 | route 교체와 root 종료 테스트 |

### Feedback Feature

| 경로 | 구분 | 내용 |
|---|---|---|
| `feature/feedback/impl/src/main/.../session/GuestFeedbackFlowSession.kt` | 생성 | 한 Guest 흐름의 메모리 데이터와 초안 |
| `feature/feedback/impl/src/main/.../navigation/FeedbackEntryBuilder.kt` | 생성 | 세 route의 Navigation 3 entry |
| `feature/feedback/impl/src/main/.../onboarding/*` | 생성 | Onboarding Contract, ViewModel, Screen/Content |
| `feature/feedback/impl/src/main/.../feedback/*` | 생성 | 평가 Contract, ViewModel, Screen/Content |
| `feature/feedback/impl/src/main/.../review/*` | 생성 | Review Contract, ViewModel, Screen/Content |
| `feature/feedback/impl/src/main/.../component/GuestFeedbackVideoPlayer.kt` | 생성 | Media3 player와 lifecycle·최초 안내 처리 |
| `feature/feedback/impl/src/main/.../component/GuestFeedbackCommentModal.kt` | 생성 | Feedback·Review 공용 임시 코멘트 편집 UI |
| `feature/feedback/impl/src/test/.../MainDispatcherRule.kt` | 생성 | ViewModel coroutine test rule |
| `feature/feedback/impl/src/test/.../GuestFeedbackTestFixture.kt` | 생성 | 비식별 합성 테스트 fixture |
| `feature/feedback/impl/src/test/.../*ViewModelTest.kt` | 생성 | 세 ViewModel 동작 테스트 11개 |
| `feature/feedback/impl/src/androidTest/.../GuestFeedbackContentTest.kt` | 생성 | Content 렌더링·상호작용 테스트 3개 |

### Design System, Catalog와 문서

| 파일 | 구분 | 내용 |
|---|---|---|
| `designsystem/src/commonMain/kotlin/com/dminus14/designsystem/component/modal/HilitModal.kt` | 변경 | 기존 API를 유지한 제품 Modal과 1·2버튼 배치 |
| `catalog/src/wasmJsMain/kotlin/stories/components/designsystem/hilitmodal/HilitModalStories.kt` | 변경 | 변경된 Modal 설명과 Story 갱신 |
| `report-phase-3.md` | 생성 | 구현, 단순화, 검증과 잔여 위험 보고 |

작업 시작 전에 존재하던 `PR.md`, `plan-phase-1.md`, `plan-phase-2.md`, `plan-phase-3.md`,
`prd.md`, `report.md`, `specify.md`의 사용자 변경은 이번 구현에서 수정하거나 되돌리지 않았다.

## 4. 오캄의 면도날을 반영한 지점

### 하나의 구체적인 flow session

화면 간 민감 데이터를 전달하기 위해 범용 coordinator, 상태 저장소나 직렬화 계층을 만들지 않았다.
실제 세 화면이 공유하는 값만 가진 `GuestFeedbackFlowSession` 하나를 두고 수명을 Activity retained로
제한했다.

### player 상태를 복제하지 않음

재생 준비, 재생/일시정지와 현재 위치를 ViewModel이나 session에 복제하지 않았다. ExoPlayer를
소유한 Composable이 runtime 상태와 lifecycle을 처리하고, 제품 상태인 확대·최초 안내·치명 오류만
MVI에 전달한다.

### 화면 전용 코드는 Feature에 유지

평가 카드, 코멘트 입력과 player를 성급하게 Design System API로 승격하지 않았다. 반복 사용이
확정된 코멘트 Modal만 Feature 내부에서 두 화면이 공유한다.

### 기존 Modal 계약 유지

`HilitModal`의 public API나 전역 queue 추상화를 늘리지 않고 내부 레이아웃만 제품 시각으로
바꿨다. 한 버튼과 두 버튼 모두 기존 `HilitFixedBottomButton`을 조합했다.

### 추측성 기능과 asset을 추가하지 않음

seek, 반복, 재생 속도, 화면 회전, 질문 구간 UI, 서버 임시 저장, 재시도와 별도 완료 화면을
추가하지 않았다. 확인되지 않은 Figma vector를 임의로 그리지 않고 기존 Design System icon을
재사용했다.

### 작은 분기에서 새 계층을 만들지 않음

별칭 validator, 축별 ViewModel, 범용 form reducer, 오류 mapper를 추가하지 않았다. 실제 책임을
가진 UseCase와 각 ViewModel의 작은 함수로 검증과 상태 갱신을 유지했다.

## 5. 검증 결과

### 최종 성공

| 명령 | 결과 |
|---|---|
| `.\gradlew.bat --console=plain -q --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug` | 성공 |
| `.\gradlew.bat :domain:test :feature:feedback:impl:testDebugUnitTest :feature:feedback:impl:assembleDebugAndroidTest` | 성공 |
| `.\gradlew.bat :feature:feedback:impl:lintDebug :app:detekt :app:lintDebug :domain:detekt :designsystem:detekt` | 성공 |
| `.\gradlew.bat :catalog:compileKotlinWasmJs` | 사용자 제외 요청 전에 성공 |
| `git diff --check` | 오류 없음; Version Catalog의 향후 CRLF 변환 경고만 출력 |
| 민감 데이터 저장·로그 API 정적 검색 | 일치 항목 없음 |

대상 단위 테스트 결과:

- `domain:test`: 13개, 실패 0, 오류 0, 건너뜀 0
- `core:common:testDebugUnitTest`: 10개, 실패 0, 오류 0, 건너뜀 0
- `feature:feedback:impl:testDebugUnitTest`: 11개, 실패 0, 오류 0, 건너뜀 0
- `app:testDebugUnitTest`: 11개, 실패 0, 오류 0, 건너뜀 0

검증 중 발견한 별칭 중복 load race, Detekt 복잡도·매직 넘버, Media3 opt-in, Compose `Modifier`
규칙과 import/줄바꿈 포맷 오류는 범위 안에서 수정했고, 위 최종 전체 CI로 재검증했다.

### 실행하지 않거나 완료하지 않은 검증

- `connectedDebugAndroidTest`: `adb devices`에서 연결 기기가 없어 실행하지 않았다. AndroidTest APK
  compile·assemble까지만 성공했다.
- 실제 HTTPS 영상 재생, background 전환, Modal, Toast와 `finishAffinity()`의 기기 동작: 연결
  기기가 없어 수동 확인하지 않았다.
- `:designsystem:allTests :catalog:wasmJsTest :catalog:wasmJsBrowserDistribution`: 두 번의 실행이
  각각 약 2분, 5분 제한에서 출력 없이 완료되지 않았다. 이후 사용자가 Wasm 빌드를 제외하고 직접
  확인하겠다고 명시해 더 실행하지 않았다. 따라서 이 세 task의 성공은 주장하지 않는다.

Android 정적 검증·단위 테스트·assemble은 완료됐고, 기기 동작과 Wasm 출력은 부분 미검증 상태다.

## 6. 잔여 위험과 후속 확인

- 실제 스트림의 codec, 인증, redirect와 오류 형태는 합성 단위 테스트만으로 확인할 수 없다. 실기기에서
  정상 영상과 실패 영상을 각각 확인해야 한다.
- 최초 안내의 2초 timer·300ms fade, 터치 재생 전환, background 일시정지와 player 해제는 실기기
  수동 확인이 필요하다.
- 온보딩의 기존 Profile icon이 Figma의 정확한 제품 일러스트를 대체할 수 있는지는 시각 확인이
  필요하다. 정확한 export asset이 제공되면 Feature 전용 drawable 한 개로 교체하면 된다.
- Catalog/Design System의 Wasm test와 browser distribution은 사용자가 직접 확인해야 한다.

추가 제품 정책이나 아키텍처 결정이 필요한 모호성은 구현 과정에서 새로 식별되지 않았다.
