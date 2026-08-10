package com.dminus14.app.feature.home

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface HomeIntent : MviIntent {
    data object Load : HomeIntent

    data object OpenMyPage : HomeIntent

    data class ReportExpandClick(
        val reportId: String,
    ) : HomeIntent

    data class ReportActionClick(
        val reportId: String,
    ) : HomeIntent
}

data class HomeReportItem(
    val id: String,
    val date: String,
    val title: String?,
)

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val reports: List<HomeReportItem> = emptyList(),
    val expandedReportId: String? = null,
    /**
     * 홈에 페이드인으로 노출할 세션 시작 오버레이. null이면 아무것도 표시하지 않는다.
     * 트리거 로직은 후속에서 인텐트/UseCase를 통해 채우며, 지금은 상태 필드만 준비해 둔다.
     */
    val sessionStartOverlay: HomeSessionStartOverlayState? = null,
) : MviState

sealed interface HomeEffect : MviEffect {
    data object GoToMyPageRequested : HomeEffect
}

internal val PreviewHomeReports =
    listOf(
        HomeReportItem(
            id = "1",
            date = "7월 11일 월",
            title = "캐시 도입 결정의 이유와 한계까지 구체적인 수치로 설명해 주셨어요",
        ),
        HomeReportItem(
            id = "2",
            date = "7월 10일 월",
            title = "서비스 아키텍처 변경 배경을 명확히 설명해 주셨어요",
        ),
        HomeReportItem(
            id = "3",
            date = "7월 10일 월",
            title = "팀 협업 경험을 구체적인 사례로 설명해 주셨어요",
        ),
        HomeReportItem(
            id = "4",
            date = "7월 10일 월",
            title = "문제 해결 과정을 단계별로 설명해 주셨어요",
        ),
        HomeReportItem(
            id = "5",
            date = "7월 10일 월",
            title = "기술 선택 근거를 비교 관점에서 설명해 주셨어요",
        ),
    )
