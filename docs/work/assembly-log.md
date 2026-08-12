# 기능 조립 로그

완성된 기능(Feature)을 `app` 레이어에 실제로 조립하면서 확정되는 사실을 기록하는 작업 문서다. 조립 전 PRD(`docs/prd/*.md`)는 예측이고, 이 로그는 조립 후 확정값이다. 나중에 문서 아키텍처(도메인 노트·`domain.map` 등)를 갱신할 때 이 로그를 1차 재료로 쓴다.

기능 하나 조립이 끝날 때마다 그 자리에서 새 절을 추가한다. 세션 컨텍스트가 요약·손실돼도 이 파일은 그대로 남는다.

## 절 형식

```
## [기능명] — YYYY-MM-DD

### Effect → 목적지 매핑
- `EffectA` → (app이 실제로 여는 화면/route)
- ...

### PRD·기존 문서 대비 확정/변경된 사실
- (조립하며 새로 드러났거나 PRD 예측과 달라진 점)

### 모듈 경계 결정
- (어느 모듈이 무엇을 소유하기로 확정됐는지, cross-feature 숨은 의존)

### 남은 이슈 / TODO
- (조립 중 발견했지만 이번 범위 밖이라 미룬 것)
```

---

## Login (`:feature:login`) — 2026-08-12

### Effect → 목적지 매핑

`app/navigation/di/LoginNavigationModule.kt` 기준, 실제 조립된 값.

- `SplashEffect.Ready` → `replaceAll(Home)`
- `SplashEffect.RequireConsent` → `replaceAll(Term)`
- `SplashEffect.RequireOnboarding` → `replaceAll(Onboarding)`
- `TermEffect.Closed`(닫기) → `replaceAll(Splash)`
- `TermEffect.DeniedPerm` → `goTo(PermissionConsent)`
- `TermEffect.ExistProfile` → `goTo(Home)`
- `TermEffect.NonExistProfile` → `goTo(Onboarding)`
- `PermissionConsentEffect.NavigateHome` / `NavigateOnboarding` / `NavigateDenied` → 전부 `replaceAll` (Home / Onboarding / PermissionConsentDenied)
- `PermissionConsentDeniedScreen.onHome`, `SuspensionNoticeScreen.onHome` → 둘 다 `replaceAll(Home)`
- `OnboardingEffect.Completed`(프로필 등록 완료) → `replaceAll(Home)`, `CloseRequested` → `replaceAll(Splash)`
- (이번에 신규 배선) `feature:onboarding`(면접 준비 위저드)의 `OnBoardingInterviewEffect.NavigateToSuspensionNotice` → `onNavigate(SuspensionNotice)`, 이 경로만 `goTo`(위저드 스텝이 backstack에 남음 — "홈으로" 클릭 시 `replaceAll`로 정리되므로 기능상 문제는 없음)

### PRD·기존 문서 대비 확정/변경된 사실

- `SuspensionNotice`(계정 이용 제한) 화면은 처음엔 로그인/가입 흐름 소유로 추정됐으나, `api-docs.json` 대조 결과 실제 트리거는 면접 세션 생성(`POST /api/v1/interview/sessions`) 403 게이트1(`ACCOUNT_SUSPENDED`)임을 확정. 화면·route 소유는 `feature:login:api`, 트리거 소유는 `feature:onboarding`(면접 준비 위저드)로 갈림 — 이런 소유 분리는 어떤 문서에도 사전 기술 없었음.
- 연차 입력 옵션이 서버 `careerYears`(정수 0~10) 계약과 정확히 일치해야 함이 `api-docs.json` 스키마로 확정됨. 기존 코드는 12개 옵션(인덱스 0~11)이라 마지막 옵션 선택 시 서버가 100% 거부하는 상태였음 — 11개로 정정.
- `PermissionConsentDenied → Home` 경로는 프로필 재확인 없이 바로 Home으로 가지만, `HomeViewModel.loadProfile()`이 독립적으로 프로필 완성 여부를 재검증하는 이중 안전망이 실제로 존재함을 코드 추적으로 확인. 문서엔 이 이중 검증 관계가 명시된 적 없음.

### 모듈 경계 결정

- `SuspensionNotice`는 `feature:login:api` 소유 유지, `feature:onboarding:impl`이 이를 직접 import(`implementation(project(":feature:login:api"))` 신규 추가) — 기존 `feature:interview:api` 크로스임포트와 같은 패턴으로 통일. app 레이어에 별도 콜백 신설 없음(`onNavigate: (Any) -> Unit`가 이미 범용이라 그대로 재사용).
- `ACCOUNT_SUSPENDED` 예외 매핑(`AccountSuspendedException`, `ApiErrorCode`)은 `domain`/`data` 레이어에 정착 — 서버 게이트가 여러 기능이 공유하는 지점이라 특정 Feature에 두지 않음.
- `KakaoLoginClient`는 ViewModel 주입이 아니라 Hilt `EntryPoint`로 Composable에서 직접 획득(Activity 컨텍스트 필요) — `feature:login:impl` 내부로 완결, app 레이어 개입 없음.

### 남은 이슈 / TODO

- 게이트3(`CONSENT_VERSION_STALE`, 세션 생성 시점 재동의 필요)도 `api-docs.json`에 존재 확인됨. 현재 `feature:onboarding`은 이 코드를 여전히 generic 인라인 `errorMessage`로만 처리 — `ACCOUNT_SUSPENDED`처럼 전용 라우팅이 필요한지 미확인, 이번 범위에서 안 다룸.
- `PermissionConsentDenied`(권한 거부 안내)와 `SuspensionNotice`(계정 정지 안내)가 원인은 다르지만 "제한 안내"라는 유사 주제를 각각 별도 구현 — 통합 여지는 있으나 범위 밖.
- 애플 로그인 경로는 이번 평가·수정에서 전혀 확인 안 함(카카오만 봄).

---

## Home (`:feature:home`) — 2026-08-12

### Effect → 목적지 매핑

`HomeScreen.kt`가 직접 route를 import해 분기(다른 feature api 모듈 직접 참조 컨벤션 그대로, app 레벨 `HomeNavigationModule`은 `onNavigate=goTo`/`onReplaceAll=replaceAll`만 범용으로 넘겨줌).

- `GoToMyPageRequested` → `onNavigate(MyPage)`
- `UserNameNotRegistered` → `onReplaceAll(Onboarding)`
- `UserNotFound` → `onReplaceAll(Splash)`
- `GoToOnboardingInterviewRequested` → `onNavigate(OnBoardingInterview)`
- (이번에 신규 배선) `GoToInterviewRequested` → `onNavigate(InterviewRoute)` — "이어서 진행" 확정 시. `feature:home:impl`이 `feature:interview:api`에 새로 의존.
- `ReportSheetResetRequested` → 네비게이션 아님, 로컬 UI 신호(`peekResetSignal` 증가)
- `GoToReportRequested(reportId)` → 여전히 미배선(아래 "남은 이슈" 참고)

### PRD·기존 문서 대비 확정/변경된 사실

- "이어서 진행"은 Home이 `confirmResume`을 직접 부르지 않는다. `InterviewViewModel.load()`가 로컬 `InterviewProgress.timerStartedAtEpochMillis` 존재 여부만으로 이미 자동으로 `NETWORK` 복구 화면(`InterviewErrorViewModel`)에 태워 재개를 처리하는 기존 경로가 있었음 — Home의 역할은 그 경로로 들여보내는 네비게이션 트리거 하나로 확정됨. 서버 재개 판정 로직을 Home에 새로 만들지 않음.
- "처음부터 시작"은 원래(조립 전) 확인 없이 곧장 새 세션으로 진입했으나, 이번에 `ConfirmRestart` 확인 오버레이(화면 자체는 이미 있었지만 트리거가 없던 상태)를 경유하고, 확정 시에만 `AbandonInterviewUseCase(sessionId, UserExit)`를 호출하도록 확정됨. 기존 세션을 서버에 정리 요청하지 않고 방치하던 동작이 이번에 없어짐.
- 남은 질문 개수(`{N}개의 질문이 남았어요`)는 서버 응답 필드가 없어 클라이언트가 로컬 타이머 기반 규칙(3분 미만 1개, 3~5분 미만 2개, 5~7분 3개, 7분 초과 4개)으로 직접 계산하는 것으로 확정. `GetInterviewElapsedTimeUseCase` + `InterviewTimeCalculator.HARD_CAP_MILLIS`(12분) 재사용, 임시 상수(`TEMP_REMAINING_QUESTION_COUNT=2`)는 제거.

### 모듈 경계 결정

- `feature:home:impl`이 `feature:interview:api`에 신규 의존(기존엔 `mypage`/`login`/`onboarding` api만 의존). Interview 목적지 진입은 `feature:onboarding`의 `NavigateToResult`와 동일하게 route를 직접 import하는 기존 컨벤션을 그대로 따름 — app 레벨에 새 콜백을 만들지 않음.
- "처음부터 시작" 확정의 세션 정리 시퀀스(`AbandonInterviewUseCase` → `RetainInterviewSessionForCleanupUseCase`)는 `InterviewErrorViewModel.abandon()`이 이미 쓰던 것과 동일한 조합을 `HomeViewModel`도 독립적으로 호출 — 공용 UseCase로는 이미 domain에 있어 재사용했지만, 이 시퀀스를 감싸는 상위 함수는 두 ViewModel이 각자 소유(다른 사용자 흐름이라 지금은 통합 안 함, 로직 자체는 domain에 있어 드리프트 위험은 낮음).
- `InterviewRecoveryStore`(재개·중단 결과를 Interview 화면에 넘기는 브릿지)는 `feature:interview:impl` 내부 전용이라 Home이 접근할 수 없음을 확인 — 그래서 Home은 이 메커니즘을 쓰지 않고 순수 네비게이션만 맡고, 실제 재개 판정은 Interview 화면 진입 후 그 화면 자신이 다시 수행하는 구조로 확정됨.

### 남은 이슈 / TODO

- `GoToReportRequested`는 여전히 미배선 — 리포트 상세를 보여줄 Feature 모듈 자체가 레포에 없음(`settings.gradle.kts`에 report 모듈 없음). Home 몫이 아니라 별도 기능 신설이 선행돼야 함.
- `ConfirmRestart` 오버레이의 "뒤로가기" 버튼은 `InProgress`로 되돌아가지 않고 기존과 동일하게 오버레이를 완전히 닫는다(`dismissSessionOverlay`). 리포트 시트를 다시 접으면 같은 오버레이가 재조회되어 기능상 문제는 없지만, "뒤로가기"라는 라벨과 완전히 일치하는 동작은 아님.
- "이미 종료된 세션" abandon 실패(`InterviewSessionAlreadyEndedException`)를 성공으로 흡수하는 처리를 `InterviewErrorViewModel`과 동일하게 맞췄으나, 서버가 Home 발(發) "처음부터 시작" 시나리오에서도 정확히 같은 코드를 반환하는지는 `api-docs.json`으로 재확인하지 않음(로그인 세션의 `ACCOUNT_SUSPENDED`/`NO_REMAINING_TICKET`처럼 문서 대조 안 거침).
- 남은 질문 개수 규칙(3/5/7분 구간)은 사용자가 지정한 규칙을 그대로 구현한 것이며, 서버 스펙 문서에 명시된 공식 규칙인지는 확인 안 됨. 서버가 실제 "남은 질문 수" 필드를 내려주기 시작하면 이 규칙 전체를 걷어내고 교체해야 함.

---

## MyPage (`:feature:mypage`) — 2026-08-12 (평가만, 코드 변경 없음 — 보류)

조립/수정 작업이 아니라 배선 평가만 진행하고 발견 사항은 전부 보류 확정됨. 코드 변경 없음.

### Effect → 목적지 매핑 (현재 상태 — 전부 빈 람다)

`app/navigation/di/MyPageNavigationModule.kt`에서 세 Effect가 미배선으로 확인됨:
- `ProfileEditRequested` → `onProfileEditRequested = {}`
- `ReportViewRequested(reportId)` → `onReportViewRequested = {}`
- `GuestFeedbackRequested(reportId)` → `onGuestFeedbackRequested = {}`

`CloseRequested`/`LogoutCompleted`/`WithdrawalCompleted`는 정상 배선(`goBack`/`replaceAll(Splash)`).

### 보류 사유 (사용자 확정)

- **프로필 편집**: 목적지 후보(`feature:login`의 `Onboarding`)가 있었으나 **최초 가입 온보딩 전용으로 작성돼 마이페이지에서 재사용 불가**로 확정(`Completed` Effect가 항상 `Home`으로 이동하도록 하드코딩돼 "편집 후 원래 화면 복귀" 시나리오 미지원). 사용자 정보 갱신용 별도 디자인 시안이 아직 없어 지금은 구현 불가.
- **리포트 보기**: 리포트 상세를 보여줄 Feature 자체가 레포에 없음(홈의 `GoToReportRequested` 미배선과 동일 원인). 화면 신설 선행 필요.
- **지인 피드백 받기**: `feature:feedback` 모듈은 전부 게스트(지인) 쪽 화면(`FeedbackOnboarding`/`Feedback`/`FeedbackReview`)뿐이고, 소유자가 "링크 생성" 하는 화면은 없음. 화면 신설 선행 필요.

### 참고 (배선 아닌 품질 이슈, 이번 범위 아님)

- 포트폴리오 삭제 불가 모달 문구가 "진행 중 면접"과 "이번 달 삭제 기회 소진" 두 원인을 구분 안 함(하나로 고정).
- `InterviewReportListItem.portfolioDeleted`가 domain엔 있는데 `MyPageUiMapper`가 UI 모델로 안 옮겨 "삭제된 포트폴리오" 배지가 리포트 카드에 안 뜸.

### 남은 이슈 / TODO

- 프로필 편집 화면 디자인 시안 나오면 재작업 — `Onboarding` 재사용이 아니라 별도 화면(또는 복귀 목적지 파라미터를 받는 변형)으로 설계돼야 함.
- 리포트 상세 Feature, 지인 피드백 링크 생성 Feature 둘 다 신설 필요 — 신설되면 `MyPageNavigationModule`의 빈 람다 두 곳만 채우면 됨(Intent/Effect 쪽은 이미 완성·테스트됨).

---

## FeedbackShare(소유자용) + 딥링크 수신 — 2026-08-12 (domain/data/딥링크 배선만, 소유자 화면 제외)

### 이번에 한 일

- `FeedbackShareRepository`/`GetFeedbackShareStatusUseCase`/`CreateFeedbackShareUseCase`/
  `MakeFeedbackSharePrivateUseCase`를 신설해 서버 FeedbackShare API(`GET`/`POST`/`PATCH
  /api/v1/feedback/sessions/{sessionId}/share`)를 domain/data 전 계층에 배선. `api-docs.json`
  스키마를 직접 대조해 필드·에러코드를 확정.
- ChottuLink Android SDK(`com.chottulink:android-sdk:1.2.0`, Maven Central) 의존성 추가,
  `local.properties`의 `CHOTTULINK_API_KEY`를 `BuildConfig`로 흘려 `DMinus14App.onCreate()`에서
  `ChottuLink.init()`.
- `AndroidManifest.xml`에 `hilit://feedback/{token}` 커스텀 scheme intent-filter를
  `MainActivity`에 추가(`launchMode="singleTop"`), `onNewIntent`/`onCreate`에서
  `ChottuLink.getAppLinkData(intent)`로 링크를 조회해 `FeedbackOnboarding(token)`으로
  `navigator.goTo` 배선. ChottuLink 조회가 비거나 실패하면 intent의 raw `data`로 폴백.

### 이번 범위에서 제외한 것 (사용자 확정)

- 소유자용 "링크 생성" 화면 UI 자체는 이번 범위 밖 — domain/data 계층(UseCase 3종)만 준비됨.
  화면이 생기면 그 화면이 `CreateFeedbackShareUseCase`/`GetFeedbackShareStatusUseCase`/
  `MakeFeedbackSharePrivateUseCase`를 호출하면 됨.
- `MyPage`의 `GuestFeedbackRequested` 콜백은 여전히 빈 람다 — 목적지 화면이 없어 배선 불가,
  화면 신설 후 진행.
- 딥링크 scheme/host(`hilit://feedback/{token}`)는 서버·기획과 정식 협의된 값이 아니라 iOS
  기존 구현을 그대로 재사용한 값(사용자 확정 지시). 서버가 다른 값을 확정하면 Manifest의
  `data android:scheme/host`와 `MainActivity`의 상수만 바꾸면 됨.
- ChottuLink SDK의 실제 Android 통합 문서(`docs.chottulink.com`)에 `onNewIntent` 파싱 예제가
  없어 `getAppLinkData(Intent)` 시그니처만으로 구현 — 실제 링크로 앱 동작을 검증하지 않음(로컬
  빌드·유닛테스트만 통과 확인).

### 모듈 경계 결정

- FeedbackShare는 `feature:feedback` 소유가 아니라 `domain`/`data`에 직접 둠 — 소유자 쪽
  기능 자체가 아직 어느 Feature 모듈에도 속하지 않기 때문. 화면 Feature가 정해지면 그
  `impl`이 이 UseCase 3종만 주입받으면 된다.
- FeedbackShare axis는 Guest Feedback과 같은 5개 값(GAZE/EXPRESSION/POSTURE/GESTURE/VOICE)
  이라 `domain.model.GuestFeedbackAxisCode`를 그대로 재사용(중복 enum 신설 안 함). wire DTO도
  `GuestFeedbackAxisCodeDto`를 재사용하되, 도메인↔DTO 변환 확장함수는 기존
  `GuestFeedbackMapper.kt`의 `private` 버전과 이름이 겹치지 않도록 `toShareAxis()`/
  `toShareAxisDto()`로 새로 명명(같은 패키지 내 동일 시그니처 확장함수 중복으로 인한 overload
  ambiguity 회피).

### 남은 이슈 / TODO

- 소유자용 링크 생성/상태조회/비공개전환 화면 신설 필요 — 신설되면 이번에 만든 UseCase 3종을
  그대로 호출.
- `MyPageNavigationModule`의 `onGuestFeedbackRequested` 배선은 위 화면이 생긴 뒤 진행.
- 딥링크 scheme/host 확정값을 서버·기획에 재확인 필요(현재는 iOS 값 그대로 차용).
- ChottuLink short link 생성 API(`create-links/android-create` 문서의
  `ChottuLink.createDynamicLink()` 빌더)는 이번에 사용하지 않음 — 소유자 화면에서 실제 공유
  링크(문자열)를 만들 때 서버 `POST .../share`가 반환하는 token을 그대로 조립할지, ChottuLink
  short link로 감쌀지는 화면 설계 시 결정 필요.

---

## Interview (`:feature:interview`) — 2026-08-12 (평가 + 최소 수정 1건)

### Effect → 목적지 매핑

`app/navigation/di/InterviewNavigationModule.kt` 기준, `interviewEntryBuilder`(feature 레벨)로 전달되는 콜백 5개.

- `InterviewEffect.PermissionDeniedExitRequested` / `PrerequisiteMissing` → `onNavigateHome` → `replaceAll(Home)`
- `InterviewEffect.NavigateToError(errorType)` → `onNavigateError` → `goTo(InterviewErrorRoute(errorType))`(면접 화면 위에 쌓임, 스택 유지)
- `InterviewErrorEffect.InterviewResumeConfirmed` → `onResumeInterview` → `navigator::goBack`(에러 화면만 벗기고 살아있는 Interview 화면으로 복귀)
- `InterviewErrorEffect.InterviewAbandonCompleted` → `onInterviewAbandoned`(=`onNavigateHome` 재사용) → `replaceAll(Home)`
- `InterviewErrorEffect.SttFailureAcknowledged` → `onSttAcknowledged` → `replaceAll(Home)`
- (이번에 수정) `InterviewEffect.InterviewEnded(reason)` → `onInterviewEnded` → 이제 `reason` 무관하게 `replaceAll(Home)`. 수정 전엔 `reason == ABANDONED`일 때만 이동하고 `COMPLETED`(자연/수동/하드캡 정상 종료 전부)는 아무 데도 안 가서 화면이 그 자리에 멈추는 버그였음.

### PRD·기존 문서 대비 확정/변경된 사실

- `docs/prd/interview.md` 6장("현재 연결·미디어 구현 공백")이 나열한 갭 — UseCase 미주입, ErrorViewModel의 A6/A7 미호출, NavigationModule 콜백 빈 함수 — 은 이번 세션 이전에 이미 대부분 메워져 있었음(`InterviewViewModel`이 다수 UseCase를 주입받고, `InterviewErrorViewModel`이 `confirmResume`/`abandon`을 실제로 호출). 확인만 했고 이번에 새로 구현한 건 아님.
- 유일하게 실사용 경로에 남아있던 진짜 버그: 면접을 정상적으로 끝내면(앱의 메인 해피패스) `InterviewCompletionReason.COMPLETED`가 오는데, app 콜백이 `ABANDONED`만 처리해서 화면이 멈춤. `InterviewViewModel.completeInterview()`(업로드 없는 경우)와 `finishUploadHandoff()`(업로드 핸드오프 끝난 뒤) 두 지점 모두 기본값 `COMPLETED`로 `InterviewEnded`를 보낸다는 것까지 코드 추적으로 확정 — 즉 정상 종료 100%가 이 버그를 탔음. 발견 즉시 최소 수정(홈으로 통일)함.
- 리포트 화면이 아직 없어 `COMPLETED`/`ABANDONED` 구분 자체가 지금은 의미가 없음(어차피 갈 곳이 홈 하나뿐) — 리포트 Feature가 생기면 이 둘을 다시 나눠야 한다는 주석을 코드에 남겨둠.

### 모듈 경계 결정

- `InterviewErrorRoute`는 `InterviewRoute` 위에 `goTo`로 얹는 구조(스택에 Interview가 남아있음)로 확정 — `goBack` 하나로 "재개"를 표현할 수 있는 이유가 이거임. `InterviewAbandonCompleted`/`SttFailureAcknowledged`만 `replaceAll`로 스택을 통째로 비움.
- `interviewEntryBuilder`가 `InterviewRoute`·`InterviewErrorRoute` 둘 다 한 곳에서 등록 — 두 화면이 콜백 5개를 공유하는 구조라 분리하지 않음(Login의 여러 화면이 `LoginNavigationModule` 하나를 공유하는 것과 같은 패턴).

### 남은 이슈 / TODO

- 리포트 화면이 생기면 `onInterviewEnded`를 `COMPLETED`(→리포트)/`ABANDONED`(→홈)로 다시 분기해야 함 — 지금 코드에 주석으로 남겨둠.
- PRD 6장의 세부 구현 사항(A3 스트리밍 인증 헤더, A4 media type, A9 별도 PUT 클라이언트)은 이번에 확인 안 함 — "배선"(Effect→목적지) 관점만 봤고, 녹화·병합·업로드 워커·타이머 상태머신·네트워크 모니터 내부 로직 정확성은 검증 범위 밖.
- `app/src/test/.../InterviewNavigationModuleTest.kt`는 실제 `InterviewNavigationModule.provideInterviewEntryInstaller`를 호출하지 않고 로컬 람다로 흉내내는 방식이라 회귀 보호력이 약함(이번에 버그를 못 잡았던 이유이기도 함). 이번엔 테스트 내용만 현재 동작에 맞게 고쳤고, `EntryProviderScope` 안에서 실제 모듈을 실행해 검증하는 방식으로의 교체는 안 함.
