# 면접 기능 구현 계획

## 1. 목적과 범위

이 문서는 [`service_flow.md`](service_flow.md)를 실제 개발 작업으로 분해한다. 구현은 두 브랜치로 나눈다.

1. `feat/#136`: 면접 UI 배선을 위한 기반 코드 구현
2. `feat/#137`: 면접 UI 배선

0~1단계의 세션 생성은 완료된 선행 작업으로 간주한다. 리포트 조회·대기·렌더링, AI 질문 생성·채점 규칙은 두 브랜치 모두 범위 밖이다.

민감한 영상·음성·STT·질문, `sessionId`, 파일 경로, presigned URL은 로그·분석·크래시 리포트·테스트 fixture·Preview에 넣지 않는다. 테스트에서는 실제 사용자 데이터가 아닌 합성 메타데이터와 빈 임시 파일만 사용한다.

## 2. 브랜치와 커밋 선후 관계

현재 작업 브랜치는 `feat/#136`이다. `feat/#137`은 `feat/#136`의 마지막 검증·커밋이 끝난 뒤 그 HEAD에서 생성한다.

```text
기준 커밋
  └─ feat/#136: C136-1 → C136-2 → ... → C136-N
                                          └─ feat/#137: C137-1 → ... → C137-N
```

진행 규칙:

1. `feat/#136`의 모든 작업과 검증을 완료하고 마지막 커밋 `C136-N`을 만든다.
2. `C136-N`에서 `feat/#137`을 생성한다. 따라서 `C137-1`의 직접 부모는 `C136-N`이다.
3. #136이 병합되기 전 #137 PR을 열어야 한다면 PR base를 `feat/#136`으로 둔다.
4. #136이 squash merge되면 #137을 squash 결과 커밋 위로 rebase하여 #136 결과가 계속 #137의 조상이 되게 한다.
5. 두 브랜치 사이에서 같은 파일 변경을 cherry-pick으로 복제하지 않는다.

## 3. 공통 구현 원칙

- `feature:interview:impl`은 `domain`, `core:common`, `core:permission`, `designsystem`만 직접 사용하고 `data`나 `app`에 의존하지 않는다.
- ViewModel은 UseCase만 호출한다. 파일, CameraX, Media3, WorkManager 객체를 State·Intent에 넣지 않는다.
- 파일은 `InterviewMediaFileRef` 같은 불투명 참조로 전달하고 실제 경로 해석은 `data`의 로컬 저장 구현이 담당한다.
- 질문 재생·녹화·변환은 `feature:interview:impl`, API·S3 PUT·DataStore·JSON 파일은 `data`, 규칙과 UseCase는 `domain`, Navigation·WorkerFactory·전역 표시 조립은 `app`이 소유한다.
- API 문자열은 data mapper에서 타입 안전한 domain 값으로 변환한다. 알려지지 않은 값은 `Unknown(rawValue)`로 보존한다.
- 테스트 함수 이름은 기대 동작을 설명하는 한국어 문장으로 작성한다.
- 각 커밋은 최소한 영향 모듈의 compile 또는 test를 통과하는 상태로 만든다.

---

# 1부. 면접 UI 배선을 위한 기반 코드 구현

## 4. 브랜치 정보

- 브랜치: `feat/#136`
- 이슈: `#136`
- 목표: #137의 ViewModel과 Screen이 Android·data 구현 세부사항을 새로 결정하지 않고 사용할 수 있는 계약, 저장소, 실행기와 백그라운드 작업을 완성한다.
- 완료 상태: 실제 면접 화면 흐름은 아직 연결하지 않지만 각 기반 구성 요소는 DI로 생성 가능하고 독립 테스트가 가능해야 한다.

## 5. #136 작업 순서

### 1-1. 전역 오류 Envelope와 면접 오류 Route 계약 정렬

선행 조건: 없음. 다른 작업보다 먼저 공통 오류 계약을 컴파일 가능한 상태로 바꾼다.

코드 계약:

```kotlin
data class GlobalAppEventEnvelope(
    val event: GlobalAppEvent,
    val deliveryId: String? = null,
)

suspend fun GlobalErrorHandler.emit(
    event: GlobalAppEvent,
    deliveryId: String? = null,
)

enum class InterviewErrorType {
    MIC_DEVICE,
    NETWORK,
    STT,
    SERVER_TEMPORARY,
}
```

- `GlobalErrorHandler.events`는 `SharedFlow<GlobalAppEventEnvelope>`가 된다.
- 일반 화면 오류는 `deliveryId=null`이다.
- 로컬에 저장된 Worker 오류만 `pendingGlobalEventId`를 `deliveryId`로 전달한다.
- `GlobalModalRequest`에는 `deliveryId`나 업로드 정보를 추가하지 않는다.
- `GlobalErrorHost`는 `envelope.event`를 기존 Modal/Toast로 표시하고, 표시가 app UI 상태에 반영된 뒤에만 `onGlobalEventRendered(deliveryId)`를 호출한다.
- app callback이 domain의 `AcknowledgePendingInterviewUploadGlobalEventUseCase`를 호출한다. `core:common`은 domain에 의존하지 않는다.
- `InterviewErrorRoute`는 위 네 오류 유형만 인자로 받고 `sessionId`, timer, manifest 또는 pending answer를 받지 않는다.
- 기존 `InterviewErrorScreen`과 Preview는 네 유형을 모두 exhaustive하게 렌더링할 수 있는 최소 정적 상태로 먼저 맞춘다. API 호출과 버튼 동작은 #137에서 연결한다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 수정 | `docs/architecture/error-handling.md` | Envelope Flow, app 수집과 acknowledgment 책임을 권위 계약에 반영 |
| 수정 | `core/common/src/main/kotlin/com/dminus14/app/core/common/event/GlobalAppEvent.kt` | `GlobalAppEventEnvelope` 추가 |
| 수정 | `core/common/src/main/kotlin/com/dminus14/app/core/common/event/GlobalErrorHandler.kt` | Envelope Flow와 nullable ID emit |
| 수정 | `app/src/main/java/com/dminus14/app/error/GlobalErrorHost.kt` | Envelope 소비와 렌더 완료 callback 추가 |
| 수정 | `app/src/main/java/com/dminus14/app/MainActivity.kt` | 렌더 완료 callback 전달 자리 마련 |
| 수정 | `feature/interview/api/src/main/kotlin/com/dminus14/app/feature/interview/api/InterviewRoute.kt` | 최종 `InterviewErrorType` 네 종류와 민감 인자를 갖지 않는 Route 확정 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorContract.kt` | 네 오류 유형을 수용하는 기본 State로 정렬 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorScreen.kt` | 네 유형의 정적 문구·버튼과 Preview를 컴파일 가능한 상태로 정렬 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorViewModel.kt` | 확장된 계약에 맞춘 최소 생성자·초기 State 정렬; API 배선은 보류 |
| 수정 | `core/common/src/test/kotlin/com/dminus14/app/core/common/event/GlobalErrorHandlerTest.kt` | 기본 null ID, 지정 ID 보존, 연속 event 전달 검증 |
| 생성 | `app/src/androidTest/java/com/dminus14/app/error/GlobalErrorHostTest.kt` | event 종류별 표시와 ID가 있을 때만 acknowledgment하는지 검증 |
| 생성 | `feature/interview/impl/src/androidTest/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorContractScreenTest.kt` | 네 유형이 모두 정적 UI로 렌더링되는지 검증 |

권장 커밋: `refactor: 전역 및 면접 오류 기반 계약 정렬`

### 1-2. 미디어·WorkManager 의존성과 앱 실행 기반 추가

선행 조건: 1-1. 의존성은 실제 소유 모듈에만 추가한다.

추가할 artifact:

- CameraX: `camera-video`
- Media3: `media3-exoplayer`, `media3-transformer`, `media3-datasource-okhttp`
- WorkManager: `work-runtime-ktx`, 테스트용 `work-testing`
- Hilt Worker: `androidx.hilt:hilt-work`, `androidx.hilt:hilt-compiler`
- OkHttp: app composition bridge에서 타입을 참조할 최소 runtime artifact

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 수정 | `gradle/libs.versions.toml` | 위 artifact의 version/alias 추가. 기존 CameraX·Media3 version 축을 재사용하고 Work/Hilt Work만 별도 version 축 추가 |
| 수정 | `feature/interview/impl/build.gradle.kts` | `:domain`, Camera Video, Media3 ExoPlayer/Transformer/DataSource, Work runtime/Hilt Work와 테스트 dependency 추가 |
| 수정 | `app/build.gradle.kts` | WorkManager 설정, Hilt WorkerFactory, composition bridge와 Worker 테스트에 필요한 dependency 추가 |
| 수정 | `data/build.gradle.kts` | 별도 S3 PUT·질문 음성 client 테스트에 필요한 OkHttp test dependency 확인 |
| 수정 | `app/src/main/AndroidManifest.xml` | 기존 CAMERA/RECORD_AUDIO 선언은 유지하고 `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_DATA_SYNC`, `POST_NOTIFICATIONS` 추가, `SystemForegroundService`를 `dataSync` 유형으로 병합, Hilt WorkManager configuration 사용을 위해 기본 initializer 제거 |
| 수정 | `app/src/main/java/com/dminus14/app/DMinus14App.kt` | `Configuration.Provider`, `HiltWorkerFactory` 조립. Global Modal 시작 책임은 유지 |

새 Convention Plugin은 만들지 않는다. 같은 구성이 다른 모듈에 반복되기 전까지 개별 모듈 dependency로 유지한다.

권장 커밋: `build: 면접 미디어와 백그라운드 작업 의존성 추가`

### 1-3. 면접 원격 API의 타입 안전한 domain 계약 정리

선행 조건: 1-2. 기존 String과 긴 인자 목록을 UI가 직접 조합하지 않도록 먼저 정리한다.

확정 타입:

| 타입 | 알려진 값 |
|---|---|
| `InterviewPreparationStatus` | `Processing`, `Ready`, `Failed`, `Unknown(rawValue)` |
| `InterviewResumeState` | `Resumable`, `Ended`, `Unknown(rawValue)` |
| `InterviewTerminalStatus` | `Completed`, `Abandoned`, `Invalid`, `Unknown(rawValue)` |
| `InterviewAnswerEndRequest` | `Skip`, `ManualEnd`, `HardCap`, `BackExit` (`null`은 일반 답변) |
| `InterviewEndType` | `NormalEnd`, `ManualEnd`, `HardCap`, `BackExit`, `SttReset`, `Unknown(rawValue)` |
| `InterviewAbandonRequestCause` | `NetworkDisconnect`, `UserExit` |
| `InterviewAbandonCause` | `NetworkDisconnect`, `UserExit`, `HoldExpired`, `Unknown(rawValue)` |
| `InterviewTicketOutcome` | `Committed`, `Released`, `Unknown(rawValue)` |

추가·변경 규칙:

- `InterviewResumeStatus.elapsedSeconds`는 `Long?`으로 파싱하되 UI 타이머에는 전달하지 않는다.
- `SummaryQuestion`에 `QuestionTurn`을 보존하고 `ttsAudio`는 UI에서 사용하지 않는다.
- 답변 요청은 `SubmitInterviewAnswerCommand` 하나로 묶는다. `questionId`, `isWrapUp`, 다섯 타임라인 값, nullable `endType`, nullable `InterviewMediaFileRef`를 가진다.
- `SubmitAnswerUseCase`와 `InterviewRepository.submitAnswer`는 command를 받는다.
- 답변 파일은 `.m4a`, multipart filename도 `.m4a`, MIME type은 `audio/mp4`로 고정한다.
- `AI_TEMPORARILY_UNAVAILABLE`, `ANSWER_ALREADY_SUBMITTED`, `SESSION_ALREADY_ENDED`, `SESSION_NOT_STARTED`, `SESSION_PRELOAD_FAILED`는 개별 domain 예외로 매핑한다.
- BE `InterviewAbandonPersister.PersistResult`가 `USER_EXIT`에 `COMMITTED`, 그 밖의 중단에 `RELEASED`를 직접 기록하므로 이 두 값을 알려진 `ticketOutcome`으로 사용한다. `HELD`는 알려진 값으로 보정하지 않고 `Unknown(rawValue)`로 보존한다.
- `A8`은 URL과 content type만 domain에 반환한다. URL은 저장·로그하지 않는다.
- `A9` 계약과 구현은 `InterviewFileStore`가 완성되는 1-7에서 추가한다. 1-3에서는 A8/A10까지만 typed 계약으로 정리한다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/model/InterviewSessionStatus.kt` | 준비 상태, `SummaryQuestion.turn` 반영 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/model/InterviewSessionStatusType.kt` | raw 값을 잃는 `UNKNOWN` enum을 `Unknown(rawValue)` 계약으로 교체 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/model/InterviewAnswer.kt` | typed end type과 `SubmitInterviewAnswerCommand` 추가 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/model/InterviewResume.kt` | typed resume/terminal/abandon 상태, `Long?` elapsed |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/model/InterviewAbandon.kt` | typed cause/ticket outcome 적용 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/model/InterviewVideo.kt` | A8 URL·content type과 A10 완료 계약 적용 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/model/InterviewMediaFileRef.kt` | 실제 경로를 노출하지 않는 불투명 파일 참조 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/exception/InterviewException.kt` | 위 API별 복구 가능 예외 추가 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/repository/InterviewRepository.kt` | typed command와 A3 URL/A8·A10 계약 정리; A9는 1-7에서 추가 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/SubmitAnswerUseCase.kt` | command 단위 제출 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/GetQuestionAudioStreamUrlUseCase.kt` | A3 인증 스트림 URL 조회 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/GetInterviewSessionUseCase.kt`<br>`domain/src/main/kotlin/com/dminus14/app/domain/usecase/GetInterviewResumeUseCase.kt`<br>`domain/src/main/kotlin/com/dminus14/app/domain/usecase/ConfirmInterviewResumeUseCase.kt`<br>`domain/src/main/kotlin/com/dminus14/app/domain/usecase/AbandonInterviewUseCase.kt`<br>`domain/src/main/kotlin/com/dminus14/app/domain/usecase/IssueVideoUploadUrlUseCase.kt`<br>`domain/src/main/kotlin/com/dminus14/app/domain/usecase/CompleteVideoUploadUseCase.kt` | typed 입력·응답 적용 |
| 수정 | `data/src/main/kotlin/com/dminus14/app/data/remote/dto/interview/InterviewSessionStatusDto.kt`<br>`data/src/main/kotlin/com/dminus14/app/data/remote/dto/interview/SubmitAnswerDto.kt`<br>`data/src/main/kotlin/com/dminus14/app/data/remote/dto/interview/InterviewResumeStatusDto.kt`<br>`data/src/main/kotlin/com/dminus14/app/data/remote/dto/interview/InterviewResumeConfirmDto.kt`<br>`data/src/main/kotlin/com/dminus14/app/data/remote/dto/interview/InterviewAbandonDto.kt`<br>`data/src/main/kotlin/com/dminus14/app/data/remote/dto/interview/InterviewVideoUploadUrlDto.kt`<br>`data/src/main/kotlin/com/dminus14/app/data/remote/dto/interview/InterviewVideoCompleteDto.kt` | schema 우선 파싱, `turn`, `Long elapsedSeconds`, nullable 필드 반영 |
| 수정 | `data/src/main/kotlin/com/dminus14/app/data/remote/mapper/ApiErrorCode.kt` | 면접 답변·재개 오류 코드 추가 |
| 생성 | `data/src/main/kotlin/com/dminus14/app/data/local/interview/InterviewFileStore.kt` | 불투명 media ref를 no-backup 상대 경로로 발급·해석하는 최소 기반; manifest·인계·삭제는 1-5에서 확장 |
| 수정 | `data/src/main/kotlin/com/dminus14/app/data/repository/InterviewRepositoryImpl.kt` | typed mapper, `audio/mp4`, 답변 media ref 해석, API별 예외 매핑 |
| 수정 | `data/src/main/kotlin/com/dminus14/app/data/remote/datasource/InterviewRemoteDataSource.kt`<br>`data/src/main/kotlin/com/dminus14/app/data/remote/datasource/InterviewRemoteDataSourceImpl.kt` | typed repository가 필요한 최신 API 응답 전달 |
| 수정 | `data/src/test/kotlin/com/dminus14/app/data/repository/InterviewRepositoryImplTest.kt` | raw 매핑, MIME type, 복구 예외 검증 |
| 수정 | `data/src/test/kotlin/com/dminus14/app/data/remote/datasource/InterviewRemoteDataSourceTest.kt` | A2~A10 전송 계약 회귀 검증 |
| 생성 | `domain/src/test/kotlin/com/dminus14/app/domain/usecase/InterviewSessionUseCaseTest.kt` | A2~A7 typed command 전달과 cancellation 보존 검증 |
| 생성 | `domain/src/test/kotlin/com/dminus14/app/domain/usecase/InterviewVideoUseCaseTest.kt` | A8~A10 typed command 전달과 cancellation 보존 검증 |

권장 커밋: `refactor: 면접 API 도메인 계약을 타입으로 정리`

### 1-4. 진행 상태 DataStore와 클라이언트 타이머 기반 구현

선행 조건: 1-3.

domain 모델:

```kotlin
data class InterviewProgress(
    val sessionId: Long,
    val retentionDeadlineEpochMillis: Long,
    val retentionRemainingAtCheckpointMillis: Long,
    val retentionCheckpointElapsedRealtimeMillis: Long?,
    val timerStartedAtEpochMillis: Long?,
    val elapsedAtCheckpointMillis: Long?,
    val checkpointedAtEpochMillis: Long?,
)
```

저장·계산 규칙:

- `sessionId` 존재가 진행 중 면접 존재의 단일 원본이다.
- 전용 Preferences DataStore 파일은 `noBackupFilesDir/datastore/interview_progress.preferences_pb`에 둔다.
- 최초 `sessionId` 저장과 `retentionDeadlineEpochMillis=현재 epoch+24시간` 기록은 한 `edit`에서 수행한다.
- 타이머 시작 세 필드도 한 `edit`에서 기록한다.
- 실행 중 타이머는 `elapsedRealtime`, 복원은 마지막 checkpoint의 epoch 차이를 사용하며 절대 감소시키지 않고 `0..720_000ms`로 제한한다.
- 보존 시간은 epoch deadline과 단조 시계 checkpoint 중 더 짧은 값을 사용한다. `currentElapsedRealtime < storedElapsedRealtime`이면 재부팅으로 보고 epoch deadline만 사용한다.
- 기존 `sessionId`에 deadline이 없을 때만 manifest/미디어/DataStore의 가장 이른 유효 시각으로 한 번 이관한다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/model/InterviewProgress.kt` | 진행 상태와 타이머 checkpoint 모델 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/repository/InterviewLocalRepository.kt` | 진행 상태·manifest·업로드 작업의 domain 저장 계약 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/time/InterviewClock.kt` | epoch/단조 시계 공급 계약 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/time/InterviewTimeCalculator.kt` | 감소하지 않는 타이머와 보존 시간 계산 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/GetInterviewProgressUseCase.kt` | 로컬 `sessionId` 존재 조회 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/StartInterviewTimerUseCase.kt` | 타이머 원자 시작 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/CheckpointInterviewProgressUseCase.kt` | background/세그먼트/답변 checkpoint |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/GetInterviewElapsedTimeUseCase.kt` | SSoT 타이머 계산 |
| 생성 | `data/src/main/kotlin/com/dminus14/app/data/local/interview/InterviewProgressStore.kt` | `PreferenceDataStoreFactory` 기반 no-backup 전용 저장소 |
| 생성 | `data/src/main/kotlin/com/dminus14/app/data/time/AndroidInterviewClock.kt` | `System.currentTimeMillis`와 `SystemClock.elapsedRealtime` 제공 |
| 생성 | `data/src/main/kotlin/com/dminus14/app/data/repository/InterviewLocalRepositoryImpl.kt` | 진행 상태 저장 계약 구현 시작 |
| 생성 | `data/src/main/kotlin/com/dminus14/app/data/di/local/InterviewLocalModule.kt` | repository/store/clock Hilt binding |
| 생성 | `domain/src/test/kotlin/com/dminus14/app/domain/time/InterviewTimeCalculatorTest.kt` | 시각 역행, 재부팅, process 복원, hard cap 검증 |
| 생성 | `data/src/androidTest/kotlin/com/dminus14/app/data/local/interview/InterviewProgressStoreTest.kt` | 실제 no-backup DataStore 원자 저장·삭제 검증 |

권장 커밋: `feat: 면접 진행 상태와 타이머 저장 기반 구현`

### 1-5. 미디어 manifest와 파일 생명주기 저장 기반 구현

선행 조건: 1-4.

확정 모델:

```kotlin
enum class InterviewMediaSegmentType { QUESTION_VIDEO, ANSWER_VIDEO, ANSWER_AUDIO }
enum class InterviewMediaFinalizeState { WRITING, FINALIZED }

data class InterviewMediaSegment(
    val sequence: Int,
    val type: InterviewMediaSegmentType,
    val mediaRef: InterviewMediaFileRef,
    val questionId: Long?,
    val startedAtMillis: Long,
    val endedAtMillis: Long?,
    val gapBeforeMillis: Long,
    val finalizeState: InterviewMediaFinalizeState,
)
```

manifest v1에는 `schemaVersion`, `sessionId`, `nextSequence`, 현재 `questionId`, pending A4 command, 영상 설정(480p/H.264/AAC/30fps/실제 bitrate), segment 목록과 nullable wrap-up 구간을 둔다.

파일 규칙:

- 진행 중: `noBackupFilesDir/interview/{sessionId}/manifest.json`과 상대 경로 media.
- 업로드 인계: 세션 디렉터리에 `task.json`을 쓴 뒤 `noBackupFilesDir/interview/uploads/{uploadTaskId}/`로 같은 볼륨 rename.
- 재생성 가능한 중간 파일: `cacheDir/interview/uploads/{uploadTaskId}/`.
- JSON과 병합 결과는 `.tmp` 완전 쓰기 후 rename한다.
- 파일명·수정 시각이 아니라 manifest sequence만 병합 순서로 사용한다.
- 확정되지 않은 마지막 파일은 병합에서 제외한다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/model/InterviewMediaManifest.kt` | 위 manifest/segment/wrap-up/pending answer 모델 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/CreateInterviewMediaSegmentUseCase.kt` | 다음 sequence와 불투명 ref 발급 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/FinalizeInterviewMediaSegmentUseCase.kt` | 파일 확정 후 manifest 원자 갱신 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/GetInterviewMediaManifestUseCase.kt` | process 복원용 조회 |
| 생성 | `data/src/main/kotlin/com/dminus14/app/data/local/interview/InterviewManifestStore.kt` | Gson JSON v1 read/write와 schema 검사 |
| 수정 | `data/src/main/kotlin/com/dminus14/app/data/local/interview/InterviewFileStore.kt` | 1-3의 ref 해석 기반에 no-backup/cache 디렉터리 생명주기와 원자 rename·삭제 추가 |
| 수정 | `data/src/main/kotlin/com/dminus14/app/data/repository/InterviewLocalRepositoryImpl.kt` | manifest와 파일 생명주기 계약 구현 |
| 생성 | `data/src/test/kotlin/com/dminus14/app/data/local/interview/InterviewManifestStoreTest.kt` | 원자 교체, 순번, 손상 schema 거부 검증 |
| 생성 | `data/src/androidTest/kotlin/com/dminus14/app/data/local/interview/InterviewFileStoreTest.kt` | no-backup/cache 경로와 인계·삭제 검증 |

권장 커밋: `feat: 면접 미디어 manifest와 파일 저장 기반 구현`

### 1-6. 질문·마무리 멘트 재생, 녹화·발화 감지·변환·저장 공간 검사 실행기 구현

선행 조건: 1-2, 1-3, 1-5.

구성 요소:

- `InterviewAudioPlayer`: `AuthenticatedQuestionStream(url)`은 A3 URL을 progressive 재생하고, `Base64WrapUpMp3(payload)`는 응답의 Base64 MP3를 메모리에서 디코딩해 재생한다. 두 source 모두 started/completed/failure callback을 반환한다.
- `Base64WrapUpMp3`의 디코딩 바이트는 파일·State·manifest에 저장하지 않고 재생 종료·실패·취소 시 즉시 참조를 해제한다.
- `InterviewVideoRecorder`: 480p, H.264, AAC, 30fps, 목표 2 Mbps 이하 지원값으로 segment를 시작·중지하고 `VideoRecordEvent.Status.recordingStats.audioStats`의 시각·음량·audio state sample을 반환한다.
- `InterviewSpeechDetector`: 별도 `AudioRecord`를 열지 않고 CameraX `AudioStats.audioAmplitude(0.0..1.0)`만 소비한다. 진폭 `0.08` 이상인 active `Status` sample이 두 번 연속 오면 최초 발화, 발화 후 `0.03` 이하가 sample timestamp 기준 연속 10초이면 침묵 완료로 판정하는 hysteresis를 사용한다. audio state가 active가 아니거나 error이면 감지가 아니라 마이크 실패 결과를 반환한다.
- `InterviewMediaTransformer`: 같은 질문의 답변 AAC 구간을 하나의 `.m4a`로 내보내고 확정 영상들을 sequence 순서로 하나의 `.mp4`로 합친다.
- `InterviewMediaSessionManager`: 화면 생존 중 질문/답변 경계, background pause, foreground resume, segment gap과 wrap-up 범위를 조율한다.
- `InterviewStorageChecker`: `noBackupFilesDir`가 속한 볼륨의 `StatFs.availableBytes`를 읽고 `450 MiB(471_859_200 bytes)` 이상인지 반환한다. 경계값은 `InterviewConstants`의 단일 상수로 둔다.

네트워크 경계:

- data는 인증 interceptor와 authenticator는 유지하되 body logger가 없는 질문 음성 전용 `OkHttpClient`를 제공한다.
- feature는 data qualifier를 직접 보지 않는다. app composition module이 data client를 feature 소유 qualifier로 bridge한다.
- S3 PUT client는 별도로 두며 Bearer, authenticator, 설치 ID와 logger를 모두 붙이지 않는다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/media/InterviewMediaModels.kt` | recorder/player/transformer 결과와 `InterviewAudioPlaybackRequest` 모델 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/media/InterviewAudioPlayer.kt` | 인증 질문 stream과 Base64 마무리 MP3를 받는 단일 플레이어 계약 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/media/Media3InterviewAudioPlayer.kt` | 인증 DataSource progressive 재생과 메모리 ByteArray DataSource 재생 구현 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/media/InterviewVideoRecorder.kt` | 녹화기 계약 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/media/CameraXInterviewVideoRecorder.kt` | CameraX VideoCapture와 `AudioStats` sample callback 구현 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/media/InterviewSpeechDetector.kt` | 진폭 hysteresis, 최초 발화와 10초 침묵의 순수 상태 머신 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/media/InterviewMediaTransformer.kt` | 병합·추출 계약 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/media/Media3InterviewMediaTransformer.kt` | Media3 Transformer `Composition` 구현 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/media/InterviewMediaSessionManager.kt` | 질문/답변/중단/재개 segment 오케스트레이션 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/device/InterviewStorageChecker.kt` | 저장 공간 검사 계약 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/device/AndroidInterviewStorageChecker.kt` | no-backup 볼륨 `StatFs` 구현 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/InterviewConstants.kt` | 발화 `0.08`/침묵 `0.03`, 연속 sample 2회/10초와 450 MiB 상수 추가 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/di/InterviewMediaModule.kt` | 미디어 실행기와 저장 공간 검사기 Hilt binding |
| 생성 | `data/src/main/kotlin/com/dminus14/app/data/di/remote/interview/InterviewMediaNetworkModule.kt` | 인증·무로그 A3 client와 무인증·무로그 S3 client 제공 |
| 생성 | `app/src/main/java/com/dminus14/app/di/InterviewMediaCompositionModule.kt` | data client를 feature DataSource 계약으로 bridge |
| 생성 | `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/media/InterviewMediaSessionManagerTest.kt` | 순번, 경계, 재개, 같은 질문 조각 선택 검증 |
| 생성 | `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/media/InterviewSpeechDetectorTest.kt` | 발화 임계값, hysteresis, 10초 경계, inactive/error audio state 검증 |
| 생성 | `feature/interview/impl/src/androidTest/kotlin/com/dminus14/app/feature/interview/media/InterviewMediaTransformerTest.kt` | 합성 무음 샘플로 m4a/mp4 출력 형식과 순서 검증 |
| 생성 | `feature/interview/impl/src/androidTest/kotlin/com/dminus14/app/feature/interview/media/Media3InterviewAudioPlayerTest.kt` | 합성 URL stream과 Base64 MP3의 완료·실패·취소 및 메모리 source 해제 검증 |
| 생성 | `feature/interview/impl/src/androidTest/kotlin/com/dminus14/app/feature/interview/device/AndroidInterviewStorageCheckerTest.kt` | 실제 app 전용 볼륨 조회와 450 MiB 경계 매핑 검증 |

권장 커밋: `feat: 면접 미디어 실행 기반 구현`

### 1-7. 영상 업로드 task 저장소와 Worker 구현

선행 조건: 1-3, 1-5, 1-6.

확정 타입:

```kotlin
enum class InterviewUploadTaskStatus {
    PENDING_MERGE,
    PENDING_UPLOAD,
    PENDING_COMPLETE,
    FAILED_RETRYABLE,
    FAILED_GLOBAL,
}

enum class InterviewPendingGlobalErrorType { SERVER, UNKNOWN }
enum class InterviewUploadNetworkPolicy { UNMETERED, CONNECTED }
```

`InterviewUploadTask`에는 `schemaVersion=1`, `uploadTaskId`, `sessionId`, manifest 상대 위치, status, 현재 작업 묶음 retry count, nullable `pendingGlobalErrorType`, nullable `pendingGlobalEventId`를 둔다.

A9 계약:

```kotlin
data class UploadInterviewVideoCommand(
    val uploadUrl: String,
    val contentType: String,
    val mediaRef: InterviewMediaFileRef,
)
```

- `UploadInterviewVideoUseCase`와 `InterviewRepository.uploadVideo`는 위 command를 받는다.
- data는 완성된 `InterviewFileStore`로 `mediaRef`를 파일에 해석하고, 전용 S3 client로 streaming PUT한다.
- presigned URL은 command 수명 동안만 메모리에 두며 task·manifest·WorkManager Data에 저장하지 않는다.

Worker 순서:

1. `uploadTaskId`로 task를 조회한다.
2. `PENDING_MERGE`면 확정 segment를 병합하고 checkpoint를 저장한다.
3. `A8`로 URL을 받고 `A9` PUT을 실행한다. URL은 저장하지 않는다.
4. A9 성공 checkpoint 뒤 `A10`을 호출한다.
5. A10 성공 후 Work와 no-backup/cache 작업 디렉터리를 삭제한다.
6. Network와 안전한 A8~A10 Server Error는 exponential backoff 최대 3회 처리한다.
7. 3회 소진은 `FAILED_RETRYABLE`, 복구 계약 없는 Server/Unknown은 `FAILED_GLOBAL`로 원자 저장한다.
8. 손상·누락·미지원 task schema는 복구하지 않고 Work와 두 작업 디렉터리를 즉시 삭제한다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/model/InterviewUploadTask.kt` | task/status/error/network policy 모델 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/repository/InterviewWorkController.kt` | Android 타입 없는 enqueue/cancel/query 계약 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/PrepareInterviewUploadUseCase.kt` | 세션 디렉터리→업로드 작업 인계 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/GetInterviewUploadTaskUseCase.kt` | Worker checkpoint 조회 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/UpdateInterviewUploadTaskUseCase.kt` | 상태·재시도·오류 원자 갱신 |
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/UploadInterviewVideoUseCase.kt` | A9 직접 PUT command 전달 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/repository/InterviewRepository.kt` | A9 upload command 계약 추가 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/model/InterviewVideo.kt` | `UploadInterviewVideoCommand` 추가 |
| 생성 | `data/src/main/kotlin/com/dminus14/app/data/local/interview/InterviewUploadTaskStore.kt` | `task.json` v1 원자 read/write/scan/delete |
| 수정 | `data/src/main/kotlin/com/dminus14/app/data/repository/InterviewLocalRepositoryImpl.kt` | task 저장과 upload handoff 구현 |
| 생성 | `data/src/main/kotlin/com/dminus14/app/data/remote/uploader/InterviewVideoUploader.kt` | 전용 S3 client의 streaming PUT, A8 content type 적용 |
| 수정 | `data/src/main/kotlin/com/dminus14/app/data/repository/InterviewRepositoryImpl.kt` | A9 repository 계약 구현 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/work/InterviewVideoUploadWorker.kt` | 병합·A8~A10·checkpoint·retry 실행 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/work/WorkManagerInterviewWorkController.kt` | unique work/tag/inputData에 opaque ID만 저장 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/work/InterviewUploadNotification.kt` | 민감 정보 없는 foreground 알림/channel |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/di/InterviewWorkModule.kt` | WorkController binding |
| 생성 | `data/src/test/kotlin/com/dminus14/app/data/local/interview/InterviewUploadTaskStoreTest.kt` | status 전이, 손상 schema 삭제, ID 스냅샷 검증 |
| 생성 | `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/work/InterviewVideoUploadWorkerTest.kt` | 단계 복원, retry, A10 선행조건과 민감 input 금지 검증 |

권장 커밋: `feat: 면접 영상 업로드 Worker 구현`

### 1-8. 만료·계정 정리와 지연 오류 재전달 구현

선행 조건: 1-1, 1-4, 1-5, 1-7. 이 작업의 완료 커밋이 #136의 마지막 기능 커밋이다.

정리 UseCase:

- `CleanupExpiredInterviewDataUseCase`: 24시간 경과 시 Work 취소 후 진행·manifest·media·task 삭제.
- `ClearInterviewLocalDataUseCase`: 로그아웃·탈퇴 시 모든 Interview Work와 파일 삭제.
- `RecoverRetryableInterviewUploadsUseCase`: foreground에서 만료 정리 후 `FAILED_RETRYABLE` 중 실행·대기 중이 아닌 ID만 `KEEP` enqueue.
- `GetPendingInterviewUploadGlobalEventUseCase`: 기존 열린 ID를 우선 재전달하고, 없으면 Server 우선 한 유형 스냅샷에 새 ID 발급.
- `AcknowledgePendingInterviewUploadGlobalEventUseCase`: 정확히 같은 ID의 task와 media만 삭제.

호출 순서:

- 로그아웃: 원격 로그아웃 시도 → 면접 로컬 정리 → 인증 세션 삭제.
- 탈퇴: 서버 탈퇴 성공 → 면접 로컬 정리 → 인증 세션 삭제.
- 면접 정리 실패는 `isInterviewCleanupPending=true`만 별도 no-backup Cleanup DataStore에 저장하고 인증 완료를 막지 않는다.
- 앱 시작·foreground에서 pending cleanup → 만료 정리 → retryable enqueue → pending global event 순으로 실행한다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 생성 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/CleanupExpiredInterviewDataUseCase.kt`<br>`domain/src/main/kotlin/com/dminus14/app/domain/usecase/ClearInterviewLocalDataUseCase.kt`<br>`domain/src/main/kotlin/com/dminus14/app/domain/usecase/RecoverRetryableInterviewUploadsUseCase.kt`<br>`domain/src/main/kotlin/com/dminus14/app/domain/usecase/GetPendingInterviewUploadGlobalEventUseCase.kt`<br>`domain/src/main/kotlin/com/dminus14/app/domain/usecase/AcknowledgePendingInterviewUploadGlobalEventUseCase.kt` | 만료·정리·복구·event 묶음과 ack 규칙 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/LogoutUseCase.kt` | 확정 호출 순서와 부분 실패 처리 |
| 수정 | `domain/src/main/kotlin/com/dminus14/app/domain/usecase/WithdrawUserUseCase.kt` | 확정 호출 순서와 부분 실패 처리 |
| 생성 | `data/src/main/kotlin/com/dminus14/app/data/local/interview/InterviewCleanupPendingStore.kt` | 별도 no-backup boolean DataStore |
| 수정 | `data/src/main/kotlin/com/dminus14/app/data/repository/InterviewLocalRepositoryImpl.kt` | 전체/ID별/만료 삭제와 pending marker |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/work/InterviewRetentionCleanupWorker.kt` | deadline OneTime Work 실행 |
| 생성 | `app/src/main/java/com/dminus14/app/interview/InterviewAppLifecycleCoordinator.kt` | 앱 시작·foreground 작업 순서 조립 |
| 수정 | `app/src/main/java/com/dminus14/app/DMinus14App.kt` | 시작 시 coordinator 호출 |
| 수정 | `app/src/main/java/com/dminus14/app/MainActivity.kt` | foreground 진입과 GlobalErrorHost ack callback 연결 |
| 수정 | `feature/mypage/impl/src/test/kotlin/com/dminus14/app/feature/mypage/MyPageViewModelTest.kt` | 변경된 계정 UseCase fixture 반영 |
| 수정 | `domain/src/test/kotlin/com/dminus14/app/domain/usecase/LogoutUseCaseTest.kt` | 원격 실패·정리 실패·인증 삭제 순서 검증 |
| 수정 | `domain/src/test/kotlin/com/dminus14/app/domain/usecase/UserUseCaseTest.kt` | 탈퇴 부분 실패와 cleanup pending 검증 |
| 생성 | `domain/src/test/kotlin/com/dminus14/app/domain/usecase/InterviewUploadRecoveryUseCaseTest.kt` | Server 우선, 스냅샷 격리, 재전달과 ack 삭제 검증 |

권장 커밋: `feat: 면접 데이터 정리와 업로드 복구 기반 구현`

## 6. #136 완료 조건과 검증

- #137에서 사용할 domain model, repository, UseCase와 Feature 미디어/Work 계약이 모두 컴파일된다.
- `InterviewErrorRoute`는 최종 네 오류 유형만 가지며 기존 오류 화면과 Preview가 모두 컴파일된다.
- 진행 상태와 민감 media는 no-backup 저장소에만 있으며 WorkManager Data에는 `uploadTaskId`만 들어간다.
- 실제 면접 UI를 열지 않고도 타이머, manifest, media manager, upload Worker, cleanup과 event ack를 테스트할 수 있다.
- A3 URL과 Base64 마무리 MP3 재생, CameraX `AudioStats` 기반 발화·10초 침묵 판정, 450 MiB 저장 공간 경계를 UI 없이 검증할 수 있다.
- `GlobalModalRequest`와 Global Modal queue 계약은 바뀌지 않는다.
- 다음 검증을 순서대로 실행한다.

```text
./gradlew spotlessApply
./gradlew :core:common:test :domain:test :data:testDebugUnitTest :feature:interview:impl:testDebugUnitTest :app:testDebugUnitTest
./gradlew :data:connectedDebugAndroidTest :feature:interview:impl:connectedDebugAndroidTest :app:connectedDebugAndroidTest
./gradlew --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug
```

실기기 수동 확인:

- 빈 합성 화면을 480p로 짧게 녹화해 H.264/AAC mp4가 생성되는지 확인.
- 실제 기기 소음·일반 발화에서 `0.08/0.03` hysteresis가 최초 발화와 10초 침묵을 구분하는지 확인하고, 오탐이 있으면 제품 동작을 바꾸지 않는 범위에서 상수만 조정한다.
- 합성 Base64 MP3 마무리 멘트가 재생되고 종료·취소 뒤 임시 파일이 남지 않는지 확인.
- 두 segment 병합 순서, 질문 구간 m4a 추출, background segment 교체를 확인.
- Wi-Fi/모바일 제약, 알림, 앱 재시작 뒤 Worker 복원을 확인.
- 로그와 크래시 출력에 ID·URL·경로·media 내용이 없는지 확인.

---

# 2부. 면접 UI 배선

## 7. 브랜치 정보

- 브랜치: `feat/#137`
- 이슈: `#137`
- 부모: 검증과 커밋이 끝난 `feat/#136`의 마지막 커밋
- 목표: #136의 기반을 기존 Interview/InterviewError UI에 연결해 `service_flow.md`의 2~13단계를 사용자 흐름으로 완성한다.

## 8. #137 작업 순서

### 2-1. Interview·InterviewError MVI 계약 확정

선행 조건: #136 전체.

화면 단계:

```kotlin
enum class InterviewScreenState {
    DEVICE_CHECK,
    QUESTION_PREPARING,
    START_GUIDE,
    QUESTION_PLAYING,
    ANSWER_RECORDING,
    ANSWER_SUBMITTING,
    FINISHING,
}

```

`InterviewErrorType`과 `InterviewErrorRoute`는 #136에서 확정한 계약을 그대로 사용하고 #137에서 종류를 추가하거나 route 인자를 늘리지 않는다.

`InterviewState` 주요 필드:

- `screenState`, nullable `sessionId`, nullable `questionId`
- 카메라·마이크 권한/장치 준비, 질문 준비 status, `availableStorageBytes`, 저장 공간 충족 여부
- `elapsedMillis`, `hasSpeechStarted`, `canFinishEarly`, `isWrapUp`, `countdownSeconds`
- 질문 재생/답변 제출/종료 요청 in-flight 상태
- `pendingEndRequest`, 업로드 인계 상태, 사용자 모바일 데이터 승인 상태

Intent 그룹:

- 로드·권한·저장 공간: `LoadInterview`, `CheckCameraPermission`, `ClickRetryDeviceCheck`, `ClickOpenSettings`, `ReportCameraReady`, `ReportCameraBindingFailure`, `ReportMicrophoneReady`, `ReportMicrophoneFailure`, `ReportStorageAvailability(availableBytes)`, `ClickPermissionDeniedBack`(권한 거부 준비 화면 전용으로 유지)
- 시작·lifecycle: `StartInterview`, `ReportAppBackgrounded`, `ReportAppForegrounded`, `UpdateElapsedTime`, `ReportHardCapReached`
- 질문·답변: `ReportQuestionPlaybackStarted/Completed/Failure`, `ClickRetryQuestionAudio`, `ReportAnswerSpeechStarted`, `ReportAnswerSilenceElapsed`, `ClickFinishAnswer`, `ReportAnswerRecordingCompleted`
- 종료·업로드: `ClickFinishInterview`, `ConfirmFinishInterview`, `ClickExitInterview`, `ConfirmEarlyExit`, `DismissEarlyExit`, `ConfirmMeteredUpload`, `DismissMeteredUpload`, `ReportUploadNotificationPermission(isGranted)`, `ReportVideoUploadEnqueued/Failure`
- 연결·미디어 결과: `ReportNetworkDisconnected/Restored`, `ReportRecordingSegmentFinalized`, `ReportAnswerAudioFragmentFinalized`, `ReportVideoMergeCompleted/Failure`

Effect 그룹:

- Android 실행: 카메라·마이크 권한 요청, 앱 설정, `CheckStorageAvailability`, 녹화 시작/중지/일시정지/재개, 질문 재생, 마무리 멘트 재생, 답변 구간 시작/중지, 답변 음성 병합, `RequestUploadNotificationPermission`, 업로드 enqueue.
- UI 일회성: 질문 재시도, 8분 Toast, 10초 countdown 음성, 종료/이탈/모바일 데이터 확인 Modal.
- app 결과: 질문 준비 실패, 연결 중단, 두 번째 503의 답변 제출 재시도 필요(`SERVER_TEMPORARY` 진입), timeout/409의 답변 제출 복구 필요, STT 세션 무효화, 면접 종료, 권한 거부 종료.
- 24시간 삭제 안내는 지속 State 정보이므로 Effect를 만들지 않고 `FINISHING` State가 렌더링한다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/interview/InterviewContract.kt` | 위 State/Intent/Effect, immutable `val`, 기존 `ChangeSpeaker` 제거 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorContract.kt` | 오류별 지속 State, retry/resume/abort Intent와 Feature 결과 Effect |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/InterviewConstants.kt` | 8:00, 8:45, 11:50, 12:00, 침묵 10초, A2 폴링 5초 상수 |
| 생성 | `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/interview/InterviewContractTest.kt` | 계산 State와 시간 경계 검증 |

권장 커밋: `refactor: 면접 MVI 계약을 실제 흐름에 맞게 확장`

### 2-2. 준비·시작·타이머 ViewModel 배선

선행 조건: 2-1.

처리 순서:

1. `LoadInterview`에서 만료 정리 후 로컬 progress를 읽는다. `sessionId`가 없으면 선행 흐름 복구 결과를 보낸다.
2. 카메라 → 마이크 권한/장치를 순서대로 확인하고 `CheckStorageAvailability` Effect 결과를 받으면서 A2를 즉시 호출하고 5초 폴링한다.
3. 권한·장치·A2 READY·450 MiB 공간이 모두 충족되면 `START_GUIDE`로 전환한다.
4. `StartInterview`를 한 번만 수락해 timer를 저장하고 session recording과 첫 A3 재생 Effect를 발행한다.
5. ticker는 저장된 timer UseCase 값만 State에 반영하고 8:00, 8:45, 11:50, 12:00 사건을 각각 한 번만 발생시킨다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/interview/InterviewViewModel.kt` | UseCase 주입, A2 polling job, 준비 gate, timer event 처리 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/interview/InterviewTimerCoordinator.kt` | ticker와 한 번만 발생하는 시간 경계 제어 |
| 생성 | `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/interview/InterviewViewModelPreparationTest.kt` | session 없음, 만료, polling, 장치·저장 공간 실패, 450 MiB 경계, 중복 시작 검증 |
| 생성 | `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/interview/InterviewTimerCoordinatorTest.kt` | 8:00/8:45/11:50/12:00 단일 발생과 시간 점프 검증 |

권장 커밋: `feat: 면접 준비와 클라이언트 타이머 연결`

### 2-3. 질문·답변·종료 직렬 상태 머신 배선

선행 조건: 2-2.

상태 머신 규칙:

- A3 완료 후에만 답변 녹음을 시작한다. player 실패는 한 번 자동 재시도 후 사용자 재시도 State로 전환한다.
- 첫 발화 뒤 10초 침묵 또는 버튼으로 답변을 한 번 확정한다.
- 모든 A4와 종료 request는 하나의 `Mutex`/actor 직렬 경로로 처리한다.
- 첫 503은 같은 command로 자동 1회 재시도하고 두 번째 503은 pending command를 저장한 뒤 `SERVER_TEMPORARY` 결과를 보낸다.
- timeout/`ANSWER_ALREADY_SUBMITTED`는 media를 보존하고 A5→사용자 확인→A6 복구로 전환한다.
- 12분 도달 중 A4가 실행 중이면 `HardCap`을 pending에 한 번 저장하고 기존 결과가 계속 상태일 때만 다음 request로 보낸다.
- `SttReset`과 이미 종료된 결과가 pending 종료보다 우선한다.
- `reportGenerating=true`일 때만 업로드 인계, false면 media를 24시간 정리 대상으로 유지한다.
- 준비 중이거나 첫 질문 시작 전 이탈은 `A4`/`A7`을 호출하지 않고 로컬 진행 상태만 삭제한다. 서버의 보류 만료·환불 처리에 의존하며, 서버 정리 API 부재와 상태 불일치 위험 및 향후 교체 지점을 구현 KDoc에 명시한다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/interview/InterviewTurnStateMachine.kt` | A3/A4/retry/pending end의 순수 상태 전이 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/interview/InterviewViewModel.kt` | player/recorder callback, A4, 종료, upload handoff 연결 |
| 생성 | `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/interview/InterviewTurnStateMachineTest.kt` | 정상 턴, 503, timeout/409, hard cap race, STT 우선 검증 |
| 생성 | `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/interview/InterviewViewModelTurnTest.kt` | UseCase 호출값과 Effect 순서 검증 |

권장 커밋: `feat: 면접 질문 답변 종료 상태 머신 연결`

### 2-4. InterviewScreen Android Effect와 기존 UI 연결

선행 조건: 2-2, 2-3.

Screen 책임:

- `rememberMultiplePermissionRequester`로 CAMERA→RECORD_AUDIO 순차 요청, 영구 거부는 app settings launcher 실행.
- Android 13 이상은 최초 업로드 인계 직전에 `POST_NOTIFICATIONS` 시스템 권한을 한 번 요청한다. 거부해도 업로드는 막지 않고 WorkManager foreground 실행을 계속하며, 앱은 별도 반복 요청이나 자체 권한 안내 화면을 만들지 않는다.
- lifecycle callback을 `ReportAppBackgrounded/Foregrounded` Intent로 전달.
- Camera preview와 `InterviewMediaSessionManager`를 같은 lifecycle에 연결.
- Effect로 storage checker/player/recorder/transformer/WorkController를 호출하고 결과만 `Report...` Intent로 반환.
- CameraX `AudioStats` sample은 Screen 실행 계층의 `InterviewSpeechDetector`에서 소비하고 최초 발화·10초 침묵·audio state 실패 결과만 각각 `ReportAnswerSpeechStarted`, `ReportAnswerSilenceElapsed`, `ReportMicrophoneFailure` Intent로 반환한다.
- Content는 Framework·file·media 객체를 받지 않고 State와 `onIntent`만 사용.

UI 변경:

- 기존 준비 layer를 `DEVICE_CHECK`, `QUESTION_PREPARING`, `START_GUIDE` State에 연결.
- 진행 layer를 질문 듣기, 답변 녹음, 답변 정리, 종료 중 상태에 연결.
- 타이머를 남은 시간이 아닌 0:00부터 증가하는 경과 시간으로 변경.
- 8분 이후 종료 버튼과 잔여 인디케이터, 11:50 countdown을 연결.
- 질문 텍스트·다시 듣기 버튼은 추가하지 않는다.
- 기존 종료/이탈 Modal을 확정 Intent와 연결하고 모바일 데이터 확인 Modal만 추가한다.
- 기존 `FINISHING` UI에 “영상은 기본적으로 Wi-Fi 연결 시 업로드되며, 면접 세션 생성 후 24시간 안에 업로드되지 않으면 자동으로 삭제돼요.” 안내 텍스트를 추가한다. 그 밖의 새 화면·별도 디자인 흐름은 만들지 않는다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/interview/InterviewScreen.kt` | lifecycle, 권한, Effect 실행기와 callback Intent 연결 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/component/InterviewCameraPreview.kt` | preview와 VideoCapture/session manager 연결 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/interview/layer/InterviewScreenPrepareLayer.kt` | 준비 세부 State 렌더링 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/interview/layer/InterviewScreenOngoingLayer.kt` | 질문/답변/정리/종료 상태 렌더링 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/component/InterviewTimer.kt` | 경과 시간 표시 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/component/InterviewStartButton.kt`<br>`feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/component/InterviewReadinessIndicator.kt`<br>`feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/component/InterviewSpeakerIndicator.kt`<br>`feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/component/InterviewCompletableBubble.kt` | 새 State 입력과 불필요한 speaker toggle 제거 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/component/InterviewFinishModal.kt`<br>`feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/component/InterviewAbortModal.kt` | Confirm/Dismiss Intent 연결 |
| 생성 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/component/InterviewMeteredUploadModal.kt` | Wi-Fi 기본/모바일 데이터 승인 UI |
| 생성 | `feature/interview/impl/src/androidTest/kotlin/com/dminus14/app/feature/interview/interview/InterviewScreenTest.kt` | State별 UI, 저장 공간 Effect, CameraX 음량 판정 결과와 callback Intent 계측 검증 |

권장 커밋: `feat: 면접 화면에 미디어 실행과 상태를 연결`

### 2-5. InterviewErrorScreen 재개·STT·503 복구 배선

선행 조건: 2-3, 2-4.

오류별 동작:

| 오류 | 버튼 | API |
|---|---|---|
| `MIC_DEVICE` | 확인 단일 버튼 | 추가 호출 없음 |
| `STT` | 확인 | 추가 호출 없음 |
| `NETWORK` | 중단, 이어서 진행 | 진입·재시도 A5, 사용자 선택 후 A7(NETWORK_DISCONNECT) 또는 A6 |
| `SERVER_TEMPORARY` | 중단, 답변 재시도 | 동일 A4 또는 A7(USER_EXIT) |

- 실제 `sessionId`, timer, manifest와 pending A4는 route args로 전달하지 않고 local repository에서 복원한다.
- A5가 `resumeState=Ended`이면서 `status=Invalid`이면 STT 화면으로 보낸다. `status=Completed/Abandoned`는 정의된 종료 경로, `Unknown(rawValue)`는 로컬 `sessionId`·미디어를 보존한 채 Global Unknown 경로로 보낸다.
- A6 성공은 최신 question부터 A3로 복귀하고 hold 만료 결과는 종료한다.
- 409 `SESSION_ALREADY_ENDED`는 A7 중복 성공으로 처리한다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorViewModel.kt` | 로컬 복원, A5/A6/A7/동일 A4 호출과 State 갱신 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorScreen.kt` | 네 enum의 exhaustive UI, loading와 버튼 enable 연결 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorContract.kt` | 결과 이름을 `InterviewResumeConfirmed`, `InterviewAbandonCompleted`, `SttFailureAcknowledged`, `AnswerSubmissionRecovered`로 확정 |
| 생성 | `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorViewModelTest.kt` | 오류별 API 호출 유무, 409, hold 만료, 동일 A4 검증 |
| 생성 | `feature/interview/impl/src/androidTest/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorScreenTest.kt` | 네 유형의 문구·버튼·loading 검증 |

권장 커밋: `feat: 면접 오류 화면에 재개와 재시도 연결`

### 2-6. app Navigation과 면접 결과 조립

선행 조건: 2-4, 2-5.

- Feature DI에 있는 빈 Navigation callback module을 제거한다.
- app이 `interviewEntryBuilder`의 callback을 수신한다. 이번 브랜치에서 목적지가 정해진 결과만 `Navigator`에 연결하고, 종료 결과는 수신·정리까지만 처리한다.
- 연결 중단·장치 오류·STT·두 번째 503은 각각 올바른 `InterviewErrorRoute`를 push한다.
- 재개 성공은 오류 route를 제거하고 기존 `InterviewRoute`로 돌아간다.
- 준비 실패·권한 거부·중단은 `Home`으로 `replaceAll`한다.
- 면접 정상 종료(`InterviewEnded`)와 STT 무효화 확인은 Feature 결과 callback만 노출하고 실제 목적지 배선은 이번 브랜치 범위 밖이다. app은 종료 유형과 업로드 인계 여부를 수신해 로컬 세션 정리 완료만 보장하고, 홈·리포트 이동은 후속 브랜치에서 연결한다. 리포트 route는 추가하지 않는다.
- 화면 foreground 복귀는 local `sessionId`가 있을 때 A5를 호출하지만 A6은 사용자 확정 전 호출하지 않는다.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 삭제 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/di/InterviewNavigationModule.kt` | 빈 app-level callback 조립 제거 |
| 수정 | `feature/interview/impl/src/main/kotlin/com/dminus14/app/feature/interview/navigation/InterviewEntryBuilder.kt` | Feature 결과 callback 계약 노출 |
| 생성 | `app/src/main/java/com/dminus14/app/navigation/di/InterviewNavigationModule.kt` | app Navigator와 Home/InterviewErrorRoute 조립 |
| 수정 | `app/src/main/java/com/dminus14/app/MainActivity.kt` | lifecycle coordinator와 전역 오류 ack 조립 최종 확인 |
| 생성 | `app/src/test/java/com/dminus14/app/navigation/InterviewNavigationModuleTest.kt` | 목적지가 정해진 결과의 back stack 전이와 종료 결과가 route를 바꾸지 않음을 검증 |

권장 커밋: `feat: 면접 내비게이션과 종료 결과 연결`

### 2-7. 전체 흐름 회귀 테스트와 수동 검증

선행 조건: 2-1~2-6.

자동 검증 시나리오:

- 준비 READY/FAILED, 권한 거부, 장치 실패, 저장 공간 부족.
- A3 성공/플레이어 중단/한 번 자동 재시도/사용자 재시도.
- 발화 감지/10초 침묵/수동 답변 종료/중복 탭.
- 일반 답변, 자연·수동·hard cap·back exit, 부분 audio.
- A4 첫/두 번째 503, timeout, 이미 제출, hard cap race.
- background/foreground, process 복원, A5/A6/A7, STT reset.
- 다중 segment 병합, A8/A9/A10, URL 만료, retry 소진과 다음 foreground 복구.
- 24시간 만료, 로그아웃·탈퇴, pending cleanup, Global Event acknowledgment.

| 구분 | 파일 | 주요 내용 |
|---|---|---|
| 생성 | `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/InterviewFlowScenarioTest.kt` | fake UseCase/media 실행기로 2~13단계 상태 전이 검증 |
| 생성 | `feature/interview/impl/src/androidTest/kotlin/com/dminus14/app/feature/interview/InterviewLifecycleTest.kt` | background/foreground와 화면 복원 계측 검증 |
| 수정 | `feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/interview/InterviewViewModelPreparationTest.kt`<br>`feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/interview/InterviewTimerCoordinatorTest.kt`<br>`feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/interview/InterviewTurnStateMachineTest.kt`<br>`feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/interview/InterviewViewModelTurnTest.kt`<br>`feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorViewModelTest.kt`<br>`feature/interview/impl/src/test/kotlin/com/dminus14/app/feature/interview/work/InterviewVideoUploadWorkerTest.kt`<br>`feature/interview/impl/src/androidTest/kotlin/com/dminus14/app/feature/interview/interview/InterviewScreenTest.kt`<br>`feature/interview/impl/src/androidTest/kotlin/com/dminus14/app/feature/interview/error/InterviewErrorScreenTest.kt`<br>`app/src/test/java/com/dminus14/app/navigation/InterviewNavigationModuleTest.kt` | 발견된 회귀 경로 보강 |

권장 커밋: `test: 면접 전체 흐름 회귀 검증 추가`

## 9. #137 완료 조건과 검증

- UI의 모든 사용자 입력과 lifecycle 사건이 Intent로 ViewModel에 들어간다.
- 모든 Android 실행 결과가 `Report...` Intent로 돌아온다.
- ViewModel은 API 구현체, 파일 경로, CameraX, Media3, WorkManager 객체를 직접 사용하지 않는다.
- 오류 네 유형과 화면 단계 일곱 종류가 exhaustive `when`으로 처리된다.
- 2~13단계의 최소 수락 조건이 자동 테스트 또는 명시된 수동 테스트와 연결된다.

```text
./gradlew spotlessApply
./gradlew :domain:test :data:testDebugUnitTest :feature:interview:impl:testDebugUnitTest :app:testDebugUnitTest
./gradlew :data:connectedDebugAndroidTest :feature:interview:impl:connectedDebugAndroidTest :app:connectedDebugAndroidTest
./gradlew --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug
```

수동 실기기 확인:

1. 실제 CAMERA/RECORD_AUDIO 권한 승인·거부·영구 거부와 Android 13 이상 POST_NOTIFICATIONS 승인·거부.
2. 질문 음성 스트리밍과 녹화 동시 동작, 이어폰/스피커 전환 시 회귀.
3. 홈 이동·background·process kill 뒤 로컬 `sessionId` 기반 재접속.
4. Wi-Fi와 모바일 데이터 업로드 선택, foreground 알림과 24시간 안내.
5. 네트워크 차단·복구, 서버 503 test double, STT reset test response.
6. 로그·분석·크래시 출력에 실제 질문·음성·영상·ID·경로·URL이 없는지 확인.

## 10. 브랜치별 최종 커밋 예시

`feat/#136`:

1. `refactor: 전역 및 면접 오류 기반 계약 정렬`
2. `build: 면접 미디어와 백그라운드 작업 의존성 추가`
3. `refactor: 면접 API 도메인 계약을 타입으로 정리`
4. `feat: 면접 진행 상태와 타이머 저장 기반 구현`
5. `feat: 면접 미디어 manifest와 파일 저장 기반 구현`
6. `feat: 면접 미디어 실행 기반 구현`
7. `feat: 면접 영상 업로드 Worker 구현`
8. `feat: 면접 데이터 정리와 업로드 복구 기반 구현`

`feat/#137` — 위 8번 커밋을 직접 부모로 시작:

1. `refactor: 면접 MVI 계약을 실제 흐름에 맞게 확장`
2. `feat: 면접 준비와 클라이언트 타이머 연결`
3. `feat: 면접 질문 답변 종료 상태 머신 연결`
4. `feat: 면접 화면에 미디어 실행과 상태를 연결`
5. `feat: 면접 오류 화면에 재개와 재시도 연결`
6. `feat: 면접 내비게이션과 종료 결과 연결`
7. `test: 면접 전체 흐름 회귀 검증 추가`
