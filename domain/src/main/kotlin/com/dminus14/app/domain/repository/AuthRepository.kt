package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.AuthSession

/**
 * 소셜 로그인을 담당한다.
 *
 * 로그인 이후 세션의 조회/재발급/삭제는 [SessionRepository]가 담당한다.
 */
interface AuthRepository {
    suspend fun loginWithKakao(credential: String): AuthSession

    /** 서버에 저장된 Refresh Token을 폐기한다. */
    suspend fun logout()
}
