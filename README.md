# 📆 Hilit

> Hilit은 이직 예정자 또는 취업 준비생의 면접 준비를 돕는 서비스예요.

이 저장소는 [기업형 IT 연합 동아리 YAPP](https://github.com/YAPP-Github) 28기의 서비스 중 하나인 **Hilit**의 Android 앱 코드
저장소예요.

Hilit은 면접까지 14일, 즉 **2주의 기간** 동안 실전처럼 연습하고, AI와 지인 피드백을 함께 보고서로 종합해 면접자의 개선점을 정리할 수 있게 도와요.

## 🔎 Features

### AI 모의 면접

스마트폰의 카메라를 통해 AI와 실시간으로 면접을 진행해볼 수 있어요.

### 투 트랙 피드백

사용자는 면접을 진행한 후 2가지 방법으로 피드백을 받을 수 있어요:

#### AI 피드백

직무 관련 질문에 대한 사용자의 면접 답변을 STT로 텍스트 변환하여, 내용에 관한 피드백을 제공해요.

#### 지인 피드백

면접 중 촬영한 면접 영상을 믿을 수 있는 지인 또는 직무 전문가에게 공유하여 표정, 시선, 말투, 자세, 전달력 등 사람이 판단하기 좋은 요소 및 직무 전문성에 대한 피드백을
받을 수 있어요.

### 피드백 보고서

위 과정에서 산출된 피드백 내용을 보고서로 제공해요.

## ☀️ Technical Highlights

- GitHub Actions 기반 CI 및 기타 유지보수 진행
- Clean Architecture 기반 레이어 분리
- MVI 기반 단방향 상태 관리
- Hilt를 활용한 의존성 주입
- Compose Multiplatform 기반 디자인 시스템
- Kotlin Multiplatform 기반 Storybook-like 웹 디자인 시스템 카탈로그
- Gradle Convention Plugin 기반 멀티 모듈 아키텍처 적용 및 빌드 설정 공통화

## 🛠️ Stack

| Area           | Stack                                              |
|----------------|----------------------------------------------------|
| Language       | Kotlin                                             |
| UI             | Jetpack Compose, Compose Multiplatform             |
| Architecture   | Clean Architecture, MVI, Multi-module Architecture |
| DI             | Hilt                                               |
| Build          | Gradle Convention Plugin                           |
| Network        | Retrofit2, okhttp3                                 |
| Design Catalog | Compose Multiplatform, Kotlin Multiplatform (WASM) |

## 🙋 Contributors

| <img src="https://avatars.githubusercontent.com/u/77564014" width="120"  alt="@i-meant-to-be"> | <img src="https://avatars.githubusercontent.com/u/32947391" width="120"  alt="@lyh5427"> |
|------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| [@i-meant-to-be](https://github.com/i-meant-to-be)                                             | [@lyh5427](https://github.com/lyh5427)                                                   |

## 📄 More About

- [에이전트 규칙](./AGENTS.md)
- [프로젝트 헌법](./docs/CONSTITUTION.md)
- [아키텍처 개요](./docs/ARCHITECTURE.md)
