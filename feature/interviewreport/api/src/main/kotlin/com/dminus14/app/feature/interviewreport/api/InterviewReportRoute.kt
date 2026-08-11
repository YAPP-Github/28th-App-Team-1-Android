@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.feature.interviewreport.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Navigation 3 면접 리포트 목적지 key. */
@Serializable
data class InterviewReport(
    val sessionId: Long,
) : NavKey

/** Navigation 3 면접 리포트 영상 플레이어 목적지 key. */
@Serializable
data class InterviewReportPlayer(
    val sessionId: Long,
    val startSec: Float? = null,
) : NavKey
