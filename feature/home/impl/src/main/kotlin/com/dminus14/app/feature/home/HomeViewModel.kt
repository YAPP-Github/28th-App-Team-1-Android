package com.dminus14.app.feature.home

import androidx.lifecycle.viewModelScope
import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.common.mvi.MviViewModel
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val checkUserProfileUseCase: CheckUserProfileUseCase,
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
         * 홈 진입 시 유저 프로필을 조회해 인사말에 표시할 이름을 채운다.
         *
         * 이름이 비어 있으면 필수 프로필 미완성으로 보고 온보딩(직무·연차 입력)으로 라우팅한다.
         * `UserNotFoundException`은 세션은 있으나 서버에 유저가 없어 재부팅이 필요한 상태이므로
         * 스플래시로 되돌린다.
         *
         * 리포트 목록은 API 배선 전까지 [PreviewHomeReports] 목업으로 채운다.
         */
        private fun load() {
            reduce {
                copy(
                    isLoading = true,
                    reports = PreviewHomeReports,
                    expandedReportIds = setOfNotNull(PreviewHomeReports.firstOrNull()?.id),
                )
            }
            viewModelScope.launch {
                checkUserProfileUseCase()
                    .onSuccess { profile ->
                        val displayName = profile.name?.takeIf { it.isNotBlank() }
                        if (displayName == null) {
                            reduce { copy(isLoading = false) }
                            sendEffect(HomeEffect.UserNameNotRegistered)
                        } else {
                            reduce {
                                copy(
                                    isLoading = false,
                                    userName = displayName,
                                )
                            }
                        }
                    }.onFailure { error ->
                        when (error) {
                            is UserNotFoundException -> {
                                reduce { copy(isLoading = false) }
                                sendEffect(HomeEffect.UserNotFound)
                            }

                            else -> {
                                handleBootstrapFailure(error)
                            }
                        }
                    }
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
