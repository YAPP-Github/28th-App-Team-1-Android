package com.dminus14.app.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.home_background
import com.dminus14.app.feature.home.component.HomeReportSheet
import com.dminus14.app.feature.home.component.HomeReportSheetCallbacks
import com.dminus14.app.feature.home.component.HomeSheetAnchor
import com.dminus14.app.feature.login.api.Onboarding
import com.dminus14.app.feature.login.api.Splash
import com.dminus14.app.feature.mypage.MyPage
import com.dminus14.designsystem.component.topbar.HilitLogoTopBar
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.painterResource

private val HeroHorizontalPadding = 20.dp
private val GreetingTopSpacing = 54.dp
private val GreetingToHintSpacing = 60.dp
private val HintHorizontalPadding = 10.dp
private val HintVerticalPadding = 16.dp
private val TopBarShadowHeight = 16.dp
private val TopBarExpandedShadowColor = Color(0x99DDDFE5)
private val HomeTopBarHeight = 52.dp
private val FallbackExpandedTop = HomeTopBarHeight

@Composable
fun HomeScreen(
    onNavigate: (Any) -> Unit,
    onReplaceAll: (Any) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(HomeIntent.Load)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.GoToMyPageRequested -> onNavigate(MyPage)
                // 온보딩·스플래시로 이동할 때는 홈으로 되돌아오지 못하도록 스택을 비운다.
                HomeEffect.UserNameNotRegistered -> onReplaceAll(Onboarding)
                HomeEffect.UserNotFound -> onReplaceAll(Splash)
            }
        }
    }

    HomeContent(
        state = state,
        onReportExpandClick = { viewModel.onIntent(HomeIntent.ReportExpandClick(it)) },
        onReportActionClick = { viewModel.onIntent(HomeIntent.ReportActionClick(it)) },
        onReportSheetCollapsed = { viewModel.onIntent(HomeIntent.ReportSheetCollapsed) },
        modifier = modifier,
    )
}

@Composable
internal fun HomeContent(
    state: HomeState,
    onReportExpandClick: (String) -> Unit,
    onReportActionClick: (String) -> Unit,
    onReportSheetCollapsed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    var topBarBottomPx by remember { mutableFloatStateOf(Float.NaN) }
    var sheetAnchor by remember { mutableStateOf(HomeSheetAnchor.Peek) }
    val expandedTopPx =
        if (topBarBottomPx.isNaN()) {
            with(density) { FallbackExpandedTop.toPx() }
        } else {
            topBarBottomPx
        }

    Box(modifier = modifier.fillMaxSize()) {
        HomeBackgroundWithHero(userName = state.userName)

        HomeReportSheet(
            reports = state.reports,
            expandedReportIds = state.expandedReportIds,
            expandedTopPx = expandedTopPx,
            callbacks =
                HomeReportSheetCallbacks(
                    onReportExpandClick = onReportExpandClick,
                    onReportActionClick = onReportActionClick,
                    onSheetAnchorChange = { anchor ->
                        sheetAnchor = anchor
                        if (anchor == HomeSheetAnchor.Collapsed) onReportSheetCollapsed()
                    },
                ),
            modifier = Modifier.fillMaxSize(),
        )

        HomeTopBar(
            showExpandedShadow = sheetAnchor == HomeSheetAnchor.Expanded,
            onBottomPositioned = { topBarBottomPx = it },
            modifier =
                Modifier
                    .zIndex(1f)
                    .fillMaxWidth(),
        )

        // 세션 시작 오버레이. state가 non-null이면 페이드인으로 위에 얹혀 다른 UI를 가린다.
        // 트리거·콜백 배선은 후속 작업에서 채운다.
        HomeSessionStartOverlay(
            state = state.sessionStartOverlay,
            callbacks = HomeSessionStartCallbacks(),
            modifier = Modifier.zIndex(2f),
        )

        if (state.isLoading) {
            HomeLoadingOverlay()
        }
    }
}

@Composable
private fun HomeBackgroundWithHero(userName: String) {
    Box(modifier = Modifier.fillMaxSize()) {
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
                    .padding(top = HomeTopBarHeight),
        ) {
            HomeHeroSection(userName = userName)
        }
    }
}

@Composable
private fun HomeLoadingOverlay() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(HilitTheme.colors.hilitWhite.copy(alpha = 0.6f))
                .clickable(
                    enabled = false,
                    indication = null,
                    interactionSource = null,
                    onClick = { },
                ),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun HomeTopBar(
    showExpandedShadow: Boolean,
    onBottomPositioned: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HilitLogoTopBar(
            modifier =
                Modifier.onGloballyPositioned { coordinates ->
                    onBottomPositioned(coordinates.positionInRoot().y + coordinates.size.height)
                },
        )
        if (showExpandedShadow) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(TopBarShadowHeight)
                        .background(
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        TopBarExpandedShadowColor,
                                        TopBarExpandedShadowColor.copy(alpha = 0f),
                                    ),
                            ),
                        ),
            )
        }
    }
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
                    ).padding(top = GreetingToHintSpacing),
        )
    }
}
