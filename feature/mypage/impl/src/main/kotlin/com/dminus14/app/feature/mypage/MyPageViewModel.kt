package com.dminus14.app.feature.mypage

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.PortfolioNotFoundException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.PortfolioOverview
import com.dminus14.app.domain.model.PortfolioStatus
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.DeletePortfolioUseCase
import com.dminus14.app.domain.usecase.GetInterviewReportListUseCase
import com.dminus14.app.domain.usecase.GetPortfolioOverviewUseCase
import com.dminus14.app.domain.usecase.GetPortfolioStatusUseCase
import com.dminus14.app.domain.usecase.LogoutUseCase
import com.dminus14.app.domain.usecase.UploadPortfolioUseCase
import com.dminus14.app.domain.usecase.WithdrawUserUseCase
import com.dminus14.app.feature.mypage.portfolio.PortfolioFileReader
import com.dminus14.app.feature.mypage.portfolio.PortfolioFileResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@Suppress("TooManyFunctions")
@HiltViewModel
class MyPageViewModel
    @Inject
    @Suppress("LongParameterList")
    constructor(
        private val checkUserProfileUseCase: CheckUserProfileUseCase,
        private val getPortfolioOverviewUseCase: GetPortfolioOverviewUseCase,
        private val uploadPortfolioUseCase: UploadPortfolioUseCase,
        private val getPortfolioStatusUseCase: GetPortfolioStatusUseCase,
        private val deletePortfolioUseCase: DeletePortfolioUseCase,
        private val getInterviewReportListUseCase: GetInterviewReportListUseCase,
        private val logoutUseCase: LogoutUseCase,
        private val withdrawUserUseCase: WithdrawUserUseCase,
        private val portfolioFileReader: PortfolioFileReader,
    ) : MviViewModel<MyPageIntent, MyPageState, MyPageEffect>(MyPageState()) {
        private var uploadJob: Job? = null
        private var pollingJob: Job? = null
        private var pendingTempFile: File? = null

        @Suppress("CyclomaticComplexMethod", "LongMethod")
        override fun onIntent(intent: MyPageIntent) {
            when (intent) {
                MyPageIntent.Load -> {
                    load()
                }

                MyPageIntent.ClickClose -> {
                    sendEffect(MyPageEffect.CloseRequested)
                }

                MyPageIntent.ClickUpload, MyPageIntent.ClickUploadRetry -> {
                    sendEffect(MyPageEffect.PortfolioSelectionRequested)
                }

                is MyPageIntent.SelectPortfolioFile -> {
                    selectPortfolioFile(intent.uri)
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
                    requestPortfolioDelete()
                }

                MyPageIntent.ClickPortfolioReupload -> {
                    requestPortfolioReupload()
                }

                MyPageIntent.ConfirmModal -> {
                    confirmModal()
                }

                MyPageIntent.CloseModal -> {
                    closeModal()
                }

                is MyPageIntent.ToggleReport -> {
                    toggleReport(intent.id)
                }

                MyPageIntent.ClickProfileEdit -> {
                    sendEffect(MyPageEffect.ProfileEditRequested)
                }

                MyPageIntent.ClickTicketInfo -> {
                    toggleTicketInfoTooltip()
                }

                MyPageIntent.DismissTicketInfoTooltip -> {
                    dismissTicketInfoTooltip()
                }

                MyPageIntent.ClickLogout -> {
                    reduce { copy(modalType = MyPageModalType.Logout) }
                }

                MyPageIntent.ClickWithdrawal -> {
                    requestWithdrawal()
                }

                MyPageIntent.ClickReportView -> {
                    sendEffect(MyPageEffect.ReportViewRequested)
                }

                MyPageIntent.ClickGuestFeedback -> {
                    sendEffect(MyPageEffect.GuestFeedbackRequested)
                }
            }
        }

        // ---- 초기 로드 ----

        private fun load() {
            viewModelScope.launch {
                portfolioFileReader.clearCache()
                reduce { copy(isInitialLoading = true) }

                val profileDeferred = async { checkUserProfileUseCase() }
                val overviewDeferred = async { getPortfolioOverviewUseCase() }
                val reportsDeferred = async { getInterviewReportListUseCase() }

                val profileResult = profileDeferred.await()
                val overviewResult = overviewDeferred.await()
                val reportsResult = reportsDeferred.await()

                reduce { copy(isInitialLoading = false) }

                profileResult.getOrNull()?.let { profile ->
                    reduce {
                        copy(
                            profile = profile.toProfileUiModel(),
                            remainingTicketCount = profile.remainingTicketCount,
                            socialAccount = profile.toSocialAccountUiModel(),
                        )
                    }
                }

                overviewResult.getOrNull()?.let { overview ->
                    applyOverviewToState(overview)
                    val portfolio = overview.portfolio
                    if (portfolio != null && portfolio.status == PortfolioStatus.PROCESSING) {
                        startPolling(portfolio.portfolioId, portfolio.fileName)
                    }
                }

                reportsResult.getOrNull()?.let { reportList ->
                    reduce { copy(reports = reportList.reports.map { it.toReportUiModel() }) }
                }

                val failures =
                    listOfNotNull(
                        profileResult.exceptionOrNull(),
                        overviewResult.exceptionOrNull(),
                        reportsResult.exceptionOrNull(),
                    )
                if (failures.isNotEmpty()) handleLoadFailures(failures)
            }
        }

        private fun handleLoadFailures(failures: List<Throwable>) {
            val globalEventError =
                failures.firstOrNull { error ->
                    error is NetworkUnavailableException || error is ServerException ||
                        error is UnknownException
                }
            if (globalEventError != null) {
                routeToGlobalEventIfNeeded(globalEventError)
                return
            }
            val message =
                if (failures.size == 1) {
                    failures.first().message?.takeIf { it.isNotBlank() }
                        ?: PARTIAL_LOAD_FAILURE_MESSAGE
                } else {
                    PARTIAL_LOAD_FAILURE_MESSAGE
                }
            sendEffect(MyPageEffect.ShowToast(message))
        }

        private fun applyOverviewToState(overview: PortfolioOverview) {
            reduce {
                val portfolio = overview.portfolio
                copy(
                    portfolioId = portfolio?.portfolioId,
                    portfolioState =
                        portfolio?.toPortfolioState(portfolioState) ?: MyPagePortfolioState.Empty,
                    isPortfolioReplaceAvailable = overview.isReplaceAvailable,
                    isPortfolioDeleteAvailable = overview.isDeleteAvailable,
                    nextPortfolioAvailableAt = overview.nextReplaceAvailableAt,
                    isInterviewInProgress = portfolio?.isInterviewInProgress ?: false,
                )
            }
        }

        private suspend fun refreshOverviewSilently() {
            getPortfolioOverviewUseCase().onSuccess { overview -> applyOverviewToState(overview) }
        }

        // ---- 업로드 ----

        private fun selectPortfolioFile(uri: String) {
            val previousState = state.value.portfolioState
            viewModelScope.launch {
                when (val result = portfolioFileReader.read(uri)) {
                    is PortfolioFileResult.Valid -> {
                        startUpload(result.file, result.fileName, result.pageCount, previousState)
                    }

                    is PortfolioFileResult.Invalid -> {
                        reduce {
                            copy(
                                portfolioState =
                                    MyPagePortfolioState.Failed(
                                        fileName = result.fileName,
                                    ),
                            )
                        }
                    }

                    is PortfolioFileResult.Unreadable -> {
                        reduce {
                            copy(
                                portfolioState =
                                    MyPagePortfolioState.Failed(
                                        fileName = result.fileName,
                                    ),
                            )
                        }
                    }
                }
            }
        }

        @Suppress("LongMethod")
        private fun startUpload(
            file: File,
            fileName: String,
            pageCount: Int,
            previousState: MyPagePortfolioState,
        ) {
            pendingTempFile = file
            val previousPortfolio = (previousState as? MyPagePortfolioState.Uploaded)?.portfolio
            val failedPortfolioId = (previousState as? MyPagePortfolioState.Failed)?.portfolioId
            reduce {
                copy(
                    portfolioState =
                        MyPagePortfolioState.Uploading(
                            fileName = fileName,
                            previousPortfolio = previousPortfolio,
                        ),
                )
            }

            uploadJob =
                viewModelScope.launch {
                    try {
                        val cleanupResult = deletePreviousFailedPortfolioIfNeeded(failedPortfolioId)
                        val cleanupError = cleanupResult.exceptionOrNull()
                        if (cleanupError != null) {
                            reduce {
                                copy(
                                    portfolioState =
                                        MyPagePortfolioState.Failed(
                                            fileName = fileName,
                                            portfolioId = failedPortfolioId,
                                        ),
                                )
                            }
                            handleGenericError(cleanupError)
                            return@launch
                        }

                        uploadPortfolioUseCase(
                            file = file,
                            fileName = fileName,
                            pageCount = pageCount,
                        ).fold(
                            onSuccess = { result ->
                                reduce {
                                    copy(
                                        portfolioId = result.portfolioId,
                                        portfolioState =
                                            MyPagePortfolioState.Uploading(
                                                fileName = fileName,
                                                portfolioId = result.portfolioId,
                                            ),
                                    )
                                }
                                startPolling(result.portfolioId, fileName)
                            },
                            onFailure = { error ->
                                reduce {
                                    copy(
                                        portfolioState =
                                            MyPagePortfolioState.Failed(
                                                fileName = fileName,
                                            ),
                                    )
                                }
                                handleGenericError(error)
                            },
                        )
                    } finally {
                        cleanupTempFile()
                    }
                }
        }

        /**
         * 실패한 포트폴리오를 정리하고 다시 업로드한다.
         *
         * 서버는 계정당 활성 포트폴리오 1개만 허용하므로 실패 레코드가 남아 있으면 재업로드가
         * `PORTFOLIO_ALREADY_EXISTS`로 거부될 수 있다. 교체·삭제 기회는 `READY` 상태에서만
         * 소진되므로(서버 확인), `FAILED_*` 레코드를 지우는 것은 이번 달 기회에 영향을 주지 않는다.
         */
        private suspend fun deletePreviousFailedPortfolioIfNeeded(
            portfolioId: String?,
        ): Result<Unit> {
            if (portfolioId == null) return Result.success(Unit)
            val error = deletePortfolioUseCase(portfolioId).exceptionOrNull()
            return if (error == null || error is PortfolioNotFoundException) {
                Result.success(Unit)
            } else {
                Result.failure(error)
            }
        }

        private fun startPolling(
            portfolioId: String,
            fileName: String,
        ) {
            pollingJob?.cancel()
            pollingJob =
                viewModelScope.launch {
                    val maxIterations = POLLING_TIMEOUT_SECONDS / POLLING_INTERVAL_SECONDS
                    for (iteration in 1..maxIterations) {
                        val elapsedSeconds = iteration * POLLING_INTERVAL_SECONDS
                        reduce {
                            copy(
                                portfolioState =
                                    MyPagePortfolioState.Uploading(
                                        fileName = fileName,
                                        progress = calculatePollingProgress(elapsedSeconds),
                                        portfolioId = portfolioId,
                                    ),
                            )
                        }
                        delay(POLLING_INTERVAL_MILLIS)

                        when (getPortfolioStatusUseCase(portfolioId).getOrNull()?.status) {
                            PortfolioStatus.READY -> {
                                reduce {
                                    copy(
                                        portfolioState = MyPagePortfolioState.Completed(fileName),
                                    )
                                }
                                return@launch
                            }

                            PortfolioStatus.FAILED_FILE, PortfolioStatus.FAILED_SYSTEM -> {
                                reduce {
                                    copy(
                                        portfolioState =
                                            MyPagePortfolioState.Failed(
                                                fileName = fileName,
                                                portfolioId = portfolioId,
                                            ),
                                    )
                                }
                                return@launch
                            }

                            else -> {
                                Unit
                            }
                        }
                    }
                    reduce {
                        copy(
                            portfolioState =
                                MyPagePortfolioState.Failed(
                                    fileName = fileName,
                                    portfolioId = portfolioId,
                                ),
                        )
                    }
                }
        }

        private fun calculatePollingProgress(elapsedSeconds: Int): Float =
            (elapsedSeconds / POLLING_TIMEOUT_SECONDS.toFloat() * MAX_POLLING_PROGRESS)
                .coerceAtMost(MAX_POLLING_PROGRESS)

        /**
         * 업로드를 취소한다.
         *
         * 접수(HTTP 202) 전이면 요청 코루틴만 취소한다. 접수 후에는 서버가 파싱·임베딩을 계속
         * 진행하므로 화면만 되돌리면 다음 진입에서 취소한 포트폴리오가 다시 보인다. 그래서 접수
         * 이후에는 삭제 API로 접수분을 지운다. 교체·삭제 기회는 `READY` 상태에서만 소진되므로
         * (서버 확인) 이 삭제는 이번 달 기회를 쓰지 않는다.
         *
         * 다만 취소 요청과 서버의 처리 완료가 겹치면 삭제가 `READY` 삭제로 집계되어 기회를 쓸 수
         * 있다. 이를 줄이려고 삭제 직전에 상태를 한 번 더 조회하고, 이미 `READY`면 삭제하지 않고
         * 완료 안내로 전환한다. 조회와 삭제 사이의 경합까지 없애지는 못한다.
         *
         * 삭제가 실패하면 화면을 되돌리지 않고 실패 상태를 유지한다. 성공했다고 표시한 뒤 서버에
         * 포트폴리오가 남아 있는 상황을 만들지 않기 위해서다.
         */
        private fun cancelUpload() {
            val uploading = state.value.portfolioState as? MyPagePortfolioState.Uploading ?: return
            val portfolioId = uploading.portfolioId

            if (portfolioId == null) {
                uploadJob?.cancel()
                uploadJob = null
                cleanupTempFile()
                revertToPreviousOrEmpty(uploading.previousPortfolio)
                return
            }

            viewModelScope.launch {
                reduce { copy(isSubmitting = true) }
                val status = getPortfolioStatusUseCase(portfolioId).getOrNull()
                if (status == null) {
                    reduce { copy(isSubmitting = false) }
                    return@launch
                }

                when (status.status) {
                    PortfolioStatus.READY -> {
                        pollingJob?.cancel()
                        pollingJob = null
                        reduce {
                            copy(
                                isSubmitting = false,
                                modalType = MyPageModalType.UploadAlreadyCompleted,
                                portfolioState = MyPagePortfolioState.Completed(uploading.fileName),
                            )
                        }
                    }

                    PortfolioStatus.FAILED_FILE, PortfolioStatus.FAILED_SYSTEM -> {
                        pollingJob?.cancel()
                        pollingJob = null
                        reduce {
                            copy(
                                isSubmitting = false,
                                portfolioState =
                                    MyPagePortfolioState.Failed(
                                        fileName = uploading.fileName,
                                        portfolioId = portfolioId,
                                    ),
                            )
                        }
                    }

                    else -> {
                        val deleteError = deletePortfolioUseCase(portfolioId).exceptionOrNull()
                        pollingJob?.cancel()
                        pollingJob = null
                        reduce { copy(isSubmitting = false) }
                        if (deleteError == null) {
                            revertToPreviousOrEmpty(uploading.previousPortfolio)
                        } else {
                            handleGenericError(deleteError)
                        }
                    }
                }
            }
        }

        private fun revertToPreviousOrEmpty(previousPortfolio: MyPagePortfolioUiModel?) {
            reduce {
                copy(
                    portfolioState =
                        previousPortfolio?.let(MyPagePortfolioState::Uploaded)
                            ?: MyPagePortfolioState.Empty,
                )
            }
            viewModelScope.launch { refreshOverviewSilently() }
        }

        private fun cleanupTempFile() {
            pendingTempFile?.delete()
            pendingTempFile = null
        }

        // ---- 삭제 ----

        private fun requestPortfolioDelete() {
            when {
                isInterviewInProgress() -> {
                    reduce { copy(modalType = MyPageModalType.PortfolioDeleteUnavailable) }
                }

                !state.value.isPortfolioDeleteAvailable -> {
                    reduce { copy(modalType = MyPageModalType.PortfolioDeleteUnavailable) }
                }

                else -> {
                    reduce { copy(modalType = MyPageModalType.PortfolioDelete) }
                }
            }
        }

        private fun deletePortfolio() {
            val portfolioId = state.value.portfolioId
            if (portfolioId == null) {
                reduce { copy(modalType = null) }
                return
            }
            viewModelScope.launch {
                reduce { copy(isSubmitting = true) }
                val error = deletePortfolioUseCase(portfolioId).exceptionOrNull()
                reduce { copy(isSubmitting = false, modalType = null) }
                if (error == null) {
                    reduce { copy(portfolioId = null, portfolioState = MyPagePortfolioState.Empty) }
                    refreshOverviewSilently()
                } else {
                    handleGenericError(error)
                }
            }
        }

        private fun requestPortfolioReupload() {
            if (!state.value.isPortfolioReplaceAvailable) {
                reduce { copy(modalType = MyPageModalType.PortfolioReuploadUnavailable) }
            } else {
                reduce { copy(modalType = MyPageModalType.PortfolioReupload) }
            }
        }

        // ---- 모달 ----

        private fun confirmModal() {
            when (state.value.modalType) {
                MyPageModalType.PortfolioReupload -> {
                    reduce { copy(modalType = null) }
                    sendEffect(MyPageEffect.PortfolioSelectionRequested)
                }

                MyPageModalType.PortfolioDelete -> {
                    deletePortfolio()
                }

                MyPageModalType.Logout -> {
                    logout()
                }

                MyPageModalType.WithdrawalNotice -> {
                    reduce { copy(modalType = MyPageModalType.WithdrawalConfirm) }
                }

                MyPageModalType.WithdrawalConfirm -> {
                    withdraw()
                }

                else -> {
                    Unit
                }
            }
        }

        private fun closeModal() {
            val wasUploadAlreadyCompleted =
                state.value.modalType == MyPageModalType.UploadAlreadyCompleted
            reduce { copy(modalType = null) }
            if (wasUploadAlreadyCompleted) {
                viewModelScope.launch { refreshOverviewSilently() }
            }
        }

        // ---- 프로필 · 리포트 ----

        private fun toggleUploadFailureTooltip() {
            if (state.value.portfolioState !is MyPagePortfolioState.Failed) return
            reduce { copy(isUploadFailureTooltipVisible = !isUploadFailureTooltipVisible) }
        }

        private fun dismissUploadFailureTooltip() {
            if (!state.value.isUploadFailureTooltipVisible) return
            reduce { copy(isUploadFailureTooltipVisible = false) }
        }

        private fun toggleTicketInfoTooltip() {
            if (state.value.remainingTicketCount == null) return
            reduce { copy(isTicketInfoTooltipVisible = !isTicketInfoTooltipVisible) }
        }

        private fun dismissTicketInfoTooltip() {
            if (!state.value.isTicketInfoTooltipVisible) return
            reduce { copy(isTicketInfoTooltipVisible = false) }
        }

        private fun toggleReport(id: String) {
            if (state.value.reports.none { it.id == id }) return
            reduce {
                copy(
                    expandedReportIds =
                        if (id in
                            expandedReportIds
                        ) {
                            expandedReportIds - id
                        } else {
                            expandedReportIds + id
                        },
                )
            }
        }

        // ---- 로그아웃 · 탈퇴 ----

        private fun logout() {
            viewModelScope.launch {
                reduce { copy(isSubmitting = true) }
                val error = logoutUseCase().exceptionOrNull()
                reduce { copy(isSubmitting = false, modalType = null) }
                if (error == null) {
                    sendEffect(MyPageEffect.LogoutCompleted)
                } else {
                    handleGenericError(error)
                }
            }
        }

        private fun requestWithdrawal() {
            if (isInterviewInProgress()) {
                reduce { copy(modalType = MyPageModalType.WithdrawalBlocked) }
                return
            }
            reduce { copy(modalType = MyPageModalType.WithdrawalNotice) }
        }

        private fun withdraw() {
            viewModelScope.launch {
                reduce { copy(isSubmitting = true) }
                val error = withdrawUserUseCase().exceptionOrNull()
                reduce { copy(isSubmitting = false, modalType = null) }
                if (error == null) {
                    sendEffect(MyPageEffect.WithdrawalCompleted)
                } else {
                    handleGenericError(error)
                }
            }
        }

        /**
         * 진행 중 면접 여부를 판정한다.
         *
         * 탈퇴 API에는 "진행 중 면접" 전용 오류 코드가 없어, 포트폴리오 개요의
         * `interviewInProgress`를 임시 근거로 사용한다. 면접은 포트폴리오 없이 시작할 수 없으므로
         * 실무상 동등하지만 정식 계약은 아니다. 서버가 전용 판정·오류 코드를 제공하면 이 함수만
         * 교체한다. (서버 협의 진행 중)
         */
        private fun isInterviewInProgress(): Boolean = state.value.isInterviewInProgress

        // ---- 공통 오류 처리 ----

        private fun handleGenericError(error: Throwable) {
            if (routeToGlobalEventIfNeeded(error)) return
            sendEffect(
                MyPageEffect.ShowToast(
                    error.message?.takeIf { it.isNotBlank() } ?: DEFAULT_ERROR_MESSAGE,
                ),
            )
        }

        private fun routeToGlobalEventIfNeeded(error: Throwable): Boolean {
            val event =
                when (error) {
                    is NetworkUnavailableException -> GlobalAppEvent.ShowNetworkErrorAndExit
                    is ServerException -> GlobalAppEvent.ShowServerErrorAndExit
                    is UnknownException -> GlobalAppEvent.ShowUnknownError
                    else -> null
                } ?: return false
            viewModelScope.launch { GlobalErrorHandler.emit(event) }
            return true
        }

        private companion object {
            const val POLLING_INTERVAL_MILLIS = 3_000L
            const val POLLING_INTERVAL_SECONDS = 3
            const val POLLING_TIMEOUT_SECONDS = 120
            const val MAX_POLLING_PROGRESS = 95f
            const val PARTIAL_LOAD_FAILURE_MESSAGE = "일부 정보를 불러오지 못했어요."
            const val DEFAULT_ERROR_MESSAGE = "알 수 없는 오류가 발생했습니다."
        }
    }
