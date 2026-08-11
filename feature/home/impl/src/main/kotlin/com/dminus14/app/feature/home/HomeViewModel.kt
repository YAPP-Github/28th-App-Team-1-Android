package com.dminus14.app.feature.home

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.model.InterviewResumeState
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.GetInterviewProgressUseCase
import com.dminus14.app.domain.usecase.GetInterviewReportListUseCase
import com.dminus14.app.domain.usecase.GetInterviewResumeUseCase
import com.dminus14.app.feature.home.mapper.toHomeReportItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions")
class HomeViewModel
    @Inject
    constructor(
        private val checkUserProfileUseCase: CheckUserProfileUseCase,
        private val getInterviewReportListUseCase: GetInterviewReportListUseCase,
        private val getInterviewResumeUseCase: GetInterviewResumeUseCase,
        private val getInterviewProgressUseCase: GetInterviewProgressUseCase,
    ) : MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {
        override fun onIntent(intent: HomeIntent) {
            when (intent) {
                HomeIntent.Load -> {
                    load()
                }

                HomeIntent.ClickMyPage -> {
                    sendEffect(HomeEffect.GoToMyPageRequested)
                }

                is HomeIntent.ClickReportExpand -> {
                    reduce {
                        copy(
                            expandedReportIds =
                                if (intent.reportId in expandedReportIds) {
                                    expandedReportIds - intent.reportId
                                } else {
                                    expandedReportIds + intent.reportId
                                },
                        )
                    }
                }

                is HomeIntent.ClickReportOpen -> {
                    sendEffect(HomeEffect.GoToReportRequested(intent.reportId))
                }

                HomeIntent.ReportSheetCollapsed -> {
                    viewModelScope.launch { checkInterviewSession() }
                }

                HomeIntent.ClickSessionStart -> {
                    startInterview()
                }

                HomeIntent.ClickSessionOverlayDismiss -> {
                    dismissSessionOverlay()
                }

                HomeIntent.ClickSessionResume -> {
                    Unit // 후속 구현: 진행 중 면접 이어서 진행 플로우 연동
                }
            }
        }

        /**
         * 면접 시작 계열 버튼 처리.
         * 잔여 이용권이 1회 이상이면 온보딩 인터뷰로 이동하고, 0회(또는 미확인)면 소진(NoTickets)
         * 오버레이를 띄운다.
         */
        private fun startInterview() {
            val tickets = state.value.remainingTicketCount ?: 0
            if (tickets > 0) {
                sendEffect(HomeEffect.GoToOnboardingInterviewRequested)
            } else {
                reduce {
                    copy(
                        sessionStartOverlay =
                            HomeSessionStartOverlayState.NoTickets(
                                userName = userName,
                            ),
                    )
                }
            }
        }

        /** 세션 오버레이를 닫고 리포트 시트를 중간(Peek)으로 되돌린다. */
        private fun dismissSessionOverlay() {
            reduce { copy(sessionStartOverlay = null) }
            sendEffect(HomeEffect.ReportSheetResetRequested)
        }

        /**
         * 홈 진입 시 유저 프로필을 먼저 조회하고, 홈에 머무는 경우에만 면접 리포트 목록을
         * 이어서 조회한다.
         *
         * 프로필 조회 결과로 라우팅이 결정되므로(온보딩·스플래시로 이동) 홈에 남는 경우에만
         * 리포트를 조회하면 되고, 이 경우 두 호출을 병렬로 돌릴 이점이 없어 순차로 호출한다.
         *
         * 진행 중 세션 확인([checkInterviewSession])은 로드 시점이 아니라 리포트 시트를
         * 하단까지 내렸을 때([HomeIntent.ReportSheetCollapsed]) 수행한다.
         */
        private fun load() {
            reduce { copy(isLoading = true) }
            viewModelScope.launch {
                if (!loadProfile()) return@launch
                loadReports()
            }
        }

        /**
         * 프로필을 조회해 인사말 이름을 채운다.
         *
         * 이름이 비어 있으면 필수 프로필 미완성으로 보고 온보딩(직무·연차 입력)으로 라우팅한다.
         * `UserNotFoundException`은 세션은 있으나 서버에 유저가 없어 재부팅이 필요한 상태이므로
         * 스플래시로 되돌린다.
         *
         * @return 홈에 머물러 이후 로드를 계속해야 하면 `true`, 라우팅·오류로 중단해야 하면 `false`.
         */
        private suspend fun loadProfile(): Boolean {
            val profile =
                checkUserProfileUseCase().getOrElse { error ->
                    when (error) {
                        is UserNotFoundException -> {
                            reduce { copy(isLoading = false) }
                            sendEffect(HomeEffect.UserNotFound)
                        }

                        else -> {
                            handleBootstrapFailure(error)
                        }
                    }
                    return false
                }

            val displayName = profile.name?.takeIf { it.isNotBlank() }
            val shouldContinue = displayName != null
            if (!shouldContinue) {
                reduce { copy(isLoading = false) }
                sendEffect(HomeEffect.UserNameNotRegistered)
            } else {
                reduce {
                    copy(
                        userName = displayName,
                        remainingTicketCount = profile.remainingTicketCount,
                    )
                }
            }
            return shouldContinue
        }

        /**
         * 면접 리포트 목록을 조회해 상태에 반영한다.
         *
         * 리포트 조회 실패는 치명적이지 않아 빈 목록(빈 상태 UI)으로 처리한다.
         * 공통 에러 처리 도입 시 재검토 예정.
         */
        private suspend fun loadReports() {
            val reports =
                getInterviewReportListUseCase()
                    .getOrNull()
                    ?.reports
                    ?.map { it.toHomeReportItem() }
                    .orEmpty()
            reduce {
                copy(
                    isLoading = false,
                    reports = reports,
                    // 첫 리포트만 기본으로 펼치고, 리포트가 없으면 빈 집합.
                    expandedReportIds = reports.firstOrNull()?.let { setOf(it.id) }.orEmpty(),
                )
            }
        }

        private suspend fun checkInterviewSession() {
            // 로컬에 저장된 진행 중 면접이 있으면 그 sessionId로 재개 가능 여부를 조회하고,
            // 없으면 잔여 이용권 기준의 시작 오버레이를 띄운다.
            val interviewSessionId = getInterviewProgressUseCase()?.sessionId

            if (interviewSessionId != null) {
                getInterviewState(interviewSessionId)
            } else {
                showSessionStartOverlayByTicket()
            }
        }

        /**
         * 존재하는 세션 아이디가 이어서 진행 가능한 상태인지 조회한다.
         *
         * - [InterviewResumeState.Resumable]: 진행중(InProgress) 오버레이를 띄운다.
         *   resume 응답에 남은 질문 수 필드가 없어 [TEMP_REMAINING_QUESTION_COUNT] 임시값을 사용한다.
         * - [InterviewResumeState.Ended]: 잔여 이용권에 따라 시작(Start)/소진(NoTickets) 분기.
         * - [InterviewResumeState.Unknown]: 오버레이를 띄우지 않는다.
         */
        internal suspend fun getInterviewState(sessionId: Long) {
            getInterviewResumeUseCase(sessionId)
                .onSuccess { resume ->
                    when (resume.resumeState) {
                        is InterviewResumeState.Resumable -> showResumableOverlay()
                        is InterviewResumeState.Ended -> showSessionStartOverlayByTicket()
                        is InterviewResumeState.Unknown -> Unit
                    }
                }.onFailure { error ->
                    handleBootstrapFailure(error)
                }
        }

        /** 재개 가능한 세션이 있을 때 진행중 오버레이를 띄운다. */
        private fun showResumableOverlay() {
            reduce {
                copy(
                    sessionStartOverlay =
                        HomeSessionStartOverlayState.InProgress(
                            userName = userName,
                            remainingQuestionCount = TEMP_REMAINING_QUESTION_COUNT,
                        ),
                )
            }
        }

        /**
         * 종료된 세션일 때 잔여 이용권에 따라 세션 시작 오버레이를 분기한다.
         * 1회 이상이면 시작(Start), 0회(또는 미확인)면 소진(NoTickets).
         */
        private fun showSessionStartOverlayByTicket() {
            reduce {
                val tickets = remainingTicketCount ?: 0
                copy(
                    sessionStartOverlay =
                        if (tickets > 0) {
                            HomeSessionStartOverlayState.Start(
                                userName = userName,
                                remainingTicketCount = tickets,
                            )
                        } else {
                            HomeSessionStartOverlayState.NoTickets(userName = userName)
                        },
                )
            }
        }

        // 아래 에러 처리 사항은 임시입니다. 공통 처리 기획자 문의 모든 ViewModel 일괄 수정 예정
        private suspend fun handleBootstrapFailure(error: Throwable) {
            reduce { copy(isLoading = false) }
            when {
                error is NetworkUnavailableException -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowNetworkErrorAndExit)
                }

                error is ServerException -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowServerErrorAndExit)
                }

                else -> {
                    GlobalErrorHandler.emit(GlobalAppEvent.ShowUnknownError)
                }
            }
        }
    }

/** resume 응답에 남은 질문 수가 없어 사용하는 임시값.
 * 이후 클라이언트에서 동영상 시간 체크해서 임시로 부여할 것이므로, 필드 확보 후 제거 예정. */
private const val TEMP_REMAINING_QUESTION_COUNT = 2
