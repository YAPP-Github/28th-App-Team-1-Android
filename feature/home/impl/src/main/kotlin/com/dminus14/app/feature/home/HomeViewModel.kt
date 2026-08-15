package com.dminus14.app.feature.home

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.InterviewSessionAlreadyEndedException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.model.InterviewAbandonRequestCause
import com.dminus14.app.domain.model.InterviewResumeState
import com.dminus14.app.domain.time.InterviewTimeCalculator
import com.dminus14.app.domain.usecase.AbandonInterviewUseCase
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.GetInterviewElapsedTimeUseCase
import com.dminus14.app.domain.usecase.GetInterviewProgressUseCase
import com.dminus14.app.domain.usecase.GetInterviewReportListUseCase
import com.dminus14.app.domain.usecase.GetInterviewResumeUseCase
import com.dminus14.app.domain.usecase.RetainInterviewSessionForCleanupUseCase
import com.dminus14.app.feature.home.mapper.toHomeReportItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions", "LongParameterList")
class HomeViewModel
    @Inject
    constructor(
        private val checkUserProfileUseCase: CheckUserProfileUseCase,
        private val getInterviewReportListUseCase: GetInterviewReportListUseCase,
        private val getInterviewResumeUseCase: GetInterviewResumeUseCase,
        private val getInterviewProgressUseCase: GetInterviewProgressUseCase,
        private val getInterviewElapsedTimeUseCase: GetInterviewElapsedTimeUseCase,
        private val abandonInterviewUseCase: AbandonInterviewUseCase,
        private val retainInterviewSessionForCleanupUseCase:
            RetainInterviewSessionForCleanupUseCase,
    ) : MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {
        /** [ReportSheetCollapsed]가 마지막으로 조회한 진행 중 세션 id. 재개·재시작 확정에 쓴다. */
        private var pendingInterviewSessionId: Long? = null

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
                    onClickSessionStart()
                }

                HomeIntent.ClickSessionOverlayDismiss -> {
                    dismissSessionOverlay()
                }

                HomeIntent.ClickSessionResume -> {
                    sendEffect(HomeEffect.GoToInterviewRequested)
                    dismissSessionOverlay()
                }
            }
        }

        /**
         * [HomeIntent.ClickSessionStart]를 현재 오버레이 상태에 따라 분기한다.
         * `InProgress`의 "처음부터 시작"은 곧장 시작하지 않고 재확인 오버레이로 넘어가고,
         * `ConfirmRestart`의 최종 확정에서만 실제로 기존 세션을 중단한다.
         */
        private fun onClickSessionStart() {
            when (state.value.sessionStartOverlay) {
                is HomeSessionStartOverlayState.InProgress -> {
                    reduce {
                        copy(
                            sessionStartOverlay = HomeSessionStartOverlayState.ConfirmRestart,
                        )
                    }
                }

                HomeSessionStartOverlayState.ConfirmRestart -> {
                    confirmRestart()
                }

                else -> {
                    startInterview()
                }
            }
        }

        /**
         * 진행 중이던 면접을 중단(abandon)한 뒤 새 온보딩 인터뷰로 이동한다.
         * 이미 서버에서 종료된 세션([InterviewSessionAlreadyEndedException])은 중복 성공으로 본다.
         */
        private fun confirmRestart() {
            val sessionId = pendingInterviewSessionId ?: return
            if (state.value.isLoading) return
            reduce { copy(isLoading = true) }
            viewModelScope.launch {
                val error =
                    abandonInterviewUseCase(sessionId, InterviewAbandonRequestCause.UserExit)
                        .exceptionOrNull()
                if (error == null || error is InterviewSessionAlreadyEndedException) {
                    retainInterviewSessionForCleanupUseCase(sessionId)
                    reduce { copy(isLoading = false, sessionStartOverlay = null) }
                    sendEffect(HomeEffect.GoToOnboardingInterviewRequested)
                } else {
                    handleBootstrapFailure(error)
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
                dismissSessionOverlay()
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
                // 리포트 상세 화면에 갔다가 홈으로 돌아오면 HomeScreen 의 LaunchedEffect(Unit) 이
                // 다시 실행돼 Load 가 재발행되고 이 함수도 다시 탄다. 최초 로드(this.reports 가
                // 비어 있음)에서만 첫 리포트를 기본으로 펼치고, 재조회부터는 사용자가 펼치고 접은
                // 상태를 그대로 유지한다(삭제되어 사라진 리포트 id만 걸러낸다).
                val newReportIds = reports.mapTo(mutableSetOf()) { it.id }
                copy(
                    isLoading = false,
                    reports = reports,
                    expandedReportIds =
                        if (this.reports.isEmpty()) {
                            reports.firstOrNull()?.let { setOf(it.id) }.orEmpty()
                        } else {
                            expandedReportIds intersect newReportIds
                        },
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
         *   resume 응답에 남은 질문 수 필드가 없어 [showResumableOverlay]가 로컬 타이머 기준
         *   규칙으로 환산한다.
         * - [InterviewResumeState.Ended]: 잔여 이용권에 따라 시작(Start)/소진(NoTickets) 분기.
         * - [InterviewResumeState.Unknown]: 오버레이를 띄우지 않는다.
         */
        internal suspend fun getInterviewState(sessionId: Long) {
            pendingInterviewSessionId = sessionId
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

        /**
         * 재개 가능한 세션이 있을 때 진행중 오버레이를 띄운다.
         * 남은 질문 개수는 로컬 타이머의 남은 시간(12분 하드캡 기준)을 규칙 기반으로 환산한다
         * (서버 응답에 남은 질문 수 필드가 없다).
         */
        private suspend fun showResumableOverlay() {
            val remainingMillis =
                (InterviewTimeCalculator.HARD_CAP_MILLIS - getInterviewElapsedTimeUseCase())
                    .coerceAtLeast(0L)
            val remainingSeconds = remainingMillis / MILLIS_PER_SECOND
            reduce {
                copy(
                    sessionStartOverlay =
                        HomeSessionStartOverlayState.InProgress(
                            userName = userName,
                            remainingQuestionCount = remainingQuestionCountFor(remainingSeconds),
                        ),
                )
            }
        }

        /**
         * 남은 시간을 남은 질문 개수로 환산하는 규칙.
         * 3분 미만 1개, 3~5분 미만 2개, 5~7분 3개, 7분 초과 4개.
         */
        private fun remainingQuestionCountFor(remainingSeconds: Long): Int =
            when {
                remainingSeconds < REMAINING_ONE_QUESTION_THRESHOLD_SECONDS -> ONE_QUESTION
                remainingSeconds < REMAINING_TWO_QUESTIONS_THRESHOLD_SECONDS -> TWO_QUESTIONS
                remainingSeconds <= REMAINING_THREE_QUESTIONS_THRESHOLD_SECONDS -> THREE_QUESTIONS
                else -> FOUR_QUESTIONS
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

private const val MILLIS_PER_SECOND = 1_000L
private const val ONE_QUESTION = 1
private const val TWO_QUESTIONS = 2
private const val THREE_QUESTIONS = 3
private const val FOUR_QUESTIONS = 4

/** 남은 시간이 이 값(3분) 미만이면 남은 질문 1개로 표시한다. */
private const val REMAINING_ONE_QUESTION_THRESHOLD_SECONDS = 180L

/** 남은 시간이 이 값(5분) 미만이면 남은 질문 2개로 표시한다. */
private const val REMAINING_TWO_QUESTIONS_THRESHOLD_SECONDS = 300L

/** 남은 시간이 이 값(7분) 이하면 남은 질문 3개, 초과면 4개로 표시한다. */
private const val REMAINING_THREE_QUESTIONS_THRESHOLD_SECONDS = 420L
