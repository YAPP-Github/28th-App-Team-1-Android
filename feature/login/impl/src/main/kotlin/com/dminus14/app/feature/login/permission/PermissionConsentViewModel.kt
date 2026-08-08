package com.dminus14.app.feature.login.permission

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.core.permission.PermissionManager
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionConsentViewModel
    @Inject
    constructor(
        private val checkUserProfile: CheckUserProfileUseCase,
        /** 화면에서 순차 권한 요청 헬퍼(PermissionManager 확장)를 호출하기 위해 노출한다. */
        val permissionManager: PermissionManager,
    ) : MviViewModel<PermissionConsentIntent, PermissionConsentState, PermissionConsentEffect>(
            PermissionConsentState(),
        ) {
        override fun onIntent(intent: PermissionConsentIntent) {
            when (intent) {
                PermissionConsentIntent.ClickAllow -> {
                    sendEffect(PermissionConsentEffect.RequestPermissions)
                }

                PermissionConsentIntent.ClickLater -> {
                    routeByProfile()
                }

                is PermissionConsentIntent.PermissionResult -> {
                    if (intent.allGranted) {
                        routeByProfile()
                    } else {
                        sendEffect(PermissionConsentEffect.NavigateDenied)
                    }
                }
            }
        }

        /**
         * 프로필 등록 여부로 다음 화면을 분기한다.
         * 이름·이메일 등 필수 프로필이 채워져 있으면 홈, 미등록(UserNotFound)이거나
         * 필수 값이 비어 있으면 온보딩으로 보낸다.
         */
        private fun routeByProfile() {
            reduce { copy(isLoading = true) }
            viewModelScope.launch {
                checkUserProfile()
                    .onSuccess { profile ->
                        reduce { copy(isLoading = false) }
                        if (profile.name.isNullOrBlank()) {
                            sendEffect(PermissionConsentEffect.NavigateOnboarding)
                        } else {
                            sendEffect(PermissionConsentEffect.NavigateHome)
                        }
                    }.onFailure { error ->
                        when (error) {
                            // API 예외: 프로필 미등록은 온보딩으로 보낸다.
                            is UserNotFoundException -> {
                                reduce { copy(isLoading = false) }
                                sendEffect(PermissionConsentEffect.NavigateOnboarding)
                            }

                            // 그 외 전송·서버·알 수 없는 오류는 공통 처리로 위임한다.
                            else -> {
                                handleCommonError(error)
                            }
                        }
                    }
            }
        }

        // 아래 에러 처리 사항은 임시입니다. 공통 처리 기획자 문의 모든 ViewModel 일괄 수정 예정
        private suspend fun handleCommonError(error: Throwable) {
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
    }
