package com.dminus14.app.feature.mypage

import androidx.compose.runtime.Immutable
import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface MyPageIntent : MviIntent {
    data object Load : MyPageIntent

    data object ClickClose : MyPageIntent

    data object ClickUpload : MyPageIntent

    /** 파일 선택기 결과. ViewModel 테스트 편의를 위해 [Uri]가 아닌 문자열로 받는다. */
    data class SelectPortfolioFile(
        val uri: String,
    ) : MyPageIntent

    data object ClickUploadCancel : MyPageIntent

    data object ClickUploadRetry : MyPageIntent

    data object ClickUploadFailureInfo : MyPageIntent

    data object DismissUploadFailureTooltip : MyPageIntent

    data object ClickPortfolioDelete : MyPageIntent

    data object ClickPortfolioReupload : MyPageIntent

    data object ConfirmModal : MyPageIntent

    data object CloseModal : MyPageIntent

    data class ToggleReport(
        val id: String,
    ) : MyPageIntent

    data object ClickProfileEdit : MyPageIntent

    data object ClickTicketInfo : MyPageIntent

    data object DismissTicketInfoTooltip : MyPageIntent

    data object ClickLogout : MyPageIntent

    data object ClickWithdrawal : MyPageIntent

    data object ClickReportView : MyPageIntent

    data object ClickGuestFeedback : MyPageIntent
}

@Immutable
data class MyPageState(
    val isInitialLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val profile: MyPageProfileUiModel? = null,
    val remainingTicketCount: Int? = null,
    val socialAccount: MyPageSocialAccountUiModel? = null,
    val portfolioId: String? = null,
    val portfolioState: MyPagePortfolioState = MyPagePortfolioState.Empty,
    val isPortfolioReplaceAvailable: Boolean = true,
    val isPortfolioDeleteAvailable: Boolean = true,
    val nextPortfolioAvailableAt: String? = null,
    val isInterviewInProgress: Boolean = false,
    val isUploadFailureTooltipVisible: Boolean = false,
    val isTicketInfoTooltipVisible: Boolean = false,
    val reports: List<MyPageReportUiModel> = emptyList(),
    val expandedReportIds: Set<String> = emptySet(),
    val modalType: MyPageModalType? = null,
) : MviState

@Immutable
data class MyPageProfileUiModel(
    val name: String,
    val role: String,
    val experience: String,
)

@Immutable
data class MyPageSocialAccountUiModel(
    val providerLabel: String,
    val displayAccount: String,
    val isKakaoProvider: Boolean,
)

@Immutable
data class MyPagePortfolioUiModel(
    val fileName: String,
    val uploadedAt: String,
    val fileSize: String,
)

sealed interface MyPagePortfolioState {
    data object Empty : MyPagePortfolioState

    data class Uploaded(
        val portfolio: MyPagePortfolioUiModel,
    ) : MyPagePortfolioState

    /** [portfolioId]가 null이면 접수(202) 전 전송 중, 값이 있으면 접수 완료 후 처리 중이다. */
    data class Uploading(
        val fileName: String,
        val progress: Float = 0f,
        val portfolioId: String? = null,
        val previousPortfolio: MyPagePortfolioUiModel? = null,
    ) : MyPagePortfolioState

    /** 이번 화면에서 방금 업로드를 끝낸 경우에만 노출한다. 재진입하면 [Uploaded]로 바뀐다. */
    data class Completed(
        val fileName: String,
    ) : MyPagePortfolioState

    /** [portfolioId]가 있으면 서버에 실패 레코드가 남아 있어 재업로드 전에 먼저 삭제해야 한다. */
    data class Failed(
        val fileName: String,
        val portfolioId: String? = null,
    ) : MyPagePortfolioState
}

enum class MyPageReportStatus {
    GENERATING,
    READY,
    INSUFFICIENT_ANALYSIS,
    FAILED,
    UNKNOWN,
}

enum class MyPageJobRole(
    private val displayName: String,
) {
    BACKEND("백엔드"),
    FRONTEND("프론트엔드"),
    IOS("iOS"),
    ANDROID("Android"),
    DATA_ENGINEER("데이터"),
    INFRA_SRE("인프라"),
    ;

    override fun toString(): String = displayName

    companion object {
        fun fromRaw(rawJobRole: String?): MyPageJobRole? =
            entries.firstOrNull { jobRole -> jobRole.name == rawJobRole }
    }
}

@Immutable
data class MyPageReportUiModel(
    val id: String,
    val jobRole: MyPageJobRole?,
    val jobRoleLabel: String,
    val experienceYears: Int?,
    val createdAt: String,
    val status: MyPageReportStatus,
    val portfolioFileName: String,
    val jobDescription: String,
    val isFeedbackAvailable: Boolean,
) {
    val jobRoleAndExperience: String
        get() = if (experienceYears != null) "$jobRoleLabel · ${experienceYears}년" else jobRoleLabel
}

sealed interface MyPageEffect : MviEffect {
    data object CloseRequested : MyPageEffect

    data object PortfolioSelectionRequested : MyPageEffect

    data object ProfileEditRequested : MyPageEffect

    data object ReportViewRequested : MyPageEffect

    data object GuestFeedbackRequested : MyPageEffect

    data class ShowToast(
        val message: String,
    ) : MyPageEffect

    data object LogoutCompleted : MyPageEffect

    data object WithdrawalCompleted : MyPageEffect
}

enum class MyPageModalType {
    PortfolioReupload,
    PortfolioDelete,

    /** 진행 중인 면접이 현재 포트폴리오를 참조할 때 표시한다. */
    PortfolioDeleteUnavailable,

    /** 이번 달 포트폴리오 교체 기회를 모두 사용했을 때 표시한다. */
    PortfolioReuploadUnavailable,

    /** 접수 취소 시도 중 서버 처리가 먼저 끝난 경우 표시한다. */
    UploadAlreadyCompleted,

    Logout,

    /** 회원 탈퇴 1단계 주의사항 안내. */
    WithdrawalNotice,

    /** 회원 탈퇴 2단계 최종 확인. */
    WithdrawalConfirm,

    /** 진행 중인 면접이 있어 탈퇴를 시작할 수 없을 때 표시한다. */
    WithdrawalBlocked,
}
