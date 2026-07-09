package com.dminus14.app.data.remote.datasource

import android.content.Context
import com.dminus14.app.domain.model.KakaoAuthException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class KakaoAuthDataSource
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        suspend fun loginWithKakao(): OAuthToken =
            suspendCancellableCoroutine { continuation ->
                val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                    when {
                        error != null -> continuation.resumeWithException(mapKakaoError(error))
                        token != null -> continuation.resume(token)
                        else -> continuation.resumeWithException(KakaoAuthException.Unknown())
                    }
                }

                if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                    UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                        when {
                            token != null -> continuation.resume(token)
                            error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                                continuation.resumeWithException(KakaoAuthException.Cancelled)
                            }
                            error != null -> {
                                // 카카오톡 미로그인(AuthError Unknown/LoginRequired) 등 → 카카오계정 로그인으로 전환
                                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                            }
                            else -> continuation.resumeWithException(KakaoAuthException.Unknown())
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
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
