package com.dminus14.app.data.remote.authenticator

import android.util.Log
import com.dminus14.app.data.remote.mapper.ApiErrorBodyParser
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.SessionException
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
 * 스펙상 재발급 호출 시점:
 * - [ApiErrorCode.TOKEN_EXPIRED] — AccessToken 만료
 * - [ApiErrorCode.INVALID_TOKEN] — AccessToken 무효
 *
 * 재발급 API가 [ApiErrorCode.LOGIN_EXPIRED]를 반환하면 RefreshToken도 만료된 것이므로 세션을
 * 초기화한다. 로그인 화면 이동은 관련 feature가 아직 없어 TODO로만 남긴다.
 *
 * 동시에 여러 요청이 401을 받아도 [refreshMutex]로 단일 갱신을 보장하고, 이미 갱신된 토큰이 있으면
 * 재발급 호출 없이 새 토큰으로만 재시도한다. Rotation 방식이므로 재발급 응답의 Access/Refresh
 * 토큰을 모두 저장해야 한다([SessionRepository.refreshToken]).
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
         * 401이면서 재시도 한도 이내이고, 에러 코드가 재발급 대상
         * ([ApiErrorCode.TOKEN_EXPIRED] / [ApiErrorCode.INVALID_TOKEN])일 때만 재발급을 시도한다.
         * 파싱 실패·미인식 코드를 포함한 그 외 401은 상위 에러 처리로 넘긴다.
         */
        private fun shouldAttemptRefresh(response: Response): Boolean =
            response.code == HttpURLConnection.HTTP_UNAUTHORIZED &&
                responseCount(response) < MAX_RETRY_COUNT &&
                ApiErrorCode.requiresTokenRefresh(ApiErrorBodyParser.parse(response)?.code)

        private fun refreshAndRetry(response: Response): Request? {
            val usedAccessToken =
                response.request
                    .header(HEADER_AUTHORIZATION)
                    ?.removePrefix(BEARER_PREFIX)

            return runBlocking {
                refreshMutex.withLock {
                    val currentSession = sessionRepository.getAuthSession() ?: return@withLock null

                    if (currentSession.accessToken != usedAccessToken) {
                        return@withLock retryWith(response, currentSession.accessToken)
                    }

                    val refreshedSession =
                        runCatching { sessionRepository.refreshToken(currentSession.refreshToken) }
                            .getOrElse { error ->
                                if (error is SessionException) {
                                    sessionRepository.clearAuthSession()
                                    // TODO(session): 로그인 화면으로 이동 처리
                                    // 로그인 세션 만료를 앱 전역에 알릴 이벤트/네비게이션 연동이 필요하지만
                                    // 아직 관련 로그인 feature가 구현되어 있지 않아 주석으로 남긴다.
                                }
                                return@withLock null
                            }

                    Log.d(TAG, "refresh success, retry original request")
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
            const val TAG = "TokenAuthenticator"
            const val HEADER_AUTHORIZATION = "Authorization"
            const val BEARER_PREFIX = "Bearer "
            const val MAX_RETRY_COUNT = 3
        }
    }
