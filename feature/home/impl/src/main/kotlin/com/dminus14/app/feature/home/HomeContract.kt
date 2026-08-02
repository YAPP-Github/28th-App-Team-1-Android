package com.dminus14.app.feature.home

import com.dminus14.app.core.common.mvi.MviEffect
import com.dminus14.app.core.common.mvi.MviIntent
import com.dminus14.app.core.common.mvi.MviState

sealed interface HomeIntent : MviIntent {
    data object Load : HomeIntent

    data class ReportClick(
        val reportId: String,
    ) : HomeIntent
}

data class HomeReportItem(
    val id: String,
    val date: String,
    val title: String?,
    val isExpanded: Boolean,
)

data class HomeState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val reports: List<HomeReportItem> = emptyList(),
) : MviState

sealed interface HomeEffect : MviEffect

internal val PreviewHomeReports =
    listOf(
        HomeReportItem(
            id = "1",
            date = "7월 11일 월",
            title = "캐시 도입 결정의 이유와 한계까지 구체적인 수치로 설명해 주셨어요",
            isExpanded = true,
        ),
        HomeReportItem(
            id = "2",
            date = "7월 10일 월",
            title = null,
            isExpanded = false,
        ),
        HomeReportItem(
            id = "3",
            date = "7월 10일 월",
            title = null,
            isExpanded = false,
        ),
        HomeReportItem(
            id = "4",
            date = "7월 10일 월",
            title = null,
            isExpanded = false,
        ),
        HomeReportItem(
            id = "5",
            date = "7월 10일 월",
            title = null,
            isExpanded = false,
        ),
    )
