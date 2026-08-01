# Guest Feedback 브랜치 평가 및 검증 결과

## 1. 결론

**최종 PR 요청 가능 여부: 아니오**

구현은 MVI 분리, app 소유 Navigation 조립, 메모리 전용 session, Domain 최종 검증, 단일
ExoPlayer 사용 등 핵심 계획을 상당 부분 따랐다. 그러나 아래 PR 차단 사항이 남아 있다.

1. 축 선택/영상 확대 전환 시 사용 중인 ExoPlayer를 해제하는 수명 결함
2. 제출 중 Review 이탈이 가능해 요청 취소 또는 서버 제출 상태 불확실성이 생기는 경쟁 조건
3. Onboarding의 Unknown 오류 뒤 session 없이 평가 화면으로 진행할 수 있는 잘못된 경로
4. 이번 변경 파일에서 발생한 Spotless·Detekt 실패와 `git diff --check` 오류

위 항목을 수정하고 실제 기기에서 플레이어 전환·재생·Lifecycle·제출 흐름을 확인하기 전에는 PR을
요청하지 않는 것이 안전하다.

## 2. 평가 기준과 범위

- 기준 브랜치: `develop`
- 비교 방식: `git diff develop...HEAD`
- 검토 문서: `plan-phase-3.md`, `report-phase-3.md`, `plan.md`, `report.md`, `prd.md`
- 제품 판단 우선순위: 최신 계획 문서 우선, PRD의 Outdated 내용은 참고만 함
- 저장소 계약: `docs/CONSTITUTION.md`, `docs/ARCHITECTURE.md`와 라우팅된 Architecture 문서
- Catalog/Wasm 빌드: 사용자 요청에 따라 평가 대상에서 제외
- 작업 트리의 기존 사용자 변경: `plan.md`, `report.md`; 수정하지 않음

## 3. 주요 발견 사항

### [차단] 3.1 분할/확대 전환이 동일 Player를 해제한다

`GuestFeedbackVideoPlayer.kt:84`의 `DisposableEffect` key에 `showBlurredBackdrop`이 포함되어 있고,
`GuestFeedbackVideoPlayer.kt:120-124`의 `onDispose`에서 `player.release()`를 호출한다. 사용자가 축을
선택하면 `showBlurredBackdrop`이 `false -> true`, 확대하면 `true -> false`로 바뀌므로 단순한 표시
전환마다 effect가 dispose되고 `remember(videoUrl)`이 보관한 동일 Player가 해제된다. 이어서
`GuestFeedbackVideoPlayer.kt:127-135`가 해제된 Player에 영상 효과를 다시 설정하려 한다.

이는 다음 계획과 정면으로 충돌한다.

- 전환 중 Player 식별자와 수명 유지
- 하나의 Player로 현재 위치와 재생 의도 보존
- 확대·분할 반복 중 Player를 재생성하거나 해제하지 않음

[Android 공식 Media3 시작 안내](https://developer.android.com/media/media3/exoplayer/hello-world)도
Player는 사용이 끝났을 때 `release()`하도록 설명한다. 현재 구현은 화면 수명이 끝난 때가 아니라
UI mode가 바뀔 때 해제한다.

권장 수정:

- Player 소유·release effect의 key에서 `showBlurredBackdrop`을 제거한다.
- listener가 최신 값을 읽어야 한다면 `rememberUpdatedState` 또는 효과 상태 전용 참조를 사용한다.
- 축 선택과 확대를 반복해도 동일 Player가 해제되지 않는 Compose/기기 테스트를 추가한다.

### [차단] 3.2 제출 중 Review에서 이탈할 수 있다

`FeedbackReviewScreen.kt:182-185`는 제출 버튼만 `isSubmitting` 동안 비활성화한다. 그러나 다음 경로는
계속 활성이다.

- 시스템 뒤로가기: `FeedbackReviewScreen.kt:86`
- 영상 다시보기: `FeedbackReviewScreen.kt:160-169`
- ViewModel의 replay 처리: `FeedbackReviewViewModel.kt:45-49`

최종 제출 확인 후 네트워크 요청 중 사용자가 이 경로로 Feedback 화면으로 돌아가면 Review
ViewModel의 coroutine이 취소될 수 있다. 서버가 이미 요청을 처리했는지는 클라이언트가 확정할 수
없으므로, session이 남은 상태에서 재제출하거나 성공 안내·session 정리를 놓칠 수 있다.

권장 수정:

- `isSubmitting` 동안 BackHandler와 replay/edit 동작을 차단한다.
- 취소 불가능한 서버 확정 요청의 UX와 중복 방지 정책을 테스트한다.
- 최소한 “제출 확인 후 완료 전 이탈 불가”를 ViewModel 단위 테스트와 UI 테스트에 추가한다.

### [차단] 3.3 Onboarding Unknown 오류 뒤 잘못된 진행이 가능하다

`FeedbackOnboardingViewModel.kt:139-162`는 Unknown 오류에서 loading을 해제하고 고정 Toast만
발행한다. 이때 `hasLoaded=false`이고 session은 시작되지 않았지만 화면은 로딩이 아니므로
`FeedbackOnboardingScreen.kt:95-165`의 정상 본문과 활성 시작 버튼을 보여준다.
`StartClicked`와 `NicknameConfirmed`도 `hasLoaded`를 검사하지 않아 별칭 입력 후
`FeedbackReady`를 발행할 수 있다. app은 Feedback으로 이동하지만 session이 없으므로 다음 화면은
곧바로 종료 Effect를 낸다.

권장 수정:

- 진입 성공 전에는 시작 버튼과 별칭 확정을 차단한다.
- Unknown 오류 뒤 재시도 UI를 제공하거나 명확한 종료 상태를 사용한다.
- Unknown 오류 -> 시작 클릭 -> 별칭 확정 시 Navigation Effect가 발생하지 않는 테스트를 추가한다.

### [차단] 3.4 현재 브랜치가 품질 게이트를 통과하지 못한다

전체 CI 명령은 4개 task 실패로 종료됐다. 이번 기능 변경에서 직접 발생한 실패는 다음과 같다.

- `:feature:feedback:impl:spotlessKotlinCheck`
  - `GuestFeedbackVideoPlayer.kt` 포맷 위반
- `:feature:feedback:impl:detekt`
  - `FeedbackReviewScreen.kt:202` `LongMethod`
  - `FeedbackReviewScreen.kt:215` `MagicNumber`

추가 Detekt 실패:

- `:feature:home:api:detekt`: `HomeRoute.kt`의 `MatchingDeclarationName`
- `:feature:login:impl:detekt`: `SplashViewModel.kt`의 기존 TODO `ForbiddenComment`

Home/Login 문제 중 Login TODO는 develop에도 존재하지만, Feedback의 Spotless와 Detekt 실패는 이번
변경 범위 안에 있다. 또한 `git diff --check develop...HEAD`는 `prd.md:1025`의 새 EOF 공백 오류를
보고했다. 따라서 기존 문제를 제외해도 PR 품질 게이트는 실패다.

### [높음] 3.5 블러 효과가 “종횡비 보존 중앙 Fit”을 보장하지 않는다

`GuestFeedbackVideoPresentationEffect.kt:13-18`은
`GaussianBlurWithFrameOverlaid(20f, 0.74f, 1f)`를 고정 적용한다.
[Media3 API](https://developer.android.com/reference/androidx/media3/effect/GaussianBlurWithFrameOverlaid)에서
두 scale 값은 출력 프레임 대비 선명한 영상의 가로·세로 크기 배율이다. 가로와 세로에 서로 다른
고정 배율을 사용하므로 입력 영상과 실제 PlayerSurface 비율에 일반적으로 대응하지 못하며, 계획의
“원본 종횡비를 유지한 중앙 Fit”을 보장할 수 없다.

이 부분은 GPU 실제 출력이 미검증이므로 기기에서 왜곡·crop·letterbox를 반드시 확인해야 한다.
필요하면 입력/출력 비율을 사용해 배율을 계산하거나, 정확한 Fit 요구가 아니라면 계획과 UI 기준을
더 단순한 검정 배경 + Fit으로 축소하는 편이 안전하다.

### [중간] 3.6 Feature 전용 illustration을 공용 Icon API로 승격했다

계획은 온보딩 illustration을 `feature:feedback:impl`의 Feature 전용 drawable로 두도록 했다.
실제 구현은 다음과 같다.

- `core/resources/.../feedback.xml`
- `core/resources/.../talk.xml`
- `HilitIconAsset.Feedback`
- `HilitIconAsset.Talk`

`Feedback`은 현재 Onboarding 한 곳에서만 사용하고 `Talk`는 소비처가 없다. Feature 전용 대형
illustration과 미사용 asset을 공용 resource 및 public Design System enum으로 노출한 것은
`core:resources` 최소화와 화면 전용 코드의 Feature 소유 원칙에 맞지 않으며 변경 범위를 키운다.

권장 수정:

- `Feedback` illustration은 Feature 전용 resource/API로 이동한다.
- 사용되지 않는 `Talk` resource와 enum 항목은 요구가 생길 때까지 제거한다.

### [중간] 3.7 자동화 테스트가 핵심 Player 수명 계약을 검증하지 않는다

`GuestFeedbackVideoPlayerTest.kt`는 effect 오류 분기 predicate와 seek 계산만 검증한다. 실제
Composable 재구성, `DisposableEffect` dispose, Player release, 효과 on/off, intro timer,
background pause는 검증하지 않는다. 그래서 3.1의 결함이 단위 테스트 4개를 모두 통과했다.

계측 테스트 APK는 조립됐지만 연결 기기가 없어 실행되지 않았다. 최소한 fake Player adapter 또는
Player lifecycle을 분리해 다음 계약을 자동 검증할 필요가 있다.

- split/expand 변경은 release하지 않음
- Composable 이탈 때 정확히 한 번 release
- background 진입 시 pause
- 효과 실패는 한 번만 fallback하고 원본 실패만 fatal 처리

### [낮음] 3.8 범위 밖 정리 변경이 PR 노이즈를 늘린다

Feedback 구현과 무관한 Home/Login ViewModel 포맷 변경과 `HomeRoute.kt` suppression 변경이 포함됐다.
기능 동작에는 영향이 작지만 사용자 요청 범위와 무관하고, 특히 Home suppression은 Detekt의
`MatchingDeclarationName` 실패를 해결하지 못한다. 별도 변경으로 분리하거나 제외하는 편이 낫다.

### [낮음] 3.9 접근성 설명이 실제 의미와 맞지 않는다

`FeedbackOnboardingScreen.kt:263-268`은 서로 다른 guide icon에도 고정
`contentDescription = "Profile"`을 사용하고, illustration도 영문 `"Feedback"`으로 읽는다.
장식용이면 `null`, 의미가 있으면 각 안내의 한국어 의미를 제공해야 한다.

## 4. 계획 대비 구현 평가

### 잘 구현된 부분

- 화면별 Intent/State/Effect/ViewModel/Screen/Content 책임이 분리됐다.
- ViewModel이 Navigator를 직접 사용하지 않고 app의 installer가 back stack을 조립한다.
- Onboarding -> Feedback은 `replaceAll`, Review -> Feedback은 `goBack`을 사용한다.
- 민감 payload를 내부 route에 싣지 않고 `ActivityRetainedScoped` session 하나에 메모리로만 둔다.
- route `toString()`에서 token을 가리고 실제 사용자 데이터를 fixture/Preview에 넣지 않았다.
- 별칭 필수·트리밍·줄바꿈 금지·1~12자와 평가/코멘트 검증을 Domain에서 재검증한다.
- 서버 지정 축만 표시하고 4 -> 1 순서, 코멘트 임시 편집/확정, Review 검토 흐름을 구현했다.
- Network/Server/Unknown과 Guest 오류의 Feature/전역 경계를 유지했다.
- 질문 경계 UI, 서버 중간 저장, 전반 피드백, 회전·배속 등 승인되지 않은 기능을 추가하지 않았다.
- Player를 두 개 만들거나 프레임을 파일로 저장·로그하는 코드는 발견되지 않았다.

### 부분 충족 또는 미검증

- 실제 영상 재생, codec/redirect, intro fade, control 터치, background pause, GPU effect와 release는
  기기에서 검증되지 않았다.
- Modal의 Android 실제 배치·dismiss·버튼 동작도 연결 기기에서 검증되지 않았다.
- Catalog/Wasm은 사용자 요청으로 평가에서 제외했다.
- 외부 App Link/Manifest 진입은 계획에서 명시적으로 후속 범위로 제외됐으므로 결함으로 보지 않는다.

### 계획과 다른 부분

- UI mode 전환 시 Player 수명을 유지한다는 계획과 달리 현재 Player가 release된다.
- Feature 전용 illustration 계획과 달리 공용 resource/Icon API로 승격했다.
- `report.md`는 Player 대상 Spotless 성공을 적었지만 현재 전체 Spotless는 해당 파일에서 실패한다.
- 최신 보고서는 전체 CI가 기존 문제만으로 실패한 것처럼 설명하지만, 현재 Feedback 변경 자체에도
  Spotless와 Detekt 실패가 있다.

## 5. 오캄의 면도날 관점의 계획 평가

### 적절하게 단순화된 결정

- 세 화면을 잇는 구체적 session 하나는 민감 데이터를 route/디스크에 넣지 않기 위한 최소 장치다.
- 축별 ViewModel, 범용 form reducer, mapper 계층을 만들지 않은 점은 타당하다.
- Player runtime 상태를 ViewModel/session에 복제하지 않은 점도 타당하다.
- 화면 전용 UI를 Feedback Feature에 두고 서버 중간 저장이나 불필요한 영상 기능을 제외한 범위는
  적절하다.
- 36개 Q&A는 길지만 민감 데이터, 종료, 오류, 제출 불변성과 같이 임의 추정하면 안 되는 결정을
  명시한 것이므로 문서 길이 자체를 과설계로 보기는 어렵다.

### 줄일 수 있는 부분

- 블러 backdrop이 반드시 필요한 시각 요구라면 `media3-effect` 선택은 정당화된다. 다만 핵심 평가
  흐름보다 복잡성과 기기 의존 위험이 크므로, 제품이 허용한다면 중앙 Fit + 검정 배경이 더 단순하고
  안정적이다. 블러가 필수라면 현재 수명·비율·fallback 설계를 먼저 바로잡아야 한다.
- UI에서 사용하지 않는 `questionBoundaries`를 session뿐 아니라 `FeedbackState`에도 복제하는 것은
  현재 렌더링에 불필요하다. “ViewModel에 포함” 요구가 session 주입으로 충족되는지 재확인하면 State
  복제를 제거할 수 있다.
- 공용 `Feedback`/`Talk` icon 승격, 특히 미사용 `Talk`는 명백히 앞선 추상화다.
- 기능과 무관한 Home/Login 포맷 수정은 PR에서 제거하는 것이 가장 단순하다.

## 6. 사용자 시나리오와 기계적 동작

### 6.1 정상 진입과 Onboarding

1. app이 `FeedbackOnboarding(token)` route를 back stack에 넣는다.
2. Screen의 `LaunchedEffect(token)`이 `Load(token)` Intent를 한 번 전달한다.
3. ViewModel이 `EnterGuestFeedbackUseCase`를 호출한다.
4. `OPEN + submissionOpen=true`이면 token, 요청자, 축, 영상 URL, 질문 경계를 메모리 session에 넣고
   본문을 표시한다.
5. 사용자가 `피드백 시작하기`를 누르면 별칭 bottom sheet가 열린다.
6. 별칭은 트리밍 후 1~12자이고 줄바꿈이 없을 때만 다음이 활성화된다.
7. `다음`을 누르면 별칭을 session에 저장하고 `FeedbackReady` Effect를 발행한다.
8. app이 back stack 전체를 `Feedback` 하나로 교체해 Onboarding 복귀를 막는다.

### 6.2 영상 시청과 평가

1. Feedback ViewModel이 session을 읽어 요청자, 영상 URL, 지정 축과 기존 초안을 State로 만든다.
2. Player Composable이 ExoPlayer를 만들고 URL을 prepare한다.
3. 최초 안내가 2초 후 또는 터치로 300ms fade된 뒤 재생을 시작하고 완료 Intent를 전달한다.
4. 사용자가 영상을 누르면 재생 control overlay가 열리고, 다시 배경을 누르면 닫힌다.
5. 가운데 버튼은 play/pause, 좌우 버튼은 현재 위치에서 +/-10초 seek한다.
6. 사용자가 축을 누르면 `AxisSelected`가 선택 축을 저장하고 3:2 분할 화면으로 바꾼다.
7. 이때 현재 구현은 블러 효과를 켜기 전에 같은 Player를 release하는 결함이 있다.
8. 사용자가 4단계 값을 누르면 State와 session 초안이 즉시 갱신된다.
9. 코멘트 편집에서 입력은 임시 State에만 있고 `다음`에서 session에 확정된다. 닫기/바깥/뒤로가기는
   임시 값을 버린다.
10. 모든 지정 축의 level이 채워지면 `피드백 종료하기`가 활성화되고 `ReviewReady`를 발행한다.
11. app이 `FeedbackReview`를 push한다.

### 6.3 Review와 제출

1. Review ViewModel이 session의 별칭, 축별 level/문구와 코멘트를 카드 State로 변환한다.
2. 사용자는 코멘트만 수정할 수 있고 level 수정은 영상 다시보기로 Feedback에 돌아가 수행한다.
3. 시스템 뒤로가기 또는 `영상 다시보기`는 app의 `goBack()`으로 Feedback을 다시 표시한다.
4. `피드백 전송하기`를 누르면 취소/제출 Modal이 열린다.
5. `제출`을 확인하면 ViewModel이 session으로 `GuestFeedbackSubmission`을 만들고 Domain UseCase를
   호출한다.
6. UseCase가 token, 별칭, 지정 축 일치, level 1~4와 코멘트 100자를 검증·정규화한 뒤 Repository를
   한 번 호출한다.
7. 성공 시 session clear -> 성공 Toast Effect -> 완료 Effect 순서로 발행한다.
8. Screen이 Toast를 표시한 뒤 app이 `finishAffinity()`로 task를 종료한다.
9. 현재는 5~7 사이에도 뒤로가기/다시보기가 가능하므로 제출 중 이탈 결함이 있다.

### 6.4 차단·오류·뒤로가기

- `PRIVATE`, `EXPIRED`, `FULL`, `ALREADY_SUBMITTED`, `submissionOpen=false`: session을 지우고 닫을
  수 없는 종료 Modal을 표시한 뒤 app을 종료한다.
- 영상 원본 재생 실패: 평가 입력을 막고 닫을 수 없는 종료 Modal 뒤 session을 지우고 종료한다.
- 영상 effect만 실패: 효과를 한 번 제거하고 원본 영상 재생을 시도한다.
- Network/Server: session을 지우고 app 전역 종료 Modal을 표시한다.
- Unknown: 안전한 고정 Toast 뒤 현재 화면에 남는다. Onboarding에서는 현재 정상 시작 UI가 열리는
  결함이 있다.
- Onboarding 뒤로가기: 첫 입력은 Toast, 2초 안 두 번째 입력은 session clear와 앱 종료다.
- Feedback 뒤로가기: 종료 확인 Modal을 열고 확인한 경우에만 session clear와 앱 종료다.
- Review 뒤로가기: 초안을 보존한 채 Feedback으로 돌아간다.

## 7. 검증 결과

| 명령 | 실제 결과 |
|---|---|
| `.\gradlew.bat --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug` | **실패**. Feedback Spotless 1 task, Feedback/Home/Login Detekt 3 task 실패 |
| `.\gradlew.bat :domain:test :feature:feedback:impl:testDebugUnitTest :feature:feedback:impl:assembleDebugAndroidTest :app:assembleDebug` | **성공** |
| `adb devices` | 연결 기기 없음 |
| `git diff --check develop...HEAD` | **실패**. `prd.md:1025` 새 EOF 공백 |
| 민감 데이터 저장·로그 API 정적 검색 | 새 저장/로그 호출 없음 |
| Catalog/Wasm | 사용자 요청으로 제외, 판정하지 않음 |

테스트 결과에서 Domain 13개와 Feedback 단위 테스트 17개는 실패 0이었다. AndroidTest APK는
조립됐지만 연결 기기가 없어 계측 테스트는 실행하지 않았다.

첫 전체 CI 시도는 실행 도구의 2분 제한으로 중단되어 판정에서 제외했고, 더 긴 제한으로 재실행한
결과가 위 실패 결과다. Catalog 명령은 사용자 중단 후 평가 범위에서 제외했으며 완료 결과를
주장하지 않는다.

## 8. PR 전 필수 조치

1. Player release effect를 표시 mode와 분리하고 split/expand 반복 테스트 추가
2. 제출 중 Review back/replay/edit 차단과 경쟁 조건 테스트 추가
3. Onboarding 진입 실패 상태에서 시작/별칭 확정 차단 및 재시도/종료 UX 확정
4. Feedback Spotless·Detekt와 `prd.md` diff-check 오류 수정
5. Feature 전용 illustration과 미사용 `Talk` 공용 asset 정리
6. 기기에서 정상/실패 영상, intro, controls, +/-10초, split/expand, background pause, effect fallback,
   Modal과 제출 성공 종료를 수동 검증
7. 전체 Android CI 재실행 성공 확인

Catalog/Wasm은 이번 평가에서 제외됐으므로 PR 정책상 별도 확인이 필요하다면 담당자가 따로 결과를
남겨야 한다.
