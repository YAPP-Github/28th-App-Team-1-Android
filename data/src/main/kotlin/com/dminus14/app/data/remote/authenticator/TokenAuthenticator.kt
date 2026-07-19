package com.dminus14.app.data.remote.authenticator

import com.dminus14.app.domain.model.LoginExpiredException
import com.dminus14.app.domain.repository.SessionRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 모든 API 요청에서 401 응답을 감지해 AccessToken 재발급을 시도하는 OkHttp [Authenticator].
 *
 * - AccessToken 만료(`TOKEN_EXPIRED`) 시 refresh를 시도하고 성공하면 원 요청을 새 토큰으로 재시도한다.
 * - RefreshToken 만료(`LOGIN_EXPIRED`) 시에는 세션을 초기화한다. 로그인 화면 이동은 관련 feature가
 *   아직 없어 TODO로만 남긴다.
 * - refresh 호출도 동일 [okhttp3.OkHttpClient]를 사용하므로, refresh 엔드포인트 자체가 401을 받으면
 *   재귀 호출 없이 즉시 포기한다.
 * - 동시에 여러 요청이 401을 받아도 [refreshMutex]로 단일 갱신을 보장하고, 이미 갱신된 토큰이 있으면
 *   재발급 호출 없이 새 토큰으로만 재시도한다.
 */
@Singleton
class TokenAuthenticator
    @Inject
    constructor(
        private val sessionRepository: SessionRepository,
    ) : Authenticator {
        private val refreshMutex = Mutex()

        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            if (!shouldAttemptRefresh(response)) {
                return null
            }
            return refreshAndRetry(response)
        }

        /**
         * 401 응답이면서 재시도 한도 이내이고, refresh 엔드포인트 자체의 401(재귀 호출)이 아닐 때만 재발급을 시도한다.
         * refresh 엔드포인트 자체가 401을 받으면 즉시 포기한다. LOGIN_EXPIRED 판단과 세션 초기화는
         * 이 401이 예외로 전파된 뒤 원 요청 쪽의 [refreshAndRetry] 호출에서 처리한다.
         */
        private fun shouldAttemptRefresh(response: Response): Boolean =
            response.code == HttpURLConnection.HTTP_UNAUTHORIZED &&
                responseCount(response) < MAX_RETRY_COUNT &&
                !response.request.url.encodedPath
                    .endsWith(REFRESH_TOKEN_PATH)

        private fun refreshAndRetry(response: Response): Request? {
            val usedAccessToken =
                response.request
                    .header(HEADER_AUTHORIZATION)
                    ?.removePrefix(BEARER_PREFIX)

            return runBlocking {
                refreshMutex.withLock {
                    val currentSession = sessionRepository.getAuthSession() ?: return@withLock null

                    if (currentSession.accessToken != usedAccessToken) {
                        // 다른 요청이 이미 재발급을 완료한 경우, 재발급 호출 없이 새 토큰으로만 재시도한다.
                        return@withLock retryWith(response, currentSession.accessToken)
                    }

                    val refreshedSession =
                        runCatching { sessionRepository.refreshToken(currentSession.refreshToken) }
                            .getOrElse { error ->
                                if (error is LoginExpiredException) {
                                    sessionRepository.clearAuthSession()
                                    // TODO(session): 로그인 화면으로 이동 처리
                                    // 로그인 세션 만료를 앱 전역에 알릴 이벤트/네비게이션 연동이 필요하지만
                                    // 아직 관련 로그인 feature가 구현되어 있지 않아 주석으로 남긴다.
                                }
                                // 네트워크/서버 등 그 외 오류는 세션을 유지한 채 이번 요청만 실패시킨다.
                                return@withLock null
                            }

                    retryWith(response, refreshedSession.accessToken)
                }
            }
        }

        private fun retryWith(
            response: Response,
            accessToken: String,
        ): Request =
            response.request
                .newBuilder()
                .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX$accessToken")
                .build()

        private fun responseCount(response: Response): Int {
            var count = 1
            var prior = response.priorResponse
            while (prior != null) {
                count++
                prior = prior.priorResponse
            }
            return count
        }

        private companion object {
            const val HEADER_AUTHORIZATION = "Authorization"
            const val BEARER_PREFIX = "Bearer "
            const val MAX_RETRY_COUNT = 3
            const val REFRESH_TOKEN_PATH = "/api/v1/auth/token/refresh"
        }
    }
