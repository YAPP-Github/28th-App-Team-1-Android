package com.dminus14.app.domain.repository

/**
 * 지인 피드백 공유 링크 token 의 기기 내 저장소 계약이다.
 *
 * 링크 생성([FeedbackShareRepository.createShare]) 성공 시 저장하고, 종료
 * ([FeedbackShareRepository.closeShare]) 성공 시 지운다. 화면 진입 시 이 값의 존재 여부로
 * 하단 버튼을 "피드백 링크 생성"/"피드백 종료하기"로 분기한다.
 */
interface FeedbackShareLocalRepository {
    suspend fun getToken(sessionId: Long): String?

    suspend fun saveToken(
        sessionId: Long,
        token: String,
    )

    suspend fun clearToken(sessionId: Long)
}
