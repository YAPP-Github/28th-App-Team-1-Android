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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.home.HomeReportItem
import com.dminus14.designsystem.component.reportcard.HilitReportCard
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.CoroutineScope

private val SheetPeekTop = 331.dp
private val SheetMinVisibleHeight = 120.dp
private val DragHandleContainerHeight = 20.dp
private val DragHandleWidth = 60.dp
private val DragHandleHeight = 5.dp
private val ReportHeaderPadding = 20.dp
private val ReportItemSpacing = 1.dp
private val VideoOverlayHeight = 76.dp
private val EmptyStateTopSpacing = 64.dp
private const val VIDEO_OVERLAY_MID_STOP = 0.5f
private const val VIDEO_OVERLAY_MID_ALPHA = 0.4f
private const val VIDEO_OVERLAY_END_STOP = 1.09f
private const val EXPANDED_POSITION_TOLERANCE_PX = 2f

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

        LaunchedEffect(listState) {
            var wasScrolling = false
            snapshotFlow { listState.isScrollInProgress }
                .collect { isScrolling ->
                    if (wasScrolling && !isScrolling) {
                        sheetLayout.onGestureEnd(0f)
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
                        sheetTopPx > sheetLayout.anchors.expandedTopPx + EXPANDED_POSITION_TOLERANCE_PX,
                ),
        )
    }
}

private data class HomeReportSheetLayout(
    val anchors: HomeSheetAnchors,
    val nestedScrollConnection: NestedScrollConnection,
    val onDragEnd: (Float) -> Unit,
    val onGestureEnd: (Float) -> Unit,
)

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
        rememberUpdatedState<(Float) -> Unit> { velocityY ->
            snapController.snap(velocityY)
        }
    val nestedScrollConnection =
        rememberHomeReportSheetNestedScroll(
            listState = params.listState,
            anchors = anchors,
            getSheetTopPx = params.currentSheetTopPx,
            onSheetTopPxChange = params.onSheetTopPxChange,
            onGestureEnd = { velocityY -> onGestureEndState.value(velocityY) },
        )

    return HomeReportSheetLayout(
        anchors = anchors,
        nestedScrollConnection = nestedScrollConnection,
        onDragEnd = { velocityY -> snapController.snap(velocityY) },
        onGestureEnd = { velocityY -> onGestureEndState.value(velocityY) },
    )
}

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

private data class HomeReportSheetHandleState(
    val anchors: HomeSheetAnchors,
    val getSheetTopPx: () -> Float,
    val onSheetTopPxChange: (Float) -> Unit,
    val onDragEnd: (Float) -> Unit,
)

@Composable
private fun HomeReportSheetContainer(
    contentState: HomeReportSheetContentState,
) {
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
                    .background(HilitTheme.colors.hilitWhite)
                    .navigationBarsPadding(),
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

@Composable
private fun rememberHomeReportSheetNestedScroll(
    listState: LazyListState,
    anchors: HomeSheetAnchors,
    getSheetTopPx: () -> Float,
    onSheetTopPxChange: (Float) -> Unit,
    onGestureEnd: (Float) -> Unit,
): NestedScrollConnection {
    val currentGetSheetTopPx by rememberUpdatedState(getSheetTopPx)
    val currentOnSheetTopPxChange by rememberUpdatedState(onSheetTopPxChange)
    val currentOnGestureEnd by rememberUpdatedState(onGestureEnd)
    var sheetMovedDuringGesture by remember { mutableStateOf(false) }

    return remember(listState, anchors) {
        object : NestedScrollConnection {
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
                    return Velocity(0f, consumed)
                }
                return Velocity.Zero
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity,
            ): Velocity {
                if (sheetMovedDuringGesture) {
                    currentOnGestureEnd(available.y)
                    sheetMovedDuringGesture = false
                }
                return Velocity.Zero
            }
        }
    }
}

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

@Composable
private fun HomeReportSheetHeader(reportCount: Int) {
    val title =
        buildAnnotatedString {
            withStyle(HilitTheme.typography.sub7.toSpanStyle().copy(color = HilitTheme.colors.hilitBlack800)) {
                append("면접 리포트 ")
            }
            withStyle(HilitTheme.typography.sub7.toSpanStyle().copy(color = HilitTheme.colors.gray500)) {
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
                                    VIDEO_OVERLAY_MID_STOP to Color.White.copy(alpha = VIDEO_OVERLAY_MID_ALPHA),
                                    VIDEO_OVERLAY_END_STOP to Color.White,
                                ),
                        ),
                ),
    )
}
