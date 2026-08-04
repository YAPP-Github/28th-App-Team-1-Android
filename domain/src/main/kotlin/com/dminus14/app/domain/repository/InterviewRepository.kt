package com.dminus14.app.domain.repository

import com.dminus14.app.domain.model.InterviewAbandon
import com.dminus14.app.domain.model.InterviewReport
import com.dminus14.app.domain.model.InterviewReportList
import com.dminus14.app.domain.model.InterviewResumeConfirm
import com.dminus14.app.domain.model.InterviewResumeStatus
import com.dminus14.app.domain.model.InterviewSessionRequest
import com.dminus14.app.domain.model.InterviewSessionResult
import com.dminus14.app.domain.model.InterviewSessionStatus
import com.dminus14.app.domain.model.InterviewVideoExpiry
import com.dminus14.app.domain.model.InterviewVideoUploadUrl
import com.dminus14.app.domain.model.JdValidationResult
import com.dminus14.app.domain.model.SubmitAnswerResult
import java.io.File

@Suppress("TooManyFunctions")
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
    suspend fun getInterviewSession(sessionId: Long): InterviewSessionStatus

    /**
     * 면접 세션 준비 상태를 조회한다. (하위 호환)
     */
    suspend fun getInterviewSessionStatus(sessionId: Long): InterviewSessionStatus

    /**
     * 면접 리포트 목록을 조회한다.
     */
    suspend fun getReportList(): InterviewReportList

    /**
     * 면접 답변을 제출한다.
     */
    @Suppress("LongParameterList")
    suspend fun submitAnswer(
        sessionId: Long,
        questionId: Long,
        isWrapUp: Boolean,
        questionAudioStartAt: Float? = null,
        questionAudioEndAt: Float? = null,
        answerStartAt: Float? = null,
        answerEndAt: Float? = null,
        answerDuration: Float? = null,
        endType: String? = null,
        audioFile: File? = null,
    ): SubmitAnswerResult

    /**
     * 면접 질문 음성 스트리밍 URL을 반환한다.
     */
    fun getAudioStreamUrl(
        sessionId: Long,
        questionId: Long,
    ): String

    /**
     * 면접 재개 상태를 조회한다.
     */
    suspend fun getResume(sessionId: Long): InterviewResumeStatus

    /**
     * 면접 재개를 확정한다.
     */
    suspend fun confirmResume(sessionId: Long): InterviewResumeConfirm

    /**
     * 면접을 포기(중단)한다.
     */
    suspend fun abandon(
        sessionId: Long,
        cause: String,
    ): InterviewAbandon

    /**
     * 면접 리포트 상세를 조회한다.
     */
    suspend fun getReport(sessionId: Long): InterviewReport

    /**
     * 면접 영상 업로드 URL을 발급받는다.
     */
    suspend fun issueUploadUrl(sessionId: Long): InterviewVideoUploadUrl

    /**
     * 면접 영상 업로드 완료를 보고한다.
     */
    suspend fun completeUpload(
        sessionId: Long,
        wrapUpStartSec: Float? = null,
        wrapUpEndSec: Float? = null,
    )

    /**
     * 면접 영상 만료 정보를 조회한다.
     */
    suspend fun getExpiry(sessionId: Long): InterviewVideoExpiry
}
