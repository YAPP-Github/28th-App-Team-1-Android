package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.model.UserProfileUpdate

/**
 * 회원 프로필 조회와 수정 및 회원 탈퇴를 담당한다.
 */
interface UserRepository {
    suspend fun getUserProfile(): UserProfile

    suspend fun updateUserProfile(update: UserProfileUpdate)

    suspend fun withdraw()
}
