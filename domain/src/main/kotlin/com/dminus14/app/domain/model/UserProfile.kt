package com.dminus14.app.domain.model

/**
 * 회원 프로필 정보.
 *
 * @property name 유저 닉네임. 서버가 아직 설정하지 않은 경우 null일 수 있다.
 * @property email 유저 이메일. 서버가 아직 설정하지 않은 경우 null일 수 있다.
 * @property provider 유저 가입 sns
 * @property jobRole 서버가 내려주는 직무 코드 (예: `BACKEND`).
 * @property jobRoleLabel [jobRole]에 대응하는 화면 표시용 라벨 (예: `백엔드`).
 * @property careerYears 연차.
 * @property remainingTicketCount 잔여 이용권 수.
 */
data class UserProfile(
    val name: String?,
    val email: String?,
    val provider: String,
    val jobRole: String,
    val jobRoleLabel: String,
    val careerYears: Int,
    val remainingTicketCount: Int,
) {
    /** 닉네임이 없으면 온보딩이 필요하다. */
    val requiresOnboarding: Boolean
        get() = name.isNullOrBlank()
}
