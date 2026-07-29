# 공통 컴포넌트 구현 계획

## 1. 목표와 적용 기준

- 대상: 토글 버튼, 선택 버튼, 미니 버튼, 탭, 로딩 인디케이터
- 구현 기준: Kotlin + Compose Multiplatform, `:designsystem`의 `commonMain`
- 상태 소유권: 선택 여부와 활성 여부 등 호출자가 복원해야 하는 상태는 컴포넌트 밖으로 끌어올리고, 컴포넌트는 값과 콜백만 받는다.
- 테마: Figma에 명시된 색상과 글꼴을 `HilitTheme.colors`, `HilitTheme.typography`에 직접 대응한다. 새 제품 토큰이나 Material 의미 색상은 만들지 않는다.
- 플랫폼 경계: Android Framework, Lifecycle, Navigation, Hilt, Android 전용 리소스 API를 사용하지 않는다.
- 검수 수단: 각 공개 컴포넌트 파일에 `@Preview`를 두고, `:catalog`에 기본 Story와 주요 상태 비교 Story를 등록한다.
- 파일 범위: 컴포넌트와 Preview는 `:designsystem`, Story는 `:catalog`, 로딩 SVG 원본은 `:core:resources`가 소유한다.

### Figma 확인 결과

| 컴포넌트 | Figma 노드 | 확인한 명세 |
|---|---|---|
| 토글 버튼 | [`2044:4999`](https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=2044-4999&m=dev) | `on`/`off`, 전체 50×28, 내부 여백 4, 손잡이 20×20. 배경 Gray 900, on 손잡이 Green 500, off 손잡이 Gray 50. 상태 변경 시 손잡이는 200ms 동안 0↔22dp 이동 |
| 선택 버튼 | [`2227:4501`](https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=2227-4501&m=dev) | 공개 이름 `HilitOptionalButton`. Figma 이름 `button-optional`, 예시 너비 334·높이 42, 12 여백, 8 간격, 16×16 plus 아이콘, Body 6 본문, Gray 100의 1.2 dashed 외곽선과 4/4 dash·gap, Gray/Small `선택` 태그 |
| 미니 버튼 | [`3356:10226`](https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=3356-10226&m=dev) | `light`/`dark`, 예시 크기 64×34, 8 여백과 간격, 16×16 video 아이콘, Body 5. light는 Gray 100/Black 800, dark는 Gray 900/White |
| 탭 | [`2044:4765`](https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=2044-4765&m=dev) | `default`/`disabled`/`selected`, 예시 크기 69×38, 가로 14·세로 8 여백, Body 2. 선택 시 Black 800의 1.5 하단선, 비활성 글자는 Gray 500 |
| 로딩 인디케이터 | [`2854:11086`](https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=2854-11086&m=dev) | 74×74 프레임 안의 72×73 그래픽, Black 800/Green 500. 시계 방향, 1회전 1500ms, 멈추지 않는 주기적 속도 변화 적용 |

## 2. 작업 순서

1. 공통 API 원칙을 먼저 맞춘다.
   - 토글·탭은 상태를 끌어올려 호출자가 제어하는 API로 정의한다.
   - `HilitOptionalButton`과 미니 버튼은 클릭 콜백과 `content: @Composable () -> Unit`을 받아 내부 콘텐츠의 구성과 문구를 호출자가 결정하게 한다.
   - 두 버튼은 Figma의 내부 간격, 기본 글자 스타일과 내용 색상을 제공하고, Preview와 Story의 대표 콘텐츠에서 기존 `HilitIconAsset.Add`/`Video`, `HilitIcon`, `HilitTag`를 재사용한다.
   - 토글·선택 버튼·미니 버튼에는 시안에 없는 disabled 상태를 추가하지 않는다. 탭의 disabled 상태만 구현한다.
   - 로딩 인디케이터는 진행률이 정해지지 않은 로딩이라는 접근성 정보와 nullable `contentDescription`을 제공한다.
2. `:designsystem`에 토글 버튼을 구현한다.
   - 50×28 컨테이너와 20×20 손잡이, 두 상태의 정렬·색상, switch 접근성 역할을 구현한다. 상태가 바뀌면 손잡이를 200ms 동안 0↔22dp로 이동시킨다.
   - Preview에서 on/off 상태를 함께 비교한다.
   - 순수 스타일 결정 함수를 대상으로 상태별 색상·정렬 테스트를 추가한다.
3. `:designsystem`에 `HilitOptionalButton`을 구현한다.
   - 최소 높이 42dp와 12dp 내부 여백을 보장하되 너비는 고정하지 않고 호출자의 `Modifier`에 맡긴다.
   - Gray 100의 1.2dp dashed 외곽선과 4dp dash/4dp gap, 8dp 콘텐츠 간격을 적용한다.
   - 버튼 역할, 클릭 콜백과 `content: @Composable () -> Unit`을 제공하고 Preview에서 Add 아이콘·본문·Gray/Small `HilitTag`로 Figma 대표 구성을 만든다.
   - Preview에서 기본/긴 문구/호출자 지정 너비 동작을 확인한다.
   - 색상·타이포그래피·외곽선 정책을 순수 테스트로 검증한다.
4. `:designsystem`에 미니 버튼을 구현한다.
   - light/dark enum, 클릭 콜백과 `content: @Composable () -> Unit`을 공개 API로 둔다.
   - 8dp 여백과 콘텐츠 간격, Body 5 글자 스타일과 상태별 내용 색상을 제공한다.
   - Preview와 Story에서 Video 아이콘·본문 대표 구성을 만들고 두 색상 및 호출자 구성 콘텐츠를 비교한다.
5. `:designsystem`에 단일 탭 항목과 탭 행을 구현한다.
   - `selected`, `enabled`, `onClick`을 받아 default/disabled/selected를 표현한다.
   - 선택 하단선은 레이아웃 높이가 상태에 따라 흔들리지 않도록 동일 영역을 유지한다.
   - `HilitTabRow`는 `HilitTabItem` 목록, 선택 index와 선택 콜백을 받아 선택 상태를 호출자에게 끌어올린다.
   - 항목 수는 2~5개로 제한하고 범위를 벗어나면 `require`로 즉시 실패한다. 선택 index도 목록 범위 안이고 활성 항목을 가리키도록 검증한다.
   - 행 전체 하단 구분선과 가로 스크롤은 두지 않는다.
   - 3개일 때의 26dp 간격 두 곳, 즉 총 52dp 간격을 기준으로 `gap = 52dp / (항목 수 - 1)`을 적용한다. 간격을 제외한 행의 나머지 너비는 각 탭이 같은 비율로 나눈다.
   - Preview와 테스트에서 단일 탭의 세 상태, 2~5개 탭 행의 간격 계산·동일 너비·선택 변경과 잘못된 입력 차단을 확인한다.
6. `:designsystem`에 로딩 인디케이터를 구현한다.
   - Figma 원본 그래픽에 시계 방향, 1회전 1500ms의 무한 회전을 적용한다. 시작과 끝의 속도를 연결하고 최저 속도를 0보다 크게 유지해, 느림과 빠름을 반복하면서도 멈추지 않게 한다.
   - Preview에서는 실제 크기와 대비 배경을 확인한다.
   - 순수 애니메이션 명세와 치수/색상 매핑을 테스트한다.
7. `:catalog`에 컴포넌트별 Story와 Controls 어댑터를 추가하고 `CatalogStories`에 등록한다.
   - 각 그룹은 `id = "default"` Story를 포함한다.
   - 토글·탭은 Story 내부 상태로 상호작용을 확인하고, 별도 Story에서 전체 시각 상태를 한 화면에 비교한다.
   - `HilitOptionalButton`과 미니 버튼은 어댑터가 문자열·색상·아이콘/태그 표시 여부를 Controls로 받아 대표 `content`를 조합한다. 임의 Composable 자체는 Controls 대상으로 삼지 않는다.
   - 탭 Story에 단일 탭의 세 상태, 2/3/4/5개 탭 행의 간격 비교와 선택 상호작용을 포함한다.
   - 로딩 인디케이터는 기본 크기와 크기 비교 Story를 제공한다.
   - Story 본문은 `HilitTheme` 안에서 렌더링하고 합성 문구만 사용한다.
8. 포매팅을 가장 먼저 적용한 다음 Wasm을 제외한 테스트와 정적 검증을 수행하고 이 문서의 `정적 검증 결과`를 실제 결과로 갱신한다. Wasm 카탈로그 컴파일과 배포 산출물 빌드는 사용자가 직접 수행한다.

## 3. 추가·수정 예정 파일

아래 경로는 현재 저장소 패키지와 Story 구성 방식을 따른다.

### `:designsystem`

| 구분 | 파일 | 예상 내용 |
|---|---|---|
| 추가 | `designsystem/src/commonMain/kotlin/com/dminus14/designsystem/component/toggle/HilitToggle.kt` | 호출자가 상태를 제어하는 토글 API, on/off 렌더링, 200ms 손잡이 이동, switch 접근성 역할, 상태별 스타일 결정, `@Preview` |
| 추가 | `designsystem/src/commonTest/kotlin/com/dminus14/designsystem/component/toggle/HilitToggleTest.kt` | on/off 색상, 0/22dp 손잡이 위치와 200ms 이동 명세 테스트 |
| 추가 | `designsystem/src/commonMain/kotlin/com/dminus14/designsystem/component/button/HilitOptionalButton.kt` | 최소 높이 42dp, 12dp 여백, 8dp 간격, 1.2dp dashed 외곽선과 4/4 dash·gap, 클릭 처리, 범용 `content` 슬롯, 대표 구성 `@Preview` |
| 추가 | `designsystem/src/commonTest/kotlin/com/dminus14/designsystem/component/button/HilitOptionalButtonTest.kt` | 색상·타이포그래피·최소 높이·외곽선 스타일 정책 테스트 |
| 추가 | `designsystem/src/commonMain/kotlin/com/dminus14/designsystem/component/button/HilitMiniButton.kt` | light/dark 색상 타입, 클릭 처리, 범용 `content` 슬롯, 상태별 기본 글자 스타일·내용 색상, 대표 구성 `@Preview` |
| 추가 | `designsystem/src/commonTest/kotlin/com/dminus14/designsystem/component/button/HilitMiniButtonTest.kt` | 색상별 배경·내용 색·타이포그래피 정책 테스트 |
| 추가 | `designsystem/src/commonMain/kotlin/com/dminus14/designsystem/component/tab/HilitTab.kt` | default/disabled/selected 표현, 상태를 끌어올린 클릭 API, 하단선, `@Preview` |
| 추가 | `designsystem/src/commonTest/kotlin/com/dminus14/designsystem/component/tab/HilitTabTest.kt` | 세 상태의 글자색·하단선·활성 정책 테스트 |
| 추가 | `designsystem/src/commonMain/kotlin/com/dminus14/designsystem/component/tab/HilitTabRow.kt` | `HilitTabItem`과 상태를 끌어올린 행 API, 2~5개 입력 검증, 동일 너비 배치, `52dp / (항목 수 - 1)` 간격, 하단선·스크롤 없는 탭 행, `@Preview` |
| 추가 | `designsystem/src/commonTest/kotlin/com/dminus14/designsystem/component/tab/HilitTabRowTest.kt` | 2~5개별 52/26/약 17.33/13dp 간격과 입력 범위·선택 index·활성 선택 항목 검증 테스트 |
| 추가 | `designsystem/src/commonMain/kotlin/com/dminus14/designsystem/component/loading/HilitLoadingIndicator.kt` | 74×74 기본 인디케이터, 원본 그래픽, 시계 방향 1500ms 가변 속도 무한 회전, 로딩 접근성 정보, `@Preview` |
| 추가 | `designsystem/src/commonTest/kotlin/com/dminus14/designsystem/component/loading/HilitLoadingIndicatorTest.kt` | 치수·토큰·1500ms 회전·방향·무정지 가변 속도 명세의 순수 정책 테스트 |

기존 `Color.kt`, `Typography.kt`, `HilitIcon.kt`, `HilitTag.kt`, Gradle 파일은 현재 확인한 명세만으로 수정할 필요가 없다. 필요한 색상·글꼴 토큰과 Add/Video 아이콘이 이미 존재한다.

### `:catalog` — Story 소유 모듈

| 구분 | 파일 | 예상 내용 |
|---|---|---|
| 추가 | `catalog/src/wasmJsMain/kotlin/stories/components/designsystem/hilittoggle/HilitToggleCatalogAdapter.kt` | checked 값을 Controls에 노출하고 Story 내부에서 상호작용 상태를 갱신 |
| 추가 | `catalog/src/wasmJsMain/kotlin/stories/components/designsystem/hilittoggle/HilitToggleStories.kt` | 기본 상호작용 및 on/off 비교 Story |
| 추가 | `catalog/src/wasmJsMain/kotlin/stories/components/designsystem/hilitoptionalbutton/HilitOptionalButtonCatalogAdapter.kt` | 본문·태그 문구와 아이콘/태그 표시 여부를 받아 대표 `content`를 조합 |
| 추가 | `catalog/src/wasmJsMain/kotlin/stories/components/designsystem/hilitoptionalbutton/HilitOptionalButtonStories.kt` | 기본, 긴 문구, 호출자 지정 너비 비교 Story |
| 추가 | `catalog/src/wasmJsMain/kotlin/stories/components/designsystem/hilitminibutton/HilitMiniButtonCatalogAdapter.kt` | 문구, light/dark, 아이콘 표시 여부를 받아 대표 `content`를 조합 |
| 추가 | `catalog/src/wasmJsMain/kotlin/stories/components/designsystem/hilitminibutton/HilitMiniButtonStories.kt` | 기본 및 색상/아이콘 조합 Story |
| 추가 | `catalog/src/wasmJsMain/kotlin/stories/components/designsystem/hilittab/HilitTabCatalogAdapter.kt` | selected/enabled/text를 Controls에 노출하고 클릭 상호작용 제공 |
| 추가 | `catalog/src/wasmJsMain/kotlin/stories/components/designsystem/hilittab/HilitTabStories.kt` | 단일 탭 기본 상호작용과 세 상태, 2~5개 탭 행의 간격 비교 및 선택 상호작용 Story |
| 추가 | `catalog/src/wasmJsMain/kotlin/stories/components/designsystem/hilitloadingindicator/HilitLoadingIndicatorCatalogAdapter.kt` | 크기와 접근성 문구 등 조작 가능한 값 연결 |
| 추가 | `catalog/src/wasmJsMain/kotlin/stories/components/designsystem/hilitloadingindicator/HilitLoadingIndicatorStories.kt` | 기본 및 크기 비교 Story |
| 수정 | `catalog/src/wasmJsMain/kotlin/stories/CatalogStories.kt` | 위 5개 StoryGroup을 수동 Registry에 등록 |

Controls 어댑터를 함께 추가했으므로 사용자가 `:catalog:compileKotlinWasmJs`를 실행해 생성되는 `Args`/`Controls` 선언을 확인한다. 제품 색상·타이포그래피 토큰 자체는 바꾸지 않았으므로 Color/Typography Foundation Story는 수정하지 않는다.

### `:core:resources` — 공용 리소스 원본 소유 모듈

| 구분 | 파일 | 예상 내용 |
|---|---|---|
| 추가 | `core/resources/src/commonMain/composeResources/drawable/loading_indicator.svg` | Figma가 제공한 Black 800/Green 500 로딩 그래픽을 SVG 원본으로 저장. `:designsystem` 또는 `:catalog`에 리소스를 복사하지 않음 |

Figma가 로딩 그래픽을 별도 이미지 자산으로 반환했으므로 픽셀 충실도를 유지하면서 공용 리소스 원본의 소유권을 지키기 위해 `:core:resources`에 둔다.

## 4. 정적 검증 결과

### 구현 결과

- Figma MCP: 5개 노드의 디자인 컨텍스트와 캡처 조회 완료.
- Figma 모션 MCP: 로딩 노드를 재귀 조회했으나 애니메이션 노드가 없음을 확인.
- 저장소 규칙: `docs/CONSTITUTION.md`, `docs/ARCHITECTURE.md`, 관련 `specs/`, `:designsystem`/`:catalog` 소스·테스트·빌드 파일 확인 완료.
- 포매팅: `./gradlew spotlessApply`의 첫 실행은 신규 상수 2개의 ktlint 이름 규칙 위반으로 실패했다. 해당 이름을 대문자 스네이크 표기로 수정한 뒤 재실행하여 성공했다.
- Wasm 제외 CI 검증: `./gradlew --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug` 성공.
- `:designsystem`의 Android main 소스와 신규 SVG 리소스는 위 CI에서 컴파일·패키징되었다.
- `:designsystem`의 `commonTest`는 Android host test가 활성화되지 않았다는 Gradle 경고가 있으며, Wasm 검증도 사용자 요청으로 제외하여 이번 작업에서는 컴파일·실행되지 않았다.
- 문서 검증: UTF-8 내용, 필수 항목, 구현 파일 경로, 검증 결과와 남은 모호성 표기를 확인했다.

### 구현 완료 후 실행 순서와 결과 기록란

1. 포매팅을 먼저 수행한다.

   ```powershell
   .\gradlew.bat spotlessApply
   ```

   - 결과: 성공 — ktlint 이름 규칙 위반 수정 후 재실행

2. 포매팅 완료 후 전체 CI 검증을 수행한다.

   ```powershell
   .\gradlew.bat --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug
   ```

   - 결과: 성공

3. Controls 생성 및 Web/WASM 카탈로그 산출물은 사용자가 직접 검증한다.

   ```powershell
   .\gradlew.bat :catalog:compileKotlinWasmJs
   .\gradlew.bat :catalog:wasmJsBrowserDistribution
   ```

   - 결과: 미실행 — 사용자 요청에 따라 Wasm 빌드 제외

검증 실패 시 실패 명령과 직접 원인을 기록하고, 범위 밖 수정이 필요하면 임의로 확대하지 않고 중단한다.

## 5. 확정된 결정

- 컴포넌트와 Preview는 `:designsystem`, Story는 `:catalog`, 로딩 SVG 원본은 `:core:resources`에 둔다.
- 선택 버튼의 공개 이름은 `HilitOptionalButton`으로 한다.
- `HilitOptionalButton`은 너비를 고정하지 않고 최소 높이 42dp와 내부 여백 12dp를 보장한다.
- `HilitOptionalButton`과 `HilitMiniButton`은 내부 콘텐츠를 `content: @Composable () -> Unit`으로 받아 아이콘, 문구, 태그 등을 호출자가 직접 구성한다.
- 탭은 단일 `HilitTab`과 여러 항목을 배치하는 `HilitTabRow`를 모두 구현하며, 선택 상태는 호출자가 소유한다.
- 토글 손잡이는 상태 변경 시 200ms 동안 0↔22dp를 이동하며 Gray 50과 Green 500 사이의 색상을 함께 보간한다.
- 로딩 인디케이터는 시계 방향으로 1500ms마다 한 바퀴 회전한다. 시작과 끝의 속도가 이어지고 최저 속도가 0보다 큰 주기 곡선으로 느림과 빠름을 반복한다.
- 토글, `HilitOptionalButton`, `HilitMiniButton`에는 시안에 없는 disabled 상태를 추가하지 않는다. `HilitTab`의 disabled 상태는 시안에 있으므로 포함한다.
- `HilitOptionalButton`의 외곽선은 Gray 100, 1.2dp, dashed, dash 4dp/gap 4dp로 구현한다.
- `HilitTabRow`는 2~5개 항목만 받으며, 간격을 제외한 가용 너비를 모든 탭이 동일하게 나눈다.
- 탭 행의 총 항목 간격은 3개일 때의 26dp 간격 두 곳을 기준으로 52dp를 유지한다. 항목별 간격은 `52dp / (항목 수 - 1)`로 계산하여 2개 52dp, 3개 26dp, 4개 약 17.33dp, 5개 13dp를 사용한다.
- 탭 행에는 전체 하단 구분선과 가로 스크롤을 두지 않는다.
- 항목 수, 선택 index 또는 선택 항목의 활성 상태가 계약에 맞지 않으면 `require`로 즉시 실패한다.

## 6. 남은 모호한 부분

추가로 식별된 모호한 부분 없음.
