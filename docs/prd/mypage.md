# 마이페이지 사용자 플로우와 Android 구현 현황

## 1. 문서 목적

이 문서는 마이페이지(`feature:mypage`)의 화면·정책을 사용자 흐름 순서로 정리한 확정 사양이다. `feature/mypage/`는 이미 구현이 끝난 상태이며, 이 문서는 **구현된 코드를 기준으로** 화면·정책·데이터 계약을 기록한다.

참고 자료:

- `feature/mypage/api`, `feature/mypage/impl`의 `MyPageRoute`, `MyPageContract`, `MyPageViewModel`, `MyPageUiMapper`, `MyPageScreen`, `component/*`, `portfolio/PortfolioFileReader`
- `domain`의 `Portfolio`, `PortfolioOverview`, `PortfolioStatus`, `UserProfile`, `InterviewReportListItem`, 관련 UseCase
- `feature/mypage/impl`의 `MyPageViewModelTest`, `MyPageContractTest`, `MyPageUiMapperTest`
- `docs/CONSTITUTION.md`, `docs/ARCHITECTURE.md`

표기:

- **범위 밖**: 마이페이지 모듈이 소유하지 않고 Effect로 다른 화면에 위임한다.

## 2. 범위 (In / Out)

### In (구현됨)

- 프로필 조회: 이름·직군·연차·잔여 이용권·소셜 로그인 배지 표시.
- 프로필 수정 진입: `ClickProfileEdit` → `ProfileEditRequested` Effect로 편집 화면 진입만 요청한다. 실제 수정 UI는 마이페이지 모듈이 소유하지 않는다(**범위 밖**).
- 포트폴리오: 조회, 삭제, 재업로드(교체), 업로드 진행 상태 폴링·취소, 실패 재시도.
- 면접 리포트 목록: 조회, 펼침/접힘, 메타데이터(직군·연차·포트폴리오 파일명·JD) 표시, 리포트 보기·지인 피드백 받기 진입 요청.
- 계정: 로그아웃, 회원 탈퇴(주의사항 → 최종 확인 2단계).

### Out (범위 밖 — 다른 모듈이 소유)

- 프로필 직군·연차 실제 수정 화면(입력 폼) — `ProfileEditRequested` Effect가 가리키는 목적지.
- 리포트 상세 뷰·지인 피드백 화면 — `ReportViewRequested`, `GuestFeedbackRequested` Effect가 가리키는 목적지.
- 로그아웃·탈퇴 완료 후 실제 라우팅 — `LogoutCompleted`, `WithdrawalCompleted` Effect를 app 계층이 해석한다.

## 3. 용어

**포트폴리오 개요(`PortfolioOverview`)**: 포트폴리오 1건과 계정 단위 정책 플래그(`isReplaceAvailable`, `isDeleteAvailable`, `nextReplaceAvailableAt`, `nextDeleteAvailableAt`)를 함께 담는 조회 단위. 포트폴리오가 없어도(`portfolio == null`) 정책 플래그는 항상 채워진다.

**교체(재업로드)**: 기존 포트폴리오 삭제 여부와 무관하게, 새 파일을 올려 기존 파일을 대체하는 동작. `PortfolioStatus.Uploaded` 카드를 탭하면 시작한다(`onReuploadClick`).

**삭제**: 포트폴리오 카드의 휴지통 아이콘을 탭해 시작한다(`onDeleteClick`). 삭제 자체도 계정 단위로 가능 여부가 별도로 판정된다(4.2 참고).

**접수(202) 전/후**: 업로드 요청을 보낸 시점(전송 중)과, 서버가 202로 받아 `portfolioId`를 내려준 시점(처리 중)을 구분한다. 취소 동작이 이 시점에 따라 달라진다(4.2 참고).

## 4. 화면·기능

### 4.1 진입과 초기 로드

`MyPageIntent.Load`는 화면 진입 시(`LaunchedEffect(Unit)`) 자동 발행된다.

구현:

- `PortfolioFileReader.clearCache()`로 이전 세션이 남긴 임시 PDF 캐시를 먼저 정리한다.
- 프로필(`CheckUserProfileUseCase`), 포트폴리오 개요(`GetPortfolioOverviewUseCase`), 리포트 목록(`GetInterviewReportListUseCase`)을 `async`로 동시에 호출하고 세 결과를 모두 기다린 뒤 `isInitialLoading`을 내린다.
- 각 결과는 성공한 것만 State에 반영한다. 하나만 실패하면 나머지 섹션은 정상 표시하고 `MyPageEffect.ShowToast`를 한 번 발행한다.
- 실패 중 `NetworkUnavailableException`·`ServerException`·`UnknownException`이 하나라도 있으면 Toast 대신 `GlobalErrorHandler.emit`으로 전역 이벤트를 발행하고, 부분 실패 Toast는 발행하지 않는다(전역 이벤트가 우선).
- 포트폴리오가 `PROCESSING` 상태로 조회되면(재진입 시 업로드가 아직 끝나지 않은 경우) 즉시 폴링을 재개한다.

### 4.2 프로필·이용권·계정 배지

`MyPageProfileSection`이 표시한다.

정책:

- 항목: 이름, 직군(`MyPageJobRole` 6종 — `BACKEND`/`FRONTEND`/`IOS`/`ANDROID`/`DATA_ENGINEER`/`INFRA_SRE`), 연차, 잔여 면접 이용권 수, 가입한 소셜 제공자 배지.
- 값이 없으면 각 필드는 "내용 없음"으로 표시한다(`MyPageUiMapper`).
- 이메일은 로컬 파트만 마스킹해 표시한다(`maskEmail`). 로컬 파트 4자 이상이면 앞 3자, 2~3자면 앞 1자, 그 이하면 전부 마스킹하고 도메인 파트는 그대로 노출한다. 마스킹 전 원문은 로그·분석에 남기지 않는다.
- 소셜 제공자가 `KAKAO`면 "카카오"로 표시하고 카카오 로고 아이콘을 붙인다. 그 외 값은 원문(예: `APPLE`)을 그대로 라벨로 쓴다.
- 잔여 이용권 안내 아이콘(`ClickTicketInfo`)을 누르면 3초 뒤 자동으로 닫히는 말풍선을 띄운다. `remainingTicketCount`가 `null`이면 아이콘을 눌러도 말풍선을 띄우지 않는다.
- 프로필 편집 아이콘은 항상 `ProfileEditRequested` Effect만 발행한다. 실제 수정 로직·서버 검증은 이 모듈 밖이다.
- 로그아웃은 이 섹션 하단 텍스트에서 시작한다(`ClickLogout` → `Logout` 모달).

### 4.3 포트폴리오 — 조회

`MyPagePortfolioSection`이 `MyPagePortfolioState`(`Empty`/`Uploaded`/`Uploading`/`Completed`/`Failed`)를 기준으로 카드를 그린다.

- `Uploaded` 카드: 파일명(1줄, ellipsis), 업로드일(`yyyy.MM.dd`), 파일 크기(1MB 미만은 KB 1자리, 이상은 MB 1자리)를 보여준다. 카드 전체를 탭하면 재업로드, 휴지통 아이콘을 탭하면 삭제를 시작한다.
- 서버가 `READY`를 내려줘도 이번 화면에서 방금 업로드를 끝낸 경우(`Completed`)라면 계속 `Completed`로 유지하고, 재진입 등 그 외의 경우에는 `Uploaded`로 표시한다(`toPortfolioState`).
- `PROCESSING`은 진행률 바(`Uploading`)로, `FAILED_FILE`/`FAILED_SYSTEM`은 `Failed` 카드 + 상단 제약 안내(최대 20MB, 30쪽 이내 PDF 1개)로 표시한다. 서버의 알 수 없는 상태값은 `PortfolioStatus.UNKNOWN` → 화면상 `Empty`로 흡수한다.
- `Failed` 상태가 아닐 때는 "포트폴리오는 한 달에 한 번 바꿀 수 있어요. 지워도 지난 면접 리포트는 그대로 남아요." 안내를 항상 노출한다.

### 4.4 포트폴리오 — 업로드(최초/재업로드)

Flow:

1. `Empty` 상태의 업로드 버튼 또는 `Uploaded` 카드 탭 → `ClickUpload`/`ClickPortfolioReupload`.
2. `ClickUpload`(최초 업로드에 해당하는 진입점)는 정책 모달 없이 바로 `PortfolioSelectionRequested` Effect(파일 선택기 실행)를 발행한다.
3. `ClickPortfolioReupload`는 `isPortfolioReplaceAvailable`을 먼저 본다. 가능하면 `PortfolioReupload` 확인 모달, 불가하면 `PortfolioReuploadUnavailable` 차단 모달을 띄운다. 모달을 확인해야 파일 선택기가 열린다.
4. 파일 선택 결과(`SelectPortfolioFile`)는 `PortfolioFileReader.read()`로 검증한다. `Invalid`(비밀번호 걸림 등 `PdfInvalidReason`)·`Unreadable`(파일 열기 실패)이면 즉시 `Failed` 카드로 전환하고 업로드를 시작하지 않는다.
5. 유효하면 이전 상태가 `Failed`였고 서버에 실패 레코드(`portfolioId`)가 남아 있는 경우, 새 업로드 전에 그 레코드를 먼저 삭제한다(`deletePreviousFailedPortfolioIfNeeded`). 삭제가 `PortfolioNotFoundException`이면 이미 없는 것으로 간주하고 계속 진행하며, 그 외 오류면 업로드를 중단하고 오류를 알린다.
6. `UploadPortfolioUseCase` 호출 → 접수(202)되면 `portfolioId`를 받아 `Uploading` 상태로 전환하고 폴링을 시작한다.
7. 폴링(`startPolling`)은 3초 간격으로 `GetPortfolioStatusUseCase`를 호출하며 진행률은 경과 시간 비례로 최대 95%까지만 표시한다. `READY`면 `Completed`, `FAILED_FILE`/`FAILED_SYSTEM`이면 `Failed`로 전환하고 폴링을 끝낸다. 120초(3초 × 40회)를 넘기면 자동으로 `Failed`로 전환한다.
8. 업로드 자체(파일 전송)가 실패하면(`onFailure`) `Failed` 상태로 전환하고 오류를 알린다.

### 4.5 포트폴리오 — 업로드 취소

`ClickUploadCancel`은 접수 전/후를 구분한다(`cancelUpload`):

- **접수 전**(`Uploading.portfolioId == null`): 업로드 코루틴만 취소하고, 임시 파일을 지운 뒤 이전 상태(있으면 `Uploaded`, 없으면 `Empty`)로 되돌린다. 서버 호출은 없다.
- **접수 후**(`portfolioId`가 있음): 삭제 API를 부르기 전에 상태를 한 번 더 조회한다.
  - 조회 결과가 이미 `READY`면 삭제하지 않고 `UploadAlreadyCompleted` 모달을 띄우며 화면은 `Completed`로 표시한다. (취소 요청과 서버 완료가 겹친 경우. 조회~삭제 사이 경합까지는 막지 못한다.)
  - `FAILED_FILE`/`FAILED_SYSTEM`이면 `Failed` 카드로 전환한다.
  - 그 외(`PROCESSING` 등)면 삭제 API를 호출해 접수분을 지우고, 성공하면 이전 상태로 되돌리며 실패하면 상태를 바꾸지 않고 오류를 알린다.
- 상태 조회 자체가 실패하면 제출 상태(`isSubmitting`)만 되돌리고 화면은 그대로 두며 오류를 알린다.

### 4.6 포트폴리오 — 삭제

`ClickPortfolioDelete` → `requestPortfolioDelete()`가 아래 순서로 분기한다:

1. 현재 포트폴리오가 진행 중 면접에 참조되고 있으면(`isInterviewInProgress`) `PortfolioDeleteUnavailable` 모달.
2. 아니면 계정 단위 삭제 가능 여부(`isPortfolioDeleteAvailable`)를 본다. 불가면 같은 `PortfolioDeleteUnavailable` 모달을 재사용한다.
3. 둘 다 아니면 `PortfolioDelete` 확인 모달("삭제되어도 지난 리포트는 남는다" 안내 + 이번 달 남은 삭제 기회 수).
4. 확인(`ConfirmModal`) 시 `DeletePortfolioUseCase` 호출. 성공하면 `portfolioId`/`portfolioState`를 비우고 개요를 재조회한다. 실패하면 모달만 닫고 오류를 알린다(카드는 그대로 유지).

`PortfolioDeleteUnavailable` 모달은 "현재 면접이 진행되고 있어요. 면접이 끝나면 다시 삭제를 시도해주세요." 한 가지 문구로 진행 중 면접·삭제 기회 소진 두 원인을 모두 안내한다.

**포트폴리오 없음 상태에서 삭제 아이콘은 노출되지 않는다.** `portfolioId == null`이면 `deletePortfolio()`가 즉시 모달만 닫고 종료한다.

### 4.7 면접 리포트 목록

`MyPageReportSection`이 `GetInterviewReportListUseCase` 결과를 표시한다.

- 카드 헤더: `{직군} · {연차}년차 면접`, 생성일. `GENERATING`/`FAILED` 상태만 상단 배지("생성 중"/"생성 실패", 실패는 빨간색)를 붙인다.
- 펼치면 직군·연차, 포트폴리오 파일명, JD(`jdUrl`이 없으면 "JD 직접 입력")를 보여준다.
- `FAILED` 상태는 펼침 영역에 "리포트 생성에 실패했어요 · 횟수는 차감되지 않았어요" 안내를 추가로 보여준다.
- `READY`/`INSUFFICIENT_ANALYSIS`만 하단 액션("리포트 보기" 항상, "지인 피드백 받기"는 `isFeedbackAvailable`일 때만)을 보여준다. "리포트 보기"는 `ClickReportView`, "지인 피드백 받기"는 `ClickGuestFeedback`으로 각각 Effect를 발행해 목적지 화면 진입을 요청한다(실제 화면은 범위 밖).
- 목록이 비어 있으면 "내용 없음" 카드를 보여준다.
- 리포트 카드는 항상 당시 `portfolioFileName`만 보여준다. 포트폴리오 삭제 여부에 따른 별도 배지는 없다.

### 4.8 로그아웃

`ClickLogout` → `Logout` 모달 → 확인 시 `LogoutUseCase` 호출.

- `LogoutUseCase`는 서버 Refresh Token 폐기(`AuthRepository.logout()`)가 실패해도 삼키고, 면접 로컬 데이터 정리(`ClearInterviewLocalDataUseCase`)도 실패를 삼킨 뒤, 로컬 인증 세션 삭제(`SessionRepository.clearAuthSession()`)만 성공하면 `LogoutCompleted` Effect를 발행한다.
- 로컬 세션 삭제 자체가 실패하면 오류를 알리고 모달만 닫는다(로그아웃 미완료로 처리).
- 처리 중에는 모달의 두 버튼을 모두 잠근다(`isSubmitting`).

### 4.9 회원 탈퇴

Flow:

1. `ClickWithdrawal` → 진행 중 면접이 있으면(`isInterviewInProgress`) `WithdrawalBlocked` 차단 모달, 없으면 `WithdrawalNotice`(1단계 주의사항) 모달.
2. `WithdrawalNotice`의 확인은 삭제 API를 부르지 않고 `WithdrawalConfirm`(2단계 최종 확인)으로만 전환한다.
3. `WithdrawalConfirm`의 확인에서만 `WithdrawUserUseCase`가 실제로 실행된다. 연속으로 여러 번 눌러도 `isSubmitting` 가드로 요청은 한 번만 나간다.
4. 성공하면 `WithdrawalCompleted` Effect. 실패하면 모달만 닫고 계정·데이터는 그대로 남는다(오류 Toast).
5. 각 단계에서 취소(`CloseModal`)하면 그 단계에서 중단되고 계정은 그대로 남는다.

`WithdrawalNotice` 화면 문구: "탈퇴하면 아래 정보가 모두 삭제되고 복구할 수 없어요 — 포트폴리오, 모든 면접 리포트, 지인 피드백 기록, 프로필, 잔여 면접 횟수." (`MyPageWithdrawalModal`)

`WithdrawUserUseCase`는 서버 탈퇴(`UserRepository.withdraw()`) 성공을 전제로 하며, 이후 면접 로컬 데이터 정리 실패는 삼키고 로컬 인증 세션 삭제만 성공해야 `Unit`을 반환한다. 서버 탈퇴 자체가 실패하면 전체가 실패로 반환되어 계정은 그대로 남는다.

재인증(생체인증·비밀번호 재확인 등)은 요구하지 않는다. 실수 방지는 주의사항 + 최종 확인 2단계 모달이 담당한다.

## 5. Contract 요약

### `MyPageState`

프로필(`profile`, `remainingTicketCount`, `socialAccount`), 포트폴리오(`portfolioId`, `portfolioState`, `isPortfolioReplaceAvailable`, `isPortfolioDeleteAvailable`, `nextPortfolioAvailableAt`, `nextPortfolioDeleteAvailableAt`, `isInterviewInProgress`), 리포트(`reports`, `expandedReportIds`), 화면 상태(`isInitialLoading`, `isSubmitting`, 두 개의 tooltip 플래그, `modalType`)로 구성된다. `var` 없이 전부 `val`이며 `MviState`를 따른다.

### `MyPageModalType`

`PortfolioReupload` · `PortfolioDelete` · `PortfolioDeleteUnavailable`(면접 진행 중 **또는** 삭제 기회 소진 공용) · `PortfolioReuploadUnavailable` · `UploadAlreadyCompleted` · `Logout` · `WithdrawalNotice` · `WithdrawalConfirm` · `WithdrawalBlocked`. 탈퇴 2종은 `MyPageWithdrawalModal`이, 나머지는 `MyPageModal`이 그린다.

### `MyPageEffect`

`CloseRequested`, `PortfolioSelectionRequested`, `ProfileEditRequested`, `ReportViewRequested(reportId)`, `GuestFeedbackRequested(reportId)`, `ShowToast(message)`, `LogoutCompleted`, `WithdrawalCompleted`. 목적지 결정은 모두 app 계층(`myPageEntryBuilder`의 콜백)이 담당하고, 이 모듈은 실제 Navigation 3 route를 조립하지 않는다.

### 공통 오류 처리

`NetworkUnavailableException`/`ServerException`/`UnknownException`은 `GlobalErrorHandler.emit`으로 전역 이벤트로 보내고(`ShowNetworkErrorAndExit`/`ShowServerErrorAndExit`/`ShowUnknownError`), 그 외 오류는 메시지가 있으면 그대로, 없으면 기본 문구로 `ShowToast`를 발행한다. `PortfolioNotFoundException`은 재업로드 전 정리 흐름에서만 예외적으로 성공으로 취급된다(4.4-5).

## 6. 구현 소유권

| 책임 | 소유 모듈 |
|---|---|
| 화면 State, Intent, Effect, 모달 조립 | `feature:mypage:impl` |
| PDF 검증·캐시 복사(`PortfolioFileReader`) | `feature:mypage:impl` (`portfolio/`) |
| 프로필·포트폴리오·리포트 조회, 업로드·삭제 UseCase | `domain` |
| API 호출, DTO, 원격 데이터 소스 | `data` |
| 로그아웃·탈퇴의 면접 로컬 데이터 정리(`ClearInterviewLocalDataUseCase`) 조합 | `domain`의 `LogoutUseCase`/`WithdrawUserUseCase` |
| `MyPage` Navigation 3 key와 entry 조립 | `feature:mypage:api`(`MyPageRoute`), `feature:mypage:impl`(`MyPageEntryBuilder`) |
| 프로필 편집·리포트 상세·지인 피드백·로그아웃 후·탈퇴 후 실제 라우팅 | `app` |

`MyPageViewModel`은 Repository나 API를 직접 호출하지 않고 UseCase만 호출한다.

## 7. 엣지 케이스

| 상황 | 처리 |
|---|---|
| 진행 중 면접이 있는데 포트폴리오 삭제 시도 | `PortfolioDeleteUnavailable` 모달, API 호출 없음 |
| 이번 달 삭제 기회 소진 | 같은 `PortfolioDeleteUnavailable` 모달 |
| 이번 달 교체 기회 소진 후 재업로드 시도 | `PortfolioReuploadUnavailable` 모달, 파일 선택기 열지 않음 |
| 취소 요청과 서버 처리 완료가 동시에 발생 | 삭제하지 않고 `UploadAlreadyCompleted` 모달 + `Completed` 카드로 전환 |
| 실패 레코드가 남은 채 재업로드 | 새 업로드 전 실패 레코드 삭제, 이미 없으면(404) 조용히 통과 |
| 폴링 120초 초과 | 자동으로 `Failed` 전환 |
| 리포트 목록만 조회 실패 | 나머지 섹션은 정상 표시, Toast 1회 |
| 진행 중 면접이 있는데 탈퇴 시도 | `WithdrawalBlocked` 모달, 주의사항 단계로 가지 않음 |
| 탈퇴 최종 확인 연타 | 탈퇴 요청은 1회만 실행(`isSubmitting` 가드) |
| 잔여 이용권이 `null` | 이용권 안내 아이콘을 눌러도 말풍선 미표시 |

## 8. 수용 기준 (현재 테스트로 검증됨)

`MyPageViewModelTest` 기준. 신규 변경 시 이 목록의 시나리오를 회귀 없이 유지해야 한다.

- 초기 상태는 로딩 없음·빈 화면(`profile=null`, `portfolioState=Empty`, `reports=[]`, `modalType=null`)이다.
- `Load` 성공 시 프로필·이용권·소셜 계정·포트폴리오·리포트가 모두 State에 반영된다.
- 포트폴리오 개요의 `nextReplaceAvailableAt`/`nextDeleteAvailableAt`은 각각 별도 필드로 State에 반영된다.
- 리포트 조회만 실패해도 나머지 섹션은 그대로 보이고 Toast는 1회만 발행된다.
- 네트워크 오류는 Toast가 아니라 전역 이벤트(`ShowNetworkErrorAndExit`)로만 전달된다.
- 진입 시 `PROCESSING` 포트폴리오가 있으면 폴링을 이어서 완료까지 수행한다.
- 진입 시 실패 포트폴리오가 있으면 `portfolioId`를 포함한 `Failed` 카드를 보여준다.
- 폴링이 2분(40회 × 3초)을 넘기면 실패로 처리한다.
- 실패한 포트폴리오를 다시 올리면 기존 실패 레코드를 먼저 삭제하고, 대상이 이미 없으면(404) 그대로 업로드를 진행한다.
- 유효하지 않은 PDF는 사유(`PdfInvalidReason`)를 `Failed` 상태에 보존한다.
- 완료 카드에서 닫기를 눌러도 즉시 삭제하지 않고 삭제 확인 모달을 띄운다.
- 접수 전 취소는 삭제 API를 호출하지 않고, 접수 후 취소는 상태를 재확인한 뒤에만 삭제 API를 호출한다.
- 취소 직전 상태가 이미 `READY`면 삭제하지 않고 완료 안내 모달을 띄운다.
- 취소 전 상태 조회가 실패하면 제출 상태를 복구하고 Toast를 발행한다.
- 진행 중 면접이 있으면 삭제 확인 모달 대신 차단 모달을 띄운다.
- 이번 달 교체 기회가 없으면 파일 선택기 대신 차단 모달을 띄운다.
- 최초 업로드는 정책 고지 모달 없이 바로 파일 선택기를 요청한다.
- 로그아웃은 서버 폐기가 실패해도 완료 Effect를 발행하고 로컬 세션을 지운다.
- 삭제가 실패해도 모달은 닫히고 Toast가 발행된다(카드는 유지).
- 탈퇴가 실패해도 모달은 닫히고 계정은 그대로 남는다.
- 탈퇴는 주의사항 모달만으로는 실행되지 않고 최종 확인에서만 실행된다.
- 탈퇴 최종 확인을 연타해도 탈퇴 요청은 1회만 실행된다.
- 진행 중 면접이 있으면 탈퇴 주의사항 대신 차단 모달을 띄운다.
- 잔여 이용권이 없으면(`null`) 안내 말풍선을 표시하지 않는다.
