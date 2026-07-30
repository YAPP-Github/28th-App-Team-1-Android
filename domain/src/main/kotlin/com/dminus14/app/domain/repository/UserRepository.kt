package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.UserProfile

/**
 * 회원 프로필(이름, 직무, 연차, 잔여 이용권 수) 조회를 담당한다.
 */
interface UserRepository {
    suspend fun getUserProfile(): UserProfile
}
