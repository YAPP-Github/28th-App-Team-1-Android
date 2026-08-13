package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.GuestFeedbackAxisCode

/**
 * 리포트 화면에서 지인에게 영상을 공유할 링크(토큰)를 생성·종료하는 호스트(본인) 원격 계약이다.
 *
 * 최초 생성이 곧 '피드백 요청' 사건이며 면접당 활성 링크는 1개다. 이 계약 자체는 token 을
 * 저장하지 않는다 — 기기 저장은 [FeedbackShareLocalRepository] 가 맡고,
 * `CreateFeedbackShareUseCase`/`EndFeedbackShareUseCase` 가 두 Repository 를 함께 호출해
 * 원격 결과와 로컬 저장을 맞춘다.
 */
interface FeedbackShareRepository {
    /**
     * 선택한 태도 항목([axes], 1~5개)으로 공유 링크를 생성하고 딥링크 조립용 token 을 반환한다.
     */
    suspend fun createShare(
        sessionId: Long,
        axes: List<GuestFeedbackAxisCode>,
    ): String

    /**
     * 공유 링크를 비공개로 전환해 지인 피드백 요청을 종료한다. 되돌릴 수 없다(feedback.md).
     */
    suspend fun closeShare(sessionId: Long)
}
