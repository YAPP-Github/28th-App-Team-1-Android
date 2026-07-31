package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.GuestFeedbackEntry
import com.dminus14.app.domain.model.GuestFeedbackSubmission

/**
 * 비회원 지인 피드백의 진입 조회와 확정 제출을 담당한다.
 *
 * 공유 token과 피드백 데이터는 진행 중 메모리에서만 사용하며 이 계약은 로컬 저장이나 임시저장을
 * 제공하지 않는다. 외부 오류는 Guest Feedback Domain 오류 또는 프로젝트 공통 오류로 전달된다.
 */
interface GuestFeedbackRepository {
    /** 공유 token의 게이트와 작성 데이터를 조회한다. non-OPEN 게이트도 정상 결과로 반환한다. */
    suspend fun enter(token: String): GuestFeedbackEntry

    /** 정규화와 검증이 끝난 피드백을 제출하며 서버의 제출 ID와 시각은 노출하지 않는다. */
    suspend fun submit(
        token: String,
        submission: GuestFeedbackSubmission,
    )
}
