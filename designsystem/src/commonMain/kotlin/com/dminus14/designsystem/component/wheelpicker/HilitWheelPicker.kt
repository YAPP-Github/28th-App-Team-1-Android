package com.dminus14.designsystem.component.wheelpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs

private val WheelItemHeight = 34.dp
private val WheelVisibleItemCount = 5
private val WheelWidth = 104.dp
private val WheelSentenceSpacing = 12.dp
private val HighlightHorizontalPadding = 8.dp
private val HighlightSlant = 4.dp
private const val LEADING_TEXT = "내 경력은"
private const val TRAILING_TEXT = "이다"

/**
 * 문장형 세로 휠 피커. `내 경력은` / 선택 항목 / `이다`를 한 줄로 구성한다.
 *
 * Figma: wheel-picker (`2280:8138`)
 *
 * @param items 휠에 표시할 선택 항목 목록
 * @param selectedItem 현재 선택된 항목. [items]에 없으면 첫 항목으로 스크롤한다
 * @param onSelectedItemChange 스냅으로 선택이 바뀔 때 호출된다
 * @param modifier 외부 레이아웃 Modifier
 * @param label 항목을 화면에 그릴 때 쓰는 문자열 변환. 기본값은 `toString()`
 */
@Composable
fun <T> HilitWheelPicker(
    items: List<T>,
    selectedItem: T,
    onSelectedItemChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
) {
    // 외부 selectedItem → 리스트 인덱스. 없으면 0으로 fallback.
    val selectedIndex =
        remember(items, selectedItem) {
            items.indexOf(selectedItem).takeIf { it >= 0 } ?: 0
        }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val onSelectedItemChangeState = rememberUpdatedState(onSelectedItemChange)

    // 현재 뷰포트 중앙에 가까운 아이템 인덱스 (하이라이트 표시용)
    val centeredIndex by remember {
        derivedStateOf { listState.centeredItemIndex() }
    }

    // scrollToItem()도 스크롤 락을 거쳐 isScrollInProgress를 true→false로 만들기 때문에,
    // 이 플래그로 감싸지 않으면 프로그래밍 스크롤이 아래쪽 UI→props 콜백을 오발화시킨다.
    var isProgrammaticScroll by remember { mutableStateOf(false) }

    // props → UI: 외부에서 selectedItem이 바뀌면 휠도 해당 위치로 스크롤
    LaunchedEffect(selectedIndex, items.size) {
        if (items.isEmpty()) return@LaunchedEffect
        val targetIndex = selectedIndex.coerceIn(0, items.lastIndex)
        if (listState.centeredItemIndex() != targetIndex) {
            isProgrammaticScroll = true
            try {
                listState.scrollToItem(targetIndex)
            } finally {
                isProgrammaticScroll = false
            }
        }
    }

    // UI → props: 사용자 스크롤이 멈춘 뒤 중앙 아이템을 선택값으로 콜백
    LaunchedEffect(listState, items) {
        var wasScrolling = false
        snapshotFlow { listState.isScrollInProgress to listState.centeredItemIndex() }
            .distinctUntilChanged()
            .collect { (scrolling, index) ->
                if (wasScrolling && !scrolling && items.isNotEmpty() && !isProgrammaticScroll) {
                    val coercedIndex = index.coerceIn(0, items.lastIndex)
                    onSelectedItemChangeState.value(items[coercedIndex])
                }
                wasScrolling = scrolling
            }
    }

    val selectedTextColor = HilitTheme.colors.hilitBlack800
    val unselectedTextColor = HilitTheme.colors.gray400
    val highlightColor = HilitTheme.colors.hilitGreen500
    val surfaceColor = HilitTheme.colors.hilitWhite
    val textStyle = HilitTheme.typography.sub4

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        modifier =
            modifier
                .padding(horizontal = WheelSentenceSpacing)
                .height(WheelItemHeight * WheelVisibleItemCount)
                .background(surfaceColor)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush =
                            Brush.verticalGradient(
                                0f to surfaceColor,
                                0.28f to surfaceColor.copy(alpha = 0f),
                                0.72f to surfaceColor.copy(alpha = 0f),
                                1f to surfaceColor,
                            ),
                    )
                },
        contentPadding =
            PaddingValues(
                vertical = WheelItemHeight * (WheelVisibleItemCount / 2),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(items) { index, item ->
            WheelPickerItem(
                text = label(item),
                selected = index == centeredIndex,
                selectedTextColor = selectedTextColor,
                unselectedTextColor = unselectedTextColor,
                highlightColor = highlightColor,
                textStyle = textStyle,
            )
        }
    }
}

@Composable
private fun WheelPickerItem(
    text: String,
    selected: Boolean,
    selectedTextColor: Color,
    unselectedTextColor: Color,
    highlightColor: Color,
    textStyle: TextStyle,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(WheelItemHeight),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            modifier =
                Modifier
                    .then(
                        if (selected) {
                            Modifier.hilitMidlineHighlight(highlightColor)
                        } else {
                            Modifier
                        },
                    ).padding(horizontal = HighlightHorizontalPadding),
            style = textStyle,
            color = if (selected) selectedTextColor else unselectedTextColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

// 추후여 많이 쓰일 예정이면 Modifier Extension 별도로 패키지 만들 예정
// drawWithCache로 Path 생성을 size 변경 시 1회만 수행하고, 매 프레임에는 그리기만 재사용한다.
private fun Modifier.hilitMidlineHighlight(color: Color): Modifier =
    drawWithCache {
        val slantPx = HighlightSlant.toPx()
        val vertices =
            hilitTrapezoidVertices(
                width = size.width,
                height = size.height,
                slant = slantPx,
            )
        val path =
            Path().apply {
                moveTo(vertices[0].x, vertices[0].y)
                vertices.drop(1).forEach { vertex ->
                    lineTo(vertex.x, vertex.y)
                }
                close()
            }
        onDrawBehind {
            drawPath(path = path, color = color)
        }
    }

private fun hilitTrapezoidVertices(
    width: Float,
    height: Float,
    slant: Float,
): List<Offset> =
    listOf(
        Offset(slant, 0f),
        Offset(width, 0f),
        Offset(width - slant, height),
        Offset(0f, height),
    )

/** 뷰포트 중앙에 가장 가까운 visible 아이템의 인덱스를 반환한다. */
private fun LazyListState.centeredItemIndex(): Int {
    val visibleItems = layoutInfo.visibleItemsInfo
    if (visibleItems.isEmpty()) return firstVisibleItemIndex

    val viewportCenter =
        layoutInfo.viewportStartOffset +
            (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2

    return visibleItems
        .minByOrNull { item ->
            abs((item.offset + item.size / 2) - viewportCenter)
        }?.index
        ?: firstVisibleItemIndex
}

@Preview(
    name = "HilitWheelPicker",
    showBackground = true,
    widthDp = 360,
    heightDp = 220,
)
@Composable
private fun HilitWheelPickerPreview() {
    HilitTheme {
        HilitWheelPicker(
            items = listOf("경력 없음", "신입", "1년 이상", "2년 이상", "3년 이상"),
            selectedItem = "1년 이상",
            onSelectedItemChange = {},
            modifier = Modifier.padding(vertical = 16.dp),
        )
    }
}
