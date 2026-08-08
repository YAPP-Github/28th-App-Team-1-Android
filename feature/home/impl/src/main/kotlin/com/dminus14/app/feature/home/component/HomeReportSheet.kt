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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Velocity
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

/** 드래그 핸들 인디케이터가 그려질 영역 높이 (히트박스 겸용). */
private val DragHandleContainerHeight = 20.dp
private val DragHandleWidth = 60.dp
private val DragHandleHeight = 5.dp
private val ReportHeaderPadding = 20.dp
private val ReportItemSpacing = 1.dp

/** 리스트 최하단에 얹는 흰색 페이드 오버레이(하단 재생 컨트롤 가림용) 높이. */
private val VideoOverlayHeight = 76.dp
private val EmptyStateTopSpacing = 64.dp
private const val VIDEO_OVERLAY_MID_STOP = 0.5f
private const val VIDEO_OVERLAY_MID_ALPHA = 0.4f
private const val VIDEO_OVERLAY_END_STOP = 1.09f

/** Expanded 앵커 판정 여유치(px). 부동소수 오차로 인디케이터 깜빡임을 방지. */
private const val EXPANDED_POSITION_TOLERANCE_PX = 2f

/**
 * 홈 화면의 리포트 리스트 바텀시트.
 *
 * 3-앵커(Expanded / Peek / Collapsed) drag & fling 시트. 세로 드래그는 핸들 영역과
 * 내부 리스트의 nested scroll 양쪽에서 잡고, gesture가 끝나면 velocity·이동 방향으로
 * 가장 가까운 앵커에 snap 한다. 앵커 상태는 [rememberSaveable] 로 config change에 대비.
 *
 * @param reports 표시할 리포트 리스트. 비어 있으면 empty state 컴포넌트를 그린다.
 * @param expandedReportId 리스트에서 현재 확장된 카드 id. null이면 모두 접힘.
 * @param expandedTopPx Expanded 앵커의 화면 top 오프셋(px). 상단 topbar 아래 위치를
 *   상위에서 측정해 넘긴다. NaN 대신 실측 전 fallback 값을 상위에서 보낼 것.
 * @param onSheetAnchorChange 앵커가 실제로 settle된 뒤 호출. 상위에서 topbar shadow 등
 *   앵커 종속 UI를 갱신하는 용도.
 */
@Composable
fun HomeReportSheet(
    reports: List<HomeReportItem>,
    expandedReportId: String?,
    onReportExpandClick: (String) -> Unit,
    onReportActionClick: (String) -> Unit,
    expandedTopPx: Float,
    modifier: Modifier = Modifier,
    onSheetAnchorChange: (HomeSheetAnchor) -> Unit = {},
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
                    expandedTopPx = expandedTopPx,
                    peekTopPx = peekTopPx,
                    maxHeight = maxHeight,
                    density = density,
                    currentSheetTopPx = { currentSheetTopPx },
                    listState = listState,
                    onSheetTopPxChange = { sheetTopPx = it },
                    onAnchorSettled = { settledAnchor = it },
                ),
            )

        LaunchedEffect(sheetLayout.anchors) {
            sheetTopPx = settledAnchor.toTopPx(sheetLayout.anchors)
        }

        LaunchedEffect(settledAnchor) {
            onSheetAnchorChange(settledAnchor)
        }

        // fling 없이 리스트 스크롤이 그냥 멈춘 케이스(느린 손가락 놓기 등)를 커버.
        // onPostFling 이 이미 실제 velocity·direction 으로 snap 을 시작했다면
        // velocity=0 재호출로 그 애니메이션을 뒤엎지 않도록 skip 한다.
        LaunchedEffect(listState) {
            var wasScrolling = false
            snapshotFlow { listState.isScrollInProgress }
                .collect { isScrolling ->
                    if (wasScrolling && !isScrolling && !sheetLayout.isSnapping()) {
                        sheetLayout.onGestureEnd(0f, 0f)
                    }
                    wasScrolling = isScrolling
                }
        }

        HomeReportSheetContainer(
            contentState =
                HomeReportSheetContentState(
                    sheetTopPx = sheetTopPx,
                    reports = reports,
                    expandedReportId = expandedReportId,
                    listState = listState,
                    anchors = sheetLayout.anchors,
                    nestedScrollConnection = sheetLayout.nestedScrollConnection,
                    onReportExpandClick = onReportExpandClick,
                    onReportActionClick = onReportActionClick,
                    getSheetTopPx = { currentSheetTopPx },
                    onSheetTopPxChange = { value -> sheetTopPx = value },
                    onDragEnd = sheetLayout.onDragEnd,
                    showDragHandleIndicator =
                        sheetTopPx >
                            sheetLayout.anchors.expandedTopPx + EXPANDED_POSITION_TOLERANCE_PX,
                ),
        )
    }
}

/**
 * 앵커·nested scroll·snap 컨트롤러를 한 데 묶어 [HomeReportSheet]로 반환하는 번들.
 * 개별 콜백을 시트 본체가 각각 remember 하지 않도록 하나로 묶었다.
 */
private data class HomeReportSheetLayout(
    val anchors: HomeSheetAnchors,
    val nestedScrollConnection: NestedScrollConnection,
    /** 핸들의 vertical drag가 끝났을 때 호출. velocity만 넘긴다. */
    val onDragEnd: (Float) -> Unit,
    /** 리스트 nested scroll·fling이 끝났을 때 호출. velocity + 이동 방향 힌트. */
    val onGestureEnd: (velocityY: Float, directionHint: Float) -> Unit,
    /** 현재 snap 애니메이션이 진행 중인지. 중복 snap 방지용 가드에 사용. */
    val isSnapping: () -> Boolean,
)

/** [rememberHomeReportSheetLayout] 파라미터 묶음. 파라미터 6개 이상을 피하려는 형태. */
private data class HomeReportSheetLayoutParams(
    val scope: CoroutineScope,
    val expandedTopPx: Float,
    val peekTopPx: Float,
    val maxHeight: androidx.compose.ui.unit.Dp,
    val density: Density,
    val currentSheetTopPx: () -> Float,
    val listState: LazyListState,
    val onSheetTopPxChange: (Float) -> Unit,
    val onAnchorSettled: (HomeSheetAnchor) -> Unit,
)

/**
 * 3-앵커, nested scroll connection, snap 컨트롤러를 조립해서 반환.
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
    val snapController =
        remember(params.scope) {
            HomeSheetSnapController(
                scope = params.scope,
                anchors = { anchors },
                getOffsetPx = params.currentSheetTopPx,
                setOffsetPx = params.onSheetTopPxChange,
                onAnchorSettled = params.onAnchorSettled,
            )
        }
    val onGestureEndState =
        rememberUpdatedState<(Float, Float) -> Unit> { velocityY, directionHint ->
            snapController.snap(velocityY = velocityY, directionHint = directionHint)
        }
    val nestedScrollConnection =
        rememberHomeReportSheetNestedScroll(
            listState = params.listState,
            anchors = anchors,
            getSheetTopPx = params.currentSheetTopPx,
            onSheetTopPxChange = params.onSheetTopPxChange,
            onGestureEnd = { velocityY, directionHint ->
                onGestureEndState.value(velocityY, directionHint)
            },
        )

    return HomeReportSheetLayout(
        anchors = anchors,
        nestedScrollConnection = nestedScrollConnection,
        onDragEnd = { velocityY -> snapController.snap(velocityY = velocityY) },
        onGestureEnd = { velocityY, directionHint ->
            onGestureEndState.value(velocityY, directionHint)
        },
        isSnapping = { snapController.isSnapping },
    )
}

/**
 * [HomeReportSheetContainer] 렌더에 필요한 모든 값 묶음.
 * @param showDragHandleIndicator 시트 위치가 expanded 앵커에서 [EXPANDED_POSITION_TOLERANCE_PX]
 *   이상 떨어져 있을 때만 true — 완전히 펼쳐진 상태에선 핸들 인디케이터를 숨긴다.
 */
private data class HomeReportSheetContentState(
    val sheetTopPx: Float,
    val reports: List<HomeReportItem>,
    val expandedReportId: String?,
    val listState: LazyListState,
    val anchors: HomeSheetAnchors,
    val nestedScrollConnection: NestedScrollConnection,
    val onReportExpandClick: (String) -> Unit,
    val onReportActionClick: (String) -> Unit,
    val getSheetTopPx: () -> Float,
    val onSheetTopPxChange: (Float) -> Unit,
    val onDragEnd: (Float) -> Unit,
    val showDragHandleIndicator: Boolean,
)

/** [HomeReportSheetHandle]에 넘길 값 묶음. */
private data class HomeReportSheetHandleState(
    val anchors: HomeSheetAnchors,
    val getSheetTopPx: () -> Float,
    val onSheetTopPxChange: (Float) -> Unit,
    val onDragEnd: (Float) -> Unit,
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
            if (contentState.showDragHandleIndicator) {
                HomeReportSheetHandle(
                    state = handleState,
                    showIndicator = true,
                )
                HomeReportSheetHeader(reportCount = contentState.reports.size)
            } else {
                Box(modifier = Modifier.fillMaxWidth()) {
                    HomeReportSheetHeader(reportCount = contentState.reports.size)
                    HomeReportSheetHandle(
                        state = handleState,
                        showIndicator = false,
                        modifier = Modifier.matchParentSize(),
                    )
                }
            }
            HomeReportSheetBody(
                reports = contentState.reports,
                expandedReportId = contentState.expandedReportId,
                listState = contentState.listState,
                nestedScrollConnection = contentState.nestedScrollConnection,
                onReportExpandClick = contentState.onReportExpandClick,
                onReportActionClick = contentState.onReportActionClick,
            )
        }
    }
}

/**
 * 리포트 리스트 영역. 비어 있으면 [HomeReportEmptyState]로 대체.
 * `weight(1f)`로 header 아래 남은 세로 공간을 전부 차지하고,
 * nested scroll을 통해 시트 위치와 리스트 스크롤을 이어 붙인다.
 */
@Composable
private fun ColumnScope.HomeReportSheetBody(
    reports: List<HomeReportItem>,
    expandedReportId: String?,
    listState: LazyListState,
    nestedScrollConnection: NestedScrollConnection,
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
            modifier =
                Modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection),
        ) {
            items(
                items = reports,
                key = HomeReportItem::id,
            ) { report ->
                HilitReportCard(
                    date = report.date,
                    title = report.title.orEmpty(),
                    expanded = expandedReportId == report.id,
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
 * 리스트-시트 사이의 nested scroll 브릿지.
 *
 * - **위로 드래그(delta < 0)** — 시트가 Expanded보다 아래에 있으면 리스트보다 먼저
 *   시트를 위로 올린다 (`onPreScroll`).
 * - **아래로 드래그(delta > 0)** — 리스트가 최상단까지 스크롤된 뒤 남은 델타로
 *   시트를 내린다 (`onPostScroll`).
 * - **Fling** — 위와 동일한 규칙을 velocity 단위로 적용 (`onPreFling`).
 * - **gesture 종료** — 이번 gesture 동안 시트가 실제로 움직였다면
 *   [onGestureEnd] 로 velocity + 순 이동 방향(directionHint) 을 넘겨 snap 시킨다.
 */
@Composable
private fun rememberHomeReportSheetNestedScroll(
    listState: LazyListState,
    anchors: HomeSheetAnchors,
    getSheetTopPx: () -> Float,
    onSheetTopPxChange: (Float) -> Unit,
    onGestureEnd: (velocityY: Float, directionHint: Float) -> Unit,
): NestedScrollConnection {
    val currentGetSheetTopPx by rememberUpdatedState(getSheetTopPx)
    val currentOnSheetTopPxChange by rememberUpdatedState(onSheetTopPxChange)
    val currentOnGestureEnd by rememberUpdatedState(onGestureEnd)
    // 이번 gesture 동안 시트가 한 번이라도 움직였는지. 리스트만 스크롤하고 끝난 경우엔
    // snap을 호출하지 않아야 리스트 fling 애니메이션이 부드럽게 이어진다.
    var sheetMovedDuringGesture by remember { mutableStateOf(false) }

    return remember(listState, anchors) {
        object : NestedScrollConnection {
            /**
             * 이번 gesture 동안 시트가 이동한 순수 y 델타.
             * 양수 = 아래, 음수 = 위. onPostFling 에서 direction hint 로 snap 에 전달하고 리셋한다.
             */
            var gestureNetDeltaY: Float = 0f

            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                val currentTopPx = currentGetSheetTopPx()
                if (delta < 0f && currentTopPx > anchors.expandedTopPx) {
                    val consumed = (-delta).coerceAtMost(currentTopPx - anchors.expandedTopPx)
                    currentOnSheetTopPxChange(currentTopPx - consumed)
                    sheetMovedDuringGesture = consumed > 0f
                    gestureNetDeltaY -= consumed
                    return Offset(0f, -consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val delta = available.y
                val currentTopPx = currentGetSheetTopPx()
                val isListAtTop =
                    listState.firstVisibleItemIndex == 0 &&
                        listState.firstVisibleItemScrollOffset == 0
                if (delta > 0f && isListAtTop && currentTopPx < anchors.collapsedTopPx) {
                    val consumedDelta = delta.coerceAtMost(anchors.collapsedTopPx - currentTopPx)
                    currentOnSheetTopPxChange(currentTopPx + consumedDelta)
                    sheetMovedDuringGesture = sheetMovedDuringGesture || consumedDelta > 0f
                    gestureNetDeltaY += consumedDelta
                    return Offset(0f, consumedDelta)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val currentTopPx = currentGetSheetTopPx()
                val isListAtTop =
                    listState.firstVisibleItemIndex == 0 &&
                        listState.firstVisibleItemScrollOffset == 0
                if (available.y > 0f && isListAtTop && currentTopPx < anchors.collapsedTopPx) {
                    val consumed = available.y.coerceAtMost(anchors.collapsedTopPx - currentTopPx)
                    currentOnSheetTopPxChange(currentTopPx + consumed)
                    sheetMovedDuringGesture = sheetMovedDuringGesture || consumed > 0f
                    gestureNetDeltaY += consumed
                    return Velocity(0f, consumed)
                }
                return Velocity.Zero
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity,
            ): Velocity {
                if (sheetMovedDuringGesture) {
                    currentOnGestureEnd(available.y, gestureNetDeltaY)
                    sheetMovedDuringGesture = false
                    gestureNetDeltaY = 0f
                }
                return Velocity.Zero
            }
        }
    }
}

/**
 * 상단 드래그 핸들.
 *
 * @param showIndicator true면 회색 인디케이터 바를 그리고 [DragHandleContainerHeight] 만큼의
 *   전용 히트박스를 차지. false면 인디케이터를 숨기고 header 위에 `matchParentSize`로 겹쳐
 *   header 영역 전체를 드래그 히트박스로 사용한다(완전 확장 상태).
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
            modifier
                .then(
                    if (showIndicator) {
                        Modifier
                            .fillMaxWidth()
                            .height(DragHandleContainerHeight)
                    } else {
                        Modifier
                    },
                )
                .pointerInput(state.anchors) {
                    val velocityTracker = VelocityTracker()
                    detectVerticalDragGestures(
                        onDragStart = { velocityTracker.resetTracking() },
                        onDragEnd = {
                            val velocity = velocityTracker.calculateVelocity().y
                            currentOnDragEnd(velocity)
                        },
                        onDragCancel = { currentOnDragEnd(0f) },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            velocityTracker.addPosition(change.uptimeMillis, change.position)
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
        contentAlignment = Alignment.Center,
    ) {
        if (showIndicator) {
            Box(
                modifier =
                    Modifier
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
            reports = emptyList(),
            expandedReportId = null,
            onReportExpandClick = {},
            onReportActionClick = {},
            expandedTopPx = 0f,
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
            reports = PreviewHomeReports,
            expandedReportId = null,
            onReportExpandClick = {},
            onReportActionClick = {},
            expandedTopPx = 0f,
        )
    }
}

@Preview(
    name = "HomeReportSheet - expanded item",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun HomeReportSheetExpandedItemPreview() {
    HilitTheme {
        HomeReportSheet(
            reports = PreviewHomeReports,
            expandedReportId = PreviewHomeReports.first().id,
            onReportExpandClick = {},
            onReportActionClick = {},
            expandedTopPx = 0f,
        )
    }
}
