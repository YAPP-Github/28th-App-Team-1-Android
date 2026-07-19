package com.dminus14.app.feature.login.kakao

import android.app.Activity
import com.dminus14.app.domain.model.KakaoAuthException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class KakaoLoginClient
    @Inject
    constructor() {
        suspend fun login(activity: Activity): String =
            suspendCancellableCoroutine { continuation ->
                val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                    when {
                        error != null -> continuation.resumeWithException(mapKakaoError(error))
                        token != null -> continuation.resume(token.accessToken)
                        else -> continuation.resumeWithException(KakaoAuthException.Unknown())
                    }
                }

                if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
                    UserApiClient.instance.loginWithKakaoTalk(activity) { token, error ->
                        when {
                            token != null -> {
                                continuation.resume(token.accessToken)
                            }

                            error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                                continuation.resumeWithException(KakaoAuthException.Cancelled)
                            }

                            error != null -> {
                                UserApiClient.instance.loginWithKakaoAccount(
                                    activity,
                                    callback = callback,
                                )
                            }

                            else -> {
                                continuation.resumeWithException(KakaoAuthException.Unknown())
                            }
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
                }
            }

        private fun mapKakaoError(error: Throwable): KakaoAuthException =
            when (error) {
                is ClientError if error.reason == ClientErrorCause.Cancelled -> {
                    KakaoAuthException.Cancelled
                }

                is AuthError if error.reason == AuthErrorCause.AccessDenied -> {
                    KakaoAuthException.AccessDenied
                }

                is AuthError if error.reason == AuthErrorCause.ServerError -> {
                    KakaoAuthException.Server(cause = error)
                }

                is AuthError if error.reason == AuthErrorCause.Unknown -> {
                    KakaoAuthException.Unknown(cause = error)
                }

                is AuthError -> {
                    KakaoAuthException.Client(
                        message = error.response.errorDescription ?: "카카오 로그인에 실패했습니다.",
                        cause = error,
                    )
                }

                is ClientError -> {
                    KakaoAuthException.Client(
                        message = error.msg.ifBlank { "카카오 로그인에 실패했습니다." },
                        cause = error,
                    )
                }

                else -> {
                    KakaoAuthException.Unknown(
                        message = error.message.orEmpty(),
                        cause = error,
                    )
                }
            }
    }
