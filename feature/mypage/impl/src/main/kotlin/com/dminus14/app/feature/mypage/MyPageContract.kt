package com.dminus14.app.feature.mypage

import androidx.compose.runtime.Immutable
import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface MyPageIntent : MviIntent {
    data object ClickClose : MyPageIntent

    data object ClickUpload : MyPageIntent

    data object ClickUploadCancel : MyPageIntent

    data object ClickUploadRetry : MyPageIntent

    data object ClickUploadFailureInfo : MyPageIntent

    data object DismissUploadFailureTooltip : MyPageIntent

    data object ClickPortfolioDelete : MyPageIntent

    data object ClickPortfolioReupload : MyPageIntent

    data object CloseModal : MyPageIntent

    data class ToggleReport(
        val id: String,
    ) : MyPageIntent

    data object ClickProfileEdit : MyPageIntent

    data object ClickTicketInfo : MyPageIntent

    data object ClickLogout : MyPageIntent

    data object ClickWithdrawal : MyPageIntent

    data object ClickReportView : MyPageIntent

    data object ClickGuestFeedback : MyPageIntent
}

@Immutable
data class MyPageState(
    val profile: MyPageProfileUiModel? = null,
    val remainingTicketCount: Int? = null,
    val socialAccount: MyPageSocialAccountUiModel? = null,
    val portfolioState: MyPagePortfolioState = MyPagePortfolioState.Empty,
    val isUploadFailureTooltipVisible: Boolean = false,
    val reports: List<MyPageReportUiModel> = emptyList(),
    val expandedReportIds: Set<String> = emptySet(),
) : MviState

@Immutable
data class MyPageProfileUiModel(
    val name: String,
    val role: String,
    val experience: String,
)

@Immutable
data class MyPageSocialAccountUiModel(
    val provider: String,
    val displayAccount: String,
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

    data class Uploading(
        val fileName: String,
        val progress: Float = 0f,
        val previousPortfolio: MyPagePortfolioUiModel? = null,
    ) : MyPagePortfolioState

    data class Completed(
        val fileName: String,
    ) : MyPagePortfolioState

    data class Failed(
        val fileName: String,
    ) : MyPagePortfolioState
}

enum class MyPageReportStatus {
    Completed,
    Failed,
}

enum class MyPageJobRole(
    private val displayName: String,
) {
    Backend("백엔드"),
    Frontend("프론트엔드"),
    Android("Android"),
    Ios("iOS"),
    Data("데이터"),
    Ai("AI"),
    ;

    override fun toString(): String = displayName
}

@Immutable
data class MyPageReportUiModel(
    val id: String,
    val jobRole: MyPageJobRole,
    val experienceYears: Int,
    val createdAt: String,
    val status: MyPageReportStatus,
    val portfolioFileName: String,
    val jobDescription: String,
) {
    val jobRoleAndExperience: String
        get() = "$jobRole · ${experienceYears}년"
}

sealed interface MyPageEffect : MviEffect {
    data object CloseRequested : MyPageEffect

    data object PortfolioSelectionRequested : MyPageEffect

    data object ProfileEditRequested : MyPageEffect

    data object ReportViewRequested : MyPageEffect

    data object GuestFeedbackRequested : MyPageEffect

    data object WithdrawalRequested : MyPageEffect

    data class ShowModal(
        val type: MyPageModalType,
    ) : MyPageEffect
}

enum class MyPageModalType {
    PortfolioReupload,
    PortfolioDelete,

    /** 진행 중인 면접이 현재 포트폴리오를 참조할 때 표시한다. */
    PortfolioDeleteUnavailable,

    /** 이번 달 포트폴리오 교체 기회를 모두 사용했을 때 표시한다. */
    PortfolioReuploadUnavailable,

    Logout,
}
