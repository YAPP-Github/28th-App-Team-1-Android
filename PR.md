# 🚩 연관 이슈

closed #77

# 📝 작업 내용

## 배경

이 브랜치에는 비회원 지인 피드백을 위한 Retrofit API, 요청·응답 DTO, Guest 전용
OkHttpClient와 RemoteDataSource가 먼저 구현되어 있었습니다.

이번 작업에서는 Feature가 Retrofit이나 DTO를 직접 알지 않고도 진입 정보를 조회하고 피드백을
제출할 수 있도록 Domain Model, Repository, Mapper와 UseCase를 연결했습니다. 화면과 딥 링크는
이번 범위에 포함하지 않았습니다.

## 전체 호출 흐름

```text
후속 Guest Feedback ViewModel
    → EnterGuestFeedbackUseCase / SubmitGuestFeedbackUseCase
    → GuestFeedbackRepository
    → GuestFeedbackRepositoryImpl
    → GuestFeedbackRemoteDataSource
    → GuestFeedbackApi
```

서버 응답은 반대 방향으로 DTO에서 Domain Model로 변환됩니다. 이 구조로 Feature는 `data` 모듈과
Retrofit 타입에 의존하지 않습니다.

## 진입 결과 모델

공유 링크 진입 결과를 다음 두 형태로 구분했습니다.

- `Open`: 요청자 이름, 평가 항목, 영상 URL, 질문 경계처럼 작성에 필요한 데이터가 모두 있음
- `Unavailable`: `PRIVATE`, `EXPIRED`, `FULL`, `ALREADY_SUBMITTED` 중 하나의 작성 불가 사유

`OPEN` 외 게이트는 HTTP 실패가 아니라 서버가 정상적으로 판정한 상태이므로 예외로 바꾸지
않습니다. 후속 화면은 `Unavailable.reason`을 보고 안내 후 흐름을 종료할 수 있습니다.

기존 Guest Gson adapter가 `OPEN` 응답의 필수 값과 non-OPEN 응답의 null 계약을 검증하므로,
Repository에 같은 응답 validator를 다시 만들지 않았습니다.

## 제출 전 검증과 정규화

`SubmitGuestFeedbackUseCase`가 네트워크 요청 전에 다음 규칙을 한 번 보장합니다.

- 공유 token의 양끝 공백을 제거하고 빈 token 차단
- 서버가 지정한 평가 항목 `1..5`개를 누락·추가·중복 없이 모두 평가했는지 확인
- 평가 단계가 `1..4`인지 확인
- 별칭과 항목별 코멘트의 양끝 공백 제거
- 빈 별칭은 `익명의 지인`, 빈 코멘트는 빈 문자열로 변환
- 코멘트는 문자 종류와 관계없이 허용하고 Kotlin `String.length` 기준 최대 100으로 제한

검증을 별도 상태 객체나 validator 계층으로 분리하지 않고, 실제 비즈니스 규칙을 사용하는 제출
UseCase 안에 두었습니다. 제출 성공 응답의 ID와 시각은 제품에서 사용하지 않으므로 Domain
Model을 추가하지 않고 `Unit`을 반환합니다.

## 오류 전달

Guest API의 7개 비즈니스 오류 코드를 후속 화면이 구분해야 하는 네 의미로 변환합니다.

| 오류 의미 | 서버 코드 |
|---|---|
| 요청 처리 실패 | `FEEDBACK_SHARE_TOKEN_NOT_FOUND`, `INCOMPLETE_RATINGS`, `INVALID_RATING_LEVEL`, `MISSING_DEVICE_ID` |
| 공유 종료 | `FEEDBACK_SHARE_CLOSED` |
| 최대 인원 도달 | `FEEDBACK_CAPACITY_FULL` |
| 중복 제출 | `FEEDBACK_ALREADY_SUBMITTED` |

Network, HTTP 5xx와 알 수 없는 오류는 기존 공통 오류 정책을 사용합니다. Guest 응답 adapter의
파싱 실패도 알 수 없는 오류로 통일했습니다.

UI 문구와 Dialog 종류는 Domain/Data에서 결정하지 않습니다. 후속 Feature가 오류 타입을 기준으로
화면 정책을 적용합니다.

## 민감 데이터 처리

영상 URL, 질문 원문, 공유 token, 별칭과 코멘트는 실행 중 메모리에서만 전달합니다. 이번 구현은
Room, DataStore, 파일, 임시저장, 디스크 캐시, 애플리케이션 로그와 분석 전송을 추가하지
않았습니다. 기존 설치 ID 저장은 중복 제출 방지 목적으로만 유지됩니다.

## KDoc

후속 ViewModel 구현자가 내부 코드를 모두 읽지 않아도 호출 계약을 알 수 있도록 공개 Domain
Model, Repository, 오류와 UseCase의 실제 `invoke` 함수에 한국어 KDoc을 작성했습니다. KDoc에는
입력 정규화, 검증 조건, gate 결과, 오류 의미와 민감 데이터 비영속 원칙을 담았습니다.

## 검증

CRLF 문제를 먼저 정리하기 위해 포매팅 적용부터 다음 순서로 검증했습니다.

```text
.\gradlew.bat spotlessApply
.\gradlew.bat spotlessCheck
.\gradlew.bat :domain:test :data:testDebugUnitTest
.\gradlew.bat --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug
git diff --check
```

- Domain 단위 테스트: 12개 성공
- Data 단위 테스트: 56개 성공
- 전체 Spotless, Detekt, 단위 테스트, Android Lint와 assemble 성공
- Wasm 빌드는 이번 변경 대상이 아니므로 실행하지 않음

# 🏞️ 스크린샷 (선택)

UI 변경이 없어 해당하지 않습니다.

# 🗣️ 리뷰 요구사항 (선택)

다음 세 가지는 후속 UI 구조와 제품 동작에 영향을 주는 방향성이라 중점 확인을 부탁드립니다.

1. 서버의 `OPEN`을 완전한 작성 데이터로, 나머지 gate를 예외가 아닌 `Unavailable` 정상 결과로
   표현한 Domain 경계가 후속 화면 흐름에 적합한지 확인해 주세요.
2. 지정 항목 완전성·중복·평가 단계·문자열 정규화를 상태를 보관하지 않는 제출 UseCase 한 곳에서
   책임지는 구성이 Feature와 Domain의 책임 분리에 적합한지 확인해 주세요.
3. 7개 서버 코드를 네 Domain 오류 의미로 구분한 기준이 후속 Dialog와 흐름 종료 정책을 충분히
   표현하는지 확인해 주세요.
