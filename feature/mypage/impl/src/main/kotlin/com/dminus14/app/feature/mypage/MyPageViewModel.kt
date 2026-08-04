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
            MyPageIntent.ClickClose -> {
                sendEffect(MyPageEffect.CloseRequested)
            }

            MyPageIntent.ClickUpload,
            MyPageIntent.ClickUploadRetry,
            -> {
                sendEffect(MyPageEffect.PortfolioSelectionRequested)
            }

            MyPageIntent.ClickUploadCancel -> {
                cancelUpload()
            }

            MyPageIntent.ClickUploadFailureInfo -> {
                toggleUploadFailureTooltip()
            }

            MyPageIntent.DismissUploadFailureTooltip -> {
                dismissUploadFailureTooltip()
            }

            MyPageIntent.ClickPortfolioDelete -> {
                showModal(MyPageModalType.PortfolioDelete)
            }

            MyPageIntent.ClickPortfolioReupload -> {
                showModal(
                    MyPageModalType.PortfolioReupload,
                )
            }

            MyPageIntent.ClickLogout -> {
                showModal(MyPageModalType.Logout)
            }

            MyPageIntent.CloseModal,
            MyPageIntent.ClickTicketInfo,
            -> {
                Unit
            }

            is MyPageIntent.ToggleReport -> {
                toggleReport(intent.id)
            }

            MyPageIntent.ClickProfileEdit -> {
                sendEffect(MyPageEffect.ProfileEditRequested)
            }

            MyPageIntent.ClickReportView -> {
                sendEffect(MyPageEffect.ReportViewRequested)
            }

            MyPageIntent.ClickGuestFeedback -> {
                sendEffect(MyPageEffect.GuestFeedbackRequested)
            }

            MyPageIntent.ClickWithdrawal -> {
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
