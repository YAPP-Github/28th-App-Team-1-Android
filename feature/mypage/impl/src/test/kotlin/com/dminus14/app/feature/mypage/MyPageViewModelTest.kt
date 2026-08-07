package com.dminus14.app.feature.mypage

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MyPageViewModelTest {
    @Test
    fun `초기 상태는 사용자 데이터가 비어 있고 리포트가 모두 접혀 있다`() {
        val state = MyPageViewModel().state.value

        assertEquals(null, state.profile)
        assertEquals(null, state.remainingTicketCount)
        assertEquals(null, state.socialAccount)
        assertEquals(MyPagePortfolioState.Empty, state.portfolioState)
        assertTrue(state.reports.isEmpty())
        assertTrue(state.expandedReportIds.isEmpty())
    }

    @Test
    fun `기존 포트폴리오가 없는 업로드를 중단하면 빈 상태로 돌아간다`() {
        val viewModel =
            MyPageViewModel(
                MyPageState(portfolioState = MyPagePortfolioState.Uploading("샘플 포트폴리오.pdf")),
            )

        viewModel.onIntent(MyPageIntent.ClickUploadCancel)

        assertEquals(MyPagePortfolioState.Empty, viewModel.state.value.portfolioState)
    }

    @Test
    fun `기존 포트폴리오 교체 업로드를 중단하면 기존 포트폴리오로 돌아간다`() {
        val previous = MyPagePortfolioUiModel("샘플 포트폴리오.pdf", "2026.08.03", "1.2MB")
        val viewModel =
            MyPageViewModel(
                MyPageState(
                    portfolioState =
                        MyPagePortfolioState.Uploading(
                            fileName = "교체 파일.pdf",
                            previousPortfolio = previous,
                        ),
                ),
            )

        viewModel.onIntent(MyPageIntent.ClickUploadCancel)

        assertEquals(MyPagePortfolioState.Uploaded(previous), viewModel.state.value.portfolioState)
    }

    @Test
    fun `업로드 실패 정보 아이콘은 툴팁 표시 상태를 토글하고 닫기 요청은 숨긴다`() {
        val viewModel =
            MyPageViewModel(
                MyPageState(portfolioState = MyPagePortfolioState.Failed("샘플 포트폴리오.pdf")),
            )

        viewModel.onIntent(MyPageIntent.ClickUploadFailureInfo)
        assertTrue(viewModel.state.value.isUploadFailureTooltipVisible)

        viewModel.onIntent(MyPageIntent.DismissUploadFailureTooltip)
        assertFalse(viewModel.state.value.isUploadFailureTooltipVisible)
    }

    @Test
    fun `재업로드 삭제 로그아웃은 기능 내부 모달 효과를 발행한다`() =
        runTest {
            val cases =
                listOf(
                    MyPageIntent.ClickPortfolioReupload to MyPageModalType.PortfolioReupload,
                    MyPageIntent.ClickPortfolioDelete to MyPageModalType.PortfolioDelete,
                    MyPageIntent.ClickLogout to MyPageModalType.Logout,
                )

            cases.forEach { (intent, expected) ->
                val viewModel = MyPageViewModel()
                val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effect.first() }
                viewModel.onIntent(intent)
                assertEquals(MyPageEffect.ShowModal(expected), effect.await())
            }
        }

    @Test
    fun `후속 화면 동작은 실제 이동 없이 효과까지만 발행한다`() =
        runTest {
            val cases =
                listOf(
                    MyPageIntent.ClickProfileEdit to MyPageEffect.ProfileEditRequested,
                    MyPageIntent.ClickReportView to MyPageEffect.ReportViewRequested,
                    MyPageIntent.ClickGuestFeedback to MyPageEffect.GuestFeedbackRequested,
                    MyPageIntent.ClickWithdrawal to MyPageEffect.WithdrawalRequested,
                )

            cases.forEach { (intent, expected) ->
                val viewModel = MyPageViewModel()
                val effect = async(start = CoroutineStart.UNDISPATCHED) { viewModel.effect.first() }
                viewModel.onIntent(intent)
                assertEquals(expected, effect.await())
            }
        }
}
