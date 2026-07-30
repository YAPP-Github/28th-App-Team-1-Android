package com.dminus14.app.domain.model

/**
 * 회원 프로필 정보.
 *
 * @property email 유저 이메일
 * @property provider 유저 가입 sns
 * @property jobRole 서버가 내려주는 직무 코드 (예: `BACKEND`).
 * @property jobRoleLabel [jobRole]에 대응하는 화면 표시용 라벨 (예: `백엔드`).
 * @property remainingTicketCount 잔여 이용권 수.
 */
data class UserProfile(
    val name: String,
    val email: String,
    val provider: String,
    val jobRole: String,
    val jobRoleLabel: String,
    val careerYears: Int,
    val remainingTicketCount: Int,
)
