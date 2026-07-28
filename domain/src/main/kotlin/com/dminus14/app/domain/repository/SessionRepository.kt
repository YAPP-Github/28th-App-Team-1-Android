package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.AuthSession

/**
 * 로그인 세션(AccessToken/RefreshToken)의 조회, 재발급, 저장, 삭제를 담당한다.
 *
 * 소셜 로그인 자체는 [AuthRepository]가 담당하며, 이 인터페이스는 이미 발급된 세션의
 * 라이프사이클만 다룬다. `TokenAuthenticator`가 401 응답 처리 시 의존하는 대상이 이 인터페이스로
 * 한정되어, 소셜 로그인 관련 의존성과 얽히지 않는다.
 */
interface SessionRepository {
    suspend fun getAuthSession(): AuthSession?

    suspend fun refreshToken(refreshToken: String): AuthSession

    suspend fun saveAuthSession(
        accessToken: String,
        refreshToken: String,
    ): AuthSession

    suspend fun clearAuthSession()
}
