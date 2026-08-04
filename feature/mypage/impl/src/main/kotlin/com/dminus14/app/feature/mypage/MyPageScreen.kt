@file:Suppress("ktlint:standard:filename")

package com.dminus14.app.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.feature.mypage.component.MyPageModal
import com.dminus14.app.feature.mypage.component.MyPagePortfolioSection
import com.dminus14.app.feature.mypage.component.MyPageProfileSection
import com.dminus14.app.feature.mypage.component.MyPageReportSection
import com.dminus14.app.feature.mypage.component.MyPageTopBar
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun MyPageScreen(
    onClose: () -> Unit,
    onPortfolioSelectionRequested: () -> Unit,
    progress: Float,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var modalType by rememberSaveable { mutableStateOf<MyPageModalType?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MyPageEffect.CloseRequested -> onClose()

                MyPageEffect.PortfolioSelectionRequested -> onPortfolioSelectionRequested()

                MyPageEffect.ProfileEditRequested,
                MyPageEffect.ReportViewRequested,
                MyPageEffect.GuestFeedbackRequested,
                MyPageEffect.WithdrawalRequested,
                -> Unit

                is MyPageEffect.ShowModal -> modalType = effect.type
            }
        }
    }

    MyPageContent(
        state = state,
        progress = progress,
        modalType = modalType,
        onIntent = { intent ->
            if (intent == MyPageIntent.ModalClosed) {
                modalType = null
            }
            viewModel.onIntent(intent)
        },
        modifier = modifier,
    )
}

@Composable
@Suppress("LongMethod")
fun MyPageContent(
    state: MyPageState,
    progress: Float,
    modalType: MyPageModalType?,
    onIntent: (MyPageIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().background(HilitTheme.colors.hilitWhite),
    ) {
        MyPageTopBar(onCloseClick = { onIntent(MyPageIntent.CloseClicked) })
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(HilitTheme.colors.gray50)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            MyPageProfileSection(
                profile = state.profile,
                remainingTicketCount = state.remainingTicketCount,
                socialAccount = state.socialAccount,
                onProfileEditClick = { onIntent(MyPageIntent.ProfileEditClicked) },
                onTicketInfoClick = { onIntent(MyPageIntent.TicketInfoClicked) },
                onLogoutClick = { onIntent(MyPageIntent.LogoutClicked) },
            )
            MyPagePortfolioSection(
                portfolioState = state.portfolioState,
                progress = progress,
                isUploadFailureTooltipVisible = state.isUploadFailureTooltipVisible,
                onUploadClick = { onIntent(MyPageIntent.UploadClicked) },
                onUploadCancelClick = { onIntent(MyPageIntent.UploadCancelClicked) },
                onUploadRetryClick = { onIntent(MyPageIntent.UploadRetryClicked) },
                onUploadFailureInfoClick = { onIntent(MyPageIntent.UploadFailureInfoClicked) },
                onUploadFailureTooltipDismiss = {
                    onIntent(
                        MyPageIntent.UploadFailureTooltipDismissed,
                    )
                },
                onPortfolioDeleteClick = { onIntent(MyPageIntent.PortfolioDeleteClicked) },
                onPortfolioReuploadClick = { onIntent(MyPageIntent.PortfolioReuploadClicked) },
            )
            MyPageReportSection(
                reports = state.reports,
                expandedReportIds = state.expandedReportIds,
                onReportToggleClick = { onIntent(MyPageIntent.ReportToggleClicked(it)) },
                onReportViewClick = { onIntent(MyPageIntent.ReportViewClicked) },
                onGuestFeedbackClick = { onIntent(MyPageIntent.GuestFeedbackClicked) },
            )
            Spacer(modifier = Modifier.height(176.dp))
            Text(
                text = "회원탈퇴",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Button) { onIntent(MyPageIntent.WithdrawalClicked) }
                        .padding(vertical = 16.dp),
                style = HilitTheme.typography.body6,
                color = HilitTheme.colors.gray500,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }

    modalType?.let { type ->
        MyPageModal(type = type, onConfirm = {}, onClose = { onIntent(MyPageIntent.ModalClosed) })
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 900)
@Composable
private fun MyPageContentPreview() {
    HilitTheme {
        MyPageContent(
            state = previewState(),
            progress = 100f,
            modalType = null,
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 900)
@Composable
private fun MyPageEmptyPreview() {
    HilitTheme {
        MyPageContent(
            state = previewState().copy(portfolioState = MyPagePortfolioState.Empty),
            progress = 0f,
            modalType = null,
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 900)
@Composable
private fun MyPageUploadingPreview() {
    HilitTheme {
        MyPageContent(
            state =
                previewState().copy(
                    portfolioState = MyPagePortfolioState.Uploading("샘플 포트폴리오.pdf"),
                ),
            progress = 50f,
            modalType = null,
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 900)
@Composable
private fun MyPageUploadCompletedPreview() {
    HilitTheme {
        MyPageContent(
            state =
                previewState().copy(
                    portfolioState = MyPagePortfolioState.Completed("샘플 포트폴리오.pdf"),
                ),
            progress = 100f,
            modalType = null,
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 900)
@Composable
private fun MyPageUploadFailedPreview() {
    HilitTheme {
        MyPageContent(
            state =
                previewState().copy(
                    portfolioState = MyPagePortfolioState.Failed("샘플 포트폴리오.pdf"),
                    isUploadFailureTooltipVisible = true,
                ),
            progress = 0f,
            modalType = null,
            onIntent = {},
        )
    }
}

private fun previewState() =
    MyPageState(
        profile = MyPageProfileUiModel("샘플 사용자", "UX/UI 디자이너", "3년차"),
        remainingTicketCount = 2,
        socialAccount = MyPageSocialAccountUiModel("카카오", "sample****@kakao.com"),
        portfolioState =
            MyPagePortfolioState.Uploaded(
                MyPagePortfolioUiModel("샘플 포트폴리오.pdf", "2026.08.03", "1.2MB"),
            ),
        reports =
            listOf(
                MyPageReportUiModel(
                    id = "synthetic-complete",
                    jobRole = MyPageJobRole.Android,
                    experienceYears = 3,
                    createdAt = "2026.08.03",
                    status = MyPageReportStatus.Completed,
                    portfolioFileName = "샘플 포트폴리오.pdf",
                    jobDescription = "careers.example.com/jobs/1024",
                ),
                MyPageReportUiModel(
                    id = "synthetic-deleted",
                    jobRole = MyPageJobRole.Ios,
                    experienceYears = 2,
                    createdAt = "2026.07.14",
                    status = MyPageReportStatus.Completed,
                    portfolioFileName = "이전 샘플 포트폴리오.pdf",
                    jobDescription = "JD 직접 입력",
                ),
                MyPageReportUiModel(
                    id = "synthetic-failed",
                    jobRole = MyPageJobRole.Backend,
                    experienceYears = 5,
                    createdAt = "2026.06.28",
                    status = MyPageReportStatus.Failed,
                    portfolioFileName = "sample_backend_portfolio.pdf",
                    jobDescription = "careers.example.com/jobs/2048",
                ),
            ),
        expandedReportIds = setOf("synthetic-complete", "synthetic-failed"),
    )
