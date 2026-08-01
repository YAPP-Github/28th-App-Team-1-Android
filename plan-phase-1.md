# Guest Feedback API 데이터 계층 구현 계획

## 1. 요약

`api-docs.json`의 `Guest Feedback` 그룹에 정의된 다음 2개 API를 Android `data` 모듈에 구현한다.

- `GET /api/v1/feedback/guest/{token}`: 공유 링크 진입 및 게이트 판정
- `POST /api/v1/feedback/guest/{token}/submissions`: 지인 피드백 제출

Guest Feedback은 회원 인증 플로우와 목적이 다른 비회원 전용 플로우다. 기존 회원용 HTTP client를 재사용하지 않고 Authorization과 TokenAuthenticator가 없는 Guest 전용 OkHttpClient/Retrofit을 구성한다. 두 요청에는 설치 단위 식별을 위한 `Device-Id`를 항상 자동 첨부한다.

저장소 표준인 Retrofit + Gson + OkHttp와 기존 Hilt 구성 방식을 유지한다. API 인터페이스, 요청·응답 DTO, Guest 전용 JSON 설정, RemoteDataSource 인터페이스 및 구현체, DI 제공 코드와 데이터 계층 단위 테스트를 추가한다. 성공 응답은 공통 `success/data` 래퍼 없이 DTO를 직접 반환한다. 새로운 네트워크·직렬화 dependency는 추가하지 않는다.

제출 요청의 `nickname`과 각 rating의 `comment`는 값 자체는 nullable이지만 JSON key는 필수다. 값이 없을 때도 key를 생략하지 않고 명시적인 `null`로 전송한다. `gate`와 `axis`는 Kotlin enum으로 엄격하게 역직렬화하고, 정의되지 않은 값은 fallback으로 흡수하지 않고 파싱 예외로 상위 계층에 전달한다.

모든 성공 응답 필드는 non-null로 구현한다. `videoUrl`도 non-null `String`으로 선언하되, 서버 문서의 “영상 파이프라인 연결 전까지 null” 설명을 현재 확정된 client 계약과의 불일치 주의사항으로 KDoc에 기록한다.

`submittedAt`은 항상 `2026-07-30T09:58:13.348Z`와 같은 UTC ISO-8601 instant 형식으로 수신한다. `Instant`로 파싱한 뒤 `Asia/Seoul`을 적용해 서울 현지 `LocalDateTime`으로 변환한다. 이 로직은 외부 JSON을 해석하는 data 계층 책임이고 현재 다른 모듈 consumer가 없으므로 `core:common`으로 이동하지 않는다. 향후 실제 두 번째 모듈 consumer가 생길 때 공용화를 재검토한다.

면접 영상 URL, 질문 원문과 지인 피드백은 민감한 사용자 데이터다. 승인된 API 서버와 송수신하고 메모리에서만 사용하며, 로컬 저장·캐시·HTTP/애플리케이션 로그·analytics·crash report 전송을 금지한다. 공유 token도 접근 권한을 부여하는 민감 식별자로 취급해 저장하거나 로그에 남기지 않는다. 테스트에는 실제 사용자 데이터 대신 비식별 합성 데이터만 사용한다.

구현 범위는 API/DTO/JSON 설정/RemoteDataSource/DI와 해당 단위 테스트까지다. Repository, Domain model, UseCase, Feature/UI 및 오류 코드별 Domain exception 매핑은 후속 작업으로 제외한다.

## 2. 수정/생성 예정 파일

| 파일 | 구분 | 주요 내용 |
|---|---|---|
| `data/src/main/kotlin/com/dminus14/app/data/remote/api/GuestFeedbackApi.kt` | 생성 | 두 Retrofit endpoint를 선언한다. 성공 타입은 래퍼 없이 응답 DTO를 직접 사용하고, 두 요청 모두 설치 ID 인터셉터 표식을 적용한다. |
| `data/src/main/kotlin/com/dminus14/app/data/remote/dto/GuestFeedbackDto.kt` | 생성 | 진입 응답, gate/axis enum, 평가 축, 질문 경계, 제출 요청, rating, 제출 응답 DTO를 정의한다. non-null 성공 필드, 필수 nullable 요청 key와 평점 범위를 반영한다. `videoUrl`에는 서버 설명과 확정 client 계약의 차이를 KDoc으로 기록한다. |
| `data/src/main/kotlin/com/dminus14/app/data/remote/serialization/UtcInstantToSeoulLocalDateTimeAdapter.kt` | 생성 | UTC ISO-8601 `Z` 문자열을 `Instant`로 파싱하고 `Asia/Seoul` 기준 `LocalDateTime`으로 변환하는 Gson adapter를 정의한다. Gson 및 서버 wire-format 책임이므로 `data`에 둔다. |
| `data/src/main/kotlin/com/dminus14/app/data/remote/serialization/GuestFeedbackEnumTypeAdapterFactory.kt` | 생성 | Guest gate/axis에 정의되지 않은 값이 들어오면 `JsonParseException`으로 실패시키는 strict Gson adapter factory를 정의한다. |
| `data/src/main/kotlin/com/dminus14/app/data/remote/datasource/GuestFeedbackRemoteDataSource.kt` | 생성 | 토큰으로 진입 정보를 조회하고 피드백을 제출하는 data-layer 계약을 선언한다. Retrofit 타입과 UI 정책을 외부에 노출하지 않는다. |
| `data/src/main/kotlin/com/dminus14/app/data/remote/datasource/GuestFeedbackRemoteDataSourceImpl.kt` | 생성 | `GuestFeedbackApi` 호출과 제출 DTO 조립을 구현한다. DTO 직접 응답을 반환하며 응답·token을 저장하거나 로그로 출력하지 않는다. |
| `data/src/main/kotlin/com/dminus14/app/data/di/remote/feedback/GuestFeedbackRemoteModule.kt` | 생성 | `GuestFeedbackRemoteDataSourceImpl`을 인터페이스에 `@Binds`, `@Singleton`으로 연결한다. |
| `data/src/main/kotlin/com/dminus14/app/data/di/remote/network/NetworkModule.kt` | 수정 | Guest 전용 qualifier, OkHttpClient, Gson, Retrofit과 `GuestFeedbackApi` provider를 추가한다. Guest client에는 설치 ID 인터셉터만 연결하고 Authorization, TokenAuthenticator 및 HTTP logger는 연결하지 않는다. Guest Gson에는 `serializeNulls()`, 시간 adapter와 strict enum adapter를 등록한다. |
| `data/src/test/kotlin/com/dminus14/app/data/remote/api/GuestFeedbackApiTest.kt` | 생성 | MockWebServer로 method/path, token URL encoding, 필수 `Device-Id`, Authorization 부재, 필수 nullable key가 포함된 JSON 요청과 직접 성공 응답 역직렬화를 검증한다. |
| `data/src/test/kotlin/com/dminus14/app/data/remote/serialization/GuestFeedbackSerializationTest.kt` | 생성 | 명시적 JSON `null`, 정상 enum, 미지원 enum 실패, UTC instant의 서울 `LocalDateTime` 변환과 잘못된 timestamp 실패를 검증한다. |
| `data/src/test/kotlin/com/dminus14/app/data/remote/datasource/GuestFeedbackRemoteDataSourceTest.kt` | 생성 | API 호출 위임, 제출 DTO 조립, 직접 응답 반환과 Retrofit/전송/파싱 예외의 무손실 전파를 검증한다. |
| `api-docs.json` | 갱신 | 서버 원본 OpenAPI가 수정된 뒤 다시 export한 명세로 교체한다. `nickname`과 `comment`를 required이면서 nullable인 key로 표현하고, Guest operation의 `security: []`, 필수 `Device-Id`, 직접 성공 응답, 모든 성공 필드의 required/non-null, enum, UTC timestamp 예시를 현재 계약과 일치시킨다. 생성 명세를 수동으로 임의 보정하지 않는다. |

Architecture 계약 자체는 바뀌지 않으므로 `docs/CONSTITUTION.md`, `docs/ARCHITECTURE.md`, `docs/architecture/**`는 수정하지 않는다. 별도 API Markdown 문서를 만들어 OpenAPI와 중복된 계약 원본을 두지 않는다.

예정하지 않은 변경:

- `domain`, `feature:*`, `app`, `core:*`, `designsystem`, `catalog` 수정
- Repository, Domain model, UseCase, UI 또는 오류 코드별 Domain exception 구현
- Gradle dependency 또는 Version Catalog 변경
- 로컬 저장소, 캐시, analytics, crash reporting 추가
- Architecture/Constitution 문서 또는 별도 API Markdown 문서 수정
- Wasm/Catalog 구현 및 빌드 설정 변경

## 3. 작업 순서

### 1. 서버 OpenAPI 원본 정합성 확보

- 서버 측 OpenAPI에서 두 Guest operation을 명시적으로 무인증(`security: []`)으로 선언한다.
- GET/POST의 `Device-Id`를 필수 header로 선언한다.
- 성공 응답이 래퍼 없는 직접 DTO임을 선언한다.
- 모든 성공 응답 필드를 required/non-null로 선언하고 `videoUrl` 설명의 null 문구는 client 계약과 구분되도록 정정하거나 주의사항으로 명시한다.
- `nickname`과 `comment`는 key가 required이면서 값은 nullable이 되도록 OpenAPI 3.1 schema에 표현한다.
- gate/axis enum, `submittedAt`의 UTC `Z` 형식과 예시를 명시한다.
- 수정된 서버 명세를 다시 export해 `api-docs.json`을 교체하고 JSON 유효성과 두 operation schema를 확인한다.

### 2. Guest 전용 네트워크 및 JSON 구성

- Retrofit + Gson을 유지하고 새 dependency를 추가하지 않는다.
- Guest 전용 qualifier, OkHttpClient, Gson, Retrofit을 추가해 회원용 네트워크 구성과 분리한다.
- Guest OkHttpClient에는 `InsertInstallationIdInterceptor`를 연결해 두 API의 내부 선택 표식을 실제 `Device-Id` 하나로 치환한다.
- Guest OkHttpClient에는 `InsertAuthorizationInterceptor`, `TokenAuthenticator`, `HttpLoggingInterceptor`를 연결하지 않는다.
- Guest Gson에 `serializeNulls()`를 적용해 nullable `nickname`과 `comment` key를 명시적 JSON `null`로 보낸다.
- Guest gate/axis 전용 strict enum adapter factory와 UTC→서울 `LocalDateTime` adapter를 등록한다.
- Guest Gson 설정을 별도로 한정해 다른 API의 null, enum 및 date-time 동작을 변경하지 않는다.

### 3. DTO 및 adapter 구현

- `GuestFeedbackEntryResponseDto`, `GuestFeedbackAxisDto`, `GuestFeedbackQuestionBoundaryDto`를 구현한다.
- `GuestFeedbackSubmitRequestDto`, `GuestFeedbackRatingDto`, `GuestFeedbackSubmitResponseDto`를 구현한다.
- `GuestFeedbackGateDto`에 `OPEN`, `PRIVATE`, `EXPIRED`, `FULL`, `ALREADY_SUBMITTED`를 정의한다.
- `GuestFeedbackAxisCodeDto`에 `GAZE`, `EXPRESSION`, `POSTURE`, `GESTURE`, `VOICE`를 정의하고 진입 응답과 제출 rating에서 함께 사용한다.
- 정의되지 않은 gate/axis 문자열은 `UNKNOWN`으로 변환하지 않고 strict adapter에서 파싱 실패시킨다.
- `level`은 명세의 `1..4` wire 값을 표현하고 데이터 계층에서 별도 제품 검증을 추가하지 않는다.
- 모든 성공 응답 프로퍼티를 non-null로 정의한다. `videoUrl`은 non-null `String`으로 선언하고 “영상 파이프라인 연결 전까지 null”이라는 서버 설명을 KDoc 주의사항으로 남긴다.
- `nickname`과 `comment`는 Kotlin nullable 타입이지만 JSON에 항상 존재하는 필수 key로 정의한다.
- `submittedAt`은 `Instant.parse` 후 `ZoneId.of("Asia/Seoul")`을 적용한 `LocalDateTime`으로 역직렬화한다.
- 시간 adapter는 향후 data 모듈의 다른 API에서 재사용할 수 있게 Guest 명칭에 종속시키지 않되 `core:common`으로 이동하지 않는다.

### 4. Retrofit API 구현

- GET에 path token과 설치 ID 헤더 주입 표식을 선언한다.
- POST에 path token, 설치 ID 헤더 주입 표식과 JSON request body를 선언한다.
- 두 성공 응답은 `ApiResponseDto<T>` 없이 각각 진입/제출 응답 DTO를 직접 반환한다.
- API 선언과 interceptor에서 공유 token, 영상 URL, 질문 원문 또는 피드백 본문을 기록하지 않는다.

### 5. RemoteDataSource 구현

- 조회 및 제출 함수를 인터페이스에 정의하고 구현체에서 `GuestFeedbackApi`를 호출한다.
- 제출 함수는 nullable nickname/comment를 포함한 요청 DTO를 조립하되 UI 검증·표현 정책을 넣지 않는다.
- API의 직접 성공 DTO를 별도 공통 래퍼 해제 없이 반환한다.
- Repository/error-mapper 범위는 추가하지 않고 Retrofit HTTP/전송/파싱 예외를 손실 없이 상위 계층으로 전파한다.
- 반환 데이터와 공유 token을 저장, 캐시 또는 로그로 출력하지 않는다.

### 6. Hilt 연결

- `NetworkModule`에서 Guest 전용 Gson/OkHttpClient/Retrofit과 `GuestFeedbackApi`를 제공한다.
- 별도 Hilt Module에서 RemoteDataSource 구현체를 bind한다.
- Guest와 회원용 qualifier 누락, client 혼용 및 DI 순환 참조가 없는지 컴파일로 확인한다.
- 기존 회원용 Retrofit, Auth API, Authorization 및 token refresh 동작이 변경되지 않았는지 확인한다.

### 7. 단위 테스트 작성

- GET/POST의 method/path, token URL encoding과 두 요청의 필수 `Device-Id`를 검증한다.
- Guest 요청에 Authorization이 없고 회원용 interceptor/authenticator가 연결되지 않았음을 검증한다.
- 설치 ID 내부 표식이 서버 요청에서 제거되고 `Device-Id`가 정확히 하나만 전달되는지 검증한다.
- POST의 `Content-Type`, JSON field name과 `nickname`/`comment` key가 값이 없을 때도 명시적 `null`로 전송되는지 검증한다.
- 정상 gate/axis는 enum으로 변환되고 정의되지 않은 값은 파싱 예외로 상위에 전달되는지 검증한다.
- `2026-07-30T09:58:13.348Z`가 `2026-07-30T18:58:13.348` 서울 `LocalDateTime`으로 변환되는지 검증한다.
- offset 없는 문자열과 잘못된 date-time이 파싱 예외로 상위에 전달되는지 검증한다.
- RemoteDataSource의 API 호출 위임, DTO 조립, 직접 응답 반환 및 예외 전파를 검증한다.
- Guest client에 HTTP logger가 없어 공유 token과 민감 요청·응답이 로그 경로에 진입하지 않음을 검증한다.
- 테스트에는 실제 사용자 이름, 질문, 영상 URL, token 또는 피드백 대신 명백한 비식별 합성값을 사용한다.
- 모든 테스트 함수명을 기대 동작을 설명하는 한국어 문장으로 작성한다.

### 8. 포매팅 및 정적 검증

- 아래 정적 검증 계획에 따라 포매팅을 가장 먼저 적용한 뒤 나머지 검증을 수행한다.
- 실패가 범위 내 코드 때문이면 수정 후 포매팅부터 같은 순서로 재실행한다.
- 범위 밖 기존 실패이면 실패 명령과 원인을 분리해 보고하고 관련 없는 파일은 수정하지 않는다.

## 4. 수락 조건

- Guest Feedback 두 API가 갱신된 OpenAPI의 method/path/header/body/response 계약과 일치한다.
- Guest API는 회원용 client와 분리된 전용 OkHttpClient/Retrofit을 사용한다.
- Guest 요청에는 Authorization이 없고 TokenAuthenticator가 작동하지 않는다.
- GET과 POST 모두 `Device-Id`가 정확히 한 번 자동 첨부되며 내부 선택 표식은 서버로 전송되지 않는다.
- GET은 모든 필드가 non-null인 진입 DTO를 직접 응답으로 역직렬화한다.
- `videoUrl`은 non-null `String`이며 서버 문서의 파이프라인 연결 전 null 설명이 KDoc에 명시된다.
- POST 성공 응답의 `submissionId`와 서울 기준 `submittedAt`이 non-null로 역직렬화된다.
- `nickname`과 `comment`는 nullable 값이지만 요청 JSON의 필수 key이며, 값이 없으면 생략되지 않고 `null`로 전송된다.
- `gate`와 `axis`가 Kotlin enum으로 역직렬화되며 알 수 없는 값은 파싱 예외로 실패해 상위 계층에 전달된다.
- UTC `Z` 형식의 `submittedAt`이 `Instant`를 거쳐 `Asia/Seoul` 기준 `LocalDateTime`으로 변환된다.
- 공유 token, 영상 URL, 질문 원문, nickname, 평점과 comment가 HTTP/애플리케이션 로그에 평문으로 남지 않는다.
- 민감 응답과 제출 데이터가 로컬 저장소, 캐시, analytics 또는 crash reporting에 기록되지 않는다.
- Guest용 Gson 설정이 회원용 기존 API의 null/enum/date-time 동작을 변경하지 않는다.
- RemoteDataSource가 data 계층 책임만 수행하고 Retrofit HTTP/전송/파싱 예외를 임의 변환하지 않는다.
- 시간 변환 코드가 `data`에 유지되고 `core:common` 또는 새 Gradle dependency가 추가되지 않는다.
- `api-docs.json`은 서버 원본에서 재생성된 유효한 JSON이며 구현 계약과 일치한다.
- Repository/Domain/UseCase/UI, 오류 코드 매핑, Gradle dependency, 보호 문서 및 Wasm/Catalog에는 변경이 없다.
- 새 테스트는 비식별 합성 데이터와 한국어 문장형 함수명을 사용한다.
- 정적 검증 계획의 명령이 Wasm 빌드 없이 성공한다. 실행할 수 없는 검증은 명령, 사유 및 잔여 위험을 명확히 보고한다.

## 5. 정적 검증 계획

포매팅을 반드시 가장 먼저 실행하고, 포매팅 완료 후에만 검사·테스트·컴파일을 수행한다.

1. 포매팅 적용

   ```text
   ./gradlew spotlessApply
   ```

2. 포매팅 결과 확인

   ```text
   ./gradlew spotlessCheck
   ```

3. OpenAPI JSON 및 Guest schema 확인

   ```text
   python -m json.tool api-docs.json
   ```

   JSON 유효성 검사 후 Guest 두 operation의 `security`, required header, request required/nullable key, 직접 응답 schema, response required/non-null, enum 및 timestamp 예시를 구조적으로 확인한다.

4. data 모듈 단위 테스트

   ```text
   ./gradlew :data:testDebugUnitTest
   ```

5. 전체 정적 분석, 단위 테스트, Android Lint 및 Android assemble

   ```text
   ./gradlew --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug
   ```

6. diff 공백 오류 및 변경 범위 확인

   ```text
   git diff --check
   git status --short
   ```

Wasm 빌드는 명시적으로 제외한다. `:catalog:compileKotlinWasmJs`, `:catalog:wasmJsBrowserDistribution` 및 기타 Wasm compilation/distribution task는 실행하지 않는다.

## 6. 모호한 사항 Q&A

현재까지 제공된 답변과 추가 서버 확인사항을 반영한 결과, 구현 전에 추가로 결정해야 할 모호한 사항은 없다.
