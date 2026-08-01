# Feedback 화면 재구현 계획

## 1. 목표

`FeedbackScreen.kt`의 ViewModel 연결 구조는 유지하면서 `FeedbackContent`를 Figma와 아래 확정 요구에
맞게 다시 구성한다.

- 최초 상태에서는 `GuestFeedbackVideoPlayer`가 세로 화면의 사용 가능 영역 전체를 채운다.
- 평가 축 버튼 행은 영상의 하단에 겹쳐 표시한다. 버튼 행은 레이아웃 높이를 차지해 영상을
  밀어내지 않는다.
- 평가 축을 선택하면 영상 영역은 화면 높이의 60%, 화면 아래 평가 패널은 40%가 되는 3:2 비율로
  표시한다.
- 분할 상태에서 영상 오른쪽 아래의 확대 버튼을 누르면 선택값은 유지한 채 영상이 다시 화면 높이
  100%를 차지한다.
- 평가 패널은 Figma `2227:4963`의 미작성 상태와 `435:6835`·`435:6846`의 작성 상태를 기준으로
  질문, 긍정·아쉬움 구분, 4단계 평가 버튼과 코멘트 영역을 구성한다.
- 화면 아래에는 `피드백 종료하기` 버튼 하나를 고정하고, 모든 서버 지정 축 평가가 완료되어야
  활성화한다. 누르면 기존 Review 화면으로 이동한다.

참고 디자인:

- 전체 화면과 축 버튼: Figma `1855:9821`
- 미작성 하단 평가 패널: Figma `2227:4963`
- 작성 상태·영상 배치·종료 버튼: Figma `435:6835`
- 작성된 평가 패널 상세: Figma `435:6846`

## 2. 범위와 유지할 계약

- `FeedbackScreen`은 기존처럼 State 구독, 최초 `LoadSession`, Effect 수집, 뒤로가기 Intent 전달만
  담당한다.
- `FeedbackContent`는 ViewModel 없는 순수 UI로 유지하고, 모든 사용자 동작을 `FeedbackIntent`로
  올린다.
- 영상의 생성, 재생·일시정지, Lifecycle 대응과 해제 책임은 기존
  `GuestFeedbackVideoPlayer`에 유지한다. 화면 재구성 중에도 Player Composable의 식별과 수명을
  보존해 축 선택 때 플레이어가 새로 만들어지지 않게 한다. 분할 상태에서는 같은 현재 영상을
  중앙 전경과 좌우 블러 배경으로 표현하되, 추가 Player를 생성하거나 영상을 저장하지 않는다.
- 축의 선택값과 코멘트는 기존 `FeedbackState`와 `FeedbackViewModel`의 메모리 초안을 그대로
  사용한다. 서버 중간 저장은 추가하지 않는다.
- 실제 면접 영상이나 피드백은 Preview, 테스트, 문서 예시에 넣지 않고 합성 문자열과 영상 대체
  영역만 사용한다.

## 3. 화면 상태별 구조

| 상태 | 영상 영역 | 축 버튼 행 | 하단 영역 |
|---|---|---|---|
| 로딩 전 또는 영상 URL 없음 | 기존 중앙 로딩 표시 | 없음 | 없음 |
| 축 미선택 | 화면 높이 100% | 영상 하단에 겹침 | 없음 |
| 축 선택·분할 | 화면 위쪽 높이 60% | 축소된 영상 하단에 겹침 | 화면 아래쪽 높이 40% 평가 패널 |
| 축 선택·영상 확대 | 화면 높이 100% | 영상 하단에 겹침 | 없음 |
| 코멘트 편집 | 직전 화면 유지 | 직전 상태 유지 | 기존 `GuestFeedbackCommentModal`을 최상단에 표시 |
| 재생 차단 | 기존 ViewModel·전역 Modal 정책 유지 | 평가 입력 차단 정책 유지 | 별도 민감 정보 노출 없음 |

화면 전체 `Column`에서 영상 영역은 항상 같은 호출 위치를 유지한다. 분할 상태에서는 영상
`Box`에 `weight(3f)`, 하단 패널에 `weight(2f)`를 적용하고, 전체 영상 상태에서는 영상만
`weight(1f)`로 표시한다. 높이 직접 계산, `BoxWithConstraints`와 별도 비율 모델은 만들지 않는다.
영상 `Box` 안에서는 Player, 그라데이션, 축 버튼 행, 확대 버튼 순서로 선언해 뒤의 UI가 앞의 UI
위에 그려지는 Compose 기본 순서를 사용한다. 실제 가림 문제가 확인되지 않는 한 `zIndex`를
추가하지 않는다. 같은 Player 호출 위치를 유지하므로 100%와 3:2 전환 때 Player를 재생성하지
않는다.

이번 디자인에는 전환 시간이나 easing이 명시되어 있지 않으므로 우선 상태에 따른 즉시 재배치를
수락 조건으로 삼는다. 영상 축소와 패널 상승 애니메이션이 필요하면 시간과 easing을 확정한 뒤
별도 단계로 추가한다.

## 4. Figma를 Compose로 옮기는 기준

### 4.1 영상과 축 버튼

- 축 미선택 또는 확대 상태에서는 영상이 전체 Player 영역을 채운다. 분할 상태에서는 실제 영상의
  종횡비를 유지한 전경 영상을 `ContentScale.Fit` 의미로 가운데 배치하고, 남는 좌우 영역은 같은
  시점의 현재 영상을 확대·블러 처리해 채운다. 별도의 Figma 인물 이미지는 앱 자산으로 추가하지
  않는다.
- 블러 배경은 메모리 안의 재생 프레임만 사용하고 파일, 로그, Preview나 테스트 자산으로 저장하지
  않는다. 같은 URL을 재요청하는 두 번째 Player도 만들지 않는다. 단일 ExoPlayer 출력에
  `media3-effect` 기반 사용자 정의 영상 효과를 적용해 매 프레임 배경 확대·블러와 중앙 선명 영상을
  하나의 출력으로 합성한다.
- 영상 효과의 초기화나 실시간 합성만 실패하면 효과를 한 번 해제하고 중앙 Fit 영상과 검정 배경으로
  자동 대체해 평가를 계속한다. 이 경우 `VideoPlaybackFailed`를 전달하지 않는다. 효과를 제거한
  원본 영상도 재생할 수 없을 때만 기존 치명적 재생 실패 정책을 적용한다.
- 영상 아래에는 Figma의 검정 그라데이션을 적용해 흰색 미선택 글자의 대비를 확보한다. 그라데이션
  영역과 축 버튼 행은 Player 위에만 겹치며 하단 패널의 높이를 소비하지 않는다.
- 버튼은 고정된 다섯 축을 임의로 만들지 않고 서버 응답의 `state.axes`에 포함된 축만 응답 순서대로
  한 행에 균등 배치한다. 응답에 다섯 축이 모두 있으면 `시선`, `표정`, `자세`, `손동작`,
  `목소리` 다섯 버튼이 표시된다.
- 선택 버튼은 `hilitGreen500` 배경, `hilitGreen800` 글자와 `body1`; 미선택 버튼은 투명 배경,
  `hilitWhite` 글자와 `body3`를 사용한다. Figma에 대응하는 공용 컴포넌트가 없으므로 이 화면에서만
  쓰는 private Composable로 두고 새 Design System API는 만들지 않는다.
- 선택은 `FeedbackIntent.AxisSelected`로 전달하고, 다른 축을 누르면 하단 패널 내용을 해당 축으로
  교체하면서 분할 상태로 돌아간다.
- 분할 상태의 중앙 전경 영상 안쪽 오른쪽 아래에는 Figma의 30dp 회색 상자를 유지하고 그 안에
  `HilitIconAsset.Expand`를 배치한다. 상자는 전경 영상의 오른쪽과 아래에서 각각 16dp 떨어지므로
  그 아래의 축 버튼 행과 겹치지 않는다. 누르면 `VideoExpanded`를 전달한다. 확대 상태에서는 이
  버튼을 표시하지 않으며 축 버튼을 누르면 기존 `AxisSelected` 처리에 따라 다시 3:2 분할 상태가
  된다.

### 4.2 하단 평가 패널

- `ModalBottomSheet`가 아니라 화면 아래 40%에 상시 배치되는 흰색 패널로 구현한다. Figma에는
  모서리 반경, scrim, drag handle이 없으므로 임의로 추가하지 않는다.
- 바깥 여백은 가로 20dp, 세로 24dp, 주요 그룹 간격은 20dp, 내부 구분 간격은 8dp를 사용한다.
- 제목은 `sub4`, `hilitBlack800`을 사용한다.
- `좋았어요`는 기존 파란색 Small `HilitTag`, `아쉬웠어요`는 기존 빨간색 Small `HilitTag`로
  양 끝에 배치한다.
- 네 평가값은 기존 `HilitMediumButton(color = Gray)`를 같은 너비와 8dp 간격으로 한 행에
  배치한다. 선택된 값은 기존 Blue 또는 Red 상태를 사용하고 `RatingSelected(axis, level)`을
  전달한다.
- 코멘트가 작성된 축은 Figma `435:6846`처럼 `gray50` 배경, 왼쪽 4dp `hilitGreen500` 선,
  `body6` 코멘트 한 줄과 밑줄 `수정` 동작으로 구성한 Feedback 전용 인용 영역을 표시한다.
  `수정`을 누르면 기존 `CommentEditorClicked`를 전달한다.
- 코멘트가 비어 있으면 Figma `435:6831`과 기존 `HilitOptionalButton`을 사용해 16dp 외곽 상자 안
  12dp Plus 아이콘, `왜 그렇게 느꼈나요?`, Gray Small `선택` 태그를 계속 표시한다. 코멘트 저장
  후에는 같은 위치를 인용 영역으로 교체한다.
- Figma의 24dp 로딩 표시와 `저장 중 ...`은 서버 중간 저장을 나타내지만 현재 기능은 최종 제출만
  수행한다. 기존에 확정된 제품 정책대로 오해를 낳는 가짜 저장 상태는 표시하지 않는다.
- 평가 패널 맨 아래에는 기존 `HilitFixedBottomButton`을 `피드백 종료하기` 문구로 고정한다.
  `enabled = state.canReview && !state.isPlaybackBlocked`를 사용하고 클릭 시 기존 `ReviewClicked`를
  전달해 Review 화면으로 이동한다.
- 패널 내용이 작은 화면이나 큰 글꼴에서 40% 높이를 넘으면 평가 콘텐츠만 세로 스크롤한다. 종료
  버튼은 패널 맨 아래에 고정하고 영상 및 축 버튼 위치도 유지한다.

## 5. 파일별 변경 계획

### `feature/feedback/impl/.../feedback/FeedbackScreen.kt`

1. 현재 236dp 영상 높이, 영상 크게 보기 버튼, 축 탭이 패널 높이를 차지하는 Column 구조를 제거한다.
2. 하나의 `Column`에서 영상 `weight(3f)`와 패널 `weight(2f)`만으로 분할 상태를 구성하고, 전체
   영상 상태에서는 패널을 생략한다.
3. 화면 전용 private Composable은 `FeedbackVideoArea`, `FeedbackAxisSelector`,
   `FeedbackAxisPanel` 세 개를 기본으로 사용한다. 별도 Props 타입이나 표시 모드 enum은 만들지
   않는다.
4. 작성 코멘트 인용 영역은 `FeedbackAxisPanel` 안에 직접 작성하고, 함수가 과도하게 길어질 때만
   `FeedbackCommentQuote`로 분리한다.
5. 패널 아래에 `피드백 종료하기` 버튼을 고정하고 `canReview`와 `ReviewClicked`를 연결한다.
6. Preview는 축 미선택 전체 영상과 축 선택 3:2 분할 두 상태만 둔다. 선택 후 영상 확대는 전체
   영상과 레이아웃이 같으므로 Preview를 중복 추가하지 않고 상태 전이 테스트로 확인한다.

### `feature/feedback/impl/.../component/GuestFeedbackVideoPlayer.kt`

- 기존 매개변수에 `showBlurredBackdrop: Boolean`과 `onExpand: () -> Unit`만 추가한다. 별도 표시
  모드 enum이나 Props 객체는 만들지 않는다.
- `showBlurredBackdrop`이 true일 때 중앙 Fit·블러 배경과 확대 버튼을 표시하고 false일 때 기존 전체
  채움 영상을 표시한다. 확대 버튼 클릭은 `onExpand`로 상위에 전달한다.
- 하나의 Player 수명, 자동 재생, 터치 재생·일시정지와 Lifecycle 처리는 유지한다.
- 분할 상태에는 사용자 정의 Media3 영상 효과를 설정하고 확대 상태에는 효과를 제거한다. 효과는
  한 입력 프레임을 확대·블러 배경과 중앙 Fit 전경으로 같은 출력에 합성한다.
- 효과 초기화·shader 컴파일·프레임 합성 실패 여부는 ViewModel State로 올리지 않고 Player 내부
  Boolean 하나로 한 번만 복구한다. 현재 재생 위치와 재생 의도를 보존해 효과 없이 다시 준비하고,
  중앙 Fit 영상과 검정 배경으로 이어서 재생한다. 효과 없는 원본 재생 실패만
  `onFatalPlaybackError`로 보고한다.
- 현재 재생 프레임만 GPU 메모리에서 처리하고 영상 파일, 프레임 또는 썸네일을 저장·공유·기록하지
  않는다.

### `feature/feedback/impl/.../component/GuestFeedbackVideoPresentationEffect.kt`

- Media3 `GlEffect` 계약을 따르는 Feedback 전용 효과를 추가한다.
- 입력 프레임을 화면 채움 비율로 확대한 배경에 Figma와 맞는 블러를 적용하고, 원본 종횡비의 선명한
  전경을 중앙 Fit 영역에 합성한다.
- 한 프레임과 한 Player만 사용하며 CPU bitmap 추출, 파일 캐시, 두 번째 네트워크 요청을 금지한다.
- 효과가 해제되면 GPU 자원을 반환하고 화면·Player 수명 밖에 민감 프레임을 보유하지 않는다.
- 효과 오류는 URL, 프레임, 사용자 데이터와 원문을 로그에 포함하지 않고 Player가 같은 효과를
  반복 적용하지 않도록 해당 수명 동안 대체 상태를 유지한다.

### `gradle/libs.versions.toml`과 `feature/feedback/impl/build.gradle.kts`

- 현재 Media3 `1.10.1` 버전을 공유하는 `androidx.media3:media3-effect` alias를 추가하고 Feedback
  구현 모듈에만 의존성을 연결한다. 다른 Feature나 Design System으로 노출하지 않는다.

### `feature/feedback/impl/.../feedback/FeedbackContract.kt`와 `FeedbackViewModel.kt`

- `isVideoExpanded`와 `VideoExpanded`는 선택 후 영상 100% 복귀 동작에 계속 사용한다.
  `AxisSelected`는 현재와 같이 선택 축을 갱신하면서 `isVideoExpanded = false`로 만들어 3:2 분할
  상태에 진입한다. 별도 호출처가 없는 `VideoCollapsed`는 Contract와 ViewModel에서 함께 제거한다.
- 축별 질문형 제목이 확정되면 UI에 필요한 표시 문구를 `GuestFeedbackAxisCode` 기반의 feature 전용
  변환으로 추가한다. Domain 모델에는 화면 문구를 넣지 않는다.
- 질문 문구와 4단계 평가지의 확정 매핑은 다음과 같다. `{대상}`은
  `FeedbackState.requesterName`으로 치환한다.

| 축            | 질문                    | level 4 | level 3 | level 2 | level 1 |
|--------------|-----------------------|---------|---------|---------|---------|
| `GAZE`       | `{대상}님은 눈을 잘 마주치나요?`  | 잘 맞춤    | 꽤 맞춤    | 가끔 피함   | 자주 피함   |
| `EXPRESSION` | `표정이 안정되어 보이나요?`      | 안정됨     | 꽤 안정됨   | 가끔 굳음   | 자주 굳음   |
| `POSTURE`    | `{대상}님이 자세를 잘 유지하나요?` | 반듯함     | 꽤 반듯함   | 가끔 흔들림  | 자주 흔들림  |
| `GESTURE`    | `손동작이 말과 잘 어울리나요?`    | 잘 어울림   | 꽤 어울림   | 가끔 산만함  | 자주 산만함  |
| `VOICE`      | `목소리가 선명하게 들리나요?`     | 잘 들림    | 꽤 들림    | 가끔 안 들림 | 자주 안 들림 |

  이 매핑은 기존 `ratingOptions()`의 표정·자세·손동작 문구도 변경하므로 Contract 단위 테스트를
  함께 갱신한다.

### 테스트

- 자동화 테스트는 사용자 동작, 상태 분기와 오류 정책에 한정하고 화면 좌표 검증만을 위한 테스트
  식별자는 추가하지 않는다.
- `GuestFeedbackContentTest`에서는 다음을 검증한다.
  - 서버 지정 축만 표시되고 축 클릭이 `AxisSelected`를 전달한다.
  - 선택 축의 질문과 네 평가 문구가 표시되고 평가 클릭이 `RatingSelected`를 전달한다.
  - 코멘트가 없으면 `HilitOptionalButton`, 있으면 인용 영역과 `수정`이 표시되며 각각
    `CommentEditorClicked`를 전달한다.
  - 모든 지정 축 평가 전에는 `피드백 종료하기`가 비활성이고 완료 후 활성화되며 클릭 시
    `ReviewClicked`를 전달한다.
  - 확대 버튼 클릭이 `VideoExpanded`를 전달한다.
- `FeedbackViewModelTest`에서는 축 선택 시 분할 상태, `VideoExpanded` 처리와 `VideoCollapsed`
  제거에 따른 분기 정리를 검증한다.
- Player 오류 테스트에서는 효과 실패가 한 번만 복구되고 원본 재생 실패일 때만
  `VideoPlaybackFailed`가 한 번 전달되는 정책을 검증한다. Media3·GPU 실제 동작처럼 안정적으로
  자동화하기 어려운 항목은 아래 수동 검수로 넘긴다.

## 6. 검증 계획

구현 후 아래 순서로 실행하고 실제 결과를 보고한다.

1. `./gradlew :feature:feedback:impl:testDebugUnitTest`
2. 연결된 Emulator에서
   `./gradlew :feature:feedback:impl:connectedDebugAndroidTest`
3. 전체 CI 검증:
   `./gradlew --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug`
4. Figma 기준 수동 검수:
   - 세로 화면 최초 영상 100%
   - 영상 위에 겹친 서버 지정 축 버튼과 선택 색상
   - 선택 후 정확한 상·하 3:2 배치
   - 축 버튼과 확대 버튼의 가림·터치 영역 충돌 없음
   - 중앙 Fit 영상, 같은 프레임 기반 좌우 블러 배경
   - 영상 효과 실패 시 중앙 Fit·검정 배경 대체와 재생 지속
   - 전경 영상 오른쪽·아래 16dp 확대 버튼과 100% 복귀 동작
   - 작성 코멘트 인용 영역과 고정 `피드백 종료하기` 버튼
   - 375dp 너비에서 여백, 간격, 한 줄 문구 잘림 여부
   - 작은 화면과 큰 글꼴에서 패널 내부 스크롤 및 터치 가능 여부
   - 확대·분할 반복 중 단일 Player 유지와 화면 종료 후 Player·GPU 자원 해제

이번 변경은 Android 전용 Player를 Feature 모듈에 유지하며 `media3-effect`를 Feedback 구현에만
추가한다. 공용 Design System API, Catalog, 저장·전송·로깅 정책은 변경하지 않는다. 영상 프레임은
GPU 메모리에서 실시간 처리하고 영속화하지 않는다.

## 7. 반영된 결정

- 축 버튼은 서버 응답에 포함된 축만 응답 순서대로 표시한다.
- 제목의 `{대상}`은 `FeedbackState.requesterName`으로 치환한다.
- 축별 질문과 4단계 평가지 문구는 5절의 매핑을 사용한다.
- 선택 상태의 플레이어와 평가 패널은 3:2 비율로 배치한다.
- 분할 상태에서는 영상 오른쪽·아래 16dp에 `HilitIconAsset.Expand` 확대 버튼을 표시하고,
  `VideoExpanded`를 전달해 영상을 100%로 복원한다.
- 확대 버튼은 중앙 전경 영상의 오른쪽·아래 16dp에 배치하고, 축 버튼 행은 전경 영상 아래에 둔다.
- 작성된 코멘트는 인용 영역과 `수정` 동작으로 표시하고 `저장됨` 아이콘과 문구는 제외한다.
- 하단 버튼은 `피드백 종료하기` 하나만 사용하고 기존 `ReviewClicked`로 Review 화면에 이동한다.
- 모든 서버 지정 축의 평가가 완료되기 전에는 `피드백 종료하기`를 비활성화한다. 활성 조건은 기존
  `FeedbackState.canReview`를 사용한다.
- 종료 버튼은 패널 아래에 고정하고 평가 콘텐츠만 스크롤한다.
- 좌우 블러 배경은 재생 프레임과 매 프레임 동기화하고, 필요한 Media3 의존성과 Player 내부 구조
  변경을 허용한다.
- 코멘트가 없으면 `HilitOptionalButton`을 유지하고, 저장된 코멘트가 있으면 인용 영역으로 교체한다.
- 영상 효과만 실패하면 중앙 Fit 영상과 검정 배경으로 자동 대체해 평가를 계속한다. 원본 영상
  재생까지 실패한 경우에만 기존 `VideoPlaybackFailed` 종료 안내를 사용한다.
- 실시간 좌우 블러 합성은 기존 계획대로 Feedback 전용
  `GuestFeedbackVideoPresentationEffect` 사용자 정의 `GlEffect`로 구현한다.
- 3:2 배치는 `Column`의 `weight(3f)`와 `weight(2f)`만 사용하고 높이 계산용 상태나 타입을 만들지
  않는다.
- UI 겹침은 같은 `Box` 안의 선언 순서로 해결하고 실제 문제가 없으면 `zIndex`를 사용하지 않는다.
- Player 공개 API는 Boolean과 callback만 추가하고 표시 모드 enum, Props 객체와 ViewModel용 효과
  상태를 만들지 않는다.
- 화면 전용 UI는 세 private Composable을 기본으로 유지하고 인용 영역은 필요할 때만 분리한다.
- 자동화 테스트는 동작·상태·오류 정책에 집중하고 비율·좌표·블러·자원 해제는 수동 검수한다.

## 8. 영상 재생 컨트롤과 Preview 후속 계획

Figma `435:7111`의 한 번 터치 상태를 기준으로 `GuestFeedbackVideoPlayer`에 아래 후속 범위를
반영한다.

### 8.1 상호작용과 시각 기준

- 인트로가 끝난 영상을 한 번 터치하면 기존의 즉시 재생·일시정지 동작 대신 재생 컨트롤을
  표시한다.
- 컨트롤 표시 중 영상 배경을 다시 터치하면 컨트롤을 숨긴다. 각 컨트롤 버튼의 클릭은 배경
  닫기 동작으로 전파하지 않는다.
- 컨트롤이 보일 때 영상 위에 `hilitBlack800` 65% 오버레이를 표시한다. 하단 평가 축 선택기는
  기존 선언 순서에 따라 오버레이보다 위에서 표시한다.
- 가운데에는 `HilitIconAsset.Play` 또는 `HilitIconAsset.Pause`를 사용하는 74dp
  `hilitGreen500` 버튼을 두고, 재생 상태에 따라 아이콘과 접근성 설명을 전환한다.
- 좌우에는 44dp 터치 영역 안에 `HilitIconAsset.SkipLeft`와 `SkipRight`를 배치한다. 가운데
  버튼과의 간격은 각각 46dp, 아이콘 외곽 크기는 34dp를 사용한다.
- 좌우 이동은 현재 위치에서 정확히 10초를 빼거나 더한다. 결과는 0과 영상 길이 사이로
  제한하며, 아직 영상 길이를 알 수 없는 상태에서도 음수 위치를 만들지 않는다.
- 컨트롤 표시 여부는 Player 수명에 종속된 일시적 UI 상태이므로 ViewModel State나 Intent를
  추가하지 않고 `GuestFeedbackVideoPlayer` 안에서 관리한다.

### 8.2 Preview

- 같은 파일에 375×812 크기의 `재생 컨트롤 표시`와 `재생 컨트롤 숨김` Preview를 각각 둔다.
- Preview는 실제 ExoPlayer, 영상 URL, Lifecycle, network와 사용자 데이터를 사용하지 않는다.
  `HilitTheme`의 합성 회색 배경과 ViewModel-free `GuestFeedbackVideoControls`만 렌더링한다.
- 표시 Preview는 재생 중 상태로 구성해 Pause 아이콘과 좌우 이동 버튼 배치를 확인하고, 숨김
  Preview는 오버레이와 컨트롤이 없는 기본 영상 영역을 확인한다.

### 8.3 테스트와 검증

- 단위 테스트에서 ±10초 계산, 시작·끝 경계 제한과 영상 길이를 알 수 없는 상태를 검증한다.
- 대상 파일 Spotless, Feedback 단위 테스트, Kotlin 컴파일, Lint와 Debug 조립을 실행한다.
- 실제 기기에서는 한 번 터치 표시, 다시 터치 숨김, 버튼 클릭 시 오버레이 유지, 재생·일시정지
  아이콘 전환과 ±10초 이동을 수동 검수한다.
- 전체 CI가 기존 범위 밖 Detekt 또는 Spotless 문제로 실패하면 최신 후속 변경의 대상 검증 결과와
  기존 실패 위치를 분리해 보고한다.
