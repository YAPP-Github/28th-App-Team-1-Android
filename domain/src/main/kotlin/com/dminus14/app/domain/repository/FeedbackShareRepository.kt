package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.FeedbackShare
import com.dminus14.app.domain.model.GuestFeedbackAxisCode

/**
 * 소유자(면접 응시자)가 지인 피드백 공유 링크를 생성·조회·비공개 전환하는 계약이다.
 *
 * 면접 세션당 활성 링크는 1개이며, 재생성과 재공개는 지원하지 않는다.
 */
interface FeedbackShareRepository {
    /** 세션의 공유 링크 상태와 참여 현황을 조회한다. */
    suspend fun getStatus(sessionId: Long): FeedbackShare

    /** 지정 평가 항목으로 공유 링크를 생성하고 새 공유 token을 반환한다. */
    suspend fun create(
        sessionId: Long,
        axes: List<GuestFeedbackAxisCode>,
    ): String

    /** 공유 링크를 비공개로 전환한다. */
    suspend fun makePrivate(sessionId: Long)
}
