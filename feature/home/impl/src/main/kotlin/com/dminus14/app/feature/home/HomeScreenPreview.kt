package com.dminus14.app.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dminus14.designsystem.theme.HilitTheme

@Preview(
    name = "HomeDefault",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun HomeDefaultPreview() {
    HilitTheme {
        HomeContent(
            state =
                HomeState(
                    userName = "재원",
                    reports = emptyList(),
                ),
            onReportExpandClick = {},
            onReportActionClick = {},
        )
    }
}

@Preview(
    name = "HomeReport",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun HomeReportPreview() {
    HilitTheme {
        HomeContent(
            state =
                HomeState(
                    userName = "재원",
                    reports = PreviewHomeReports,
                    expandedReportIds = setOf(PreviewHomeReports.first().id),
                ),
            onReportExpandClick = {},
            onReportActionClick = {},
        )
    }
}

@Preview(
    name = "HomeOverlay - Start",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun HomeOverlayStartPreview() {
    HomeOverlayPreviewScaffold(
        overlay =
            HomeSessionStartOverlayState.Start(
                userName = "재원",
                remainingTicketCount = 3,
            ),
    )
}

@Preview(
    name = "HomeOverlay - NoTickets",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun HomeOverlayNoTicketsPreview() {
    HomeOverlayPreviewScaffold(
        overlay = HomeSessionStartOverlayState.NoTickets(userName = "재원"),
    )
}

@Preview(
    name = "HomeOverlay - InProgress",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun HomeOverlayInProgressPreview() {
    HomeOverlayPreviewScaffold(
        overlay =
            HomeSessionStartOverlayState.InProgress(
                userName = "재원",
                remainingQuestionCount = 2,
            ),
    )
}

@Preview(
    name = "HomeOverlay - ConfirmRestart",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun HomeOverlayConfirmRestartPreview() {
    HomeOverlayPreviewScaffold(overlay = HomeSessionStartOverlayState.ConfirmRestart)
}

/** 오버레이 4종을 홈 위에 얹은 프리뷰용 공통 스캐폴드. */
@Composable
private fun HomeOverlayPreviewScaffold(overlay: HomeSessionStartOverlayState) {
    HilitTheme {
        HomeContent(
            state =
                HomeState(
                    userName = "재원",
                    reports = PreviewHomeReports,
                    sessionStartOverlay = overlay,
                ),
            onReportExpandClick = {},
            onReportActionClick = {},
        )
    }
}
