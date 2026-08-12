package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.FeedbackShareCreateResponseDto
import com.dminus14.app.data.remote.dto.FeedbackShareStatusResponseDto
import com.dminus14.app.data.remote.dto.GuestFeedbackAxisCodeDto

/** Retrofit 세부 구성을 노출하지 않고 FeedbackShare 원격 작업을 제공하는 data 계층 계약이다. */
interface FeedbackShareRemoteDataSource {
    /** 세션의 공유 링크 상태와 참여 현황을 조회한다. */
    suspend fun getStatus(sessionId: Long): FeedbackShareStatusResponseDto

    /** 지정 평가 항목으로 공유 링크를 생성한다. */
    suspend fun create(
        sessionId: Long,
        axes: List<GuestFeedbackAxisCodeDto>,
    ): FeedbackShareCreateResponseDto

    /** 공유 링크를 비공개로 전환한다. */
    suspend fun makePrivate(sessionId: Long)
}
