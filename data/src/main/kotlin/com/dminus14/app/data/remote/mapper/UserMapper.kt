package com.dminus14.app.data.remote.mapper

import com.dminus14.app.data.remote.dto.user.JobDto
import com.dminus14.app.data.remote.dto.user.JobListResponseDto
import com.dminus14.app.data.remote.dto.user.UserProfileFetchResponseDto
import com.dminus14.app.data.remote.dto.user.UserProfileUpdateRequestDto
import com.dminus14.app.domain.model.Job
import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.model.UserProfileUpdate

/** 조회 응답의 null을 기본값으로 바꾸지 않고 회원 프로필 Domain 모델로 변환한다. */
internal fun UserProfileFetchResponseDto.toDomain(): UserProfile =
    UserProfile(
        name = name.orEmpty(),
        email = email,
        provider = provider,
        jobRole = jobRole,
        jobRoleLabel = jobRoleLabel,
        careerYears = careerYears,
        remainingTicketCount = remainingTicketCount,
    )

/** 이름 미변경을 null로 유지하면서 프로필 수정 Domain 입력을 요청 DTO로 변환한다. */
internal fun UserProfileUpdate.toDto(): UserProfileUpdateRequestDto =
    UserProfileUpdateRequestDto(
        name = name,
        jobRole = jobRole,
        careerYears = careerYears,
    )

/** 직무 목록 응답 DTO를 Domain 모델 리스트로 변환한다. */
internal fun JobListResponseDto.toDomain(): List<Job> = jobs.map { it.toDomain() }

internal fun JobDto.toDomain(): Job =
    Job(
        jobId = jobId,
        jobRole = jobRole,
        label = label,
    )
