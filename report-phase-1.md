# Guest Feedback 저장소·매퍼·유스케이스 구현 결과 보고서

## 1. 구현 결과 요약

Guest 전용 Retrofit API, DTO, 응답 adapter, OkHttpClient와 RemoteDataSource 위에 Domain/Data
경계를 구현했다.

- `domain`에 비회원 피드백 진입 결과, 작성 불가 사유, 평가 항목, 질문 경계와 제출 입력 모델을
  추가했다.
- 진입 결과는 `Open`과 `Unavailable`로 나눠 DTO의 nullable 조합이 Feature에 노출되지 않게 했다.
- 빈 token 차단과 제출 항목 완전성·중복·단계·코멘트 길이 검증을 유스케이스에 구현했다.
- 별칭과 코멘트의 양끝 공백을 제거하고, 빈 별칭은 `익명의 지인`, 빈 코멘트는 빈 문자열로
  정규화한다.
- 코멘트는 문자 종류를 제한하지 않고 Kotlin `String.length` 기준 최대 100까지 허용한다.
- 단일 DTO 매퍼와 Repository 구현으로 RemoteDataSource를 Domain 계약에 연결했다.
- 7개 Guest 서버 오류를 요청 처리, 공유 만료, 최대 인원 초과, 중복 제출의 네 Domain 오류로
  구분했다.
- Network/Server/Unknown 공통 오류 정책을 유지하고 응답 파싱 오류도 `UnknownException`으로
  변환한다.
- 제출 성공 응답의 ID와 시각은 사용하지 않고 Domain에 `Unit`만 반환한다.
- 새 공개 계약과 후속 ViewModel이 호출할 유스케이스에 입력·정규화·결과·오류 계약을 설명하는
  한국어 KDoc을 작성했다.

Feature UI, ViewModel, 딥 링크 parsing, Navigation과 Dialog 표시는 승인된 Phase 2 범위에 포함되지
않아 구현하지 않았다.

## 2. 수락 조건 충족 여부

| 수락 조건 | 결과 | 근거 |
|---|---|---|
| Feature가 Retrofit/Data 타입 없이 Domain 계약으로 진입·제출 가능 | 충족 | `GuestFeedbackRepository`, 진입·제출 UseCase와 Domain Model 추가 |
| `OPEN`은 완전한 작성 데이터, 나머지 gate는 정상 작성 불가 결과 | 충족 | `GuestFeedbackEntry.Open/Unavailable`과 네 `GuestFeedbackUnavailableReason` 구현 및 테스트 |
| 빈 token을 GET/POST 호출 전에 차단 | 충족 | 두 UseCase에서 트리밍 후 검증하고 Repository 미호출 테스트 통과 |
| 지정된 `1..5`개 항목을 누락·추가·중복 없이 모두 평가 | 충족 | 제출 UseCase 집합·중복 검증과 부정 경로 테스트 통과 |
| 평가 단계는 `1..4`만 허용 | 충족 | 경계 및 범위 밖 테스트 통과 |
| 문자열 정규화 정책 준수 | 충족 | 양끝 공백 제거, 빈 별칭 `익명의 지인`, 빈 코멘트 `""` 테스트 통과 |
| 모든 문자 허용, `String.length` 최대 100 | 충족 | 이모지 50개(UTF-16 길이 100) 허용, 51개 거부 테스트 통과 |
| 300자 전반 피드백 제외 | 충족 | Domain Model, DTO 변환과 Repository에 필드 미추가 |
| 제출 성공 결과는 `Unit` | 충족 | 제출 ID/시각 미노출 테스트 통과 |
| 7개 서버 코드를 네 Domain 오류 의미로 구분 | 충족 | 모든 코드별 타입·원본 code·cause 보존 테스트 통과 |
| Network/Server/Unknown 공통 정책 유지 | 충족 | IOException, HTTP 5xx, 상태·파싱 오류 변환 테스트 통과 |
| 피드백과 token을 저장·캐시·로그하지 않음 | 충족 | 새 구현에 저장소·파일·로깅 호출이 없음을 검색으로 확인 |
| 기존 설치 ID 외 Guest 영속 데이터 미추가 | 충족 | Room/DataStore/SharedPreferences 및 새 캐시 구현 없음 |
| Hilt로 Repository 구현 연결 | 충족 | Singleton binding 추가, Data 컴파일과 전체 assemble 성공 |
| 한국어 KDoc으로 후속 소비 계약 전달 | 충족 | 모델·Repository·예외·UseCase invoke·핵심 Data 함수에 KDoc 추가 |
| 새 테스트 함수명은 한국어 문장 | 충족 | Domain/Data 신규 테스트 전체 확인 |
| 전체 Android 정적 검증 통과 | 충족 | Spotless, Detekt, 전체 단위 테스트, Lint, assemble 성공 |

승인된 구현 범위 안에서 미충족 수락 조건은 없다.

## 3. 생성·변경된 파일

### Domain

| 파일 | 구분 | 내용 |
|---|---|---|
| `domain/build.gradle.kts` | 변경 | JUnit과 coroutine test용 기존 Version Catalog 의존성 추가 |
| `domain/src/main/kotlin/com/dminus14/app/domain/model/GuestFeedback.kt` | 생성 | 진입 결과, gate 사유, 평가 축, 질문 경계와 제출 모델 |
| `domain/src/main/kotlin/com/dminus14/app/domain/repository/GuestFeedbackRepository.kt` | 생성 | 진입·제출 Repository 계약 |
| `domain/src/main/kotlin/com/dminus14/app/domain/exception/GuestFeedbackException.kt` | 생성 | 입력 검증과 네 Guest 서버 오류 의미 |
| `domain/src/main/kotlin/com/dminus14/app/domain/usecase/EnterGuestFeedbackUseCase.kt` | 생성 | token 정규화·차단과 진입 조회 |
| `domain/src/main/kotlin/com/dminus14/app/domain/usecase/SubmitGuestFeedbackUseCase.kt` | 생성 | 제출 검증·정규화와 확정 제출 |
| `domain/src/test/kotlin/com/dminus14/app/domain/usecase/GuestFeedbackUseCaseTest.kt` | 생성 | 두 유스케이스의 성공·차단·경계·취소 테스트 12개 |

### Data

| 파일 | 구분 | 내용 |
|---|---|---|
| `data/src/main/kotlin/com/dminus14/app/data/remote/mapper/ApiErrorCode.kt` | 변경 | Guest 비즈니스 오류 코드 7개 추가 |
| `data/src/main/kotlin/com/dminus14/app/data/remote/mapper/GuestFeedbackMapper.kt` | 생성 | 진입 DTO→Domain, Domain 평가→DTO 변환 |
| `data/src/main/kotlin/com/dminus14/app/data/repository/GuestFeedbackRepositoryImpl.kt` | 생성 | 원격 호출, 변환, 오류 분류와 제출 응답 폐기 |
| `data/src/main/kotlin/com/dminus14/app/data/di/remote/feedback/GuestFeedbackRepositoryModule.kt` | 생성 | Repository Hilt Singleton binding |
| `data/src/test/kotlin/com/dminus14/app/data/remote/mapper/GuestFeedbackMapperTest.kt` | 생성 | 모든 gate와 axis 변환 테스트 |
| `data/src/test/kotlin/com/dminus14/app/data/repository/GuestFeedbackRepositoryImplTest.kt` | 생성 | 위임, 변환, 제출 결과와 오류 정책 테스트 |

### 문서

| 파일 | 구분 | 내용 |
|---|---|---|
| `plan-phase-2.md` | 생성·갱신 | 확정 구현 계획, 수락 조건과 검증 순서 |
| `report.md` | 생성 | 구현 결과, 수락 조건, 파일, 단순화 지점과 검증 결과 |

기존 사용자 변경인 `plan.md` → `plan-phase-1.md` rename과 `prd.md` 추가는 수정하거나 되돌리지
않았다.

## 4. 오캄의 면도날을 반영한 지점

### 상태를 두 형태로만 표현

진입 Domain Model을 nullable 필드가 많은 범용 객체로 만들지 않고 `Open`과 `Unavailable` 두
형태로만 구성했다. 별도 응답 validator나 상태 조합 검사 계층 없이 기존 Gson adapter의 검증을
신뢰하면서 Domain의 잘못된 상태를 줄였다.

### 하나의 매퍼 파일

각 DTO마다 mapper class나 DI binding을 만들지 않았다. Guest 전용 변환을
`GuestFeedbackMapper.kt`의 작은 확장 함수로 모으고 enum은 명시적인 `when`으로 대응시켰다.

### 별도 validator 객체를 만들지 않음

제출 검증은 실제로 사용하는 `SubmitGuestFeedbackUseCase` 안에 두고 작은 private 함수 하나만
사용했다. 검증 상태를 보관하는 객체나 공통 validation framework를 추가하지 않았다.

### 오류 매퍼 계층을 추가하지 않음

공통 `CommonApiErrorMapper`를 재사용하고 Guest 코드 분기는 Repository의 private 함수에 두었다.
파싱 오류의 Unknown 변환도 같은 경계에서 처리해 새 오류 pipeline을 만들지 않았다.

### 사용하지 않는 성공 데이터를 버림

서버의 `submissionId`와 `submittedAt`을 위한 Domain Model을 만들지 않고 성공 여부만 `Unit`으로
전달했다.

### 저장·재시도·임시저장을 추가하지 않음

Phase 2에 필요하지 않은 캐시, 로컬 초안, 재시도 정책과 수명 관리 객체를 만들지 않았다. 민감한
데이터는 기존 원격 호출 경로와 실행 중 메모리에만 머문다.

## 5. 정적 검증 결과

CRLF 문제를 먼저 제거하도록 모든 최종 검증은 포매팅 적용부터 순서대로 실행했다.

| 순서 | 명령 | 결과 |
|---|---|---|
| 1 | `.\gradlew.bat spotlessApply` | 성공 |
| 2 | `.\gradlew.bat spotlessCheck` | 성공 |
| 3 | `.\gradlew.bat :domain:test :data:testDebugUnitTest` | 성공 |
| 4 | `.\gradlew.bat --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug` | 성공, 541 tasks |
| 5 | `git diff --check` | 성공 |

대상 단위 테스트 결과:

- `domain:test`: 12개, 실패 0, 오류 0, 건너뜀 0
- `data:testDebugUnitTest`: 56개, 실패 0, 오류 0, 건너뜀 0

전체 CI의 최종 실행은 `BUILD SUCCESSFUL`로 완료됐다. 최초 전체 CI 시도 한 번은 코드 오류가
아니라 실행 도구의 120초 제한으로 중단됐으며, 동일 명령을 더 긴 제한으로 재실행한 뒤 성공을
확인했다.

검증 중 기존 구성에서 다음 비차단 경고가 출력됐다.

- Design System의 `commonTest`에 Android host test가 활성화되지 않았다는 경고
- Gradle 10에서 제거될 deprecated feature 사용 경고

Wasm 관련 task는 사용자 지시에 따라 실행하지 않았다. 이번 변경은 Catalog/Design System Web
출력에 영향을 주지 않는다.

## 6. 잔여 범위

다음 항목은 Phase 2 수락 범위 밖이며 후속 UI 작업에서 이번 Domain 계약을 사용해 구현한다.

- `{SERVER_URL}/feedback/guest/{token}` 딥 링크 parsing과 Navigation 연결
- 앱 실행 중 링크 재진입 시 종료 안내
- `OPEN` 외 gate와 네 오류 타입의 전역 Dialog 표시
- Guest Feedback 화면, ViewModel과 입력 TextField의 즉시 검증

현재 구현 범위 안에서 확인된 미해결 위험이나 추가 결정 사항은 없다.
