package com.dminus14.app.feature.interviewreport.model

import androidx.compose.runtime.Immutable

/**
 * 영상 플레이어가 렌더에 사용하는 UI 모델. 도메인 리포트에서 영상 URL·섹션 구간·발화 대본을
 * 미리 계산해 담는다. 시간 값은 ExoPlayer 재생 위치와 맞추기 위해 밀리초(ms)로 보관한다.
 */
@Immutable
data class PlayerContentUiModel(
    val videoUrl: String?,
    val segments: List<PlayerSegmentUiModel>,
    val scriptLines: List<PlayerScriptLineUiModel>,
)

/** 세그먼트 진행바의 섹션 하나. 카드(질문)별 scriptSegments 시간 범위에서 도출한다. */
@Immutable
data class PlayerSegmentUiModel(
    val label: String,
    val startMs: Long,
    val endMs: Long,
)

/** 대본 오버레이의 발화 라인 하나. */
@Immutable
data class PlayerScriptLineUiModel(
    val isInterviewer: Boolean,
    val text: String,
    val startMs: Long,
    val endMs: Long,
)
