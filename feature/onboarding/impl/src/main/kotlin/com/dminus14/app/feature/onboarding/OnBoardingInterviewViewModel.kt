package com.dminus14.app.feature.onboarding

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionStatusType
import com.dminus14.app.domain.model.PortfolioStatus
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.GetInterviewSessionUseCase
import com.dminus14.app.domain.usecase.GetPortfolioIdUseCase
import com.dminus14.app.domain.usecase.GetPortfolioStatusUseCase
import com.dminus14.app.domain.usecase.MakeInterviewSessionUseCase
import com.dminus14.app.domain.usecase.UploadPortfolioUseCase
import com.dminus14.app.domain.usecase.ValidateJdUrlUseCase
import com.dminus14.app.feature.onboarding.OnBoardingInterviewViewModel.Companion.JD_DEBOUNCE_MS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingInterviewViewModel
    @Inject
    constructor(
        private val checkUserProfile: CheckUserProfileUseCase,
        private val getPortfolioId: GetPortfolioIdUseCase,
        private val uploadPortfolio: UploadPortfolioUseCase,
        private val getPortfolioStatus: GetPortfolioStatusUseCase,
        private val validateJdUrl: ValidateJdUrlUseCase,
        private val makeInterviewSession: MakeInterviewSessionUseCase,
        private val getInterviewSession: GetInterviewSessionUseCase,
    ) : MviViewModel<
            OnBoardingInterviewIntent,
            OnBoardingInterviewState,
            OnBoardingInterviewEffect,
        >(
            OnBoardingInterviewState(),
        ) {
        private var jobRole: String? = null
        private var careerYears: Int? = null
        private var readyPortfolioId: String? = null
        private var existingPortfolioId: String? = null
        private var existingPortfolioFileName: String? = null
        private var jdUrl: String? = null
        private var jdText: String? = null
        private var jdValidationJob: Job? = null

        override fun onIntent(intent: OnBoardingInterviewIntent) {
            when (intent) {
                OnBoardingInterviewIntent.Load -> {
                    load()
                }

                OnBoardingInterviewIntent.ClickClose -> {
                    sendEffect(OnBoardingInterviewEffect.CloseRequested)
                }

                OnBoardingInterviewIntent.ClickSkip -> {
                    advanceStep()
                }

                OnBoardingInterviewIntent.ClickContinue -> {
                    onContinueClick()
                }

                OnBoardingInterviewIntent.ClickPrevious -> {
                    onPreviousClick()
                }

                is OnBoardingInterviewIntent.JobDescriptionTabChange -> {
                    reduce { copy(jobDescriptionTab = JobDescriptionTab.entries[intent.index]) }
                }

                is OnBoardingInterviewIntent.JobDescriptionLinkChange -> {
                    onJobDescriptionLinkChange(intent.value)
                }

                is OnBoardingInterviewIntent.JobDescriptionTextChange -> {
                    // 붙여넣기로 3000자를 넘겨 들어와도 잘라서 반영한다.
                    reduce {
                        copy(
                            jobDescriptionText = intent.value.take(JD_TEXT_MAX_LENGTH),
                            jdTextError = null,
                        )
                    }
                }

                OnBoardingInterviewIntent.ClickPortfolioUpload -> {
                    if (existingPortfolioId != null) {
                        reduce { copy(showExistingPortfolioModal = true) }
                    } else {
                        sendEffect(OnBoardingInterviewEffect.LaunchPortfolioPicker)
                    }
                }

                OnBoardingInterviewIntent.ClickPortfolioRemove -> {
                    readyPortfolioId = null
                    reduce { copy(portfolioFileName = null, isPortfolioProcessing = false) }
                }

                OnBoardingInterviewIntent.ClickPortfolioUseExisting -> {
                    readyPortfolioId = existingPortfolioId
                    reduce {
                        copy(
                            portfolioFileName = existingPortfolioFileName,
                            showExistingPortfolioModal = false,
                            showPortfolioRequiredError = false,
                        )
                    }
                }

                OnBoardingInterviewIntent.ClickPortfolioUploadNew -> {
                    reduce { copy(showExistingPortfolioModal = false) }
                    sendEffect(OnBoardingInterviewEffect.LaunchPortfolioPicker)
                }

                is OnBoardingInterviewIntent.PortfolioFileSelected -> {
                    startPortfolioUpload(intent)
                }

                is OnBoardingInterviewIntent.MainProjectTextChange -> {
                    reduce { copy(mainProjectText = intent.value) }
                }
            }
        }

        private fun load() {
            viewModelScope.launch {
                checkUserProfile()
                    .onSuccess { profile ->
                        jobRole = profile.jobRole
                        careerYears = profile.careerYears
                    }.onFailure { error ->
                        reduce { copy(errorMessage = error.message) }
                    }

                getPortfolioId()
                    .onSuccess { portfolio ->
                        existingPortfolioId = portfolio?.portfolioId
                        existingPortfolioFileName = portfolio?.fileName
                    }
            }
        }

        private fun onContinueClick() {
            when (state.value.step) {
                OnBoardingInterviewStep.JobDescription -> {
                    submitJobDescription()
                }

                OnBoardingInterviewStep.Portfolio -> {
                    if (readyPortfolioId == null) {
                        reduce { copy(showPortfolioRequiredError = true) }
                    } else {
                        advanceStep()
                    }
                }

                OnBoardingInterviewStep.MainProject -> {
                    advanceStep()
                }

                OnBoardingInterviewStep.Preload -> {
                    Unit
                }
            }
        }

        /**
         * JD 링크는 입력 즉시 `https://` 포맷을 검사하고, 포맷을 통과하면 [JD_DEBOUNCE_MS] 뒤
         * URL 검증 API를 호출한다. 하루 5회 제한이 있어 키 입력마다 호출하지 않도록 디바운스한다.
         */
        private fun onJobDescriptionLinkChange(value: String) {
            jdValidationJob?.cancel()
            jdUrl = null

            when {
                value.isEmpty() -> {
                    reduce {
                        copy(
                            jobDescriptionLink = value,
                            jdLinkStatus = JdLinkStatus.Idle,
                            jdLinkSubText = "",
                        )
                    }
                }

                !value.startsWith(HTTPS_SCHEME) -> {
                    reduce {
                        copy(
                            jobDescriptionLink = value,
                            jdLinkStatus = JdLinkStatus.Invalid,
                            jdLinkSubText = MESSAGE_LINK_FORMAT,
                        )
                    }
                }

                HTTPS_SCHEME.startsWith(value) -> {
                    // 아직 "https://" 스킴을 입력 중이라 포맷 오류로 보지 않는다.
                    reduce {
                        copy(
                            jobDescriptionLink = value,
                            jdLinkStatus = JdLinkStatus.Idle,
                            jdLinkSubText = "",
                        )
                    }
                }

                else -> {
                    if (HTTPS_SCHEME.startsWith(value)) {
                        reduce {
                            copy(
                                jobDescriptionLink = value,
                                jdLinkSubText = "",
                            )
                        }
                        scheduleJdValidation(value)
                    }
                }
            }
        }

        private fun scheduleJdValidation(link: String) {
            jdValidationJob =
                viewModelScope.launch {
                    delay(JD_DEBOUNCE_MS)
                    reduce { copy(jdLinkStatus = JdLinkStatus.Validating) }
                    validateJdUrl(link)
                        .onSuccess { result ->
                            if (result.valid) {
                                jdUrl = link
                                jdText = null
                                reduce {
                                    copy(
                                        jdLinkStatus = JdLinkStatus.Valid,
                                        jdLinkSubText = "",
                                    )
                                }
                                advanceStep()
                            } else {
                                reduce {
                                    copy(
                                        jdLinkStatus = JdLinkStatus.Invalid,
                                        jdLinkSubText = result.message ?: "",
                                    )
                                }
                            }
                        }.onFailure { error ->
                            reduce {
                                copy(
                                    jdLinkStatus = JdLinkStatus.Invalid,
                                    jdLinkSubText = error.message ?: MESSAGE_LINK_INVALID,
                                )
                            }
                        }
                }
        }

        private fun submitJobDescription() {
            val current = state.value
            if (current.jobDescriptionTab == JobDescriptionTab.Text) {
                // 카운터(raw length)와 동일한 기준으로 검증·전송한다.
                val content = current.jobDescriptionText
                if (content.length < JD_TEXT_MIN_LENGTH) {
                    reduce { copy(jdTextError = MESSAGE_TEXT_TOO_SHORT) }
                    return
                }
                jdText = content
                jdUrl = null
                advanceStep()
                return
            }

            // 링크 탭: 검증 성공(자동 전진) 또는 빈 입력(건너뜀)일 때만 다음 스텝으로 간다.
            when {
                current.jdLinkStatus == JdLinkStatus.Valid -> {
                    advanceStep()
                }

                current.jobDescriptionLink.isEmpty() -> {
                    jdUrl = null
                    jdText = null
                    advanceStep()
                }

                else -> {
                    Unit
                }
            }
        }

        private fun startPortfolioUpload(intent: OnBoardingInterviewIntent.PortfolioFileSelected) {
            reduce {
                copy(
                    portfolioFileName = intent.fileName,
                    isPortfolioProcessing = true,
                    showPortfolioRequiredError = false,
                    errorMessage = null,
                )
            }
            viewModelScope.launch {
                uploadPortfolio(file = intent.file, fileName = intent.fileName)
                    .onSuccess { result -> pollPortfolioStatus(result.portfolioId) }
                    .onFailure { error ->
                        reduce {
                            copy(
                                isPortfolioProcessing = false,
                                portfolioFileName = null,
                                errorMessage = error.message,
                            )
                        }
                    }
            }
        }

        private suspend fun pollPortfolioStatus(portfolioId: String) {
            repeat(MAX_POLL_ATTEMPTS) {
                val status =
                    getPortfolioStatus(portfolioId).getOrElse { error ->
                        reduce {
                            copy(isPortfolioProcessing = false, errorMessage = error.message)
                        }
                        return
                    }
                when (status.status) {
                    PortfolioStatus.READY -> {
                        readyPortfolioId = portfolioId
                        reduce { copy(isPortfolioProcessing = false) }
                        return
                    }

                    PortfolioStatus.FAILED_FILE,
                    PortfolioStatus.FAILED_SYSTEM,
                    -> {
                        reduce {
                            copy(
                                isPortfolioProcessing = false,
                                portfolioFileName = null,
                                errorMessage = "포트폴리오 처리에 실패했어요. 다시 업로드해 주세요.",
                            )
                        }
                        return
                    }

                    else -> {
                        delay(POLL_INTERVAL_MS)
                    }
                }
            }
            reduce {
                copy(
                    isPortfolioProcessing = false,
                    errorMessage = "포트폴리오 처리가 지연되고 있어요. 잠시 후 다시 시도해 주세요.",
                )
            }
        }

        private fun advanceStep() {
            val next =
                when (state.value.step) {
                    OnBoardingInterviewStep.JobDescription -> OnBoardingInterviewStep.Portfolio
                    OnBoardingInterviewStep.MainProject -> OnBoardingInterviewStep.Preload
                    else -> return
                }
            reduce { copy(step = next, errorMessage = null) }
            if (next == OnBoardingInterviewStep.Preload) {
                startPreload()
            }
        }

        private fun startPreload() {
            val role = jobRole
            val years = careerYears
            val portfolioId = readyPortfolioId
            if (role == null || years == null || portfolioId == null) {
                reduce { copy(errorMessage = "필요한 정보를 불러오지 못했어요. 다시 시도해 주세요.") }
                return
            }

            reduce { copy(loadingBasicInfo = OnBoardingLoadingStepStatus.InProgress) }
            viewModelScope.launch {
                val request =
                    InterviewSessionRequest(
                        portfolioId = portfolioId,
                        jobRole = role,
                        careerYears = years,
                        jdUrl = jdUrl,
                        jdText = jdText,
                        freeText =
                            state.value.mainProjectText
                                .trim()
                                .takeIf { it.isNotEmpty() },
                    )
                makeInterviewSession(request)
                    .onSuccess { result ->
                        reduce {
                            copy(
                                loadingBasicInfo = OnBoardingLoadingStepStatus.Completed,
                                loadingJd = OnBoardingLoadingStepStatus.InProgress,
                                loadingPortfolio = OnBoardingLoadingStepStatus.InProgress,
                            )
                        }
                        pollInterviewSession(result.sessionId)
                    }.onFailure { error ->
                        reduce { copy(errorMessage = error.message) }
                    }
            }
        }

        private suspend fun pollInterviewSession(sessionId: Long) {
            repeat(MAX_POLL_ATTEMPTS) {
                val status =
                    getInterviewSession(sessionId).getOrElse { error ->
                        reduce { copy(errorMessage = error.message) }
                        return
                    }
                when (status.status) {
                    InterviewSessionStatusType.READY -> {
                        reduce {
                            copy(
                                loadingJd = OnBoardingLoadingStepStatus.Completed,
                                loadingPortfolio = OnBoardingLoadingStepStatus.Completed,
                            )
                        }
                        sendEffect(OnBoardingInterviewEffect.NavigateToResult(sessionId))
                        return
                    }

                    InterviewSessionStatusType.FAILED -> {
                        reduce { copy(errorMessage = "면접 준비에 실패했어요. 다시 시도해 주세요.") }
                        return
                    }

                    else -> {
                        delay(POLL_INTERVAL_MS)
                    }
                }
            }
            reduce { copy(errorMessage = "면접 준비가 지연되고 있어요. 잠시 후 다시 시도해 주세요.") }
        }

        private fun onPreviousClick() {
            val previous =
                when (state.value.step) {
                    OnBoardingInterviewStep.JobDescription -> {
                        sendEffect(OnBoardingInterviewEffect.CloseRequested)
                        return
                    }

                    OnBoardingInterviewStep.Portfolio -> {
                        OnBoardingInterviewStep.JobDescription
                    }

                    OnBoardingInterviewStep.MainProject -> {
                        OnBoardingInterviewStep.Portfolio
                    }

                    OnBoardingInterviewStep.Preload -> {
                        OnBoardingInterviewStep.MainProject
                    }
                }
            reduce { copy(step = previous, errorMessage = null) }
        }

        private companion object {
            const val POLL_INTERVAL_MS = 3_000L
            const val MAX_POLL_ATTEMPTS = 40
            const val JD_DEBOUNCE_MS = 600L
            const val HTTPS_SCHEME = "https://"
            const val MESSAGE_LINK_FORMAT = "올바른 URL 형식이 아니에요."
            const val MESSAGE_LINK_INVALID = "공고 내용을 정리하는 데 실패했어요. 공고 내용을 직접 붙여넣어 주세요."
            const val JD_TEXT_MIN_LENGTH = 200
            const val JD_TEXT_MAX_LENGTH = 3000
            const val MESSAGE_TEXT_TOO_SHORT = "공고 내용은 200자 이상으로 입력해 주세요"
        }
    }
