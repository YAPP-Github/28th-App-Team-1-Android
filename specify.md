너는 이 브랜치에서 지인 피드백 기능(GuestFeedback)의 UI 및 뷰 모델을 구현할거야. 현재 배경 정보는 아래와 같으니, 아래 배경 정보를 전부 다 읽고 나서 작업 계획을 저장소 루트에 plan-phase-3.md로 작성해줘.

시작에 앞서, 가능한 가장 단순한 구현을 지향하며 추상성 및 복잡한 아키텍처는 반드시 필요하거나 채택으로 인한 이득이 압도적으로 큰 경우에만 반영하길 바라. 오캄의 면도날 철학이야.

# 작업 순서 및 완료 상황
- [완료] DTO 및 API 호출 구현
- [완료] 모델, 저장소, 매퍼 (DTO-모델) 구현
- [목표] UI 및 뷰 모델 구현

# 작업 목표
- UI 구현
- 뷰 모델 구현
- UI에 뷰 모델 연결
- 네비게이션 연결

# 구현 예정 페이지
feature/feedback/api/src/main/kotlin/com/dminus14/app/feature/feedback/api/FeedbackRoute.kt 참고)
- FeedbackOnboarding | 지인 온보딩 화면
- Feedback | 지인 피드백 화면
- FeedbackResult | 지인 피드백 결과 화면

# 세부 시안
## 지인 온보딩 화면
- 메인 @https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=1855-8498&m=dev
- 입력 바텀 시트 @https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=2094-7566&m=dev

## 지인 피드백 화면
- 메인 @https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=1855-9821&m=dev
- 최초 진입 시 화면 @https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=1855-8703&m=dev
- 하단 메뉴 오픈 시 @https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=2150-7278&m=dev

## 지인 피드백 결과 화면
- 메인 @https://www.figma.com/design/ZG7FUxWCvITmnvzZi7fpTS/YAPP---%EC%A0%9C%EC%9E%91%EC%9A%A9?node-id=2101-8781&m=dev

# 배경 문서
- plan-phase-1 | DTO 및 API 구현 계획 문서
- plan-phase-2 | 도메인 모델, 매퍼 및 저장소 구현 계획 문서
- prd.md 이 기능 PRD

# 예상 작업 순서
- Figma 시안대로 `:feature:feedback:impl` 내에 3개 페이지 UI 구현
- 페이지 동작에 따른 UiState, Intent 정의 및 뷰 모델 구현 (이 때 `MviViewModel` 및 `MviIntent` 상속하여 구현)
- 페이지 및 뷰 모델 연결
- Nav 3 컨벤션에 따라 `:feature:feedback:api` 내에 네비게이션 관련 필요 코드 작성

마지막으로 다시 한 번 오캄의 면도날 철학을 꼭 고려해주길 바라. 현재 단계에서는 수정 금지.
