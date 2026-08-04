package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionResult
import com.dminus14.app.domain.model.InterviewSessionStatus
import com.dminus14.app.domain.model.JdValidationResult

interface InterviewRepository {
    /**
     * JD URL을 크롤링·정제해 유효성을 검사한다.
     *
     * Access Token은 네트워크 레이어에서 자동 부착된다.
     */
    suspend fun validateJdUrl(jdUrl: String): JdValidationResult

    /**
     * 면접 세션 생성을 접수한다.
     *
     * Access Token은 네트워크 레이어에서 자동 부착된다.
     * 성공 시 HTTP 202와 함께 [InterviewSessionResult.status]가
     * [com.dminus14.app.domain.model.InterviewSessionStatusType.PROCESSING]으로 온다.
     * [InterviewSessionRequest.jdUrl]과 [InterviewSessionRequest.jdText]는 상호 배타적이다.
     */
    suspend fun createInterviewSession(request: InterviewSessionRequest): InterviewSessionResult

    /**
     * 면접 세션 준비 상태를 조회한다.
     *
     * 세션 생성 후 폴링(권장 3~5초)에 사용한다.
     */
    suspend fun getInterviewSessionStatus(sessionId: Long): InterviewSessionStatus
}
