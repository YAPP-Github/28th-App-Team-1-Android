package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.bubblefield.BubbleField
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailAlign
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailEdge
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailShape
import com.dminus14.designsystem.component.bubblefield.BubbleFieldType
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val RED_FLAG_BUBBLE_VISIBLE_MS = 3_000L

/**
 * [BubbleField] 내부에서 꼬리가 본문 왼쪽 변으로부터 떨어진 고정 거리
 * (`BubbleField.kt` 의 private `BubbleTailEdgePadding` 과 같은 값이어야 꼬리 끝이 정확히
 * 아이콘 위에 온다).
 */
private val BubbleTailEdgePadding = 40.dp

/** 말풍선 꼬리 끝과 아이콘 사이 세로 여백. */
private val BubbleIconGap = 6.dp

/**
 * "상세 리포트" 섹션 타이틀 (Figma Node: 443:7204).
 *
 * [resolutionNotice](해상도 낮음 안내, 현재 선택된 카드 기준)가 있으면 타이틀 옆에 경고 아이콘을
 * 노출한다. 화면 진입 시 안내 말풍선([BubbleField])이 한 번 자동으로 뜨고, 이후에도 아이콘을
 * 탭하면 다시 뜬다. 말풍선은 뜬 지 3초 뒤 자동으로 사라진다(재탭하면 3초 타이머가 리셋된다).
 * 말풍선은 레이아웃 공간을 차지하지 않는 오버레이로 그려지며, 꼬리 끝이 항상 아이콘 위치에
 * 오도록 아이콘 좌표를 실측해서 배치한다.
 *
 * 카드 레드플래그(`cardRedFlagNotices`)는 이 헤더가 아니라 [ReportCard]의
 * [RedFlagNoticeStrip]과 [HighlightDetailBottomSheet]에서 노출한다.
 */
@Composable
internal fun DetailReportSectionHeader(
    resolutionNotice: String?,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    var showBubble by remember { mutableStateOf(false) }
    // 0보다 크면 노출을 "요청"한 것으로 본다. 해상도 낮음 안내가 있으면 진입 즉시 1회 자동
    // 요청되고, 이후 아이콘 탭마다 값을 증가시켜 같은 이펙트를 다시 트리거(=3초 타이머 리셋)한다.
    var bubbleRequestCount by remember {
        mutableIntStateOf(if (resolutionNotice != null) 1 else 0)
    }
    var boxCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var iconCenterXPx by remember { mutableFloatStateOf(0f) }
    var iconTopYPx by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(bubbleRequestCount) {
        if (bubbleRequestCount > 0) {
            showBubble = true
            delay(RED_FLAG_BUBBLE_VISIBLE_MS)
            showBubble = false
        }
    }

    Box(modifier = modifier.onGloballyPositioned { boxCoordinates = it }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "상세 리포트",
                style = HilitTheme.typography.sub7,
                color = colors.hilitWhite,
            )
            if (resolutionNotice != null) {
                HilitIcon(
                    asset = HilitIconAsset.FillWarning,
                    contentDescription = "리포트 안내",
                    modifier =
                        Modifier
                            .padding(start = 8.dp)
                            .size(16.dp)
                            .onGloballyPositioned { iconCoordinates ->
                                val box = boxCoordinates ?: return@onGloballyPositioned
                                val origin = box.localPositionOf(iconCoordinates, Offset.Zero)
                                iconCenterXPx = origin.x + iconCoordinates.size.width / 2f
                                iconTopYPx = origin.y
                            }.clickable { bubbleRequestCount += 1 },
                )
            }
        }
        if (showBubble && resolutionNotice != null) {
            BubbleField(
                text = resolutionNotice,
                type = BubbleFieldType.Small,
                tailEdge = BubbleFieldTailEdge.Bottom,
                tailAlign = BubbleFieldTailAlign.Left,
                tailShape = BubbleFieldTailShape.Left,
                modifier =
                    Modifier.overlayAboveIcon(
                        iconCenterXPx = iconCenterXPx,
                        iconTopYPx = iconTopYPx,
                        tailEdgePadding = BubbleTailEdgePadding,
                        gap = BubbleIconGap,
                    ),
            )
        }
    }
}

/**
 * 부모(Box)의 레이아웃 크기에 영향을 주지 않는 오버레이로 그리되([layout]에 크기 0을 보고),
 * [BubbleField]의 꼬리 끝이 (iconCenterXPx, iconTopYPx) 로 표시된 아이콘 바로 위 [gap] 만큼
 * 띄운 지점에 오도록 실제 배치 좌표를 계산한다.
 */
private fun Modifier.overlayAboveIcon(
    iconCenterXPx: Float,
    iconTopYPx: Float,
    tailEdgePadding: Dp,
    gap: Dp,
): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        val x = (iconCenterXPx - tailEdgePadding.toPx()).roundToInt()
        val y = (iconTopYPx - placeable.height - gap.toPx()).roundToInt()
        layout(0, 0) {
            placeable.placeRelative(x, y)
        }
    }
