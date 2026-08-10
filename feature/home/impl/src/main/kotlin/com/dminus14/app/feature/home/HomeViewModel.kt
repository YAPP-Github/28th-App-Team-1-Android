package com.dminus14.app.feature.home

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.GetInterviewReportListUseCase
import com.dminus14.app.domain.usecase.GetInterviewResumeUseCase
import com.dminus14.app.feature.home.mapper.toHomeReportItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val checkUserProfileUseCase: CheckUserProfileUseCase,
    private val getInterviewReportListUseCase: GetInterviewReportListUseCase,
    private val getInterviewResumeUseCase: GetInterviewResumeUseCase,
) : MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {
    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load -> {
                load()
            }

            HomeIntent.OpenMyPage -> {
                sendEffect(HomeEffect.GoToMyPageRequested)
            }

            is HomeIntent.ReportExpandClick -> {
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

            is HomeIntent.ReportActionClick -> {
                Unit
            }
        }
    }

    /**
     * 홈 진입 시 유저 프로필을 먼저 조회하고, 홈에 머무는 경우에만 면접 리포트 목록을
     * 이어서 조회한다.
     *
     * 프로필 조회 결과로 라우팅이 결정되므로(온보딩·스플래시로 이동) 홈에 남는 경우에만
     * 리포트를 조회하면 되고, 이 경우 두 호출을 병렬로 돌릴 이점이 없어 순차로 호출한다.
     */
    private fun load() {
        reduce { copy(isLoading = true) }
        viewModelScope.launch {
            if (!loadProfile()) return@launch
            loadReports()
            checkInterviewSession()
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

                    else -> handleBootstrapFailure(error)
                }
                return false
            }

        val displayName = profile.name?.takeIf { it.isNotBlank() }
        if (displayName == null) {
            reduce { copy(isLoading = false) }
            sendEffect(HomeEffect.UserNameNotRegistered)
            return false
        }

        reduce {
            copy(
                userName = displayName,
                remainingTicketCount = profile.remainingTicketCount,
            )
        }
        return true
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
        // 면접 세션이 클라이언트 내에 있는지 조회.
        // 현재는 연동되어있지 않으므로 항상 없다고 표기.
        val interviewSessionId = "" // 투두 - 활성 세션 ID 조회 연동
        val sessionId = interviewSessionId.toLongOrNull() ?: return
        getInterviewState(sessionId)
    }

    /**
     * 존재하는 세션 아이디가 이어서 진행 가능한 상태인지 조회한다.
     *
     * `resumeState`가 [RESUME_STATE_RESUMABLE]일 때만 재개 가능으로 보고 진행중 오버레이를 띄운다.
     * resume 응답에 남은 질문 수 필드가 없어 [TEMP_REMAINING_QUESTION_COUNT] 임시값을 사용한다.
     */
    private suspend fun getInterviewState(sessionId: Long) {
        getInterviewResumeUseCase(sessionId)
            .onSuccess { resume ->
                if (resume.resumeState == RESUME_STATE_RESUMABLE) {
                    reduce {
                        copy(
                            sessionStartOverlay =
                                HomeSessionStartOverlayState.InProgress(
                                    userName = userName,
                                    remainingQuestionCount = TEMP_REMAINING_QUESTION_COUNT,
                                ),
                        )
                    }
                } else {

                }
            }
            // 재개 확인 실패는 치명적이지 않아 오버레이 미표시로 처리(공통 에러 처리 도입 시 재검토).
            .onFailure { error ->
                handleBootstrapFailure(error)
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

/** 재개 가능한 세션 상태 값. 서버 값 규격 확정 시 도메인 상수로 이전 예정. */
private const val RESUME_STATE_RESUMABLE = "RESUMABLE"

/** resume 응답에 남은 질문 수가 없어 사용하는 임시값. 필드 확보 후 제거 예정. */
private const val TEMP_REMAINING_QUESTION_COUNT = 2
