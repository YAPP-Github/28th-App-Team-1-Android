package com.dminus14.app.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.home.HomeReportItem
import com.dminus14.app.feature.home.PreviewHomeReports
import com.dminus14.designsystem.component.reportcard.HilitReportCard
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.CoroutineScope

/** Peek 앵커 위치 — 화면 top 기준 시트 상단 오프셋. Figma 시안 값. */
private val SheetPeekTop = 331.dp

/** Collapsed 앵커에서 최소한으로 보여야 하는 시트 높이. 화면 하단에 이만큼은 남긴다. */
private val SheetMinVisibleHeight = 120.dp

private val DragHandleWidth = 60.dp
private val DragHandleHeight = 5.dp

/** 인디케이터 바를 시트 top에서 얼마나 내릴지 (header 텍스트 위쪽 여백 안에 자리). */
private val DragHandleTopPadding = 8.dp
private val ReportHeaderPadding = 20.dp
private val ReportItemSpacing = 1.dp

/** 리스트 최하단에 얹는 흰색 페이드 오버레이(하단 재생 컨트롤 가림용) 높이. */
private val VideoOverlayHeight = 76.dp
private val EmptyStateTopSpacing = 64.dp
private const val VIDEO_OVERLAY_MID_STOP = 0.5f
private const val VIDEO_OVERLAY_MID_ALPHA = 0.4f
private const val VIDEO_OVERLAY_END_STOP = 1f

/** [HomeReportSheet]에 주입하는 콜백 묶음. */
data class HomeReportSheetCallbacks(
    val onReportExpandClick: (String) -> Unit,
    val onReportActionClick: (String) -> Unit,
    val onSheetAnchorChange: (HomeSheetAnchor) -> Unit = {},
)

/** [HomeReportSheet] 표시 데이터·레이아웃 입력 묶음. */
data class HomeReportSheetContent(
    val reports: List<HomeReportItem>,
    val expandedReportIds: Set<String>,
    val expandedTopPx: Float,
    val peekResetSignal: Int = 0,
)

/**
 * 홈 화면의 리포트 리스트 바텀시트.
 *
 * 3-앵커(Expanded / Peek / Collapsed) 시트. 시트 위치 이동은 **상단 드래그 핸들 전용**.
 * 리스트 내부 스크롤은 시트에 영향을 주지 않는다(nested scroll bridging 없음).
 * gesture 종료 시 velocity·이동 방향으로 가장 가까운 앵커에 snap. 앵커 상태는
 * [rememberSaveable] 로 config change에 대비.
 *
 * @param reports 표시할 리포트 리스트. 비어 있으면 empty state 컴포넌트를 그린다.
 * @param expandedReportIds 현재 펼쳐진 카드 id 집합. 각 카드는 독립적으로 열고 닫힌다.
 * @param expandedTopPx Expanded 앵커의 화면 top 오프셋(px). 상단 topbar 아래 위치를
 *   상위에서 측정해 넘긴다. NaN 대신 실측 전 fallback 값을 상위에서 보낼 것.
 * @param callbacks 리포트·시트 앵커 이벤트 콜백.
 */
@Composable
fun HomeReportSheet(
    content: HomeReportSheetContent,
    callbacks: HomeReportSheetCallbacks,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val peekTopPx = with(density) { SheetPeekTop.toPx() }
    var settledAnchor by rememberSaveable(stateSaver = HomeSheetAnchor.Saver) {
        mutableStateOf(HomeSheetAnchor.Peek)
    }
    var sheetTopPx by remember { mutableFloatStateOf(0f) }
    val listState = rememberLazyListState()
    val currentSheetTopPx by rememberUpdatedState(sheetTopPx)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val sheetLayout =
            rememberHomeReportSheetLayout(
                HomeReportSheetLayoutParams(
                    scope = scope,
                    expandedTopPx = content.expandedTopPx,
                    peekTopPx = peekTopPx,
                    maxHeight = maxHeight,
                    density = density,
                    currentSheetTopPx = { currentSheetTopPx },
                    onSheetTopPxChange = { sheetTopPx = it },
                    onAnchorSettled = { settledAnchor = it },
                ),
            )

        LaunchedEffect(sheetLayout.anchors) {
            sheetTopPx = settledAnchor.toTopPx(sheetLayout.anchors)
        }

        LaunchedEffect(settledAnchor) {
            callbacks.onSheetAnchorChange(settledAnchor)
        }

        // 세션 오버레이를 닫을 때 등 외부 신호로 시트를 중간(Peek)으로 되돌린다.
        // 초기값 0에서는 동작하지 않고, 신호가 증가할 때만 리셋한다.
        // 드래그 종료 경로(snap)와 동일한 spring 애니메이션을 재사용해 톤을 맞춘다.
        // settledAnchor 갱신은 snap 컨트롤러의 onAnchorSettled 콜백에서 수행된다.
        LaunchedEffect(content.peekResetSignal) {
            if (content.peekResetSignal > 0) {
                sheetLayout.snapTo(HomeSheetAnchor.Peek)
            }
        }

        HomeReportSheetContainer(
            contentState =
                HomeReportSheetContentState(
                    sheetTopPx = sheetTopPx,
                    reports = content.reports,
                    expandedReportIds = content.expandedReportIds,
                    listState = listState,
                    anchors = sheetLayout.anchors,
                    onReportExpandClick = callbacks.onReportExpandClick,
                    onReportActionClick = callbacks.onReportActionClick,
                    getSheetTopPx = { currentSheetTopPx },
                    onSheetTopPxChange = { value -> sheetTopPx = value },
                    onDragEnd = sheetLayout.onDragEnd,
                    showDragHandleIndicator = sheetTopPx > sheetLayout.anchors.expandedTopPx,
                ),
        )
    }
}

/**
 * 앵커와 snap 컨트롤러를 한 데 묶어 [HomeReportSheet]로 반환하는 번들.
 */
private data class HomeReportSheetLayout(
    val anchors: HomeSheetAnchors,
    /** 핸들의 vertical drag가 끝났을 때 호출. 가장 가까운 앵커로 snap 시킨다. */
    val onDragEnd: () -> Unit,
    /** 외부 신호로 시트를 지정 앵커로 애니메이션 이동시킨다. */
    val snapTo: (HomeSheetAnchor) -> Unit,
)

/** [rememberHomeReportSheetLayout] 파라미터 묶음. 파라미터 6개 이상을 피하려는 형태. */
private data class HomeReportSheetLayoutParams(
    val scope: CoroutineScope,
    val expandedTopPx: Float,
    val peekTopPx: Float,
    val maxHeight: androidx.compose.ui.unit.Dp,
    val density: Density,
    val currentSheetTopPx: () -> Float,
    val onSheetTopPxChange: (Float) -> Unit,
    val onAnchorSettled: (HomeSheetAnchor) -> Unit,
)

/**
 * 3-앵커와 snap 컨트롤러를 조립해서 반환.
 * collapsedTopPx는 화면 높이에서 [SheetMinVisibleHeight]를 뺀 값이며 peek 아래로 내려가지 않도록 clamp.
 */
@Composable
private fun rememberHomeReportSheetLayout(
    params: HomeReportSheetLayoutParams,
): HomeReportSheetLayout {
    val collapsedTopPx =
        with(params.density) {
            (params.maxHeight - SheetMinVisibleHeight).toPx().coerceAtLeast(params.peekTopPx)
        }
    val anchors =
        remember(params.expandedTopPx, params.peekTopPx, collapsedTopPx) {
            HomeSheetAnchors(
                expandedTopPx = params.expandedTopPx,
                peekTopPx = params.peekTopPx,
                collapsedTopPx = collapsedTopPx,
            )
        }
    val currentAnchors by rememberUpdatedState(anchors)
    val snapController =
        remember(params.scope) {
            HomeSheetSnapController(
                scope = params.scope,
                anchors = { currentAnchors },
                getOffsetPx = params.currentSheetTopPx,
                setOffsetPx = params.onSheetTopPxChange,
                onAnchorSettled = params.onAnchorSettled,
            )
        }

    return HomeReportSheetLayout(
        anchors = anchors,
        onDragEnd = { snapController.snap() },
        snapTo = { target -> snapController.snapTo(target) },
    )
}

/**
 * [HomeReportSheetContainer] 렌더에 필요한 모든 값 묶음.
 * @param showDragHandleIndicator 시트가 expanded 앵커에서 벗어나 있을 때만 true —
 *   완전히 펼쳐진 상태에선 핸들 인디케이터를 숨긴다.
 */
private data class HomeReportSheetContentState(
    val sheetTopPx: Float,
    val reports: List<HomeReportItem>,
    val expandedReportIds: Set<String>,
    val listState: LazyListState,
    val anchors: HomeSheetAnchors,
    val onReportExpandClick: (String) -> Unit,
    val onReportActionClick: (String) -> Unit,
    val getSheetTopPx: () -> Float,
    val onSheetTopPxChange: (Float) -> Unit,
    val onDragEnd: () -> Unit,
    val showDragHandleIndicator: Boolean,
)

/** [HomeReportSheetHandle]에 넘길 값 묶음. */
private data class HomeReportSheetHandleState(
    val anchors: HomeSheetAnchors,
    val getSheetTopPx: () -> Float,
    val onSheetTopPxChange: (Float) -> Unit,
    val onDragEnd: () -> Unit,
)

/**
 * 시트 본체 렌더. top padding = [HomeReportSheetContentState.sheetTopPx] 만큼 밀어
 * `Box`가 자연스레 시트 위치를 잡게 한다. header 아래에 핸들 인디케이터를 두거나,
 * 완전 확장 상태에서는 header와 핸들을 겹쳐 인디케이터만 감춘다.
 */
@Composable
private fun HomeReportSheetContainer(contentState: HomeReportSheetContentState) {
    val density = LocalDensity.current

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = with(density) { contentState.sheetTopPx.toDp() }),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(HilitTheme.colors.hilitWhite),
        ) {
            val handleState =
                HomeReportSheetHandleState(
                    anchors = contentState.anchors,
                    getSheetTopPx = contentState.getSheetTopPx,
                    onSheetTopPxChange = contentState.onSheetTopPxChange,
                    onDragEnd = contentState.onDragEnd,
                )
            // header 위에 handle을 항상 겹쳐서 트리 구조 고정.
            // 인디케이터 유무는 handle 내부에서만 결정 — 드래그로 앵커 근처에서
            // showDragHandleIndicator가 flip 되어도 pointerInput이 재생성되지 않는다.
            Box(modifier = Modifier.fillMaxWidth()) {
                HomeReportSheetHeader(reportCount = contentState.reports.size)
                HomeReportSheetHandle(
                    state = handleState,
                    showIndicator = contentState.showDragHandleIndicator,
                    modifier = Modifier.matchParentSize(),
                )
            }
            HomeReportSheetBody(
                reports = contentState.reports,
                expandedReportIds = contentState.expandedReportIds,
                listState = contentState.listState,
                onReportExpandClick = contentState.onReportExpandClick,
                onReportActionClick = contentState.onReportActionClick,
            )
        }
    }
}

/**
 * 리포트 리스트 영역. 비어 있으면 [HomeReportEmptyState]로 대체.
 * `weight(1f)`로 header 아래 남은 세로 공간을 전부 차지한다.
 * 리스트 스크롤은 시트 위치에 영향을 주지 않는다.
 */
@Composable
private fun ColumnScope.HomeReportSheetBody(
    reports: List<HomeReportItem>,
    expandedReportIds: Set<String>,
    listState: LazyListState,
    onReportExpandClick: (String) -> Unit,
    onReportActionClick: (String) -> Unit,
) {
    if (reports.isEmpty()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = EmptyStateTopSpacing),
            contentAlignment = Alignment.TopCenter,
        ) {
            HomeReportEmptyState()
        }
        return
    }

    Box(modifier = Modifier.weight(1f)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                items = reports,
                key = HomeReportItem::id,
            ) { report ->
                HilitReportCard(
                    date = report.date,
                    title = report.title.orEmpty(),
                    expanded = report.id in expandedReportIds,
                    onExpandClick = { onReportExpandClick(report.id) },
                    onActionClick = { onReportActionClick(report.id) },
                )
                Spacer(modifier = Modifier.height(ReportItemSpacing))
            }
        }
        HomeReportVideoOverlay(
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

/**
 * 상단 드래그 핸들.
 *
 * 항상 부모(header 영역) 를 `matchParentSize`로 덮어 pointerInput 트리 위치를 고정한다.
 * 시트 위치·앵커 flip에 따라 [showIndicator]만 바뀌고 gesture는 끊기지 않는다.
 *
 * @param showIndicator true면 상단 중앙에 회색 인디케이터 바를 그린다. false여도 히트박스는 유지.
 */
@Composable
private fun HomeReportSheetHandle(
    state: HomeReportSheetHandleState,
    showIndicator: Boolean,
    modifier: Modifier = Modifier,
) {
    val currentGetSheetTopPx by rememberUpdatedState(state.getSheetTopPx)
    val currentOnSheetTopPxChange by rememberUpdatedState(state.onSheetTopPxChange)
    val currentOnDragEnd by rememberUpdatedState(state.onDragEnd)

    Box(
        modifier =
            modifier.pointerInput(state.anchors) {
                detectVerticalDragGestures(
                    onDragEnd = { currentOnDragEnd() },
                    onDragCancel = { currentOnDragEnd() },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        val currentTopPx = currentGetSheetTopPx()
                        currentOnSheetTopPxChange(
                            (currentTopPx + dragAmount).coerceIn(
                                state.anchors.expandedTopPx,
                                state.anchors.collapsedTopPx,
                            ),
                        )
                    },
                )
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        if (showIndicator) {
            Box(
                modifier =
                    Modifier
                        .padding(top = DragHandleTopPadding)
                        .width(DragHandleWidth)
                        .height(DragHandleHeight)
                        .background(HilitTheme.colors.gray400),
            )
        }
    }
}

/** "면접 리포트 N개" 텍스트를 그리는 시트 상단 헤더. 색 대비를 위해 count만 회색 처리. */
@Composable
private fun HomeReportSheetHeader(reportCount: Int) {
    val title =
        buildAnnotatedString {
            withStyle(
                HilitTheme.typography.sub7.toSpanStyle().copy(
                    color = HilitTheme.colors.hilitBlack800,
                ),
            ) {
                append("면접 리포트 ")
            }
            withStyle(
                HilitTheme.typography.sub7
                    .toSpanStyle()
                    .copy(color = HilitTheme.colors.gray500),
            ) {
                append("${reportCount}개")
            }
        }

    Text(
        text = title,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(ReportHeaderPadding),
    )
}

/**
 * 리스트 최하단에 얹는 white → transparent 세로 그라디언트.
 * 시안상 하단 재생 컨트롤 영역이 리스트 위로 자연스레 페이드 아웃되도록 하는 장식용 오버레이.
 */
@Composable
private fun HomeReportVideoOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(VideoOverlayHeight)
                .background(
                    brush =
                        Brush.verticalGradient(
                            colorStops =
                                arrayOf(
                                    0f to Color.White.copy(alpha = 0f),
                                    VIDEO_OVERLAY_MID_STOP to
                                        Color.White.copy(alpha = VIDEO_OVERLAY_MID_ALPHA),
                                    VIDEO_OVERLAY_END_STOP to Color.White,
                                ),
                        ),
                ),
    )
}

@Preview(name = "HomeReportSheet - empty", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HomeReportSheetEmptyPreview() {
    HilitTheme {
        HomeReportSheet(
            content =
                HomeReportSheetContent(
                    reports = emptyList(),
                    expandedReportIds = emptySet(),
                    expandedTopPx = 0f,
                ),
            callbacks =
                HomeReportSheetCallbacks(
                    onReportExpandClick = {},
                    onReportActionClick = {},
                ),
        )
    }
}

@Preview(
    name = "HomeReportSheet - with reports",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun HomeReportSheetWithReportsPreview() {
    HilitTheme {
        HomeReportSheet(
            content =
                HomeReportSheetContent(
                    reports = PreviewHomeReports,
                    expandedReportIds = emptySet(),
                    expandedTopPx = 0f,
                ),
            callbacks =
                HomeReportSheetCallbacks(
                    onReportExpandClick = {},
                    onReportActionClick = {},
                ),
        )
    }
}

@Preview(
    name = "HomeReportSheet - expanded items",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun HomeReportSheetExpandedItemPreview() {
    HilitTheme {
        HomeReportSheet(
            content =
                HomeReportSheetContent(
                    reports = PreviewHomeReports,
                    expandedReportIds = setOf(PreviewHomeReports[0].id, PreviewHomeReports[2].id),
                    expandedTopPx = 0f,
                ),
            callbacks =
                HomeReportSheetCallbacks(
                    onReportExpandClick = {},
                    onReportActionClick = {},
                ),
        )
    }
}
