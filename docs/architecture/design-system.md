# Design System

이 문서는 Compose Multiplatform 기반 공용 UI, 제품 Theme, 상태 소유권과 공용 리소스 계약을
정의한다. 모듈 경계는 [`module-system.md`](module-system.md), Story 작성은
[`catalog.md`](catalog.md)를 함께 따른다.

## 1. 플랫폼 경계

`designsystem`의 모든 공용 UI는 Android와 Web/WASM Catalog에서 재사용할 수 있는 Compose
Multiplatform 코드로 작성한다.

다음 의존성과 동작을 금지한다.

- `Context`, `Activity`, `Intent` 등 Android Framework 타입
- Android Lifecycle API
- Android Navigation API
- Hilt 또는 Android DI API
- Android resource API 직접 접근
- Toast 직접 호출
- Android platform-specific side effect
- `feature:*` 또는 `app` 의존

플랫폼별 동작은 callback 또는 platform-independent state로 상위에 전달하고 `app` 또는
`feature:*`에서 처리한다.

## 2. 상태 소유권과 공개 API

공용 Composable은 가능한 한 stateless API로 작성한다.

- 현재 렌더링에 필요한 값을 매개변수로 받는다.
- 사용자 동작은 callback으로 전달한다.
- Figma MCP 서버로부터 가져온 디자인을 바탕으로 공용 Composable을 구현할 경우, KDoc 최상단에 해당 Figma 노드 ID/번호(예: `Figma Node: 1234:5678`)를 반드시 병기한다.
- 지속되는 화면 상태, 비동기 작업, 입력 검증과 비즈니스 정책은 호출자가 소유한다.
- `ViewModel`, `StateFlow`, `Flow`, Android Lifecycle 타입과 호출자 소유 mutable state container를
  공개 UI API로 받지 않는다.
- `Modifier`와 callback은 Compose UI 호출 계약으로 사용할 수 있지만 Design System이 호출자의
  상태 저장소를 소유하지 않는다.

단순 표시와 조작 값은 `String`, `Boolean`, 숫자와 enum 같은 안정적인 플랫폼 독립 타입을
우선한다. 유효하지 않은 조합을 막거나 UI 의미를 명확히 해야 하면 immutable value type이나
enum을 사용할 수 있다. Catalog Controls만을 위해 Props data class를 만들거나 실제 API를
primitive 집합으로 강제하지 않는다.

Composable 내부에는 animation progress, focus와 press처럼 외부 의미가 없고 복원할 필요가
없는 짧은 수명의 구현 상태만 둘 수 있다. 선택, 입력값, 열림 여부, loading과 error처럼 호출자가
제어하거나 Story에서 재현해야 하는 상태는 끌어올린다.

### 2.1 공용 Composable

구현 전 아래 목록을 확인한다. Figma 기반 Composable은 KDoc의 원천 노드도 확인한다.

| 분류 | Composable | 주 용도 |
|---|---|---|
| 버튼 | `HilitFixedBottomButton`, `HilitFixedBottomDualButton` | 하단 고정 CTA |
| 버튼 | `HilitMediumButton`, `HilitMiniButton`, `HilitOptionalButton` | 크기·선택형 버튼 |
| 버튼 | `KakaoLoginButton` | 카카오 로그인 |
| 상단 바 | `HilitTopBar` | 슬롯형 공용 뼈대 |
| 상단 바 | `HilitTextTopBar`, `HilitLogoTopBar`, `HilitIconTopBar` | 텍스트·로고·아이콘형 |
| Modal | `HilitModalScaffold`, `HilitModal` | Modal 뼈대·Figma preset |
| Modal | `HilitGlobalModal`, `HilitBookIllustration` | 전역 알림·책 일러스트 |
| 입력 | `HilitAsyncTextField`, `HilitBottomOutlinedTextField`, `HilitJDTextField` | 비동기·밑줄·장문 입력 |
| 선택 | `HilitChip`, `HilitToggle`, `TermBox` | 칩·토글·약관 동의 |
| 선택 | `HilitTab`, `HilitTabRow`, `HilitWheelPicker` | 탭·휠 선택 |
| 파일 | `FileUploadGuide`, `PdfUpload` | 업로드 안내·PDF 상태 |
| 상태 | `HilitLoadingIndicator`, `HilitProgressBar`, `HilitSubText` | 로딩·진행·보조 상태 |
| 표시 | `BubbleField`, `HilitTag` | 말풍선·태그 |
| 표시 | `HilitText`, `HilitIcon` | 강조 텍스트·공용 아이콘 |
| Sheet | `HilitBottomSheet` | 공용 Modal Bottom Sheet |

공용 Composable을 추가하면 이 표도 같은 변경에서 갱신한다.

## 3. 제품 Theme 계약

`designsystem`은 다음 타입과 진입점을 제공한다.

- `HilitColors`: Figma 컬러 이름과 값을 보존한 immutable token set
- `HilitTypography`: Pretendard 기반 text style set
- `HilitTheme.colors`: `CompositionLocal` 기반 color token 접근점
- `HilitTheme.typography`: `CompositionLocal` 기반 typography token 접근점
- `HilitTheme { ... }`: 제품 token과 고정 light Material 환경을 제공하는 Theme Composable

제품 token은 Material 3 `ColorScheme` 또는 `Typography`의 semantic slot으로 재해석하지 않는다.
`HilitTheme` 내부 Material Theme는 기본 light `ColorScheme`, 기본 `Typography`, 기본 `Shapes`만
제공하고 제품 token과 분리한다.

다크 제품 테마, Android 동적 색상과 제품 token의 임의 semantic color mapping은 사용하지
않는다.

Figma component specification이 어떤 제품 token을 사용할지 지정하지 않으면 외관만 보고
임의로 선택하지 않는다. 명세가 불완전한 상태에서는 구현을 중단하고 사용자 확인을 요청한다.

호출자는 Figma component specification이 지정한 token을 `HilitTheme.colors`와
`HilitTheme.typography`에서 명시적으로 선택한다. Material component가 제품 token을 자동으로
상속한다고 가정하지 않는다.

Pretendard 원본은 `core:resources`가 소유한다. `designsystem`은 Compose Resources로 Regular,
Medium, SemiBold와 Bold를 하나의 `FontFamily`로 구성한다. Figma의 text size와 명시적 line
height는 `sp`, 비율 기반 letter spacing은 `em`으로 표현한다.

앱 Root, Preview와 Catalog Story content는 `HilitTheme`을 사용한다. Catalog shell은 Catalog
전용 Theme과 리소스를 유지한다.

## 4. 공용 리소스 소유권

- 앱, Feature와 Design System이 공유하는 font, drawable과 string 원본은 `core:resources`가
  소유한다.
- `designsystem`은 공개된 Compose Resources accessor를 통해 리소스를 소비한다.
- 공용 리소스 원본을 `designsystem`이나 `catalog`에 복제하지 않는다.
- Catalog shell 전용 font, favicon과 Web entry resource는 `catalog`가 소유한다.
- `catalog`는 `core:resources`에 직접 의존하지 않는다.

## 5. 공용 아이콘 추가

공용 아이콘은 리소스, API와 Catalog 검수 경로를 같은 변경 단위에서 완성한다.

1. 원본을 raster image가 아닌 SVG vector로 준비한다.
2. `core/resources/src/commonMain/composeResources/drawable/`에 SVG를 추가한다.
3. `HilitIconAsset`에 generated `Res.drawable` accessor와 리소스 이름을 사용하는 항목을 추가한다.
4. 앱과 Feature는 `HilitIconAsset`과 `HilitIcon`을 사용한다.
5. Catalog의 전체 아이콘 Foundation Story에서 새 항목이 표시되는지 확인한다.
6. Icon Story의 Registry 등록을 유지한다.

현재 Icon Story가 `HilitIconAsset.entries`를 순회하면 enum 항목이 자동으로 포함된다. Story의
목록 방식이 변경되면 새 아이콘을 명시적으로 등록한다. Catalog는 `Res` accessor를 직접 쓰지
않고 `designsystem`의 icon API로 렌더링한다.

## 6. Story 동반 규칙

외부에서 직접 사용하는 `public` 재사용 Composable을 추가할 때 같은 변경 단위에서 Catalog
Story를 최소 하나 작성하고 Registry에 등록한다.

- 공개 매개변수, 시각 상태 또는 상호작용이 변경되면 기존 Story 초기값과 adapter를 갱신한다.
- `private` 또는 `internal` 구현 세부사항은 public component Story에서 변경이 관찰되면 별도
  Story를 중복 작성하지 않는다.
- 다른 Gradle 모듈인 Catalog는 `internal` Composable에 직접 접근하지 않는다.
- Story는 합성 데이터만 사용하고 실제 사용자 데이터나 제품 runtime dependency를 포함하지
  않는다.
- 제품 color 또는 typography token이 바뀌면 해당 Foundation Story를 함께 갱신한다.

Story 작성과 Controls 계약은 [`catalog.md`](catalog.md)를 따른다.
