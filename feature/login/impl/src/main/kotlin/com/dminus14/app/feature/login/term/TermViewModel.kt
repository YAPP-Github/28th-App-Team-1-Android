package com.dminus14.app.feature.login.term

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.core.permission.AppPermission
import com.dminus14.app.core.permission.PermissionManager
import com.dminus14.app.domain.exception.ConsentVersionMismatchException
import com.dminus14.app.domain.exception.InvalidConsentItemException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.RequiredConsentMissingException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.exception.ValidationException
import com.dminus14.app.domain.model.ConsentPendingStatus
import com.dminus14.app.domain.model.ConsentSubmission
import com.dminus14.app.domain.model.ConsentSubmissionItem
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.GetConsentDocumentUseCase
import com.dminus14.app.domain.usecase.GetPendingConsentListUseCase
import com.dminus14.app.domain.usecase.SubmitConsentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermViewModel
@Inject
constructor(
    private val getPendingConsentList: GetPendingConsentListUseCase,
    private val getConsentDocument: GetConsentDocumentUseCase,
    private val submitConsent: SubmitConsentUseCase,
    private val checkUserProfile: CheckUserProfileUseCase,
    private val permissionManager: PermissionManager,
) : MviViewModel<TermIntent, TermState, TermEffect>(TermState()) {
    /** 테스트 전용: 초기 State를 주입한다. UseCase는 실제 fake로 넘겨야 한다. */
    internal constructor(
        getPendingConsentList: GetPendingConsentListUseCase,
        getConsentDocument: GetConsentDocumentUseCase,
        submitConsent: SubmitConsentUseCase,
        checkUserProfile: CheckUserProfileUseCase,
        permissionManager: PermissionManager,
        initialState: TermState,
    ) : this(
        getPendingConsentList,
        getConsentDocument,
        submitConsent,
        checkUserProfile,
        permissionManager,
    ) {
        reduce { initialState }
    }

    override fun onIntent(intent: TermIntent) {
        when (intent) {
            TermIntent.Load -> {
                loadPending()
            }

            TermIntent.ClickClose -> {
                sendEffect(TermEffect.Closed)
            }

            TermIntent.ClickAllAgree -> {
                toggleAllAgree()
            }

            is TermIntent.ClickTerm -> {
                toggleTerm(intent.index)
            }

            is TermIntent.ClickViewTerm -> {
                openTermDetail(intent.index)
            }

            TermIntent.DismissTermDetail -> {
                reduce {
                    copy(
                        visibleTermDetailIndex = null,
                        visibleTermDetail = null,
                    )
                }
            }

            TermIntent.ClickAgree -> {
                submit()
            }
        }
    }

    private fun loadPending() {
        if (state.value.isLoading) return
        reduce { copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            getPendingConsentList()
                .onSuccess { pending ->
                    when (pending.status) {
                        ConsentPendingStatus.NOT_SUBMITTED,
                        ConsentPendingStatus.STALE,
                        ConsentPendingStatus.UNKNOWN,
                            -> {
                            reduce {
                                copy(
                                    terms = pending.items,
                                    isLoading = false,
                                )
                            }
                        }

                        ConsentPendingStatus.UP_TO_DATE -> {
                            // 프로필상태 확인
                        }
                    }
                }.onFailure { error ->
                    handleLoadFailure(error)
                }
        }
    }

    /**
     * 현재 체크 상태를 그대로 서버에 제출한다. 필수·선택 항목을 모두 담되 항목별 `agreed`에
     * 체크 여부를 실어 보낸다(선택 항목의 거부 이력도 서버에 저장된다). `version`은 pending에서
     * 내려준 값을 그대로 사용한다. 최초 성공 제출 시 서버가 무료 이용권 3회를 부여한다.
     */
    private fun submit() {
        if (!state.value.canSubmit) return
        reduce { copy(isLoading = true, errorMessage = null) }
        val submission =
            ConsentSubmission(
                items =
                    state.value.terms.map { term ->
                        ConsentSubmissionItem(
                            rawCode = term.rawCode,
                            version = term.version,
                            agreed = term.isChecked,
                        )
                    },
            )
        viewModelScope.launch {
            submitConsent(submission)
                .onSuccess {
                    reduce { copy(isLoading = false) }
                    checkPermissionConsent()
                }.onFailure { error ->
                    when (error) {
                        // 버전 불일치는 목록을 재조회한다. loadPending()의 isLoading 가드 때문에
                        // 재조회 전에 제출 로딩을 먼저 해제한다.
                        is ConsentVersionMismatchException -> {
                            reduce { copy(isLoading = false) }
                            sendEffect(TermEffect.ShowToast(error.message.orEmpty()))
                            loadPending()
                        }

                        // 사용자가 조치 가능한 입력 오류는 Toast로 안내한다.
                        is RequiredConsentMissingException,
                        is InvalidConsentItemException,
                        is ValidationException,
                            -> {
                            reduce { copy(isLoading = false) }
                            sendEffect(TermEffect.ShowToast(error.message.orEmpty()))
                        }

                        // 그 외 전송·서버·알 수 없는 오류는 공통 처리로 위임한다.
                        else -> handleLoadFailure(error)
                    }
                }
        }
    }

    /**
     * 약관 제출 성공 후 권한 동의 여부를 확인해 다음 화면을 분기한다.
     * 권한이 이미 허용돼 있으면 프로필을 조회하고, 아니면 DeniedPerm(권한 동의 화면)으로 보낸다.
     */
    private suspend fun checkPermissionConsent() {
        val isGrantPerm = permissionManager.isGranted(AppPermission.CAMERA) &&
            permissionManager.isGranted(AppPermission.RECORD_AUDIO)

        if (isGrantPerm) {
            routeByProfile()
        } else {
            sendEffect(TermEffect.DeniedPerm)
        }
    }

    /**
     * 프로필 등록 여부에 따라 이동을 분기한다.
     * 프로필이 있으면 홈(ExistProfile), 미등록(UserNotFound)이면 온보딩(NonExistProfile)으로 보낸다.
     */
    private suspend fun routeByProfile() {
        checkUserProfile()
            .onSuccess {
                sendEffect(TermEffect.ExistProfile)
            }.onFailure { error ->
                when (error) {
                    is UserNotFoundException -> sendEffect(TermEffect.NonExistProfile)
                    else -> handleLoadFailure(error)
                }
            }
    }

    // 아래 에러 처리 사항은 임시입니다. 공통 처리 기획자 문의 모든 ViewModel 일괄 수정 예정
    private suspend fun handleLoadFailure(error: Throwable) {
        reduce { copy(isLoading = false) }
        when (error) {
            is NetworkUnavailableException -> {
                GlobalErrorHandler.emit(GlobalAppEvent.ShowNetworkErrorAndExit)
            }

            is ServerException -> {
                GlobalErrorHandler.emit(GlobalAppEvent.ShowServerErrorAndExit)
            }

            else -> {
                GlobalErrorHandler.emit(GlobalAppEvent.ShowUnknownError)
            }
        }
    }

    private fun toggleAllAgree() {
        val checked = !state.value.isAllChecked
        reduce {
            copy(terms = terms.map { it.copy(isChecked = checked) })
        }
    }

    private fun toggleTerm(index: Int) {
        reduce {
            copy(
                terms =
                    terms.mapIndexed { termIndex, term ->
                        if (termIndex == index) {
                            term.copy(isChecked = !term.isChecked)
                        } else {
                            term
                        }
                    },
            )
        }
    }

    /**
     * ClickViewTerm(index)로 본문을 조회한다. 로컬 body가 이미 있으면 즉시 시트를 열고,
     * 서버 문서가 있으면(hasDocument=true) rawCode·version으로 조회한 뒤 body를 채우고 시트를 연다.
     */
    @Suppress("detekt:ReturnCount") // 가드 절이 중첩보다 명확하다.
    private fun openTermDetail(index: Int) {
        val term = state.value.terms.getOrNull(index) ?: return

        if (!term.hasDocument) return
        if (state.value.isLoading) return

        reduce { copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            getConsentDocument(rawCode = term.rawCode, version = term.version)
                .onSuccess { document ->
                    reduce {
                        copy(
                            isLoading = false,
                            visibleTermDetailIndex = index,
                            visibleTermDetail = TermDetailContent(
                                title = document.title,
                                content = document.contentMarkdown,
                            ),
                        )
                    }
                }.onFailure { error ->
                    reduce { copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }
}
