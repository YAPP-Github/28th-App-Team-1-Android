package com.dminus14.app.feature.mypage

import com.dminus14.app.core.common.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel internal constructor(
    initialState: MyPageState,
) : MviViewModel<MyPageIntent, MyPageState, MyPageEffect>(initialState) {
    @Inject
    constructor() : this(MyPageState())

    @Suppress("CyclomaticComplexMethod")
    override fun onIntent(intent: MyPageIntent) {
        when (intent) {
            MyPageIntent.CloseClicked -> {
                sendEffect(MyPageEffect.CloseRequested)
            }

            MyPageIntent.UploadClicked,
            MyPageIntent.UploadRetryClicked,
            -> {
                sendEffect(MyPageEffect.PortfolioSelectionRequested)
            }

            MyPageIntent.UploadCancelClicked -> {
                cancelUpload()
            }

            MyPageIntent.UploadFailureInfoClicked -> {
                toggleUploadFailureTooltip()
            }

            MyPageIntent.UploadFailureTooltipDismissed -> {
                dismissUploadFailureTooltip()
            }

            MyPageIntent.PortfolioDeleteClicked -> {
                showModal(MyPageModalType.PortfolioDelete)
            }

            MyPageIntent.PortfolioReuploadClicked -> {
                showModal(
                    MyPageModalType.PortfolioReupload,
                )
            }

            MyPageIntent.LogoutClicked -> {
                showModal(MyPageModalType.Logout)
            }

            MyPageIntent.ModalClosed,
            MyPageIntent.TicketInfoClicked,
            -> {
                Unit
            }

            is MyPageIntent.ReportToggleClicked -> {
                toggleReport(intent.id)
            }

            MyPageIntent.ProfileEditClicked -> {
                sendEffect(MyPageEffect.ProfileEditRequested)
            }

            MyPageIntent.ReportViewClicked -> {
                sendEffect(MyPageEffect.ReportViewRequested)
            }

            MyPageIntent.GuestFeedbackClicked -> {
                sendEffect(MyPageEffect.GuestFeedbackRequested)
            }

            MyPageIntent.WithdrawalClicked -> {
                sendEffect(MyPageEffect.WithdrawalRequested)
            }
        }
    }

    private fun cancelUpload() {
        val uploading = state.value.portfolioState as? MyPagePortfolioState.Uploading ?: return
        reduce {
            copy(
                portfolioState =
                    uploading.previousPortfolio
                        ?.let(MyPagePortfolioState::Uploaded)
                        ?: MyPagePortfolioState.Empty,
            )
        }
    }

    private fun toggleUploadFailureTooltip() {
        if (state.value.portfolioState !is MyPagePortfolioState.Failed) return
        reduce { copy(isUploadFailureTooltipVisible = !isUploadFailureTooltipVisible) }
    }

    private fun dismissUploadFailureTooltip() {
        if (!state.value.isUploadFailureTooltipVisible) return
        reduce { copy(isUploadFailureTooltipVisible = false) }
    }

    private fun toggleReport(id: String) {
        if (state.value.reports.none { it.id == id }) return
        reduce {
            copy(
                expandedReportIds =
                    if (id in expandedReportIds) {
                        expandedReportIds - id
                    } else {
                        expandedReportIds + id
                    },
            )
        }
    }

    private fun showModal(type: MyPageModalType) {
        sendEffect(MyPageEffect.ShowModal(type))
    }
}
