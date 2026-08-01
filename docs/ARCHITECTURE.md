# Architecture Guide

이 문서는 Hilit (구 D-14) Android 프로젝트 아키텍처의 권위 있는 단일 진입점이다. 프로젝트는 Clean
Architecture, MVI, Jetpack Compose, Compose Multiplatform, Navigation 3, Hilt와 Gradle
Convention Plugin을 기준으로 구성한다.

세부 아키텍처 계약은 `docs/architecture/`의 주제별 문서에 나누어 관리한다. 작업자는 이
문서를 먼저 읽고 작업별 라우팅 표에 지정된 세부 문서를 추가로 읽어야 한다.

## 1. 문서 권위

문서는 다음 순서로 해석한다.

1. [`docs/CONSTITUTION.md`](CONSTITUTION.md)
2. 이 문서 `docs/ARCHITECTURE.md`
3. 이 문서가 편입한 `docs/architecture/*.md`
4. [`AGENTS.md`](../AGENTS.md)
5. [`README.md`](../README.md)

`docs/architecture/*.md`는 선택적 참고 자료가 아니라 이 문서가 편입한 상세 Architecture
계약이다. 상위 문서와 충돌하면 상위 문서가 우선한다. 세부 문서끼리 충돌하면 임의로
해석하지 말고 문서 오류로 보고한다.

## 2. 기술 개요

| 항목                  | 기준                                            |
| --------------------- | ----------------------------------------------- |
| Architecture          | Clean Architecture                              |
| UI Pattern            | MVI                                             |
| Android UI            | Jetpack Compose                                 |
| Shared UI             | Compose Multiplatform                           |
| Navigation            | Navigation 3                                    |
| Dependency Injection  | Hilt                                            |
| Build Configuration   | `build-logic` Convention Plugin과 `BuildConfig` |
| Design System Catalog | Compose Multiplatform Web/WASM                  |
| Base Package          | `com.dminus14.app`                              |

## 3. 핵심 불변조건

- 의존성은 outer layer에서 inner layer로만 향한다.
- `feature:*`는 `data`와 `app`에 의존하지 않는다.
- Feature `impl`은 다른 Feature `impl`에 의존하지 않는다.
- `domain`은 Android Framework, `data`, `feature:*`에 의존하지 않는다.
- `data`는 `feature:*`, `app`에 의존하지 않는다.
- `designsystem`과 `catalog`은 Android Framework에 의존하지 않는다.
- 앱 최상위 Navigation 3 조립과 전역 UI event rendering은 `app`이 소유한다.
- 별도 `:navigation` 또는 `:feature:navigator` 모듈을 만들지 않는다.
- Feature 화면은 Intent, State, Effect, ViewModel, Screen과 Content 책임을 분리한다.
- ViewModel은 Navigation과 Android UI를 직접 실행하지 않는다.
- Design System UI는 Compose Multiplatform-compatible해야 한다.
- 실제 사용자 데이터는 source, test, fixture, screenshot, Catalog, 문서와 예시에 사용하지 않는다.
- 민감 데이터 정책이 불완전하면 구현을 중단한다.
- 공유 build configuration은 Convention Plugin이 소유한다.

세부 금지사항과 예외 조건은 관련 주제 문서에서 확인한다. 이 요약만으로 세부 문서를 대체하지
않는다.

## 4. 세부 문서

| 문서                                                        | 목적                                               |
| ----------------------------------------------------------- | -------------------------------------------------- |
| [`module-system.md`](architecture/module-system.md)         | 모듈 책임, 허용·금지 의존성과 Feature 경계         |
| [`navigation.md`](architecture/navigation.md)               | Navigation 3 소유권, route, entry와 app root 조립  |
| [`feature-ui.md`](architecture/feature-ui.md)               | MVI Contract, ViewModel, Screen, Content와 Preview |
| [`design-system.md`](architecture/design-system.md)         | CMP 공용 UI, Theme, 상태와 리소스 소유권           |
| [`catalog.md`](architecture/catalog.md)                     | Web/WASM Story, Catalog Controls와 KSP 계약        |
| [`error-handling.md`](architecture/error-handling.md)       | 오류 분류, 레이어 책임과 Global Event              |
| [`global-modal.md`](architecture/global-modal.md)           | 앱 전역 Modal 요청, 결과, queue와 lifetime         |
| [`build-conventions.md`](architecture/build-conventions.md) | Gradle Convention Plugin과 capability 적용 계약    |
| [`project-structure.md`](architecture/project-structure.md) | 현재 Gradle module 상태와 승인된 목표 구조         |

## 5. 작업별 필수 읽기

모든 코드 변경 전에 `CONSTITUTION.md`와 이 문서를 읽는다. 다음 표에 해당하는 세부 문서를
추가로 읽는다. 하나의 작업이 여러 행에 해당하면 관련 문서를 모두 읽는다.

| 작업                              | 필수 세부 문서                                                     |
| --------------------------------- | ------------------------------------------------------------------ |
| 모듈 추가 또는 경계 변경          | `module-system.md`, `project-structure.md`                         |
| Gradle dependency 추가            | `module-system.md`, `build-conventions.md`                         |
| Convention Plugin 변경            | `module-system.md`, `build-conventions.md`                         |
| Navigation route 또는 entry 변경  | `module-system.md`, `navigation.md`, `feature-ui.md`               |
| Feature MVI 화면 구현             | `module-system.md`, `feature-ui.md`                                |
| ViewModel 오류 처리               | `feature-ui.md`, `error-handling.md`                               |
| 공용 Composable 추가 또는 변경    | `module-system.md`, `design-system.md`, `catalog.md`               |
| Theme, color 또는 typography 변경 | `design-system.md`, `catalog.md`                                   |
| 공용 icon 또는 CMP resource 변경  | `module-system.md`, `design-system.md`, `catalog.md`               |
| Catalog Story 변경                | `catalog.md`, 필요하면 `design-system.md`                          |
| Catalog Controls 또는 KSP 변경    | `catalog.md`, `build-conventions.md`                               |
| 공통 오류 정책 변경               | `module-system.md`, `feature-ui.md`, `error-handling.md`           |
| 전역 Modal 호출 또는 구현 변경    | `feature-ui.md`, `design-system.md`, `global-modal.md`             |
| Preview 구성 변경                 | `feature-ui.md`, `build-conventions.md`                            |
| 목표 프로젝트 구조 변경           | `module-system.md`, `build-conventions.md`, `project-structure.md` |
| 사용자 민감 데이터 관련 작업      | 관련 세부 문서와 `CONSTITUTION.md` 사용자 데이터 조항 재확인       |

표에 정확히 일치하지 않는 작업은 변경 대상과 소비자를 기준으로 가장 가까운 문서를 선택한다.
의존성, Navigation, Design System, Catalog, Error, Build처럼 교차하는 영역을 한 문서만 읽고
처리하지 않는다.

## 6. 현재 구조와 목표 구조

세부 문서는 승인된 목표 Architecture를 설명한다. 현재 repository가 staged implementation
상태여서 목표와 다르더라도 누락된 목표 모듈이나 구조를 곧바로 문서 오류로 판단하지 않는다.
현재 상태는 [`project-structure.md`](architecture/project-structure.md)에서 확인한다.

현재 구현을 이유로 Constitution 또는 Architecture boundary를 약화하지 않는다. 새 모듈이나
기능을 도입하는 시점에 관련 계약을 충족해야 한다.

## 7. 코드 예시

세부 문서의 코드는 승인된 목표 Architecture의 핵심 계약을 보여주는 개념적 예시다.
`package`, `import`, boilerplate와 관련 없는 구현은 생략하고 필요한 경우 `...`를 사용한다.
예시는 현재 bootstrap repository에서 컴파일 가능한 완성 source를 의미하지 않는다.
