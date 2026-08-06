package com.dminus14.app.feature.interview.api

/** 면접 진행 화면 route */
data object InterviewRoute

/** 면접 오류 유형 */
enum class InterviewErrorType {
    MIC,
    NETWORK,
}

/** 면접 오류 화면 route */
data class InterviewErrorRoute(
    val errorType: InterviewErrorType,
)
