package com.dminus14.app.feature.interview.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** 면접 진행 화면 route */
@Serializable
data object InterviewRoute : NavKey

/** 면접 오류 유형 */
enum class InterviewErrorType {
    MIC,
    NETWORK,
}

/** 면접 오류 화면 route */
@Serializable
data class InterviewErrorRoute(
    val errorType: InterviewErrorType,
) : NavKey
