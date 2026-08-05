package com.dminus14.app.data.remote.mapper

import com.dminus14.app.data.remote.dto.user.UserProfileFetchResponseDto
import com.dminus14.app.data.remote.dto.user.UserProfileUpdateRequestDto
import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.model.UserProfileUpdate

/** 조회 응답의 null을 기본값으로 바꾸지 않고 회원 프로필 Domain 모델로 변환한다. */
internal fun UserProfileFetchResponseDto.toDomain(): UserProfile =
    UserProfile(
        name = name,
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
