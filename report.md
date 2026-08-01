# Feedback 화면 재구현 결과 보고서

## 1. 구현 결과 요약

`FeedbackScreen.kt`를 영상 중심의 전체 화면과 3:2 분할 평가 화면으로 재구현했다.

- 최초 진입과 영상 확대 상태에서는 `GuestFeedbackVideoPlayer`가 사용 가능 영역 전체를 차지한다.
- 서버가 지정한 평가 축 버튼을 영상 하단에 겹쳐 표시하고, 선택 축만 초록색으로 구분한다.
- 축을 선택하면 하나의 `Column`에서 영상 `weight(3f)`, 평가 패널 `weight(2f)`로 분할한다.
- 분할 화면의 확대 버튼은 선택 축과 입력값을 유지하면서 영상을 다시 전체 화면으로 복원한다.
- 하단 평가 패널에 질문, 긍정·아쉬움 태그, 4단계 평가, 선택 코멘트와
  `피드백 종료하기` 버튼을 구현했다.
- 작성된 코멘트는 인용 영역과 `수정` 동작으로 표시하고, 비어 있으면 기존 선택 버튼을 표시한다.
- 사용자가 `plan.md` 5절에서 직접 갱신한 축별 질문과 평가 선택지를 Contract와 Review 표시까지
  동일하게 적용했다.
- 사용하지 않는 `VideoCollapsed` Intent와 ViewModel 분기를 제거했다.
- 단일 ExoPlayer에 Feedback 전용 `GlEffect`를 적용해 현재 프레임으로 흐린 배경과 선명한 중앙
  영상을 합성한다.
- 효과 처리만 실패하면 효과를 한 번 제거하고 기존 재생 위치와 재생 의도를 유지한 원본 영상으로
  복구한다. 원본 영상도 실패한 경우에만 기존 치명적 재생 오류를 전달한다.

영상 효과는 AndroidX Media3가 공식 지원하는 ExoPlayer의 `setVideoEffects` 경로를 사용한다.
`media3-exoplayer`가 효과 모듈을 런타임 의존성으로 직접 제공하지 않으므로 같은 `1.10.1` 버전의
`media3-effect`를 Feedback 구현 모듈에만 추가했다. 자세한 API 계약은
[Android Media3 영상 변환 문서](https://developer.android.com/media/media3/transformer/transformations)와
[GaussianBlurWithFrameOverlaid API](https://developer.android.com/reference/androidx/media3/effect/GaussianBlurWithFrameOverlaid)를
기준으로 확인했다.

## 2. 수락 조건 충족 여부

| 수락 조건 | 결과 | 근거 |
|---|---|---|
| 최초 상태에서 세로 영상이 사용 가능 영역 전체를 차지 | 충족 | 전체 영상 상태에서 Player에 `weight(1f)` 적용, 하단 패널 미구성 |
| 평가 축 행이 영상 위에 겹쳐 표시 | 충족 | `FeedbackVideoArea`의 같은 `Box`에 Player, 그라데이션, 축 선택기를 선언 순서로 배치 |
| 서버 지정 축만 응답 순서대로 표시 | 충족 | `state.axes.forEach`로 균등 배치하고 UI 테스트 소스 컴파일 완료 |
| 축 선택 후 영상 60%, 평가 패널 40%의 3:2 분할 | 충족 | `weight(3f)`와 `weight(2f)`만 사용 |
| 분할 화면에서 영상 확대 후 선택값 유지 | 충족 | `VideoExpanded`는 `isVideoExpanded`만 변경하며 ViewModel 단위 테스트 통과 |
| 갱신된 PRD의 질문과 4단계 선택지 적용 | 충족 | 5개 질문과 선택지 매핑 단위 테스트 통과, Review 예상값도 갱신 |
| 코멘트 미작성·작성·수정 상태 구분 | 충족 | 선택 버튼과 인용 영역 분기, UI 계측 테스트 추가 및 소스 컴파일 완료 |
| 모든 서버 지정 축 평가 후에만 종료 버튼 활성화 | 충족 | 기존 `canReview`와 재생 차단 상태 연결, UI 계측 테스트 추가 및 소스 컴파일 완료 |
| 하나의 Player와 현재 프레임만 사용 | 충족 | Player 생성은 `remember(videoUrl)` 한 곳이며 파일·프레임 추출과 두 번째 요청 없음 |
| 영상 효과 실패 시 한 번만 원본 재생으로 복구 | 충족 | 오류 코드와 복구 여부 분기 및 단위 테스트 2개 통과 |
| 실제 기기에서 블러 품질·정확한 여백·자원 해제 확인 | 미검증 | 연결된 Emulator 또는 기기가 없어 수동 검수와 계측 테스트 실행 생략 |

코드로 확인 가능한 수락 조건은 충족했다. 실제 Media3 GPU 출력과 Figma 시각 일치는 기기 검수가
남아 있으므로 전체 수락 상태는 부분 검증이다.

## 3. 생성·변경된 파일

### Feedback UI와 상태

| 파일 | 구분 | 내용 |
|---|---|---|
| `feature/feedback/impl/src/main/kotlin/com/dminus14/app/feature/feedback/feedback/FeedbackScreen.kt` | 재구현 | 전체 영상, 3:2 분할, 축 선택기, 평가 패널, 코멘트와 종료 버튼 |
| `feature/feedback/impl/src/main/kotlin/com/dminus14/app/feature/feedback/feedback/FeedbackContract.kt` | 변경 | `VideoCollapsed` 제거, 최신 질문·선택지 매핑 추가 |
| `feature/feedback/impl/src/main/kotlin/com/dminus14/app/feature/feedback/feedback/FeedbackViewModel.kt` | 변경 | 사용하지 않는 `VideoCollapsed` 처리 제거 |
| `feature/feedback/impl/src/main/kotlin/com/dminus14/app/feature/feedback/feedback/FeedbackEntryBuilder.kt` | 포맷 | 기존 빈 선언을 Spotless 형식으로 정리, 동작 변경 없음 |

### 영상 처리

| 파일 | 구분 | 내용 |
|---|---|---|
| `feature/feedback/impl/src/main/kotlin/com/dminus14/app/feature/feedback/component/GuestFeedbackVideoPlayer.kt` | 변경 | 효과 전환, 확대 버튼, 효과 실패 복구와 기존 Player 수명 유지 |
| `feature/feedback/impl/src/main/kotlin/com/dminus14/app/feature/feedback/component/GuestFeedbackVideoPresentationEffect.kt` | 생성 | 흐린 배경과 선명한 중앙 프레임을 합성하는 Feedback 전용 `GlEffect` |
| `feature/feedback/impl/build.gradle.kts` | 변경 | Feedback 구현에만 `media3-effect` 의존성 추가 |
| `gradle/libs.versions.toml` | 변경 | 기존 Media3 버전을 공유하는 effect alias 추가 |

### 테스트와 문서

| 파일 | 구분 | 내용 |
|---|---|---|
| `feature/feedback/impl/src/test/kotlin/com/dminus14/app/feature/feedback/component/GuestFeedbackVideoPlayerTest.kt` | 생성 | 효과 복구와 치명적 오류 분기 테스트 |
| `feature/feedback/impl/src/test/kotlin/com/dminus14/app/feature/feedback/feedback/FeedbackViewModelTest.kt` | 변경 | 분할·확대 전이와 최신 질문·선택지 테스트 |
| `feature/feedback/impl/src/test/kotlin/com/dminus14/app/feature/feedback/review/FeedbackReviewViewModelTest.kt` | 변경 | 최신 음성 평가 문구 반영 |
| `feature/feedback/impl/src/androidTest/kotlin/com/dminus14/app/feature/feedback/GuestFeedbackContentTest.kt` | 변경 | 질문·평가·코멘트·종료·확대 UI 동작 테스트 추가 |
| `report.md` | 생성 | 구현 결과와 검증·잔여 위험 기록 |

작업 시작 전부터 존재하던 다른 모듈과 Feedback 온보딩·Review 화면의 사용자 변경은 되돌리지
않았다.

## 4. 오캄의 면도날을 반영한 지점

### 높이 계산 상태 없이 3:2 배치

화면 높이를 측정하거나 별도 표시 모드를 만들지 않았다. 하나의 `Column`과 두 개의 가중치만으로
전체 화면과 3:2 분할을 표현한다.

### 선언 순서로 영상 위 UI 배치

Player, 그라데이션, 축 선택기를 같은 `Box`에 순서대로 선언했다. 별도 레이어 관리자나 `zIndex`
값을 추가하지 않았다.

### Player API를 Boolean과 callback으로 제한

새 모드 enum이나 Props 객체 없이 `showBlurredBackdrop`과 `onExpand`만 추가했다. 효과 복구 여부도
화면 State나 ViewModel로 올리지 않고 Player 수명 안의 Boolean 하나로 관리한다.

### 단일 Player와 공식 GPU 효과 재사용

두 번째 Player, bitmap 추출, CPU 프레임 처리와 파일 캐시를 만들지 않았다. Feedback 전용
`GlEffect`는 Media3의 GPU shader program을 위임해 민감 영상 프레임을 앱 저장소에 남기지 않는다.

### 검증 책임 분리

상태 전이, 문구 매핑과 오류 정책은 자동화 테스트로 검증했다. 실제 GPU 효과, 화면 비율, 터치
충돌과 자원 해제처럼 환경 의존적인 항목은 억지 좌표 테스트를 만들지 않고 기기 수동 검수로
남겼다.

## 5. 검증 결과

| 순서 | 명령 | 결과 |
|---|---|---|
| 1 | `.\gradlew.bat :feature:feedback:impl:testDebugUnitTest` | 성공, 15개 테스트·실패 0 |
| 2 | `.\gradlew.bat :feature:feedback:impl:compileDebugAndroidTestKotlin` | 성공 |
| 3 | `.\gradlew.bat :feature:feedback:impl:spotlessCheck` | 성공 |
| 4 | `.\gradlew.bat :feature:feedback:impl:lintDebug :app:assembleDebug -x detekt` | 앱 조립 성공, 최초 Feedback Lint 1건 발견 |
| 5 | `.\gradlew.bat :feature:feedback:impl:lintDebug :feature:feedback:impl:testDebugUnitTest -x detekt` | opt-in 수정 후 성공 |
| 6 | `.\gradlew.bat --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug` | 실패, 아래 기존 정적 검증 문제 |
| 7 | `git diff --check` | 성공, 기존 XML·TOML의 CRLF 변환 경고만 출력 |
| 8 | `adb devices` | 연결 기기 없음 |

전체 CI 명령은 `--continue`로 끝까지 실행했으나 이번 화면 변경 외의 기존 작업 트리 문제 때문에
성공하지 못했다.

- `feature:feedback:impl` Detekt: 기존 `FeedbackReviewScreen.kt`의 `LongMethod`, `MagicNumber`
- `feature:home:api` Detekt: `HomeRoute.kt`의 선언명 불일치
- `feature:login:impl` Detekt: `SplashViewModel.kt`의 금지된 TODO 주석
- `feature:home:api`, `feature:home:impl`, `feature:login:impl` Spotless 위반

이번에 재구현한 `FeedbackScreen.kt`, Player와 신규 테스트에서 발생한 컴파일·Spotless·Lint 문제는
모두 수정했다. Detekt 실패를 우회한 대상 Lint와 앱 조립도 별도로 성공했다.

연결된 기기가 없어 다음 명령은 실행하지 않았다.

```text
.\gradlew.bat :feature:feedback:impl:connectedDebugAndroidTest
```

## 6. 잔여 범위와 수동 검수

기기 또는 Emulator에서 다음 항목을 확인해야 한다.

- 최초 영상이 세로 화면의 사용 가능 영역 전체를 채우는지
- 축 선택 후 실제 표시 높이가 상·하 3:2인지
- 다섯 축 버튼이 영상 위에 표시되고 확대 버튼과 터치 영역이 충돌하지 않는지
- 중앙 영상의 종횡비와 같은 시점의 좌우 블러 배경이 자연스러운지
- 효과 초기화 실패 시 검정 배경의 원본 영상으로 재생이 이어지는지
- 확대 버튼이 전경 영상 오른쪽·아래 16dp 위치에 보이는지
- 375dp 너비와 큰 글꼴에서 질문·평가 문구 및 내부 스크롤이 정상인지
- 확대·분할을 반복해도 Player가 재생성되지 않고 화면 종료 시 Player와 GPU 자원이 해제되는지

이번 변경은 영상이나 피드백을 새로 저장·공유·기록하지 않는다. `media3-effect`는 Feedback 구현
모듈의 런타임 GPU 처리에만 사용하며 Design System, Domain, Data의 모듈 경계를 변경하지 않았다.

## 7. 후속 구현: 영상 재생 컨트롤과 Preview

Figma `435:7111`을 기준으로 `GuestFeedbackVideoPlayer`의 터치 동작과 재생 컨트롤을 추가했다.

- 인트로 종료 후 영상을 한 번 터치하면 65% `hilitBlack800` 오버레이와 재생 컨트롤이 나타난다.
- 컨트롤이 보이는 상태에서 영상 배경을 다시 터치하면 오버레이와 컨트롤이 사라진다.
- 가운데 74dp 버튼은 기존 `play.xml`과 `pause.xml`을 `HilitIconAsset.Play/Pause`로 사용하고,
  실제 Player 재생 상태에 따라 재생·일시정지 아이콘과 접근성 설명을 전환한다.
- 좌우 44dp 버튼은 기존 `skip_left.xml`과 `skip_right.xml`을 사용해 현재 위치에서 ±10초를
  이동한다. 계산 결과는 영상 시작과 끝 범위를 넘지 않는다.
- Figma 치수에 맞춰 아이콘 34dp와 버튼 사이 46dp 간격을 적용했다.
- 컨트롤 표시 여부는 Player 내부의 일시 상태로 관리해 Feedback MVI Contract와 ViewModel에는
  상태나 Intent를 추가하지 않았다.

### 7.1 Preview

`GuestFeedbackVideoPlayer.kt`에 다음 ViewModel-free Preview 두 개를 추가했다.

| Preview | 상태 | 런타임 의존성 |
|---|---|---|
| `재생 컨트롤 표시` | 합성 배경 위 오버레이, Pause와 좌우 이동 버튼 표시 | 없음 |
| `재생 컨트롤 숨김` | 합성 배경만 표시 | 없음 |

두 Preview 모두 375×812 크기와 `HilitTheme`을 사용한다. 실제 ExoPlayer, 영상 URL, Lifecycle,
network, 파일 접근과 사용자 데이터를 포함하지 않는다. 같은 파일에 동시에 반영된 Kotlin
Duration 기반 delay에는 누락된 `milliseconds` import를 보완했다.

### 7.2 추가 변경 파일

| 파일 | 내용 |
|---|---|
| `feature/feedback/impl/src/main/kotlin/com/dminus14/app/feature/feedback/component/GuestFeedbackVideoPlayer.kt` | 터치 표시·숨김, 오버레이, 재생·일시정지, ±10초 이동 컨트롤과 Preview 2종 |
| `feature/feedback/impl/src/test/kotlin/com/dminus14/app/feature/feedback/component/GuestFeedbackVideoPlayerTest.kt` | ±10초 이동과 재생 위치 경계 단위 테스트 |
| `plan.md` | 후속 컨트롤·Preview 계획과 검증 기준 추가 |
| `report.md` | 후속 구현·검증 결과와 잔여 검수 추가 |

### 7.3 추가 검증 결과

| 명령 | 결과 |
|---|---|
| `.\gradlew.bat :feature:feedback:impl:testDebugUnitTest :feature:feedback:impl:lintDebug :feature:feedback:impl:assembleDebug :feature:feedback:impl:spotlessCheck -PspotlessFiles=.*GuestFeedbackVideoPlayer(Test)?\\.kt -x :feature:feedback:impl:detekt` | 성공 |
| `.\gradlew.bat -PspotlessFiles=.*GuestFeedbackVideoPlayer[.]kt :feature:feedback:impl:spotlessCheck :feature:feedback:impl:compileDebugKotlin` | 성공 |
| `git diff --check` | 성공 |

후속 변경 뒤 전체 CI 명령은 다시 실행하지 않았다. Feedback 모듈 전체 Detekt는 기존
`FeedbackReviewScreen.kt`의 `LongMethod`와 `MagicNumber` 위반 2건 때문에 실패하며, 이번에 추가한
Player 컨트롤과 Preview의 Detekt 파라미터 수 위반은 구현을 분리해 해결했다. 따라서 최신 후속
변경은 대상 컴파일·단위 테스트·Spotless·Lint·Debug 조립까지 검증했고, 저장소 전체 상태는 기존
정적 분석 문제로 인해 여전히 부분 검증이다.

### 7.4 남은 기기 검수

- 영상 한 번 터치 시 컨트롤이 표시되고 다시 터치하면 숨겨지는지
- 재생·일시정지 버튼 클릭 후 아이콘이 실제 Player 상태와 일치하는지
- 좌우 버튼이 정확히 10초 이동하고 영상 시작·끝에서 안전하게 제한되는지
- 컨트롤 버튼을 누를 때 배경 닫기 동작이 함께 실행되지 않는지
- 전체 영상과 3:2 분할 영상 모두에서 컨트롤과 축 선택기 터치 영역이 충돌하지 않는지

이 후속 구현도 영상 프레임, URL 또는 재생 위치를 저장·전송·로깅하지 않으며 기존 단일 Player
수명과 Feature 모듈 경계를 유지한다.
