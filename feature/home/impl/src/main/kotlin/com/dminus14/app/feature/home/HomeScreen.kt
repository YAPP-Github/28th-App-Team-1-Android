package com.dminus14.app.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.home_background
import com.dminus14.app.feature.home.component.HomeReportSheet
import com.dminus14.app.feature.home.component.HomeSheetAnchor
import com.dminus14.designsystem.component.topbar.HilitLogoTopBar
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.painterResource

private val HeroHorizontalPadding = 20.dp
private val GreetingTopSpacing = 54.dp
private val GreetingToHintSpacing = 60.dp
private val HintHorizontalPadding = 10.dp
private val HintVerticalPadding = 16.dp
private val TopBarExpandedShadowElevation = 6.dp
private val TopBarExpandedShadowColor = Color(0x99DDDFE5)
private val FallbackExpandedTop = 87.dp
private val HomeTopBarHeight = 52.dp

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(HomeIntent.Load)
    }

    HomeContent(
        state = state,
        onReportExpandClick = { viewModel.onIntent(HomeIntent.ReportExpandClick(it)) },
        onReportActionClick = { viewModel.onIntent(HomeIntent.ReportActionClick(it)) },
        modifier = modifier,
    )
}

@Composable
private fun HomeContent(
    state: HomeState,
    onReportExpandClick: (String) -> Unit,
    onReportActionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.home_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(top = HomeTopBarHeight),
        ) {
            HomeHeroSection(userName = state.userName)
        }

        val density = LocalDensity.current
        var topBarBottomPx by remember { mutableFloatStateOf(Float.NaN) }
        var sheetAnchor by remember { mutableStateOf(HomeSheetAnchor.Peek) }
        val expandedTopPx =
            if (topBarBottomPx.isNaN()) {
                with(density) { FallbackExpandedTop.toPx() }
            } else {
                topBarBottomPx
            }

        HomeReportSheet(
            reports = state.reports,
            expandedReportId = state.expandedReportId,
            onReportExpandClick = onReportExpandClick,
            onReportActionClick = onReportActionClick,
            expandedTopPx = expandedTopPx,
            onSheetAnchorChange = { sheetAnchor = it },
            modifier = Modifier.fillMaxSize(),
        )

        HomeTopBar(
            showExpandedShadow = sheetAnchor == HomeSheetAnchor.Expanded,
            modifier =
                Modifier
                    .zIndex(1f)
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        topBarBottomPx = coordinates.positionInRoot().y + coordinates.size.height
                    },
        )
    }
}

@Composable
private fun HomeTopBar(
    showExpandedShadow: Boolean,
    modifier: Modifier = Modifier,
) {
    val shadowModifier =
        if (showExpandedShadow) {
            Modifier.shadow(
                elevation = TopBarExpandedShadowElevation,
                shape = RectangleShape,
                clip = false,
                ambientColor = TopBarExpandedShadowColor,
                spotColor = TopBarExpandedShadowColor,
            )
        } else {
            Modifier
        }

    HilitLogoTopBar(modifier = modifier.then(shadowModifier))
}

@Composable
private fun HomeHeroSection(userName: String) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = HeroHorizontalPadding)
                .padding(top = GreetingTopSpacing),
        ) {
            Text(
                text = "오랜만이에요\n${userName}님!",
                style = HilitTheme.typography.head1,
                color = HilitTheme.colors.hilitBlack800,
            )
            Text(
                text = "밑으로 스크롤해서 면접을 시작해 보세요!",
                style = HilitTheme.typography.body3,
                color = HilitTheme.colors.hilitGreen800,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = HintHorizontalPadding,
                            vertical = HintVerticalPadding,
                    )
                    .padding(top = GreetingToHintSpacing),
        )
    }
}

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
                    expandedReportId = PreviewHomeReports.first().id,
                ),
            onReportExpandClick = {},
            onReportActionClick = {},
        )
    }
}
