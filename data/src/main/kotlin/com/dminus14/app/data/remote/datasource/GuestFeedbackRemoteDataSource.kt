package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackEntryResponseDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackRatingDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackSubmitResponseDto

/** Retrofit 세부 구성을 노출하지 않고 Guest Feedback 원격 작업을 제공하는 data 계층 계약이다. */
interface GuestFeedbackRemoteDataSource {
    /** 공유 token의 진입 상태와 평가 대상 정보를 조회한다. */
    suspend fun enter(token: String): GuestFeedbackEntryResponseDto

    /** nullable nickname과 평가 목록을 비회원 피드백으로 제출한다. */
    suspend fun submit(
        token: String,
        nickname: String?,
        ratings: List<GuestFeedbackRatingDto>,
    ): GuestFeedbackSubmitResponseDto
}
