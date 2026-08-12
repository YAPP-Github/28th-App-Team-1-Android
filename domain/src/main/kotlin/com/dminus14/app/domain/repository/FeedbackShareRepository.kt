package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.GuestFeedbackAxisCode

/**
 * 리포트 화면에서 지인에게 영상을 공유할 링크(토큰)를 생성하는 호스트(본인) 계약이다.
 *
 * 최초 생성이 곧 '피드백 요청' 사건이며 면접당 활성 링크는 1개다. 공유 token 은 진행 중
 * 메모리에서만 사용하고 저장하지 않는다.
 */
interface FeedbackShareRepository {
    /**
     * 선택한 태도 항목([axes], 1~5개)으로 공유 링크를 생성하고 딥링크 조립용 token 을 반환한다.
     */
    suspend fun createShare(
        sessionId: Long,
        axes: List<GuestFeedbackAxisCode>,
    ): String
}
