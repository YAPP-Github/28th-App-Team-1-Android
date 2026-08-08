package com.dminus14.app.domain.model

/**
 * 온보딩·프로필 등록 화면에서 선택 가능한 직무 옵션.
 *
 * @property jobId 직무 ID.
 * @property jobRole 서버 소유 직무 enum raw 값 (예: `BACKEND`).
 * @property label 직무 한글 표시명 (예: `백엔드 개발자`).
 */
data class Job(
    val jobId: Int,
    val jobRole: String,
    val label: String,
)
