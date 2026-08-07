package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.Job
import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.model.UserProfileUpdate

/**
 * 회원 프로필 조회와 수정 및 회원 탈퇴를 담당한다.
 *
 * 또한 온보딩·프로필 등록 화면에서 사용하는 선택 가능한 직무 목록 조회를 제공한다.
 */
interface UserRepository {
    suspend fun getUserProfile(): UserProfile

    suspend fun updateUserProfile(update: UserProfileUpdate)

    suspend fun withdraw()

    /** 선택 가능한 직무 목록을 조회한다. */
    suspend fun getJobList(): List<Job>
}
