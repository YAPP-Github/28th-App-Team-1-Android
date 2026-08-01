# Guest Feedback UI·ViewModel·Navigation 구현 계획

## 1. 계획 요약

이미 구현된 Guest Feedback의 DTO/API, Domain Model, Repository, Mapper와 UseCase 위에 비회원
지인 피드백 흐름의 세 화면을 구현한다.

- `FeedbackOnboarding`: 공유 링크 진입 조회, 요청 안내와 별칭 입력
- `Feedback`: 영상 재생, 지정 평가 항목별 4단계 선택과 선택 코멘트 작성
- `FeedbackReview`: 작성 내용 검토, 영상 다시보기와 최종 제출

세 화면은 `Intent`, `State`, `Effect`, `ViewModel`, `Screen`, `Content` 책임을 분리하고
`core:common`의 `MviIntent`, `MviState`, `MviEffect`, `MviViewModel`을 사용한다. ViewModel은
Navigation을 직접 실행하지 않고 현재 Feature에서 일어난 결과만 Effect로 발행한다. Screen은
Effect를 상위 callback으로 전달하고 실제 Navigation 3 back stack 변경은 `app`이 결정한다.

가장 단순한 구현을 우선한다. 화면 전용 UI를 성급하게 공용화하거나 범용 coordinator, reducer,
mapper 계층을 추가하지 않는다. 다만 세 Navigation entry 사이에서 영상 URL, 질문 경계, 별칭과
작성 중 평가를 메모리로만 전달해야 하므로, 승인된 하나의 구체적인
`GuestFeedbackFlowSession`만 `ActivityRetainedScoped`로 둔다. 이 객체는 저장소나 범용 상태
컨테이너가 아니며 한 번의 Guest Feedback 흐름 데이터만 보관하고 제출·종료·진입 실패 때 즉시
비운다.

Figma에서 확인한 제품 token과 이미 존재하는 `:designsystem` Composable을 우선 사용한다.
영상 플레이어, 평가 검토 카드와 다중 행 코멘트 입력처럼 현재 Design System에 없는 Android 또는
Feature 전용 요소만 `:feature:feedback:impl` 내부에 둔다. 제출 확인과 오류에 사용하는 공용
`HilitModal`은 Figma node `2302:6098`의 제품 시각으로 변경하고, 두 버튼은 node
`2302:5987`의 `HilitFixedBottomButton` 조합을 사용한다. 기존 public API는 확장하지 않고
Catalog Story만 함께 갱신한다.

Q1~Q36 답변은 본문에 반영했다. 답변 반영 뒤 구현을 막는 추가 모호성은 남지 않았으며, 문서 말미의
문답은 결정 이력으로 유지한다. 사용자의 구현 승인을 받은 뒤 코드 수정을 시작한다.

## 2. 조사한 계약과 현재 상태

### 2.1 적용 문서

- `docs/CONSTITUTION.md`: Feature 경계, 민감 데이터, MVI, Navigation 3와 오류 처리 불변조건
- `docs/ARCHITECTURE.md`: 작업별 세부 문서 라우팅
- `docs/architecture/module-system.md`: `feature:*`, `app`, `domain`, `designsystem` 책임
- `docs/architecture/navigation.md`: route/entry 제공과 app root Navigation 조립
- `docs/architecture/feature-ui.md`: Contract, ViewModel, Screen, Content와 Preview 분리
- `docs/architecture/error-handling.md`: Client 오류와 Network/Server/Unknown 오류 분기
- `docs/architecture/design-system.md`: 제품 token과 기존 공용 Composable 우선 사용
- `docs/architecture/global-dialog.md`: `showGlobalModal(...)`과 앱 전역 Modal queue
- `plan-phase-1.md`, `plan-phase-2.md`, `prd.md`: 이전 단계의 데이터·제품 계약

`specs/feature-request.md`는 Guest Feedback 명세가 아니라 Catalog 기능 요청 템플릿이므로 이번
기능의 제품 계약으로 사용하지 않는다.

### 2.2 현재 구현에서 재사용할 계약

- `EnterGuestFeedbackUseCase`
  - token의 양끝 공백을 제거하고 빈 token을 네트워크 호출 전에 차단한다.
  - `OPEN`은 `GuestFeedbackEntry.Open`, 그 외 gate는 실패가 아닌
    `GuestFeedbackEntry.Unavailable`로 반환한다.
- `SubmitGuestFeedbackUseCase`
  - 진입 시 받은 지정 축을 빠짐없이 한 번씩, `1..4` 단계로 평가했는지 검증한다.
  - 현재는 별칭이 비어 있으면 `익명의 지인`으로 정규화하지만, 이번 단계에서 트리밍 후 필수,
    줄바꿈 없음, `String.length` 1~12자 계약으로 변경한다.
  - `GuestFeedbackSubmission.nickname`도 `String?`에서 `String`으로 바꿔 Domain의 필수 계약을
    컴파일 시점에 드러낸다. 서버 wire DTO의 nullable 계약은 바꾸지 않지만 Repository에는 검증된
    non-null 별칭만 전달한다.
  - 코멘트는 비어 있으면 빈 문자열로 정규화한다.
  - 코멘트는 트리밍 후 Kotlin `String.length` 기준 최대 100자다.
  - 전반 피드백 300자는 현재 Domain/API에 없으므로 이번 UI에도 추가하지 않는다.
- `GuestFeedbackEntry.Open`
  - 요청자 표시명, `1..5`개의 평가 축, 민감한 영상 URL, 질문 경계와 `submissionOpen`을 제공한다.
- Guest 전용 오류
  - 요청 처리, 공유 종료, 최대 인원, 중복 제출을 서로 다른 Domain 예외로 구분한다.
  - Network/Server/Unknown은 공통 예외로 전달한다.
- 현재 `FeedbackRoute.kt`
  - `FeedbackOnboarding`, `Feedback`, `FeedbackReview`가 인자 없는 `object`로 존재한다.
  - 세 번째 route는 `FeedbackResult`가 아니라 원복된 `FeedbackReview`를 사용한다.
  - 공유 token을 전달하는 route 또는 App Link 진입 코드는 아직 없다.

### 2.3 현재 인프라의 차이

- `:feature:feedback:impl`은 아직 `:core:common`에 의존하지 않아 MVI 기반을 사용하려면 이
  의존성을 추가해야 한다.
- `app`은 `:feature:feedback:impl`에 의존하지 않아 Feedback entry installer가 앱에 조립되지
  않는다.
- Version Catalog와 프로젝트에는 영상 재생용 Media3 의존성이 없어 승인된
  `media3-exoplayer`, `media3-ui-compose`를 추가해야 한다.
- 구현 계획 작성 시점의 Android 공식 문서가 안내하는 Media3 `1.10.1`을 사용하며, 제품 제어를
  직접 구성할 수 있는 [`PlayerSurface`](https://developer.android.com/media/media3/ui/compose)를
  사용한다. 기본 의존성 구성은
  [Media3 ExoPlayer 시작 문서](https://developer.android.com/media/media3/exoplayer/hello-world)를
  따른다.
- 전역 Modal 호출·queue·Host는 구현되어 있지만 `error-handling.md`의 `GlobalErrorHandler`와
  `GlobalAppEvent`는 아직 구현되어 있지 않다.
- `Navigator.onExit`의 실제 앱 종료 구현은 TODO다. 이번 단계에서 app 계층이 해당 callback을
  현재 Activity task의 `finishAffinity()`로 연결하되 process를 강제 종료하지 않는다.

## 3. Figma 반영 범위

확인한 node는 다음과 같다.

| 화면 | Figma node  | 확인한 핵심 구성 |
|---|-------------|---|
| 온보딩 메인 | `1855:8498` | 요청자 이름과 강조 제목, 안내 일러스트, 두 안내 행, 고정 하단 시작 버튼 |
| 별칭 입력 sheet | `2094:7566` | 강조 제목, 가운데 밑줄 입력, 고정 하단 다음 버튼 |
| 평가 기본 화면 | `1855:9821` | 전체 화면 영상, 하단 지정 항목 탭 |
| 최초 진입 안내 | `1855:8702` | 영상 위 scrim, 요청자 이름과 `태도`, `이렇게` 강조 안내 |
| 평가 메뉴 열림 | `2150:7278` | 축소 영상과 크게 보기, 축 탭, 질문형 제목, 4단계 선택, 선택 코멘트, 종료 버튼 |
| 검토 화면 | `2101:8781` | 감사 제목, 영상 다시보기, 축별 카드, 전송 버튼 |
| 코멘트 입력 Modal | `2227:5014` | 선택 코멘트 제목·닫기·다중 행 입력·하단 `다음` 버튼 |
| 공용 Modal | `2302:6098` | 제품 illustration, 제목·보조 문구·안내 영역과 고정 하단 버튼을 가진 제품 Modal |
| Modal 하단 버튼 | `2302:5987` | 한 버튼, 비활성, 같은 색 두 버튼, 흰색·검정 두 버튼 variant |

Figma가 명시한 색상과 글꼴은 현재 `HilitTheme.colors`와 `HilitTheme.typography`의 token과
일치한다. 화면 구현 시 Material 기본값에 기대지 않고 다음 기존 Composable과 token을 직접
사용한다.

| Figma 요소 | 우선 사용할 기존 구현 |
|---|---|
| 강조 제목 | `HilitText` + `withHilitTextHighlight` |
| 고정 하단 버튼 | `HilitFixedBottomButton` |
| 별칭 밑줄 입력 | `HilitBottomOutlinedTextField` |
| 평가 축 탭 | `HilitTab`을 지정 축 수만큼 배치 (`HilitTabRow`는 1개 축을 허용하지 않음) |
| 4단계 선택 버튼 | `HilitMediumButton` |
| 긍정·아쉬움·선택 tag | `HilitTag` |
| 선택 코멘트 진입 | `HilitOptionalButton` |
| 코멘트 입력 Modal | Feature 공용 `GuestFeedbackCommentModal`에서 Design System token·icon·button 사용 |
| 영상 다시보기 | `HilitMiniButton` + `HilitIconAsset.Video` |
| 영상 크게 보기 | `HilitIconAsset.Expand` |
| 로딩 | `HilitLoadingIndicator` |
| 확인·오류 Modal | `showGlobalModal(...)` → 앱 `GlobalDialogHost` → Figma에 맞게 변경한 `HilitModal` |
| Modal 두 버튼 | 기존 `HilitFixedBottomButton` 두 개를 가로로 배치하고 확정된 variant 사용 |

Figma의 인물 사진은 구현·테스트·Preview asset으로 저장하지 않는다. 런타임에는 API가 제공한
영상 URL만 메모리에서 플레이어에 전달하고, Preview와 테스트에는 사람을 식별할 수 없는 단색
placeholder를 사용한다. 온보딩 제품 일러스트는 Figma에서 vector로 export 가능한지 확인한 뒤
Feature 전용 drawable 한 개로만 추가한다.

## 4. 화면별 MVI 설계

Contract는 화면마다 하나씩 두되, 표시만을 위한 중복 UiModel mapper는 만들지 않는다. Domain
모델을 그대로 노출하면 UI가 도메인 규칙을 재해석하게 되는 부분에만 작은 immutable UiModel을
둔다.

### 4.1 FeedbackOnboarding

#### Intent

- `Load(token)`: 화면 최초 진입 시 공유 링크를 조회한다.
- `StartClicked`: 별칭 입력 bottom sheet를 연다.
- `NicknameChanged(value)`: 입력 중 별칭을 갱신한다.
- `NicknameConfirmed`: 별칭을 확정하고 평가 흐름 시작을 요청한다.
- Figma에 없는 `다음에 하기` Intent는 추가하지 않는다.

#### State

- `isLoading`
- `requesterName`
- `nickname`
- `canConfirmNickname`: 양끝 공백을 제거한 별칭이 줄바꿈 없이 `String.length` 1~12자일 때만
  `true`
- `isNicknameSheetVisible`
- 작성 가능한 `axes`, `videoUrl`, `questionBoundaries`, `submissionOpen`
- 화면에 지속해서 표시해야 하는 Client 오류가 확정될 때만 해당 상태

민감한 영상 URL과 질문 경계는 메모리 State/flow session에서만 유지하고 `SavedStateHandle`,
Bundle, DataStore, 파일, 로그, analytics 또는 crash report에 기록하지 않는다.

#### Effect

- `FeedbackReady`: 진입 데이터와 별칭이 flow session에 준비됐음을 상위에 알린다.
- `FlowUnavailable`: Modal 처리 후 Guest 흐름 종료가 필요함을 상위에 알린다.

Effect에는 token, 영상 URL, 질문, 별칭이나 피드백 내용을 넣지 않는다. 공통
`MviViewModel.sendEffect()`가 실패한 Effect를 문자열로 기록할 수 있으므로 민감 값이 Effect의
`toString()`을 통해 로그에 노출되지 않게 한다.

#### ViewModel과 UI

- `LaunchedEffect(token)`에서 `Load(token)`을 한 번 전달한다.
- `EnterGuestFeedbackUseCase` 성공 시 `Open`과 `Unavailable`을 명시적으로 분기한다.
- `Open` 데이터는 세 화면이 공유하는 메모리 session에 저장한다.
- 메인 Content는 Figma의 제목, illustration, 안내 두 행과 하단 버튼을 렌더링한다.
- 시작 버튼은 Material3 `ModalBottomSheet`를 열되 내부 입력·버튼·token은 Design System을
  사용한다. sheet 열림 여부는 State가 소유한다.
- 별칭은 UI와 Domain 모두에서 필수로 받고 줄바꿈을 허용하지 않는다. 양끝 공백을 제거한 값을
  기준으로 `String.length` 1~12자일 때만 다음 버튼을 활성화하고, Domain 유스케이스도 같은
  순서로 정규화·검증해 UI 우회를 차단한다.
- PRD에만 있는 `다음에 하기`는 추가하지 않고 최신 Figma 구성을 우선한다.
- `PRIVATE`, `EXPIRED`, `FULL`, `ALREADY_SUBMITTED`와 `submissionOpen=false`는 영상·평가로
  진행하지 않고 안내 Modal 뒤 흐름을 종료한다. 네 gate의 Modal은 모두 `종료하기` 단일 버튼,
  `dismissible=false`를 사용하고 확인 시 session을 비운 뒤 앱을 종료한다.

| 상태 | 제목 | 본문 |
|---|---|---|
| `PRIVATE` | `비공개 피드백` | `요청자가 피드백을 비공개로 전환했어요.` |
| `EXPIRED` | `피드백 기간 종료` | `피드백을 작성할 수 있는 기간이 끝났어요.` |
| `FULL` | `피드백 인원 마감` | `피드백 가능한 최대 인원이 모두 찼어요.` |
| `ALREADY_SUBMITTED` | `이미 제출한 피드백` | `이 기기에서 이미 피드백을 제출했어요. 중복으로 제출할 수 없어요.` |

`OPEN + submissionOpen=false`도 제목 `피드백 작성 마감`, 본문
`현재는 피드백을 작성할 수 없어요.`, `종료하기` 단일 버튼, `dismissible=false`를 사용하고 확인
시 session을 비운 뒤 앱을 종료한다.

### 4.2 Feedback

level은 `4`가 가장 긍정적이고 `1`이 가장 아쉬운 값이다. 버튼은 긍정에서 아쉬움 순으로
`4`, `3`, `2`, `1`을 표시하고 API에는 선택한 숫자를 그대로 전달한다.

| 축 | level 4 | level 3 | level 2 | level 1 |
|---|---|---|---|---|
| `GAZE` | `잘 맞춤` | `꽤 맞춤` | `가끔 피함` | `자주 피함` |
| `EXPRESSION` | `자연스러움` | `무표정` | `좀 굳음` | `많이 굳음` |
| `POSTURE` | `꼿꼿함` | `긴장함` | `좀 산만함` | `많이 산만함` |
| `GESTURE` | `자연스러움` | `긴장함` | `좀 산만함` | `많이 산만함` |
| `VOICE` | `적당함` | `너무 큼` | `조금 작음` | `너무 작음` |

평가 메뉴 제목은 질문형 문장으로 가공하지 않고 축별로 `시선`, `표정`, `자세`, `손동작`,
`목소리`를 그대로 표시한다.

#### Intent

- `LoadSession`: 메모리 session에서 진입 데이터와 별칭을 읽는다.
- `AxisSelected(axis)`: 현재 평가할 지정 축을 선택한다.
- `RatingSelected(axis, level)`: 해당 축의 4단계 평가를 갱신한다.
- `CommentEditorClicked(axis)`: 기존 코멘트를 임시 편집값으로 복사하고 선택 코멘트 입력을 연다.
- `CommentChanged(value)`: 최대 100자 기준으로 임시 편집값만 갱신한다.
- `CommentConfirmed`: 임시 편집값을 해당 축의 메모리 초안에 저장하고 입력을 닫는다.
- `CommentDismissed`: 임시 편집값을 버리고 편집 전 코멘트를 유지한 채 입력을 닫는다.
- `VideoExpanded`, `VideoCollapsed`: 영상 표시 모드를 전환한다.
- `VideoIntroCompleted`: player Composable이 2초 경과 또는 터치에 따른 300ms 페이드아웃 완료를
  보고하면 최초 안내를 닫힌 상태로 기록한다.
- `VideoPlaybackFailed`: player Composable이 URL이나 오류 원문을 포함하지 않은 치명적 재생 실패를
  보고한다.
- `ReviewClicked`: 모든 지정 축 평가 완료 후 검토 이동을 요청한다.

#### State

- `requesterName`, 지정 `axes`, 선택한 `selectedAxis`
- 축별 `level`과 `comment`를 가진 immutable 작성 초안
- `isCommentEditorVisible`, 코멘트 `editingValue`, `isVideoExpanded`
- `isVideoIntroVisible`
- `questionBoundaries`: ViewModel/session에 유지하지만 이번 UI에서는 소비하지 않는 진입 데이터
- `canReview`: 모든 지정 축에 `1..4` level이 존재하는지에서 계산

별도의 축별 ViewModel이나 범용 form reducer를 만들지 않고 하나의 `FeedbackViewModel`이 작은
Map 또는 List copy로 초안을 갱신한다. Domain의 최종 검증은 `SubmitGuestFeedbackUseCase`에
유지하고 UI의 `canReview`는 버튼 활성화를 위한 선행 검사만 담당한다.

#### Effect

- `ReviewReady`: 작성 초안이 session에 반영되어 검토 화면으로 이동 가능함을 알린다.
- 현재 확정 범위에는 플레이어를 위한 별도 Navigation Effect를 두지 않는다.

#### ViewModel과 UI

- 지정된 축만 Figma 순서대로 표시하며 축 수가 1개여도 동작한다.
- 영상은 화면 전용 `GuestFeedbackVideoPlayer` Composable로 렌더링한다. 이 Composable이
  `ExoPlayer` 생성·준비·재생·일시정지·영상 터치 전환과 runtime 재생 상태를 단독으로 소유하고,
  ViewModel에는 확대 모드·최초 안내 표시와 치명적 재생 실패만 전달한다.
- Media3 `ExoPlayer`와 `media3-ui-compose`의 `PlayerSurface`를 사용한다. 제품 전용 제어를 직접
  배치하므로 Material3 완성형 플레이어 모듈은 추가하지 않는다.
- 최초 Feedback 진입에서는 Figma node `1855:8702`의 검정 scrim과 안내를 먼저 표시한다.
  player Composable이 2초 timer·화면 전체 터치·300ms 페이드아웃을 처리하고, 완료 즉시 자동
  재생한 뒤 `VideoIntroCompleted`를 한 번 전달한다. 이 안내는 한 Guest 흐름에서 한 번만 보여주며
  Review에서 돌아왔을 때 다시 표시하지 않는다. 이후 영상 영역 터치는 재생/일시정지만 전환한다.
- 음량은 기기 설정을 따르고 앱에서 강제 음소거하지 않는다. 크게 보기 버튼은 같은 화면 안에서
  영상 영역을 전체 크기로 전환하며 별도 seek, 앞뒤 이동, 반복, 재생 속도와 화면 회전 기능은
  추가하지 않는다.
- `GuestFeedbackVideoPlayer`가 Lifecycle을 관찰해 백그라운드 진입 시 일시정지하고 화면 수명이
  끝나면 Player를 해제한다.
- 기본 화면은 영상과 하단 축 탭만, 선택 화면은 축소 영상·질문형 제목·4단계 버튼·선택 코멘트와
  하단 검토 버튼을 표시한다.
- 4단계 선택은 session의 메모리 초안을 즉시 갱신한다. 코멘트 입력은 Modal 내부의 임시 편집값만
  바꾸고 `다음`을 눌렀을 때만 session 초안에 저장한다. 닫기 icon, 바깥 터치와 시스템 뒤로가기는
  임시 값을 버리고 편집 전 값으로 되돌린다. 서버 중간 저장은 하지 않으며 API 제출은 Review
  화면의 최종 확인 뒤 한 번만 수행하고 `저장 중...` 문구는 표시하지 않는다.
- `왜 그렇게 느꼈나요?`를 누르면 Figma node `2227:5014`의
  `GuestFeedbackCommentModal`을 연다. 코멘트 필드는 최대 3줄 높이를 유지하고 그 이상은 내부
  스크롤한다. `String.length` 100자 기준과 현재 길이를 표시하며 실제 제출 유스케이스 검증과
  기준을 맞춘다.
- Media3가 치명적 영상 준비·재생 오류를 보고하면 player Composable이 `VideoPlaybackFailed`를
  한 번 전달하고, ViewModel은 재생과 평가를 차단해 종료 안내 Modal을 표시한다.
  제목 `영상을 재생할 수 없어요`, 본문
  `영상 재생 중 문제가 발생했어요. 앱을 종료한 뒤 다시 시도해주세요.`, `종료하기` 단일 버튼,
  `dismissible=false`를 사용하고 확인 시 session을 비운 뒤 앱을 종료한다.
- 질문 경계는 ViewModel State에 포함하되 질문 목록, 구간 표시와 seek 동작에는 사용하지 않는다.

준비·재생·일시정지 같은 runtime 플레이어 상태는 `FeedbackState`나 session에 복제하지 않는다.
ViewModel 단위 테스트는 최초 안내 표시와 치명적 오류 분기만 검증하고, 플레이어 자체 동작은 해당
Composable의 수명 주기 및 수동 검증으로 확인한다.

### 4.3 FeedbackReview

#### Intent

- `LoadSession`: 요청자, 별칭, 지정 축과 초안을 읽는다.
- `ReplayVideoClicked`: 작성 화면으로 돌아가 영상을 다시 보도록 요청한다.
- `EditCommentClicked(axis)`: 선택한 축의 기존 코멘트를 임시 편집값으로 복사해 공용 입력 Modal을
  연다.
- `CommentChanged(value)`: Review에서도 임시 편집값만 갱신한다.
- `CommentConfirmed`: 임시 편집값을 선택한 축의 메모리 초안에 저장한다.
- `CommentDismissed`: 임시 편집값을 버리고 편집 전 코멘트를 유지한다.
- `SubmitClicked`: 제출 전 확인 Modal을 요청한다.
- `SubmitConfirmed`: `SubmitGuestFeedbackUseCase`를 한 번 호출한다.

#### State

- 요청자 이름과 작성자 별칭
- 축별 표시명, 선택한 단계 문구, 긍정/아쉬움 색상 방향과 선택 코멘트
- 현재 코멘트 편집 대상, `editingValue`와 `isCommentEditorVisible`
- `isSubmitting`

#### Effect

- `ReplayRequested`: 이전 평가 화면으로 이동할 것을 상위에 알린다.
- `SubmissionCompleted`: session 정리가 끝났으며 성공 Toast 뒤 앱을 종료해야 함을 상위에 알린다.

#### ViewModel과 UI

- Figma처럼 축별 카드를 지정 축 수만큼 나열하고 코멘트가 비어 있으면 인용 행을 생략한다.
- 단계 문구의 긍정/아쉬움 방향에 따라 기존 Blue/Red token과 `HilitIconAsset.Info`를 사용한다.
- 영상 다시보기는 초안을 보존한 채 Feedback 화면으로 돌아간다.
- 카드의 편집 icon은 해당 축의 `GuestFeedbackCommentModal`을 열어 Feedback 화면과 동일한
  입력 UI로 코멘트만 수정한다. Review에서는 4단계 평가를 수정하지 않는다.
- 전송 버튼은 `isSubmitting` 동안 재입력을 막는다.
- 제출 직전 Figma에 맞게 변경한 `HilitModal`을 제목 `피드백 제출`, 본문
  `제출하면 다시 고칠 수 없어요.`, `제출`·`취소` 두 버튼, `dismissible=true`로 한 번 호출하고
  Confirm일 때만 제출한다. 부가 정보 영역과 illustration은 표시하지 않고 제목·본문과 가로 두
  버튼만 사용한다. node `2302:5987`의 `2color` variant처럼 왼쪽에는 흰색 보조 버튼 `취소`,
  오른쪽에는 검정 주 버튼 `제출`을 배치한다.
- 성공 시 민감한 session을 먼저 비우고 성공 Toast를 요청한 다음 앱 종료 Effect를 발행한다.
  Toast 문구는 `피드백을 제출했어요.`이며 표시 직후 `finishAffinity()`를 요청한다.
- Guest 비즈니스 오류는 계획 2단계에서 확정한 네 메시지로 분기하고 오류별 제목,
  `종료하기` 단일 버튼, `dismissible=false`를 사용한 뒤 앱 종료 Effect를 발행한다.

## 5. Navigation 3와 메모리 수명

승인된 가장 작은 계약은 다음과 같다.

1. `FeedbackOnboarding` route만 공유 token을 받는다. route 문자열 표현에는 token을 노출하지
   않도록 `toString()`을 고정된 이름으로 가리고 이 동작을 테스트한다.
2. Onboarding 진입 성공 후 `GuestFeedbackFlowSession`이 `Open` 데이터와 별칭을 메모리로
   보관한다.
3. `Feedback`과 `FeedbackReview` route에는 민감한 payload를 싣지 않는다.
4. 각 ViewModel은 같은 `ActivityRetainedScoped` session을 주입받아 현재 단계 데이터를 읽고
   immutable copy로 갱신한다.
5. Onboarding에서 Feedback을 시작할 때 `replaceAll(Feedback)`로 Onboarding을 back stack에서
   제거한다.
6. Feedback에서 검토를 시작할 때 `goTo(FeedbackReview)`, Review에서 시스템 뒤로가기 또는
   영상 다시보기를 선택할 때 `goBack()`으로 Feedback에 돌아간다.
7. Onboarding 첫 뒤로가기는 `2번 뒤로가기 하면 종료된다` Toast를 표시한다. 2초 안의 두 번째
   뒤로가기는 앱 종료를 요청하고, 2초가 지나면 다시 첫 입력으로 취급한다.
8. Feedback 뒤로가기는 제목 `피드백을 종료하시겠습니까?`, 본문 없음, `계속 작성`·`종료하기`
   두 버튼, `dismissible=true`인 확인 Modal을 연다. 닫기·바깥 터치·시스템 뒤로가기는 작성을
   계속하고, `종료하기`를 선택한 경우에만 session을 비우고 앱 종료를 요청한다. Onboarding으로는
   돌아가지 않는다. node `2302:5987`의 `2color` variant를 사용해 왼쪽 흰색 버튼은
   `계속 작성`, 오른쪽 검정 버튼은 `종료하기`로 둔다.
9. 오류·gate 차단 시에는 session을 비운 뒤 앱 종료를 요청한다. 제출 성공 시에는 session 정리,
   성공 Toast 요청, 앱 종료 요청 순서를 지킨다.
10. process 종료 후 복원하지 않는다. 다시 링크로 진입하면 GET부터 새로 시작한다.

이 방식은 민감 데이터를 route 문자열, `SavedStateHandle`, 직렬화 가능한 NavKey나 디스크에
복사하지 않으면서 세 route를 실제 Navigation entry로 유지하기 위한 최소 상태 전달 장치다. 모든
앱 종료 Effect는 `app`에서 process를 강제 종료하지 않고 현재 Activity task의
`finishAffinity()` 호출로 해석한다.

Feature entry builder는 다음과 같이 목적지가 아니라 Feature 결과 callback을 받는다.

- Onboarding Screen: `onFeedbackReady`, `onFlowUnavailable`
- Feedback Screen: `onReviewReady`, `onExitConfirmed`
- Review Screen: `onReplayRequested`, `onSubmissionCompleted`

`app`만 이 결과를 실제 route/back stack 변경으로 해석한다. ViewModel, Contract와 Content는
`Navigator`, 다른 Feature route 또는 app 구현을 참조하지 않는다.

## 6. 오류·Modal 처리

### 6.1 Feature가 직접 처리할 오류

- `GuestFeedbackValidationException`: 제목 `피드백을 제출할 수 없어요`, 본문
  `작성한 내용을 확인한 뒤 다시 시도해주세요.`, `확인` 단일 버튼, `dismissible=true`로 표시하고
  Review에 남긴다. 사용자는 코멘트를 Review에서 수정하고, level을 확인해야 하면 기존
  `영상 다시보기`로 Feedback에 돌아가 수정한 뒤 다시 제출한다. 별칭은 Onboarding과 Domain에서
  선행 차단하고, 축 누락·중복은 UI/session 단위 테스트로 정상 흐름에서 발생하지 않게 한다.
- `GuestFeedbackRequestException`: 제목 `요청 오류`, 본문
  `서버 요청에 실패했습니다. 앱을 재실행하고 다시 시도해주세요.`
- `GuestFeedbackShareClosedException`: 제목 `피드백 기간 종료`, 본문
  `피드백 가능한 기간이 지났습니다.`
- `GuestFeedbackCapacityFullException`: 제목 `피드백 마감`, 본문
  `최대 피드백 가능 인원을 초과하여 더 이상 피드백을 받을 수 없습니다.`
- `GuestFeedbackAlreadySubmittedException`: 제목 `이미 제출한 피드백`, 본문
  `이미 이 기기에서 피드백한 이력이 있습니다. 중복 피드백은 할 수 없습니다.`

이 오류들은 ViewModel의 Intent coroutine에서 `showGlobalModal(...)`을 `종료하기` 단일 버튼과
`dismissible=false`로 호출한다. 확인 결과에는 session을 비우고 앱 종료 Effect를 발행한다.
ViewModel이 `HilitModal`, app Manager나 Android UI를 직접 참조하지 않는다.

### 6.2 영상 재생 오류

Media3의 영상 준비·디코딩·스트림 재생 실패는 Domain 네트워크 호출 예외가 아니라
`GuestFeedbackVideoPlayer`가 보고하는 Feature 오류다. player Composable은 재생을 멈춘 뒤
치명적 실패를 한 번 보고하고, Feedback은 평가 입력을 차단한 뒤 제목 `영상을 재생할 수 없어요`, 본문
`영상 재생 중 문제가 발생했어요. 앱을 종료한 뒤 다시 시도해주세요.`, `종료하기` 단일 버튼,
`dismissible=false`인 Modal을 표시한다. 확인 시 session을 비우고 앱 종료 Effect를 발행한다.

### 6.3 전역 오류

Architecture 계약상 `NetworkUnavailableException`, `ServerException`, `UnknownException`은
Feature별 Modal이 아니라 전역 app event 경로로 보내야 한다. 승인된 다음 최소 구성만 보완한다.

- `core:common`: Domain 예외 타입에 의존하지 않는 플랫폼 독립 `GlobalAppEvent`,
  `GlobalErrorHandler`
- `app`: event를 한 곳에서 수집해 기존 `showGlobalModal(...)` 또는 app-level Toast에 연결
- `FeedbackViewModel`: 오류를 분류해 `GlobalErrorHandler`에 전달하고 loading 상태만 복구

Network와 Server event는 제목 `연결 오류`, 본문
`네트워크 또는 서버 오류가 발생했어요. 잠시 후 다시 시도해주세요.`, `종료하기` 단일 버튼,
`dismissible=false`인 Dialog로 표시한다. Feature는 event를 발행하기 전에 Guest session을
비우고, app은 버튼 확인 시 `finishAffinity()`로 앱을 종료한다. Unknown event는 예외 원문을 UI에
노출하지 않고 `알 수 없는 오류가 발생했어요.` Toast를 표시하며 현재 화면에 남는다.

Guest 전용 오류 문구나 민감한 payload를 공통 event에 넣지 않고, 기존 Modal queue를 중복
구현하지 않는다.

## 7. 수정·추가 예상 파일

Q&A 답변에 따라 이름과 개수는 줄일 수 있지만 책임 경계는 다음과 같이 유지한다.

| 파일 | 구분 | 책임 |
|---|---|---|
| `feature/feedback/api/.../FeedbackRoute.kt` | 수정 | token을 받는 시작 route와 인자 없는 내부 route 계약 확정 |
| `domain/src/main/kotlin/com/dminus14/app/domain/model/GuestFeedback.kt` | 수정 | `nickname: String`의 필수 1~12자 제출 모델 계약 반영 |
| `domain/src/main/kotlin/com/dminus14/app/domain/usecase/SubmitGuestFeedbackUseCase.kt` | 수정 | 별칭 트리밍, 필수·줄바꿈·1~12자 검증과 기존 제출 정규화 |
| `domain/src/test/kotlin/com/dminus14/app/domain/usecase/GuestFeedbackUseCaseTest.kt` | 수정 | 익명 정규화 테스트를 필수 별칭의 성공·차단 경계 테스트로 교체 |
| `feature/feedback/impl/build.gradle.kts` | 수정 | `:core:common`, ViewModel 테스트와 승인된 영상 플레이어 의존성만 추가 |
| `feature/feedback/impl/.../session/GuestFeedbackFlowSession.kt` | 추가 | 세 entry 사이의 진입 데이터·별칭·작성 초안을 메모리로만 보관·정리 |
| `feature/feedback/impl/.../onboarding/FeedbackOnboardingContract.kt` | 추가 | 온보딩 Intent/State/Effect |
| `feature/feedback/impl/.../onboarding/FeedbackOnboardingViewModel.kt` | 추가 | 진입 UseCase, gate와 별칭 처리 |
| `feature/feedback/impl/.../onboarding/FeedbackOnboardingScreen.kt` | 추가 | ViewModel 연결, 메인 Content와 이름 sheet |
| `feature/feedback/impl/.../feedback/FeedbackContract.kt` | 추가 | 평가 Intent/State/Effect와 화면 전용 draft UiModel |
| `feature/feedback/impl/.../feedback/FeedbackViewModel.kt` | 추가 | 4→1 축별 초안, 임시 코멘트, 최초 안내와 치명적 영상 오류, 검토 Effect |
| `feature/feedback/impl/.../feedback/FeedbackScreen.kt` | 추가 | 영상·축 탭·평가 메뉴·코멘트 Content |
| `feature/feedback/impl/.../review/FeedbackReviewContract.kt` | 추가 | 검토/제출 Intent/State/Effect |
| `feature/feedback/impl/.../review/FeedbackReviewViewModel.kt` | 추가 | 코멘트 임시 편집·확정, 최종 확인, Submit UseCase와 오류 분기 |
| `feature/feedback/impl/.../review/FeedbackReviewScreen.kt` | 추가 | 검토 카드, 코멘트 Modal, 영상 다시보기와 제출 Content |
| `feature/feedback/impl/.../component/GuestFeedbackCommentModal.kt` | 추가 | Feedback과 Review가 공유하는 Figma 코멘트 입력 UI |
| `feature/feedback/impl/.../component/GuestFeedbackVideoPlayer.kt` | 추가 | 최초 안내 timer·fade, Player 생성·준비·재생·일시정지·터치 전환·Lifecycle 해제와 치명적 오류 보고 |
| `feature/feedback/impl/.../navigation/FeedbackEntryBuilder.kt` | 추가 | 세 route와 Screen callback 연결 |
| `app/build.gradle.kts` | 수정 | `:feature:feedback:impl` 조립 의존성 |
| `app/.../navigation/di/FeedbackNavigationModule.kt` | 추가 | Feedback entry installer callback을 Navigator 정책에 연결 |
| `feature/feedback/impl/src/main/res/drawable/...` | 추가 | Figma 온보딩 illustration 등 Feature 전용 vector만 보관 |
| `feature/feedback/impl/src/main/res/values/strings.xml` | 추가 | Guest 화면의 확정된 한국어 제품 문구 |
| 각 화면 ViewModel 단위 테스트 | 추가 | Intent, State, Effect, UseCase 위임, 오류와 session 정리 검증 |
| `feature/feedback/impl/src/androidTest/.../GuestFeedbackContentTest.kt` | 추가 | ViewModel 없는 Content의 사용자 동작·상태 전환·접근성 검증 |
| `core:common`과 `app`의 전역 오류 파일 | 추가/수정 | Architecture가 요구하지만 현재 빠진 전역 오류 경로 보완 |
| `gradle/libs.versions.toml` | 수정 | Media3 `1.10.1`과 `media3-exoplayer`, `media3-ui-compose` 등록 |
| `designsystem/.../component/modal/HilitModal.kt` | 수정 | 기존 public API를 유지하고 Figma 제품 시각과 `HilitFixedBottomButton` 한·두 버튼 상태를 내부 조합 |
| `catalog/.../hilitmodal/HilitModalStories.kt` | 수정 | 제품 Modal의 한 버튼·두 버튼·닫기 차단·긴 문구 상태 갱신 |

서버가 nullable `nickname` 키를 받는 wire 계약은 phase 1 범위 그대로 유지하므로 data DTO와 Remote
DataSource의 nullable 타입은 변경하지 않는다. Domain에서 검증된 `String`은 기존 Repository를
통해 nullable wire 인자에 안전하게 전달할 수 있으므로 data 계층 수정은 추가하지 않는다.

화면 내부에서 한 번만 쓰는 작은 Composable은 각 Screen 파일에 `private`로 둔다. 동일한 UI가
세 화면 중 둘 이상에서 실제로 필요할 때만 `component/`로 이동한다. 이번에는 Feedback과
Review가 같은 코멘트 Modal을 사용하므로 Feature 공용 컴포넌트로 둔다. `HilitModal`은 기존
public API를 그대로 유지하고 내부 시각만 변경하므로 illustration·부가 정보·표시 옵션 같은 새
parameter나 adapter Controls를 추가하지 않는다. 기존 `DMinusDialogTest`의 버튼 callback·dismiss
검증과 기존 Catalog adapter 재열기 테스트를 수정 없이 재사용하며, 제품 상태는 Catalog Story에서
확인한다. 이번 변경만을 위한 별도 common test 파일은 만들지 않는다.

최초 안내 표시 여부는 session에 넣지 않고 `FeedbackViewModel`의 `isVideoIntroVisible`에만 둔다.
Review가 위에 쌓인 동안 기존 Feedback entry와 ViewModel이 유지되므로 뒤로가기·영상 다시보기에서
안내가 다시 나타나지 않는다. Activity 재생성은 ViewModel 수명 범위에서만 유지하고 process 종료
뒤에는 다른 민감 초안과 마찬가지로 복원하지 않는다.

## 8. 구현 순서

1. 이 계획의 파일별 범위·수락 조건을 최종 확인하고 구현 승인을 받는다.
2. Domain 별칭 계약과 테스트를 먼저 변경하고 시작 route·메모리 session의 수명과 민감 데이터
   비영속을 단위 테스트한다.
3. Onboarding Contract/ViewModel/Content를 구현해 GET 성공·gate·필수 별칭 흐름을 연결한다.
4. Media3 플레이어와 확정된 `4→1` 단계 문구, 최초 안내·임시 코멘트로 Feedback
   Contract/ViewModel/Content를 구현한다.
5. Review Contract/ViewModel/Content, 공용 코멘트 Modal과 최종 확인·한 번 제출·오류 복구를
   구현한다.
6. Feature entry builder와 app installer를 연결해 세 route의 back stack 동작을 검증한다.
7. Architecture가 요구하는 전역 오류 경로를 중복 queue 없이 최소 구현한다.
8. `HilitModal`의 public API와 전역 queue 계약은 유지한 채 내부 제품 시각과 Catalog Story만
   갱신하고 기존 Android·Catalog 테스트를 실행한다.
9. Preview와 Compose UI 테스트에는 비식별 합성 데이터와 단색 영상 placeholder만 사용한다.
10. 포매팅부터 시작하는 정적 검증과 수동 영상/Navigation 검증을 수행한다.

## 9. 테스트 계획

모든 새 테스트 함수명은 기대 동작을 설명하는 한국어 문장으로 작성한다. token, 요청자, 질문,
영상, 별칭과 코멘트는 `synthetic-token`, `합성 요청자`, `.invalid` URL과 비식별 합성 문구만
사용하고 실패 메시지나 로그에 민감 값을 포함하지 않는다.

### 9.1 Domain과 ViewModel 단위 테스트

- Onboarding 최초 진입이 UseCase를 정확히 한 번 호출한다.
- `Open`은 session을 채우고 `Unavailable` 네 사유는 확정 Modal/종료 경로로 분기한다.
- UI와 Domain은 트리밍한 별칭이 비었거나 줄바꿈을 포함하거나 `String.length` 12자를 넘으면
  진행·Repository 호출을 차단하고, 1자와 12자는 허용한다.
- `GuestFeedbackSubmission.nickname`은 non-null이며 유스케이스가 검증한 문자열만 Repository로
  전달한다.
- 지정된 축만 평가 State에 생성하고 최초 축 선택 규칙을 지킨다.
- 각 축은 level `4`를 가장 긍정, `1`을 가장 아쉬운 문구로 변환하고 선택 숫자를 그대로 보존한다.
- 각 축의 level과 확정 코멘트를 immutable copy로 갱신하며 다른 축 입력을 보존한다.
- 최초 안내가 한 번 닫히면 같은 `FeedbackViewModel` 수명에서 다시 표시되지 않고, 치명적 영상
  실패 신호가 종료 안내로 분기된다. 준비·재생·일시정지 상태는 ViewModel 테스트 대상에 두지 않는다.
- 코멘트 `다음`은 임시 값을 저장하고, 닫기·바깥 터치·뒤로가기는 임시 값을 버리고 편집 전 값을
  유지한다.
- 모든 지정 축을 평가하기 전에는 검토 Effect를 발행하지 않는다.
- 100자 코멘트는 허용하고 101자 입력의 UI 처리와 Domain 최종 검증이 일치한다.
- Review가 session의 모든 축을 누락 없이 표시용 State로 변환한다.
- Feedback과 Review의 코멘트 Modal이 같은 축별 초안을 수정하고 Review에서는 level 변경 Intent를
  제공하지 않는다.
- 확인 Modal의 Confirm에서만 Submit UseCase를 한 번 호출하고 중복 탭을 차단한다.
- 제출 실패 시 `isSubmitting`을 복구하고 오류 타입별 경로를 선택한다.
- 제출 검증 실패는 확정 안내를 표시하고 Review에 남으며, 코멘트 수정 또는 Feedback 복귀 후 다시
  제출할 수 있다.
- 제출 성공 시 session 정리, 성공 Toast Effect, 앱 종료 Effect 순서를 지키고, 진입 차단·종료
  시에도 Effect 전에 session을 지운다.
- Network/Server/Unknown이 승인된 전역 오류 경로로 전달된다.
- Effect와 오류 처리 경로에 token, 영상 URL, 질문·별칭·코멘트가 포함되지 않는다.
- 시작 route의 문자열 표현과 Navigation 진단 정보에 원문 token이 나타나지 않는다.
- Onboarding 시작은 back stack을 Feedback 하나로 교체하고 Review 뒤로가기는 Feedback으로
  돌아간다.
- Onboarding 첫 뒤로가기는 Toast를 요청하고 2초 안의 두 번째 입력만 종료 Effect를 발행한다.
- Feedback 종료 Modal의 취소·dismiss는 초안을 유지하고, 종료 확인만 초안을 지운다.
- Network/Server는 Guest session을 지운 뒤 종료 Dialog를 발행하고 Unknown은 안전한 고정 Toast
  뒤 현재 화면에 남는다.
- Guest 비즈니스 오류 Modal은 단일 종료 버튼·닫기 차단 정책과 확정 제목을 사용한다.
- `submissionOpen=false`와 영상 재생 오류도 확정 문구·단일 종료 버튼·닫기 차단 정책을 사용한다.

### 9.2 Compose UI 계측 테스트

- 온보딩 제목에 요청자 이름이 표시되고 시작 버튼이 이름 sheet를 연다.
- 이름 field와 다음 버튼의 enabled 상태, IME 처리와 sheet dismissal 정책을 검증한다.
- 코멘트 Modal의 닫기·입력·하단 버튼과 Feedback/Review 양쪽 표시를 검증한다.
- 코멘트 Modal을 닫거나 뒤로가면 편집 전 문구가 유지되고 `다음`에서만 새 문구가 표시된다.
- 서버가 지정한 `1`, `2`, `5`개 축만 순서대로 표시한다.
- 평가 메뉴 제목은 `시선`, `표정`, `자세`, `손동작`, `목소리`를 그대로 표시한다.
- level `4→1` 문구와 긍정→아쉬움 스타일, 선택 시 검토 버튼 활성 상태가 갱신된다.
- 선택 코멘트가 최대 3줄 높이와 내부 스크롤을 사용하고 길이 정보를 제공한다.
- 검토 카드가 코멘트 유무에 따라 인용 행을 표시하거나 생략한다.
- 로딩·비활성·제출 중 상태에서 중복 클릭이 차단된다.
- Feature 테스트는 Modal 요청·확인·취소·dismiss에 따른 화면 동작만 검증하고 Design System의
  색상·간격·버튼 배치·token은 중복 검증하지 않는다.
- 버튼, 축, level, 영상 제어와 입력 field에 의미 있는 접근성 label/role이 있다.

### 9.3 Design System과 Catalog 검증

- 기존 app의 `DMinusDialogTest`로 한·두 버튼 callback과 dismissible 동작을 검증한다.
- `HilitModalStories`에서 한 버튼·두 버튼 `2color`·닫기 차단·긴 문구 제품 상태를 확인한다.
- 기존 `HilitModalCatalogAdapterTest`로 Modal을 닫고 다시 여는 동작을 검증한다.
- 이번 시각 변경만을 위한 Feature 색상·배치 assertion이나 새 Design System common test는 추가하지
  않는다.

### 9.4 수동 검증

- 합성 영상 URL로 준비, 최초 안내의 2초 자동·화면 전체 터치·300ms 페이드아웃, 완료 후 자동 재생,
  한 흐름 1회 표시, 이후 영상 터치 재생/일시정지, 크게 보기와 Lifecycle pause·release를 확인한다.
- 회전 또는 Activity 재생성 시 승인된 범위까지만 메모리 초안이 유지된다.
- process 종료 후 민감 초안이 복원되지 않고 링크 진입부터 다시 시작한다.
- 영상 준비 실패와 네트워크 단절 후 UI/Modal/back stack이 안전하게 복구된다.
- 세 route의 뒤로가기, 2초 이중 뒤로가기, 다시보기, 제출 성공 Toast, gate 차단과
  `finishAffinity()` 종료 정책을 확인한다.
- Feedback 진입 시 Onboarding이 back stack에서 제거되고 Review 뒤로가기가 초안을 보존하는지
  확인한다.
- 화면 캡처·로그·디버그 출력에 실제 사용자 데이터가 포함되지 않음을 확인한다.

## 10. 수락 조건

- 세 화면이 Figma의 확정된 구성·token·간격·타이포그래피와 일치한다.
- 표현 가능한 요소는 기존 `:designsystem` Composable을 사용하고 Feature 전용 요소만 로컬로
  구현한다.
- 각 화면은 `MviIntent`, `MviState`, `MviEffect`, `MviViewModel` 계약을 지킨다.
- Screen은 ViewModel 연결과 Effect 수집, Content는 ViewModel 없는 순수 UI를 담당한다.
- Feature ViewModel은 Domain UseCase만 호출하고 Repository 구현·data·Navigator를 참조하지
  않는다.
- app이 세 Feature entry와 back stack 변경을 조립한다.
- 서버가 지정한 `1..5`개 축만 표시하고 모두 `1..4` 단계로 평가해야 검토·제출할 수 있으며,
  level `4`가 가장 긍정이고 `1`이 가장 아쉬운 값이다.
- 별칭은 UI와 Domain에서 필수이며 줄바꿈 없이 트리밍 후 `String.length` 1~12 범위를 만족해야
  하고 `GuestFeedbackSubmission.nickname`은 non-null이다.
- 코멘트는 선택이며 `String.length` 100자 기준을 UI와 Domain에서 동일하게 적용한다.
- 300자 전반 피드백과 서버에 없는 중간 저장 API를 임의로 추가하지 않는다.
- 질문 경계는 ViewModel/session에 유지하되 이번 UI와 영상 이동에는 사용하지 않는다.
- 영상은 Media3로 스트리밍하고 최초 안내를 2초 또는 화면 전체 터치 뒤 300ms 동안 페이드아웃해
  한 흐름에 한 번만 보여준다. 이후 자동 재생, 영상 터치 전환, 기기 음량 준수, 크게 보기와
  백그라운드 일시정지만 제공한다.
- 준비·재생·일시정지 같은 runtime 상태는 `GuestFeedbackVideoPlayer`만 소유하고 ViewModel과
  session에는 복제하지 않는다.
- 코멘트는 `다음`에서만 메모리 초안에 반영하고 Modal dismiss는 편집 전 값을 유지한다. Review는
  코멘트만 수정하고 level은 수정하지 않는다.
- 최종 확인 후 Submit UseCase를 한 번만 호출하고 성공 전에는 제출 내용을 확정하지 않는다.
- 성공 시 session 정리, `피드백을 제출했어요.` Toast, `finishAffinity()` 요청 순서를 지킨다.
- 제출 검증 실패는 Review에 남아 수정·확인 후 재제출할 수 있다.
- Guest 비즈니스 오류와 공통 오류가 Architecture의 Feature/전역 경계에 맞게 처리된다.
- Network/Server는 `연결 오류` 종료 Dialog 뒤 앱을 종료하고 Unknown은 안전한 고정 Toast 뒤
  현재 화면에 남는다.
- token, 영상 URL, 질문, 별칭과 피드백은 흐름 중 메모리에서만 사용하고 영속 저장·로그·분석·
  crash report·Preview·테스트 asset에 남기지 않는다.
- token을 가진 시작 route의 문자열 표현은 민감 값을 가리고 back stack을 별도 영속 복원하지
  않는다.
- 실제 사용자 데이터가 source, test, fixture, screenshot, 문서와 예시에 포함되지 않는다.
- 새 범용 추상화, 불필요한 mapper, 별도 Navigation 모듈과 Feature 간 impl 의존성을 추가하지
  않는다.
- 세 번째 화면과 route는 모두 `FeedbackReview` 명칭을 사용하고 `FeedbackResult`를 다시
  도입하지 않는다.
- `HilitModal`은 public API 확장 없이 Figma 제품 시각, 한·두 버튼과 dismissible 계약을 만족하며
  Catalog 주요 상태가 함께 갱신된다.
- 대상 테스트와 전체 Android CI 검증이 성공하고 수동 영상/Navigation 결과가 보고된다.

## 11. 구현 후 검증 계획

포매팅을 먼저 적용한 뒤 나머지 검증을 실행한다.

1. 포매팅 적용

   ```text
   .\gradlew.bat spotlessApply
   ```

2. 포매팅 결과 확인

   ```text
   .\gradlew.bat spotlessCheck
   ```

3. Domain·Feedback 대상 단위 테스트와 Android 테스트 빌드

   ```text
   .\gradlew.bat :domain:test :feature:feedback:impl:testDebugUnitTest :feature:feedback:impl:assembleDebugAndroidTest
   ```

4. 연결된 Android 기기가 있으면 Compose UI 계측 테스트

   ```text
   .\gradlew.bat :feature:feedback:impl:connectedDebugAndroidTest :app:connectedDebugAndroidTest
   ```

5. 전체 Android CI 검증

   ```text
   .\gradlew.bat --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug
   ```

6. 변경 범위와 공백 오류 확인

   ```text
   git diff --check
   git status --short
   ```

7. Design System 공통 테스트와 Catalog Wasm 테스트·배포 빌드

   ```text
   .\gradlew.bat :designsystem:allTests :catalog:wasmJsTest :catalog:wasmJsBrowserDistribution
   ```

`HilitModal`과 Catalog Story를 변경하므로 Wasm 검증을 필수로 실행한다.

## 12. 반영된 결정과 추가 Q&A

Q1~Q36의 답변은 본문에 반영했다. 아래 기존 문답은 결정 근거로 유지한다. 이번 답변 반영 뒤
구현을 막는 추가 모호성은 식별되지 않았다.

### Q1. Guest 흐름의 최초 token 진입 범위

**Q.** 현재 `FeedbackOnboarding`은 인자 없는 route이고 App Link/딥 링크 처리가 없습니다. 이번
단계에 실제 `{SERVER_URL}/feedback/guest/{token}` Android App Link, Manifest intent-filter와
Intent parsing까지 포함할까요, 아니면 phase 3에서는 app이 이미 얻은 token을
`FeedbackOnboarding(token)` route에 전달하는 Navigation 계약까지만 구현할까요? 가장 작은
권장 범위는 후자이고, 실제 외부 링크 진입은 별도 단계로 분리하는 것입니다.

**A.** 권장 범위에 해당하는 후자까지만 승인.

### Q2. 세 route 사이의 민감한 진행 데이터 전달

**Q.** 영상 URL·질문 경계·별칭·평가 초안을 route나 SavedStateHandle에 직렬화하지 않고,
`ActivityRetainedScoped`의 구체적인 `GuestFeedbackFlowSession` 한 개에 메모리로만 보관한 뒤
제출·종료 시 즉시 비우는 방식을 적용해도 될까요? 이 방식은 세 route를 실제 Navigation entry로
유지하면서 현재 비영속 정책을 지키는 가장 단순한 안입니다.

**A.** 권장안 승인.

### Q3. 별칭 필수 여부와 입력 제한

**Q.** Figma 이름 sheet는 빈 값에서 `다음` 버튼이 비활성이라 필수 입력처럼 보이지만, 현재
Domain 계약은 빈 별칭을 `익명의 지인`으로 허용합니다. 별칭을 선택 입력으로 유지해 빈 값에서도
진행하게 할까요, 아니면 UI에서 필수로 바꿀까요? 또한 카드 레이아웃 보호를 위한 최대
`String.length`와 줄바꿈 허용 여부를 정해주세요.

**A.** UI에서 필수로 바꿔야 함. 줄바꿈 비허용. `String.length <= 12`로 가정.

### Q4. 온보딩의 `다음에 하기`

**Q.** PRD에는 온보딩 하단에 `피드백 시작하기`와 `다음에 하기` 두 동작이 있지만 현재 Figma에는
시작 버튼만 있습니다. 이번 구현에서 `다음에 하기`를 제외하고 시스템 뒤로가기만 제공할까요,
아니면 보조 버튼을 추가할까요? 추가한다면 누른 뒤 앱 종료, 이전 화면 이동, 브라우저 복귀 중
어떤 동작이어야 하나요?

**A.** 현재 Figma 시안을 우선순위로 두도록 해. 이 답변은 이 작업 범위에서 동일하게 적용.

### Q5. 평가 축별 질문과 4단계 문구

**Q.** 시선조차 PRD의 `잘 맞춰요 / 대체로 맞춰요 / 가끔 피해요 / 자주 피해요`와 Figma의
`잘 맞춤 / 꽤 맞춤 / 가끔 피함 / 자주 피함`이 다르며, 표정·자세·손동작·목소리의 질문형
headline과 네 단계 문구는 확정본이 없습니다. 다섯 축 각각의 headline과 level `1..4` 표시
문구를 적어주세요. level의 긍정→아쉬움 방향도 `1`이 가장 긍정, `4`가 가장 아쉬움인지 확인이
필요합니다.

**A.** PRD의 내용이 Outdated 됐어. plan-phase-1에서 확인되는 열거형을 그대로 사용하면 돼.

### Q6. 영상 플레이어 구현과 제어 범위

**Q.** 프로젝트에 영상 라이브러리가 없습니다. HTTPS URL 스트리밍과 Lifecycle 해제를 안정적으로
처리하기 위해 Media3 ExoPlayer 의존성을 Version Catalog에 추가해도 될까요? 재생 요구사항도
자동 재생 여부, 기본 음소거 여부, 재생/일시정지, seek bar, 앞뒤 이동, 전체 화면, 화면 회전,
background 진입 시 일시정지 중 어디까지 포함할지 정해주세요.

**A.** 추가해야 해. 최초 진입 시 나타나는 메시지가 사라지면 그 시점부터 자동 재생, 터치하면 일시정지/재생 전환, 기본적으로 볼륨은 기기 설정을 따라가며 전체 화면은 시안 내 버튼을 사용. 백그라운드 진입 시 일시정지. 이 외 언급되지 않은 사항은 구현 범위에 포함하지 않음.

### Q7. 질문 경계 사용 방식

**Q.** API는 `questionBoundaries(turnLevel, startAt, questionText)`를 제공하고 PRD는 질문 맥락을
지인에게 보여준다고 하지만 현재 세 Figma 화면에는 질문 목록·timeline·이동 제어가 없습니다.
이번 화면에서 질문 경계를 어떻게 노출하고 선택 시 영상을 `startAt`으로 이동시킬지, 아니면 MVP
UI에서는 사용하지 않을지 정해주세요.

**A.** 일단 `ViewModel`에는 해당 내용을 포함시키되 사용하지는 않는 방향으로 구현해줘.

### Q8. Figma 최초 진입 상태와 평가 menu 전환

**Q.** `최초 진입 시 화면` 링크의 node `1855:8703`은 375×812 scrim vector 하나만 가리켜
텍스트, 버튼, 표시 시간과 해제 조건을 확인할 수 없습니다. 최초 진입에서 무엇을 안내하고 어떤
사용자 행동 또는 시간 경과로 기본 영상 화면으로 전환해야 하나요? 또한 축 탭을 누르면 평가
menu가 열리고 `영상 크게 보기`로 다시 전체 화면에 돌아가는 흐름이 맞는지 확인해주세요.

**A.** `1855:8702` 참고 바람. 문서 초반 Figma 표에도 내가 반영해 뒀어.

### Q9. 축별 입력의 `저장 중 ...` 의미

**Q.** Figma에는 `저장 중 ...`이 표시되지만 현재 API는 최종 제출만 제공하고 plan phase 2는
임시저장을 제외했습니다. level/comment 변경은 메모리 초안에 즉시 반영하고 네트워크 저장 상태는
표시하지 않는 것이 맞을까요? 그렇다면 `저장 중 ...` label은 제거할지, `작성 중`처럼 로컬 상태
문구로 바꿀지도 정해주세요.

**A.** 메모리 초안에 즉시 반영하고 네트워크 저장 상태는 표시하지 마. `저장 중...` 레이블도 삭제.

### Q10. Review 화면의 수정·다시보기·제출 완료 흐름

**Q.** Review Figma의 편집 icon은 disabled 형태지만 제출 전 검토 화면이라 수정 가능 여부가
불명확합니다. 카드 또는 편집 icon을 누르면 해당 축 평가로 돌아가게 할까요? `영상 다시보기`는
초안을 보존한 Feedback 화면 복귀가 맞나요? 제출 성공 뒤 별도 완료 화면 시안이 없는데, 성공
Modal을 보여준 뒤 앱 종료/이전 화면/특정 route 이동 중 어떤 동작을 해야 하나요?

**A.** @https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=2227-5014&m=dev 링크 내의 모달을 출력하길 바람. 해당 모달은 `Feedback` 화면에서 '왜 그렇게 느꼈나요?' 버튼 클릭 시에도 동일하게 동작해야 함.

### Q11. 제출 확인 Modal의 정확한 문구

**Q.** PRD에는 `제출하면 다시 고칠 수 없어요`만 있습니다. 전역 Modal에 필요한 제목, 본문,
확인 버튼, 취소 버튼과 바깥 터치·뒤로가기 허용 여부를 정확히 적어주세요.

**A.** 이 질문은 다음과 같이 해결 바람:
- @https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=2302-6098&m=dev 링크에서 전역 모달 디자인을 `HilitModal`에 반영
- 제목은 "피드백 제출"
- 본문은 "제출하면 다시 고칠 수 없어요."
- 제출 버튼 및 취소 버튼 존재
- 바깥 및 뒤로가기 터치 여부는 `dismissable`

### Q12. non-OPEN gate와 `submissionOpen` 정책

**Q.** `PRIVATE`, `EXPIRED`, `ALREADY_SUBMITTED`의 화면 문구·버튼·확인 후 종료 동작을
확정해주세요. 특히 PRD는 `FULL`에서 영상 시청은 허용한다고 하지만 현재 FULL 응답의 Domain
모델에는 영상 URL·질문 경계가 없어 시청 구현이 불가능합니다. 이번 단계에서는 FULL을 안내 후
차단할지, 영상 시청을 위해 서버/DTO/Domain 계약을 먼저 확장할지 결정이 필요합니다. 또한
`gate=OPEN`인데 `submissionOpen=false`일 수 있다면 영상 시청·평가·제출 중 어디까지 허용할지도
정해주세요.

**A.** 일단 안내 후 차단. 추후 이슈로 다뤄야 할 듯.

### Q13. Guest 오류 Modal과 종료 동작

**Q.** phase 2에서 확정한 네 오류 본문 외에 Modal 제목, 확인/취소 버튼, dismissible 여부가
필요합니다. 요청 처리 오류에는 재시도 버튼을 둘지 앱 재실행 안내 후 종료할지, 공유 종료·정원
마감·중복 제출은 확인 후 어떤 back stack 동작을 할지 정해주세요.

**A.** 모달 제목은 네가 결정, 버튼은 종료하기 하나만, `dismissable == False`로 구현. 백스택 동작은 앱 종료.

### Q14. 전역 오류 인프라 보완 범위

**Q.** Architecture는 Network/Server/Unknown을 `GlobalErrorHandler`와 app event로 처리하도록
요구하지만 현재 해당 구현이 없습니다. 동시에 예외 타입은 `domain`, 이벤트 기반은 Android
모듈인 `core:common`에 있어 `core:common → domain` 의존성을 새로 만들면 안 됩니다. 이번
Feature를 계약에 맞게 연결하기 위해 `core:common`에는 예외 타입을 모르는 최소 전역 event
통로만 두고, Feedback ViewModel이 예외를 분류해 event를 발행하며, `app`이 이를 수집하는 범위를
함께 추가해도 될까요? 승인하지 않으면 Architecture를 지키는 ViewModel 오류 처리를 완성할 수
없어 구현이 중단됩니다.

**A.** 승인.

### Q15. Guest 흐름의 시스템 뒤로가기와 종료 목적지

**Q.** Onboarding, Feedback, Review 각 화면에서 시스템 뒤로가기를 누를 때의 정책을 정해주세요.
특히 작성 중 Feedback 이탈 시 확인 Modal이 필요한지, Review에서 뒤로가면 초안을 유지한 채
Feedback으로 돌아가는지, 시작 route에서 뒤로가면 Activity/app을 종료하는지 확인이 필요합니다.
현재 `Navigator.onExit`도 TODO라 종료 동작을 임의로 구현할 수 없습니다.

**A.** 다음 조건 준수:
- Onboarding은 뒤로 가기 1회 시 토스트로 "2번 뒤로가기 하면 종료된다"고 알림
- Feedback은 뒤로 가기 시 "피드백을 종료하시겠습니까?" 물어보며 Onboarding으로는 되돌아갈 수 없음. 즉, Onboarding에서 Feedback으로 넘어가면 Onboarding은 Nav 스택에서 비워야 한다는 뜻.
- Review에서는 뒤로 가기 시 Feedback 화면으로 이동

### Q16. 평가 축별 화면 문구의 실제 원본

**Q.** Q5 답변에 따라 `plan-phase-1.md`를 다시 확인했지만, 해당 문서에는 `GAZE`,
`EXPRESSION`, `POSTURE`, `GESTURE`, `VOICE` 축 enum과 level `1..4` 계약만 있고 화면에 표시할
다섯 축의 질문형 headline 및 축별 네 단계 문구는 없습니다. Figma에는 일부 축 예시만 있어 나머지를
유추할 수도 없습니다. 다섯 축 각각의 headline과 level `1`, `2`, `3`, `4` 문구를 적어주시고,
`1`이 가장 긍정적이고 `4`가 가장 아쉬운 방향인지 확인해주세요.

**A.** 순서대로 가장 긍정적인 4부터 가장 아쉬운 1까지:
- `GAZE`: 잘 맞춤, 꽤 맞춤, 가끔 피함, 자주 피함
- `EXPRESSION`: 자연스러움, 무표정, 좀 굳음, 많이 굳음
- `POSTURE`: 꼿꼿함, 긴장함, 좀 산만함, 많이 산만함
- `GESTURE`: 자연스러움, 긴장함, 좀 산만함, 많이 산만함
- `VOICE`: 적당함, 너무 큼, 조금 작음, 너무 작음

### Q17. 별칭 필수·길이 규칙의 Domain 적용 범위

**Q.** Q3에서 UI는 별칭을 필수, 줄바꿈 불가, `String.length` 1~12자로 확정했지만 현재
`SubmitGuestFeedbackUseCase`는 빈 별칭을 허용해 `익명의 지인`으로 정규화하고 12자 제한도
검증하지 않습니다. UI 우회 입력도 같은 계약으로 막도록 이번 단계에서 Domain 유스케이스와 단위
테스트까지 1~12자 규칙으로 변경할까요, 아니면 기존 Domain 계약은 유지하고 UI에서만 제한할까요?
단일 제품 계약을 유지하려면 Domain에도 같은 규칙을 적용하는 쪽을 권장합니다.

**A.** 도메인 유스케이스와 테스트까지 변경

### Q18. 최초 영상 안내의 종료 조건과 재표시

**Q.** Figma node `1855:8702`로 안내의 모양과 문구는 확인했지만 언제 사라지는지는 알 수
없습니다. 안내를 몇 초 뒤 자동으로 숨길지, 화면 터치로도 즉시 숨길 수 있는지 정해주세요. 또한
Review에서 `영상 다시보기` 또는 뒤로가기로 Feedback에 돌아왔을 때 안내를 다시 보여줄지, 한 흐름에
최초 한 번만 보여줄지도 확인이 필요합니다.

**A.** 2초 뒤 숨기고, 화면 터치할 경우 검정색 필터가 페이드-아웃되면서 바로 숨길 수 있게 구현. 한 흐름에 최초 한 번만 보여줌.

### Q19. 공용 코멘트 Modal의 저장·닫기 동작

**Q.** Figma node `2227:5014`에는 `다음` 버튼과 닫기 icon이 있습니다. Feedback과 Review에서
`다음`을 누르면 현재 축의 코멘트를 메모리 초안에 저장하고 Modal만 닫는 동작이 맞나요? 닫기 icon,
바깥 터치와 시스템 뒤로가기는 편집 전 값으로 되돌린 뒤 닫을지, 입력 중인 값까지 보존할지, 또는
닫기를 차단할지 정해주세요. Review에서 열 때는 기존 코멘트를 입력란에 채우는 것으로 이해해도 되는지
확인해주세요.

**A.** 네 동작에 대한 이해가 맞음. Dismiss 및 뒤로 가기는 편집 전 값으로 되돌린 뒤 닫으면 됨. 저장 자체를 다음 버튼이 눌러야만 실행된다고 이해.

### Q20. Review에서 허용할 수정 범위

**Q.** Q10 답변으로 Review의 편집 icon도 공용 코멘트 Modal을 사용한다는 점은 확인했지만,
평가 level 자체를 바꾸는 방법은 확정되지 않았습니다. Review에서는 코멘트만 수정하게 할까요, 아니면
카드 또는 별도 동작으로 해당 축의 4단계 평가까지 수정하게 할까요? 후자라면 Feedback의 해당 축
화면으로 이동할지 Review 안에서 선택 UI를 열지도 정해주세요.

**A.** 코멘트만 수정하도록 함.

### Q21. `HilitModal` 두 버튼 상태의 정확한 구성

**Q.** Figma node `2302:6098`의 선택된 예시는 illustration·부가 정보 영역·단일 하단 버튼을
사용하지만, 제출 확인에는 `제출`과 `취소` 두 버튼이 필요합니다. 제출 확인에서는 illustration과
부가 정보 영역을 빼고 제목·본문만 사용할지 확인해주세요. 두 버튼은 가로 배치인지 세로 배치인지,
순서와 강조색은 각각 무엇인지도 필요합니다. 가능하면 정확한 두 버튼 variant의 Figma node를
알려주세요.

**A.** 부가 정보 영역은 제외하고 제목/본문만 사용. 두 버튼은 가로 배치이며 `HilitFixedBottomButton`의 버튼 2개 시안은 다음 링크 참조: @https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=2302-5987&m=dev

### Q22. gate 차단 안내의 문구와 종료 동작

**Q.** Q12에서 모든 non-`OPEN`과 `OPEN + submissionOpen=false`를 안내 후 차단하는 정책은
확정했지만, 각 상태의 제목·본문·버튼 문구가 없습니다. `PRIVATE`, `EXPIRED`, `FULL`,
`ALREADY_SUBMITTED`, `submissionOpen=false` 각각의 표시 문구를 적어주세요. 모두
`종료하기` 단일 버튼, `dismissible=false`로 표시한 뒤 session을 비우고 앱을 종료해도 되는지도
확인해주세요.

**A.** 다음 핵심 키워드만 보고 네가 작성 바람:
- `PRIVATE` 사용자가 비공개함
- `EXPIRED` 피드백 가능 기간이 끝남
- `FULL` 피드백 가능한 최대 인원이 다 참
- `ALREADY_SUBMITTED` 이 기기에서 중복 피드백 시도
모두 종료하기 단일 버튼에 `dismissible = false`로 설정.

### Q23. Feedback 작성 중 종료 확인 Modal

**Q.** Q15의 `피드백을 종료하시겠습니까?`를 Modal 제목으로 사용하고 본문은 생략해도 될까요?
버튼을 `계속 작성`과 `종료하기` 두 개로 둘지, 바깥 터치와 시스템 뒤로가기로 Modal을 닫아 작성을
계속할 수 있는지도 정해주세요. `종료하기`를 선택한 경우에만 초안을 지우고 앱 종료를 요청하는
것으로 이해하고 있습니다.

**A.** 네 제안 승인. 버튼은 `계속 작성`과 `종료하기` 2개로 둔다. 종료에 대한 너의 이해는 정확함.

### Q24. 두 번 뒤로가기 시간과 Android 앱 종료 방식

**Q.** Onboarding의 첫 뒤로가기 이후 몇 초 안에 다시 눌러야 종료할까요? 일반적인 2초를
권장합니다. 또한 이 계획에서 말하는 앱 종료는 process 강제 종료가 아니라 app 계층에서 현재
Activity task를 `finishAffinity()`로 닫는 방식으로 확정해도 될까요?

**A.** 2초 + `finishAffinity()`를 사용하는 권장안까지 승인.

### Q25. 제출 성공 뒤 완료 흐름

**Q.** Q10에서 제출 성공 뒤 동작은 답변되지 않았고 완료 화면 Figma도 없습니다. 성공 즉시
session을 비우고 앱을 종료할지, 성공 Toast 또는 Modal을 표시한 뒤 종료할지 정해주세요. 안내를
표시한다면 정확한 제목·본문·버튼과 dismissible 여부도 적어주세요.

**A.** Session 비우기, 성공 Toast 표시, 앱 종료 순서로 진행 부탁.

### Q26. 영상 준비·재생 실패 UI

**Q.** Media3가 잘못된 URL, 디코딩 실패 또는 스트림 중단을 보고할 때 어떤 UI를 보여줄지
확정되지 않았습니다. 재시도 버튼을 제공할지, 평가를 막고 종료 안내를 표시할지, 영상 없이 평가를
계속 허용할지 정해주세요. Modal을 사용한다면 제목·본문·버튼과 dismissible 여부도 필요합니다.

**A.** 평가를 막고 종료 안내를 표시.

### Q27. 전역 Network·Server·Unknown 오류의 제품 문구와 동작

**Q.** Q14로 `GlobalAppEvent` 경로 구현은 승인됐고 Architecture는 Network/Server를 Dialog,
Unknown을 Toast로 분류하지만 실제 문구와 버튼 동작은 정하지 않습니다. Network Dialog에 재시도를
둘지, Server Dialog는 종료만 제공할지, 각 제목·본문·버튼과 dismissible 여부를 정해주세요.
Unknown Toast에는 예외의 원문 메시지를 그대로 노출하지 않고 고정된 안전 문구를 사용하는 것으로
확정해도 될까요?

**A.** Network와 Server Dialog 모두에서 종료만 제공한다. 본문은 "네트워크 또는 서버 오류가 발생했어요. 잠시 후 다시 시도해주세요."로 고정. Unknown 토스트에는 고정된 안전 문구 사용 허용.

### Q28. 두 버튼의 색 variant와 좌우 순서

**Q.** Figma node `2302:5987`에는 두 버튼이 모두 검정인 `1color`와 왼쪽 흰색·오른쪽 검정인
`2color` variant가 함께 있습니다. 제출 확인과 Feedback 종료 확인에는 `2color`를 사용해 왼쪽에
흰색 보조 버튼(`취소` 또는 `계속 작성`), 오른쪽에 검정 주 버튼(`제출` 또는 `종료하기`)을 두는
것으로 확정해도 될까요? 다른 조합을 원하면 각 Modal의 variant와 좌우 순서를 적어주세요.

**A.** 확정 승인.

### Q29. 다섯 축의 질문형 headline

**Q.** Q16 답변으로 level `4→1` 문구는 확정됐지만 질문에서 요청한 축별 질문형 headline은
답변에 포함되지 않았습니다. 평가 메뉴의 제목으로 표시할 `GAZE`, `EXPRESSION`, `POSTURE`,
`GESTURE`, `VOICE` 각각의 정확한 문장을 적어주세요.

**A.** 시선, 표정, 자세, 손동작, 목소리

### Q30. 최초 안내 페이드아웃 시간

**Q.** Q18에서 2초 뒤 또는 터치 시 검정 필터를 페이드아웃하도록 확정했지만 animation 시간은
정해지지 않았습니다. 자동·터치 모두 300ms로 페이드아웃하고 animation 완료 시 영상을 재생하는
권장안을 적용해도 될까요?

**A.** 권장안 승인.

### Q31. `submissionOpen=false` 안내 문구

**Q.** Q22 답변은 네 non-`OPEN` gate의 키워드만 제공해 `OPEN + submissionOpen=false`의
문구가 남았습니다. 제목 `피드백 작성 마감`, 본문 `현재는 피드백을 작성할 수 없어요.`와
`종료하기` 단일 버튼, `dismissible=false`를 사용하고 확인 시 session을 비운 뒤 앱을 종료해도
될까요?

**A.** 네 제안 승인.

### Q32. 제출 성공 Toast 문구

**Q.** Q25에서 성공 Toast 표시를 확정했지만 정확한 문구가 없습니다. `피드백을 제출했어요.`를
표시한 직후 `finishAffinity()`를 요청하는 것으로 확정해도 될까요?

**A.** 승인.

### Q33. 영상 재생 실패 Modal의 세부 상태

**Q.** Q26의 평가 차단·종료 안내를 제목 `영상을 재생할 수 없어요`, 본문
`영상 재생 중 문제가 발생했어요. 앱을 종료한 뒤 다시 시도해주세요.`, `종료하기` 단일 버튼,
`dismissible=false`로 표시하고 확인 시 session을 비운 뒤 앱을 종료해도 될까요?

**A.** 승인.

### Q34. 전역 오류의 제목·종료 의미와 Unknown 문구

**Q.** Q27의 Network·Server Dialog에서 말한 `종료`가 Dialog만 닫는 것인지, Guest session을
비우고 `finishAffinity()`로 앱을 종료하는 것인지 확인이 필요합니다. 후자라면 두 오류 모두 제목
`연결 오류`, 버튼 `종료하기`, `dismissible=false`로 통일해도 될까요? Unknown Toast의 고정 문구는
`알 수 없는 오류가 발생했어요.`로 확정해도 될까요?

**A.** 앱을 종료해야 함. 문구는 네 제안 승인.

### Q35. 제출 입력 검증 실패의 사용자 처리

**Q.** 유효한 UI 흐름에서는 발생하지 않아야 하지만 `SubmitGuestFeedbackUseCase`가 별칭·축·level·
코멘트 불일치로 `GuestFeedbackValidationException`을 반환할 수 있습니다. 이때 제목
`피드백을 제출할 수 없어요`, 본문 `작성한 내용을 확인한 뒤 다시 시도해주세요.`, `확인` 단일 버튼,
`dismissible=true`로 안내하고 Review에 남겨 사용자가 코멘트를 다시 확인하게 할까요? Review에서는
level을 수정할 수 없으므로 축·level 불일치는 복구 불가능한 내부 정합성 오류라는 점도 함께
고려해야 합니다.

**A.** 응. 작성한 내용을 다시 확인하게 한 뒤 재시도하게 하는 게 좋을 것 같아.

### Q36. `GuestFeedbackSubmission.nickname`의 null 허용 여부

**Q.** Q17로 Domain에서도 별칭이 필수가 됐습니다. 컴파일 시점 계약도 맞추기 위해
`GuestFeedbackSubmission.nickname`을 `String?`에서 `String`으로 변경하고, 유스케이스에서는
트리밍 후 빈 값·줄바꿈·1~12자만 검증하는 안을 권장합니다. nullable 타입을 유지한 채 유스케이스가
`null`도 검증 실패로 처리해야 한다면 그렇게 답해주세요.

**A.** 승인.
