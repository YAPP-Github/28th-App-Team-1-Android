# Guest Feedback 저장소·매퍼·유스케이스 구현 계획

## 1. 계획 요약

이미 구현된 Guest 전용 Retrofit API, DTO, 응답 adapter, OkHttpClient와
`GuestFeedbackRemoteDataSource` 위에 비회원 지인 피드백의 Domain/Data 경계를 완성한다.
대상 API는 다음 두 개로 한정한다.

- `GET /api/v1/feedback/guest/{token}`: 공유 링크 진입과 게이트 판정
- `POST /api/v1/feedback/guest/{token}/submissions`: 지인 피드백 제출

가장 단순한 구조를 유지하기 위해 다음 책임만 추가한다.

1. `domain`에 화면과 Retrofit에 독립적인 진입 결과, 평가 항목, 질문 경계, 제출 입력 모델을
   정의한다.
2. `domain`에 `GuestFeedbackRepository` 계약을 정의한다.
3. `data`의 단일 매퍼 파일에서 진입 DTO를 Domain Model로, Domain 제출 모델을 요청 DTO로
   변환한다.
4. `GuestFeedbackRepositoryImpl`이 RemoteDataSource 호출, DTO 변환과 기존 공통 오류 변환을
   조립한다.
5. 진입 유스케이스는 빈 token을 호출 전에 차단하고, 제출 유스케이스는 지정 항목 완전성과
   평가 범위를 검증한 뒤 문자열을 정규화한다.
6. 제출 성공 응답의 `submissionId`와 `submittedAt`은 제품에서 사용하지 않으므로 저장소
   경계에서는 버리고 `Unit`을 반환한다.

`OPEN` 진입 응답만 작성 가능한 데이터로 변환한다. `PRIVATE`, `EXPIRED`, `FULL`,
`ALREADY_SUBMITTED`는 오류로 던지지 않고 작성 불가 사유를 가진 정상 Domain 결과로 반환한다.
이렇게 하면 Feature가 게이트를 안정적으로 분기할 수 있고 DTO의 nullable 조합이 Domain 밖으로
노출되지 않는다. 기존 응답 adapter의 `OPEN` 데이터 non-null 계약은 변경하지 않는다.

별칭과 항목별 코멘트는 제출 직전에 Kotlin `Char.isWhitespace()` 기준 양끝 공백을 제거한다.
별칭이 `null`이거나 트리밍 후
비어 있으면 `"익명의 지인"`으로 바꿔 전송한다. 빈 코멘트는 `null`로 바꾸지 않고 빈 문자열
`""`을 전송한다. 항목별 코멘트는 종류와 관계없이 모든 문자를 허용하고, PRD의 현재 유효한
최대 100자를 Kotlin `String.length` 기준으로 Domain에서 검증한다. 화면 시안과 API에 없는
300자 전반 피드백은 구현하지 않는다.

새로 추가하거나 실질적으로 수정하는 공개 타입과 함수에는 한국어 KDoc을 작성한다. 특히 후속
UI와 ViewModel이 직접 사용하는 Domain Model, Repository와 UseCase에는 입력 정규화, 검증 조건,
게이트와 오류 의미, 반환 계약을 구현을 읽지 않아도 알 수 있게 기록한다.

민감한 영상 URL, 질문 원문, 별칭과 피드백은 흐름 진행 중 메모리에서만 사용한다. 새 로컬
저장소, 임시저장, 디스크 캐시, 로그, analytics 또는 crash report 경로를 추가하지 않는다.
기존 설치 ID 저장은 중복 제출 방지용 네트워크 기반에 포함된 별도 책임이므로 유지하되,
피드백 내용이나 공유 token을 함께 저장하지 않는다.

## 2. 구현 범위

### 포함

- Guest Feedback Domain Model과 Repository 계약
- Guest Feedback의 제품 의미를 구분하는 Domain Exception
- 진입 DTO → Domain Model 매핑
- Domain 제출 모델 → 평가 DTO 매핑
- Repository 구현과 Hilt binding
- 빈 token, 지정 항목 완전성, 중복 항목, 평가 범위와 코멘트 길이 검증
- 진입·제출 유스케이스
- 공개 계약과 핵심 변환·오류 처리 함수의 한국어 KDoc
- Domain/Data 단위 테스트

### 제외

- Feature MVI, 화면, ViewModel과 Compose UI
- 딥 링크 parsing, Manifest/App Links와 Navigation 연결
- `{SERVER_URL}/feedback/guest/{token}` 실제 링크 환경 설정
- 앱 실행 중 링크 재진입 및 앱 종료 처리
- `OPEN` 외 게이트와 오류 그룹의 Dialog 문구·버튼·종료 동작
- 사용자측 Feedback Share API
- 300자 전반 피드백
- 작성 중 임시저장과 재진입 복원
- API, DTO, 응답 adapter, OkHttpClient와 Retrofit 기반의 재설계
- Gradle dependency 버전, Architecture 문서와 보호 문서 변경
- Wasm/Catalog 변경과 빌드

제외된 UI 작업에서도 이번 Domain 결과를 그대로 사용할 수 있어야 한다. 네트워크·서버·알 수
없는 오류는 기존 `GlobalErrorHandler` 대상으로 남기고, Guest 비즈니스 오류와 작성 불가
게이트는 후속 Feature가 State/Effect 또는 `showGlobalDialog(...)`로 처리한다.

## 3. 수정·추가 예정 파일

관련성이 높은 작은 타입은 한 파일에 모으고, 독립적인 호출 책임인 Repository와 UseCase만
분리한다.

| 파일 | 구분 | 주요 내용 |
|---|---|---|
| `domain/build.gradle.kts` | 수정 | Domain 유스케이스 단위 테스트를 위해 Version Catalog에 이미 있는 JUnit을 `testImplementation`으로 추가한다. 제품 dependency나 버전은 추가하지 않는다. |
| `domain/src/main/kotlin/com/dminus14/app/domain/model/GuestFeedback.kt` | 추가 | 작성 가능 진입 결과, 작성 불가 사유, 평가 축, 표시명, 질문 경계, 제출 입력과 항목별 평가 모델을 한 파일에 정의한다. Retrofit DTO와 Android 타입을 사용하지 않으며 각 공개 모델의 불변조건과 소비 방법을 KDoc으로 설명한다. |
| `domain/src/main/kotlin/com/dminus14/app/domain/repository/GuestFeedbackRepository.kt` | 추가 | token으로 진입 결과를 조회하고 Domain 제출 모델을 전송하는 계약을 정의한다. 제출 성공은 `Unit`을 반환하며 저장하지 않는 데이터와 예외 전달 계약을 KDoc으로 명시한다. |
| `domain/src/main/kotlin/com/dminus14/app/domain/exception/GuestFeedbackException.kt` | 추가 | 서버 오류를 요청 처리, 공유 만료, 최대 인원 초과, 중복 피드백의 네 의미로 구분하고, 호출 전 입력 검증 실패를 별도 Domain 오류로 표현한다. 각 오류의 분기 의미는 KDoc으로 전달하되 UI 문구는 결정하지 않는다. |
| `domain/src/main/kotlin/com/dminus14/app/domain/usecase/EnterGuestFeedbackUseCase.kt` | 추가 | token을 트리밍하고 빈 token을 Repository 호출 전에 거부한 뒤 진입 결과를 `Result`로 반환한다. coroutine 취소는 다시 던지며 호출 전제와 gate 결과를 KDoc으로 설명한다. |
| `domain/src/main/kotlin/com/dminus14/app/domain/usecase/SubmitGuestFeedbackUseCase.kt` | 추가 | 진입 때 받은 지정 축과 작성한 평가를 비교하고, 별칭 기본값과 `String.length` 기준 코멘트 길이를 포함한 검증·정규화 후 Repository를 호출한다. 성공 결과는 `Result<Unit>`이며 후속 ViewModel이 알아야 할 모든 입력 규칙을 KDoc에 명시한다. |
| `data/src/main/kotlin/com/dminus14/app/data/remote/mapper/GuestFeedbackMapper.kt` | 추가 | 검증된 진입 DTO를 Domain의 작성 가능/작성 불가 결과로 변환하고, Domain 평가 축과 제출 평가를 wire enum/DTO로 변환한다. 문자열 검증이나 UI 정책은 넣지 않는다. |
| `data/src/main/kotlin/com/dminus14/app/data/remote/mapper/ApiErrorCode.kt` | 수정 | Guest API가 확정한 7개 비즈니스 오류 코드를 추가한다. 기존 공통 오류 코드는 변경하지 않는다. |
| `data/src/main/kotlin/com/dminus14/app/data/repository/GuestFeedbackRepositoryImpl.kt` | 추가 | RemoteDataSource 호출, DTO↔Domain 매핑, Guest 비즈니스 오류 그룹화와 공통 Network/Server/Unknown 변환을 담당한다. 민감 데이터를 저장하거나 기록하지 않는다. |
| `data/src/main/kotlin/com/dminus14/app/data/di/remote/feedback/GuestFeedbackRepositoryModule.kt` | 추가 | `GuestFeedbackRepositoryImpl`을 Domain의 `GuestFeedbackRepository`에 `@Binds`, `@Singleton`으로 연결한다. 기존 RemoteDataSource module은 유지한다. |
| `domain/src/test/kotlin/com/dminus14/app/domain/usecase/GuestFeedbackUseCaseTest.kt` | 추가 | 두 유스케이스의 token 차단, 문자열 정규화, 지정 항목 완전성·중복·범위·길이 검증, 모든 문자 종류 허용, 성공 위임과 취소 전파를 한 테스트 파일에서 검증한다. |
| `data/src/test/kotlin/com/dminus14/app/data/remote/mapper/GuestFeedbackMapperTest.kt` | 추가 | 모든 gate/axis, `OPEN` 데이터, 질문 경계와 제출 평가의 양방향 계층 변환을 검증한다. |
| `data/src/test/kotlin/com/dminus14/app/data/repository/GuestFeedbackRepositoryImplTest.kt` | 추가 | RemoteDataSource 위임, Domain 반환, 제출 응답 폐기, 네 비즈니스 오류 그룹과 공통 오류 변환을 검증한다. |

기존 `GuestFeedbackApi`, DTO, 응답 adapter, RemoteDataSource, Guest OkHttpClient/Retrofit과 그
테스트는 현재 확정 계약을 이미 충족하므로 원칙적으로 수정하지 않는다. 구현 중 새 계층 연결에
필요한 최소 signature 조정이 발견되면 기존 테스트의 의도를 보존하는 범위에서만 수정하고
완료 보고에 이유를 명시한다.

## 4. Domain 모델과 호출 계약

### 4.1 진입 결과

진입 결과는 nullable 필드를 그대로 복사한 단일 data class 대신 두 상태로 표현한다.

- 작성 가능: 요청자 표시명, 지정 평가 항목, 영상 URL, 질문 경계와 `submissionOpen`
- 작성 불가: `PRIVATE`, `EXPIRED`, `FULL`, `ALREADY_SUBMITTED` 중 하나의 사유

`OPEN`은 작성 가능 타입 자체로 표현하므로 `OPEN`인데 필수 데이터가 없는 Domain 상태를 만들지
않는다. 기존 Gson adapter가 응답 계약을 먼저 검증하므로 Repository에서 동일한 null 검증을
반복하지 않는다.

### 4.2 제출 입력

Domain 제출 모델은 다음 정보만 가진다.

- nullable 선택 입력 별칭. 미입력 또는 공백뿐인 값은 제출 유스케이스가 `"익명의 지인"`으로
  정규화한다.
- 서버가 지정한 축마다 하나씩 존재하는 평가
- 각 평가의 축, `1..4` 단계와 선택 코멘트

전반 피드백, 제출 ID와 제출 시각은 모델에 추가하지 않는다. Domain의 평가 축 enum은 서버 wire
문자열을 알지 못하며, data 매퍼가 DTO enum과 명시적으로 일대일 변환한다.

### 4.3 Repository

- `enter(token)`은 Domain 진입 결과를 반환한다.
- `submit(token, submission)`은 성공 시 `Unit`을 반환한다.
- Repository는 로컬 저장, 재시도, 임시저장이나 화면 상태를 소유하지 않는다.
- Repository 함수 자체는 예외를 던질 수 있고, UseCase가 기존 프로젝트 방식처럼
  cancellation-safe `Result`로 감싼다.

## 5. 유스케이스와 검증 규칙

### 5.1 진입

1. token 양끝에서 `Char.isWhitespace()`가 `true`인 문자를 제거한다.
2. 결과가 비어 있으면 입력 검증 오류를 반환하고 Repository를 호출하지 않는다.
3. 유효한 token으로 Repository를 한 번 호출한다.
4. `OPEN` 외 게이트도 실패가 아닌 작성 불가 Domain 결과로 반환한다.

딥 링크 parser도 token 누락을 차단할 예정이지만, 이번 유스케이스 검증은 네트워크 경계에 빈
token이 도달하지 않게 하는 마지막 Domain 보호다. URL parsing과 Dialog 표시는 포함하지 않는다.

### 5.2 제출

제출 유스케이스는 진입 응답에서 받은 지정 평가 항목과 사용자가 작성한 제출 모델을 함께 받는다.
상태를 내부에 저장하지 않고 호출 인자로만 전달해 수명 관리 객체를 만들지 않는다.

Repository 호출 전에 다음을 순서대로 검증한다.

1. token을 트리밍하고 비어 있으면 거부한다.
2. 지정 항목 수가 `1..5`인지 확인한다.
3. 지정 항목과 작성 평가 양쪽에 중복 축이 없는지 확인한다.
4. 작성 평가 축의 집합이 지정 축의 집합과 정확히 같은지 확인한다.
5. 모든 평가 단계가 `1..4`인지 확인한다.
6. nullable 별칭과 각 코멘트의 양끝에서 `Char.isWhitespace()`가 `true`인 문자를 제거한다.
7. 별칭이 `null`이거나 트리밍 후 비어 있으면 `"익명의 지인"`으로 바꾼다.
8. 트리밍한 코멘트의 Kotlin `String.length`가 100을 초과하지 않는지 확인한다.
9. 문자 종류는 검증하거나 거부하지 않는다.
10. 빈 코멘트는 `null`이 아닌 `""`로 유지한다.
11. 정규화된 새 Domain 제출 모델로 Repository를 한 번 호출한다.

검증 실패 시 API를 호출하지 않는다. 어떤 필드에 오류가 있는지는 Domain 검증 오류로 보존하되
Dialog, Toast와 사용자 표시 문구는 Domain에서 결정하지 않는다. `Result`로 변환할 때 coroutine
취소 예외는 실패 값으로 삼지 않고 다시 던진다.

## 6. 매핑과 오류 처리

### 6.1 DTO 매핑

하나의 `GuestFeedbackMapper.kt`에 Guest 전용 변환 함수만 둔다.

- `OPEN` DTO → 작성 가능 Domain 결과
- 나머지 gate DTO → 작성 불가 Domain 결과
- axis DTO/code → Domain axis/code
- question boundary DTO → Domain question boundary
- Domain rating/code → `GuestFeedbackRatingDto`/wire enum

RemoteDataSource가 현재 request wrapper를 조립하므로 별도 request mapper class나 mapper DI를
만들지 않는다. Repository가 Domain 제출 모델의 별칭과 변환된 rating 목록을 RemoteDataSource에
전달한다.

### 6.2 서버 오류 그룹

Repository는 기존 `CommonApiErrorMapper`를 재사용하고 Guest 비즈니스 코드를 다음 네 Domain 오류
의미로 구분한다.

| Domain 오류 의미 | 서버 코드 | 후속 Feature의 확정 표시 문구 |
|---|---|---|
| 요청 처리 오류 | `FEEDBACK_SHARE_TOKEN_NOT_FOUND`, `INCOMPLETE_RATINGS`, `INVALID_RATING_LEVEL`, `MISSING_DEVICE_ID` | `서버 요청에 실패했습니다. 앱을 재실행하고 다시 시도해주세요.` |
| 공유 만료 오류 | `FEEDBACK_SHARE_CLOSED` | `피드백 가능한 기간이 지났습니다.` |
| 피드백 최대 인원 제한 오류 | `FEEDBACK_CAPACITY_FULL` | `최대 피드백 가능 인원을 초과하여 더 이상 피드백을 받을 수 없습니다.` |
| 중복 피드백 오류 | `FEEDBACK_ALREADY_SUBMITTED` | `이미 이 기기에서 피드백한 이력이 있습니다. 중복 피드백은 할 수 없습니다.` |

원본 서버 `code`와 cause는 진단 가능한 형태로 보존하지만 민감한 응답 payload를 메시지나 로그에
추가하지 않는다. Network, HTTP 5xx와 알 수 없는 오류는 기존 공통 예외 정책을 그대로 사용한다.
별도 Guest 오류 매퍼 객체를 만들지 않고 Repository 구현의 작은 private 변환 함수로 제한한다.
표의 표시 문구는 후속 Feature/UI 계약으로만 기록한다. Domain과 Data는 아키텍처 계약에 따라
Dialog 문구를 결정하지 않고 네 오류 타입만 제공한다.

작성 불가 gate는 HTTP 오류가 아니므로 위 예외 그룹으로 바꾸지 않는다. 후속 Feature는 정상
결과의 사유와 제출 중 발생한 오류 타입을 각각 분기할 수 있다.

### 6.3 KDoc 작성 원칙

KDoc은 구현을 그대로 반복하지 않고 다음 계층 소비자가 지켜야 할 계약을 전달한다.

- Domain Model: 작성 가능/불가 상태의 차이, nullable 여부, 평가 축·단계와 민감 데이터의
  비영속 원칙
- Repository: 입력과 반환 타입, 제출 성공이 `Unit`인 이유, 외부 오류가 Domain 오류로 전달되는
  계약
- 진입 UseCase: token 트리밍·빈 값 차단, non-OPEN이 정상 결과라는 점, `Result`와 취소 전파
- 제출 UseCase: 지정 항목 전체 평가, 중복 금지, `1..4` 범위, 별칭 기본값, 코멘트 트리밍과
  `String.length` 기준 최대 100자, 문자 종류를 제한하지 않는다는 점
- Guest 오류 타입: 네 오류 의미와 후속 Feature가 분기해야 하는 기준. Dialog 문구 자체는
  Domain KDoc에 넣지 않는다.
- Data 매퍼와 Repository 구현의 핵심 함수: DTO/Domain 변환 방향, 응답 adapter가 이미 보장한
  non-null 계약, 민감 값 비저장·비로깅과 오류 그룹 변환 책임

private helper처럼 이름과 코드만으로 목적이 분명한 부분에는 설명을 반복하는 주석을 추가하지
않는다. KDoc이 실제 검증과 어긋나지 않는지는 구현 리뷰와 테스트 이름을 함께 대조한다.

## 7. 테스트 계획

테스트 데이터는 실제 사용자 데이터가 아닌 `synthetic-token`, `합성 요청자`, 합성 질문과
`.invalid` URL만 사용한다. 모든 테스트 함수명은 기대 동작을 설명하는 한국어 문장으로 작성한다.

### Domain 유스케이스

- 공백을 제거한 token으로 진입 Repository를 한 번 호출한다.
- 빈 token이면 진입/제출 Repository를 호출하지 않는다.
- `OPEN`과 네 가지 작성 불가 결과를 손실 없이 반환한다.
- 지정 항목 `1..5`와 정확히 같은 평가 집합이면 제출한다.
- 지정 항목 누락·추가·중복 또는 평가 중복이면 제출하지 않는다.
- `1`과 `4`는 허용하고 범위 밖 단계는 제출하지 않는다.
- nullable·빈·공백뿐인 별칭을 `"익명의 지인"`으로 정규화한다.
- 스페이스·줄바꿈·탭을 포함해 `Char.isWhitespace()`가 인정하는 양끝 공백을 제거하고 빈
  코멘트는 빈 문자열로 유지한다.
- 트리밍 후 `String.length`가 100인 코멘트는 허용하고 101이면 거부한다.
- 여러 UTF-16 code unit을 사용하는 문자도 `String.length` 결과로 같은 경계를 적용한다.
- 길이 조건 안에서는 문자 종류와 관계없이 코멘트를 허용한다.
- Repository 오류는 `Result.failure`로 전달하고 coroutine 취소는 다시 던진다.

### Data 매퍼

- `OPEN`의 요청자, 다섯 축, 표시명, 영상 URL, 질문 경계와 제출 가능 값을 변환한다.
- 네 가지 non-OPEN gate를 각각 작성 불가 사유로 변환한다.
- 다섯 Domain 축을 대응하는 wire enum으로 변환한다.
- 평가 단계와 빈 코멘트를 변경하지 않고 DTO로 변환한다.

### Repository

- 진입 token을 RemoteDataSource에 전달하고 DTO를 Domain 결과로 반환한다.
- 제출 Domain 모델을 DTO로 변환해 RemoteDataSource에 한 번 전달한다.
- 제출 성공 DTO의 ID와 시각을 노출하지 않고 `Unit`을 반환한다.
- 7개 비즈니스 오류 코드를 지정된 네 Domain 오류 의미로 변환한다.
- `IOException`, HTTP 5xx와 알 수 없는 오류가 기존 공통 오류 정책을 따른다.
- 예외 메시지나 테스트 출력에 token, 영상 URL, 질문과 피드백 본문을 포함하지 않는다.

## 8. 수락 조건

- Feature가 `data`나 Retrofit 타입을 참조하지 않고 Domain Repository/UseCase만으로 진입과 제출을
  수행할 수 있다.
- `OPEN` 응답은 모든 필수 데이터가 있는 작성 가능 Domain 결과가 되고, 기존 non-null 응답
  계약은 유지된다.
- `PRIVATE`, `EXPIRED`, `FULL`, `ALREADY_SUBMITTED`는 예외가 아닌 작성 불가 정상 결과로
  구분된다.
- 빈 token은 GET/POST 호출 전에 차단된다.
- 제출 시 서버가 지정한 `1..5`개 항목을 빠짐없이, 추가·중복 없이 정확히 한 번씩 평가해야 한다.
- 평가 단계 `1..4`만 제출할 수 있다.
- 별칭은 Kotlin `Char.isWhitespace()` 기준 양끝 공백이 제거되며 `null`·빈 값·공백뿐인 값은
  `"익명의 지인"`으로 전송된다.
- 코멘트는 같은 기준으로 양끝 공백이 제거되며 빈 값은 JSON `null`이 아니라 `""`로 전송된다.
- 항목별 코멘트는 트리밍 후 Kotlin `String.length` 기준 최대 100이며 문자 종류는 제한하지
  않는다.
- 300자 전반 피드백은 Domain Model, DTO와 Repository에 추가되지 않는다.
- 제출 성공 시 `submissionId`와 `submittedAt`을 Domain에 노출하지 않고 `Unit`을 반환한다.
- 네 개의 요청 처리 오류 코드, 공유 만료, 최대 인원 초과와 중복 피드백을 Feature가 네 가지
  타입으로 구분할 수 있다.
- 후속 Feature가 네 오류 타입에 대해 6.2절의 확정 표시 문구를 적용할 수 있다.
- Network/Server/Unknown 오류는 기존 공통 오류 모델을 유지한다.
- Repository와 Mapper는 UI 문구, Dialog, Navigation, 재시도 또는 화면 상태를 결정하지 않는다.
- 피드백 진행 데이터와 공유 token은 새 로컬 저장소, 디스크 캐시, 로그, analytics와 crash
  report에 기록되지 않는다.
- 기존 설치 ID 외에 Guest Feedback을 위한 영속 데이터가 추가되지 않는다.
- 새로운 제품 dependency, 모듈, 공통 추상화와 중복 검증 계층을 추가하지 않는다.
- 새로 추가하거나 실질적으로 수정한 공개 타입과 함수에는 한국어 KDoc이 있으며, 후속 UI와
  ViewModel이 필요한 정규화·검증·결과·오류 계약을 구현을 읽지 않고 확인할 수 있다.
- KDoc은 민감한 실제 예시 데이터를 포함하지 않고 구현·테스트와 일치한다.
- 관련 단위 테스트와 전체 Android 정적 검증이 통과한다.
- 모든 새 테스트 함수명은 기대 동작을 설명하는 한국어 문장이다.

## 9. 구현 후 정적 검증 계획

CRLF와 포매팅 문제를 먼저 정리하기 위해 검증은 반드시 아래 순서로 수행한다. 포매팅 적용 전에
테스트나 정적 분석을 먼저 실행하지 않는다.

1. 포매팅 적용

   ```text
   .\gradlew.bat spotlessApply
   ```

2. 포매팅 결과 확인

   ```text
   .\gradlew.bat spotlessCheck
   ```

3. Domain과 Data의 대상 단위 테스트

   ```text
   .\gradlew.bat :domain:test :data:testDebugUnitTest
   ```

4. 전체 Android CI 검증

   ```text
   .\gradlew.bat --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug
   ```

5. diff 공백 오류와 변경 범위 확인

   ```text
   git diff --check
   git status --short
   ```

실패가 이번 변경에서 발생하면 범위 안에서 수정한 뒤 `spotlessApply`부터 같은 순서로 다시
실행한다. 기존 또는 범위 밖 실패라면 명령, 실제 오류, 영향과 미검증 위험을 분리해 보고한다.

Wasm 관련 task는 실행하지 않는다. 특히 `:catalog:compileKotlinWasmJs`와
`:catalog:wasmJsBrowserDistribution`은 검증 대상에서 제외한다.

## 10. 반영된 결정과 추가 Q&A

### Q1. 빈 별칭의 서버 처리

`nickname: ""`을 제출하면 서버가 PRD의 정책대로 `지인1`~`지인4` 별칭을 자동 부여하는가?

**A.** 별칭이 비어 있거나 `isEmptyOrNull` 등에 해당되는 경우 "익명의 지인"으로 전송해주기 바람.

**계획 반영:** nullable 별칭을 트리밍한 뒤 비어 있으면 `"익명의 지인"`으로 정규화하고, 빈
문자열 대신 이 값을 제출한다.

### Q2. 오류 Dialog의 표시 문구

요청 처리 오류 그룹과 참여 불가 오류 그룹에 표시할 정확한 제목·본문·버튼 문구는 무엇인가?
이 답변은 이번 Domain/Data 구현을 막지 않으며 후속 Feature/UI 계획에 반영한다.

**A.** 이 부분 관련하여 6-2 서버 오류 그룹을 더 세분화했으며, 각각 "서버 요청에 실패했습니다. 앱을 재실행하고 다시 시도해주세요." / "피드백 가능한 기간이 지났습니다." / "최대 피드백 가능 인원을 초과하여 더 이상 피드백을 받을 수 없습니다." / "이미 이 기기에서 피드백한 이력이 있습니다. 중복 피드백은 할 수 없습니다."
의 메시지로 작성 부탁.

**계획 반영:** 7개 서버 코드를 요청 처리, 공유 만료, 최대 인원 초과, 중복 피드백의 네 Domain
오류 타입으로 나눈다. 확정 문구는 6.2절에 기록하고 후속 Feature가 표시한다.

### Q3. 모든 문자를 허용할 때 100자 계산 기준

이모지나 일부 결합 문자는 Kotlin `String.length`의 UTF-16 code unit 수와 사용자가 인식하는
글자 수가 다를 수 있다. 항목별 코멘트 최대 100자를 가장 단순한 `String.length`로 계산할지,
Unicode code point 또는 화면상 grapheme cluster 기준으로 계산할지 결정이 필요하다.

**A.** `String.length`로 하자. 마찬가지로 문제 생기면 추후 수정하겠음.

**계획 반영:** 트리밍한 코멘트의 최대 길이는 Kotlin `String.length`가 반환하는 UTF-16 code
unit 수로 계산한다. Domain 검증, 후속 UI와 경계 테스트가 모두 같은 기준을 사용한다.
