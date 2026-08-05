package com.dminus14.app.feature.onboarding

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.core.common.pdf.PdfInvalidReason
import com.dminus14.app.core.common.pdf.PdfValidationResult
import com.dminus14.app.core.common.pdf.validatePdf
import com.dminus14.app.domain.exception.FreeTextNotRelevantException
import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionStatusType
import com.dminus14.app.domain.model.PortfolioStatus
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.DeletePortfolioUseCase
import com.dminus14.app.domain.usecase.GetInterviewSessionUseCase
import com.dminus14.app.domain.usecase.GetPortfolioIdUseCase
import com.dminus14.app.domain.usecase.GetPortfolioStatusUseCase
import com.dminus14.app.domain.usecase.MakeInterviewSessionUseCase
import com.dminus14.app.domain.usecase.UploadPortfolioUseCase
import com.dminus14.app.domain.usecase.ValidateJdUrlUseCase
import com.dminus14.app.feature.onboarding.OnBoardingInterviewViewModel.Companion.JD_DEBOUNCE_MS
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OnBoardingInterviewViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val checkUserProfile: CheckUserProfileUseCase,
        private val getPortfolioId: GetPortfolioIdUseCase,
        private val uploadPortfolio: UploadPortfolioUseCase,
        private val getPortfolioStatus: GetPortfolioStatusUseCase,
        private val deletePortfolio: DeletePortfolioUseCase,
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

        @Suppress("detekt:LongMethod")
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
                    reduce {
                        copy(
                            jobDescriptionTab =
                                JobDescriptionTab.entries.getOrNull(intent.index)
                                    ?: state.value.jobDescriptionTab,
                        )
                    }
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
                    removePortfolio()
                }

                OnBoardingInterviewIntent.ClickExistingPortfolioModalDismiss -> {
                    reduce { copy(showExistingPortfolioModal = false) }
                }

                is OnBoardingInterviewIntent.PortfolioFileSelected -> {
                    startPortfolioUpload(intent)
                }

                is OnBoardingInterviewIntent.MainProjectTextChange -> {
                    // 붙여넣기 등으로 300자를 넘겨 들어와도 잘라서 반영한다.
                    reduce {
                        copy(
                            mainProjectText = intent.value.take(FREETEXT_MAX_LENGTH),
                            mainProjectError = null,
                        )
                    }
                }

                OnBoardingInterviewIntent.ClickRelevanceRetryPortfolio -> {
                    // "포트폴리오 다시 올리기": 다이얼로그를 닫고 포트폴리오 스텝으로 되돌아간다.
                    // 카운트는 초기화해 다음 시도가 처음부터 새로 세도록 한다.
                    reduce {
                        copy(
                            showRelevanceFailDialog = false,
                            step = OnBoardingInterviewStep.Portfolio,
                            mainProjectRelevanceFailCount = 0,
                            mainProjectError = null,
                            errorMessage = null,
                        )
                    }
                }

                OnBoardingInterviewIntent.ClickRelevanceProceedWithoutMainProject -> {
                    // "집중 프로젝트 없이 진행": free text를 비우고 preload를 다시 시작한다.
                    reduce {
                        copy(
                            showRelevanceFailDialog = false,
                            mainProjectText = "",
                            mainProjectError = null,
                            mainProjectRelevanceFailCount = 0,
                            errorMessage = null,
                        )
                    }
                    startPreload()
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
                        // 재진입 시 기존 READY 포트폴리오는 즉시 노출·재사용한다.
                        // (스펙 S2: "정상 저장(READY) 포폴은 이탈해도 보존, 다음 진입 시 재사용")
                        if (portfolio != null && portfolio.status == PortfolioStatus.READY) {
                            readyPortfolioId = portfolio.portfolioId
                            reduce { copy(portfolioFileName = portfolio.fileName) }
                        }
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
                        reduce { copy(portfolioErrorMessage = MESSAGE_PORTFOLIO_REQUIRED) }
                    } else {
                        advanceStep()
                    }
                }

                OnBoardingInterviewStep.MainProject -> {
                    submitMainProject()
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

        /**
         * 집중 프로젝트는 선택 입력이라 비어 있으면 그냥 넘어가고, 입력했다면 최소 10자를
         * 만족해야 다음 스텝으로 진행한다. (최대 300자는 입력 필드에서 캡핑된다.)
         */
        private fun submitMainProject() {
            val content = state.value.mainProjectText
            if (content.isNotEmpty() && content.length < FREETEXT_MIN_LENGTH) {
                reduce { copy(mainProjectError = MESSAGE_FREETEXT_TOO_SHORT) }
                return
            }
            advanceStep()
        }

        /**
         * 업로드했거나 선택한 포트폴리오를 서버에서 삭제한다. 삭제는 파괴적·rate-limited라
         * 서버 성공을 확인한 뒤에만 로컬 상태를 정리하고, 실패하면 인라인 에러로 노출한다.
         */
        private fun removePortfolio() {
            val portfolioId = readyPortfolioId ?: return

            reduce { copy(isPortfolioProcessing = true, portfolioErrorMessage = null) }
            viewModelScope.launch {
                deletePortfolio(portfolioId)
                    .onSuccess {
                        readyPortfolioId = null
                        existingPortfolioId = null
                        existingPortfolioFileName = null
                        reduce {
                            copy(portfolioFileName = null, isPortfolioProcessing = false)
                        }
                    }.onFailure { error ->
                        reduce {
                            copy(
                                isPortfolioProcessing = false,
                                portfolioErrorMessage = error.message,
                            )
                        }
                    }
            }
        }

        private fun startPortfolioUpload(intent: OnBoardingInterviewIntent.PortfolioFileSelected) {
            reduce {
                copy(
                    portfolioFileName = intent.fileName,
                    isPortfolioProcessing = true,
                    portfolioErrorMessage = null,
                )
            }
            viewModelScope.launch {
                val validationMessage =
                    withContext(Dispatchers.IO) {
                        validatePdf(context.contentResolver, Uri.fromFile(intent.file))
                            .toErrorMessageOrNull()
                    }
                if (validationMessage != null) {
                    reduce {
                        copy(
                            isPortfolioProcessing = false,
                            portfolioFileName = null,
                            portfolioErrorMessage = validationMessage,
                        )
                    }
                    return@launch
                }

                uploadPortfolio(file = intent.file, fileName = intent.fileName)
                    .onSuccess { result -> pollPortfolioStatus(result.portfolioId) }
                    .onFailure { error ->
                        reduce {
                            copy(
                                isPortfolioProcessing = false,
                                portfolioFileName = null,
                                portfolioErrorMessage = error.message,
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
                            copy(
                                isPortfolioProcessing = false,
                                portfolioErrorMessage = error.message,
                            )
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
                                portfolioErrorMessage = "포트폴리오 처리에 실패했어요. 다시 업로드해 주세요.",
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
                    portfolioErrorMessage = "포트폴리오 처리가 지연되고 있어요. 잠시 후 다시 시도해 주세요.",
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
                        freeText = state.value.mainProjectText.takeIf { it.isNotEmpty() },
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
                        handleSessionCreateFailure(error)
                    }
            }
        }

        /**
         * S3.5 서버 연관성 판단 실패는 일반 에러와 다르게 처리한다:
         * - 1~3회: MainProject 스텝으로 되돌리고 인라인 에러로 재입력을 유도한다.
         * - 4회째: 다이얼로그로 "포폴 다시 올리기 / 집중 프로젝트 없이 진행" 두 선택지를 제시한다.
         *   그 외 실패는 기존과 동일하게 상단 에러 메시지로 노출한다.
         */
        private fun handleSessionCreateFailure(error: Throwable) {
            if (error !is FreeTextNotRelevantException) {
                reduce {
                    copy(
                        errorMessage = error.message,
                        loadingBasicInfo = OnBoardingLoadingStepStatus.Waiting,
                        loadingJd = OnBoardingLoadingStepStatus.Waiting,
                        loadingPortfolio = OnBoardingLoadingStepStatus.Waiting,
                    )
                }
                return
            }

            val nextCount = state.value.mainProjectRelevanceFailCount + 1
            if (nextCount >= RELEVANCE_FAIL_ESCAPE_THRESHOLD) {
                // 이스케이프 다이얼로그는 MainProject 스텝 위에 띄워, 사용자가 자신이 쓴 문구를 보며
                // 재시도/우회를 결정하게 한다.
                reduce {
                    copy(
                        step = OnBoardingInterviewStep.MainProject,
                        showRelevanceFailDialog = true,
                        mainProjectRelevanceFailCount = nextCount,
                        mainProjectError = MESSAGE_FREETEXT_NOT_RELEVANT,
                        loadingBasicInfo = OnBoardingLoadingStepStatus.Waiting,
                        loadingJd = OnBoardingLoadingStepStatus.Waiting,
                        loadingPortfolio = OnBoardingLoadingStepStatus.Waiting,
                        errorMessage = null,
                    )
                }
            } else {
                reduce {
                    copy(
                        step = OnBoardingInterviewStep.MainProject,
                        mainProjectRelevanceFailCount = nextCount,
                        mainProjectError = MESSAGE_FREETEXT_NOT_RELEVANT,
                        loadingBasicInfo = OnBoardingLoadingStepStatus.Waiting,
                        loadingJd = OnBoardingLoadingStepStatus.Waiting,
                        loadingPortfolio = OnBoardingLoadingStepStatus.Waiting,
                        errorMessage = null,
                    )
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

        /** PDF 검증 결과를 사용자 메시지로 변환한다. [PdfValidationResult.Valid]이면 null. */
        private fun PdfValidationResult.toErrorMessageOrNull(): String? =
            when (this) {
                PdfValidationResult.Valid -> {
                    null
                }

                is PdfValidationResult.Invalid -> {
                    when (reason) {
                        PdfInvalidReason.INVALID_FILE_SIZE -> MESSAGE_PDF_SIZE

                        PdfInvalidReason.INVALID_PAGE_COUNT -> MESSAGE_PDF_PAGE

                        PdfInvalidReason.PASSWORD_REQUIRED -> MESSAGE_PDF_PASSWORD

                        PdfInvalidReason.UNKNOWN_FILE_SIZE,
                        PdfInvalidReason.NOT_SEEKABLE,
                        PdfInvalidReason.INVALID_PDF_FORMAT,
                        -> MESSAGE_PDF_CORRUPT
                    }
                }

                is PdfValidationResult.Error -> {
                    MESSAGE_PDF_CORRUPT
                }
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
            const val FREETEXT_MIN_LENGTH = 10
            const val FREETEXT_MAX_LENGTH = 300
            const val MESSAGE_FREETEXT_TOO_SHORT = "집중 프로젝트 설명은 10자 이상 입력해 주세요"
            const val MESSAGE_FREETEXT_NOT_RELEVANT =
                "포트폴리오에서 그 내용을 찾지 못했어요. 포트폴리오에 있는 프로젝트로 다시 적어주세요"

            /** 연관성 실패가 이 횟수에 도달하면 재선택 다이얼로그로 이스케이프 경로를 제공한다. */
            const val RELEVANCE_FAIL_ESCAPE_THRESHOLD = 4
            const val MESSAGE_PORTFOLIO_REQUIRED = "포트폴리오를 업로드해주세요"
            const val MESSAGE_PDF_SIZE = "파일이 너무 커요. 20MB 이하 PDF로 올려주세요"
            const val MESSAGE_PDF_PAGE = "페이지가 너무 많아요. 30페이지 이하 PDF로 올려주세요"
            const val MESSAGE_PDF_PASSWORD = "암호가 걸린 PDF는 열 수 없어요. 암호를 푼 PDF로 올려주세요"
            const val MESSAGE_PDF_CORRUPT = "파일이 손상된 것 같아요. 파일을 확인하고 다시 시도해 주세요"
        }
    }
