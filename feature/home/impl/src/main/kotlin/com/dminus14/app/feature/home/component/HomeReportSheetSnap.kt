package com.dminus14.app.feature.home.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.saveable.Saver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

enum class HomeSheetAnchor {
    Expanded,
    Peek,
    Collapsed,
    ;

    internal fun toTopPx(anchors: HomeSheetAnchors): Float =
        when (this) {
            Expanded -> anchors.expandedTopPx
            Peek -> anchors.peekTopPx
            Collapsed -> anchors.collapsedTopPx
        }

    companion object {
        val Saver: Saver<HomeSheetAnchor, String> =
            Saver(
                save = { it.name },
                restore = { name -> entries.firstOrNull { it.name == name } ?: Peek },
            )

        internal fun fromTopPx(
            topPx: Float,
            anchors: HomeSheetAnchors,
        ): HomeSheetAnchor {
            val targetPx = resolveSnapTargetPx(topPx, velocityY = 0f, anchors = anchors)
            return when (targetPx) {
                anchors.expandedTopPx -> Expanded
                anchors.collapsedTopPx -> Collapsed
                else -> Peek
            }
        }
    }
}

internal data class HomeSheetAnchors(
    val expandedTopPx: Float,
    val peekTopPx: Float,
    val collapsedTopPx: Float,
) {
    val snapTargetsPx: List<Float>
        get() = listOf(expandedTopPx, peekTopPx, collapsedTopPx)
}

internal class HomeSheetSnapController(
    private val scope: CoroutineScope,
    private val anchors: () -> HomeSheetAnchors,
    private val getOffsetPx: () -> Float,
    private val setOffsetPx: (Float) -> Unit,
    private val onAnchorSettled: (HomeSheetAnchor) -> Unit,
) {
    private var snapJob: Job? = null

    /**
     * 진행 중인 snap 애니메이션이 있는지 확인한다.
     *
     * NestedScroll `onPostFling` 이 이미 실제 velocity 로 snap 을 시작한 뒤,
     * 리스트 스크롤 종료를 감지한 상위 `LaunchedEffect` 가 velocity=0 으로 snap 을
     * 재호출해 애니메이션을 뒤엎는 것을 방지하는 데 사용한다.
     */
    val isSnapping: Boolean
        get() = snapJob?.isActive == true

    fun snap(
        velocityY: Float = 0f,
        directionHint: Float = 0f,
    ) {
        snapJob?.cancel()
        snapJob =
            scope.launch {
                val currentAnchors = anchors()
                val startOffsetPx = getOffsetPx()
                val targetOffsetPx =
                    resolveSnapTargetPx(
                        currentTopPx = startOffsetPx,
                        velocityY = velocityY,
                        anchors = currentAnchors,
                        directionHint = directionHint,
                    )
                if (abs(targetOffsetPx - startOffsetPx) < SNAP_POSITION_TOLERANCE_PX) {
                    setOffsetPx(targetOffsetPx)
                    onAnchorSettled(HomeSheetAnchor.fromTopPx(targetOffsetPx, currentAnchors))
                    return@launch
                }
                animate(
                    initialValue = startOffsetPx,
                    targetValue = targetOffsetPx,
                    initialVelocity = velocityY,
                    animationSpec =
                        spring(
                            stiffness = Spring.StiffnessMediumLow,
                            dampingRatio = Spring.DampingRatioNoBouncy,
                        ),
                ) { value, _ ->
                    setOffsetPx(value)
                }
                setOffsetPx(targetOffsetPx)
                onAnchorSettled(HomeSheetAnchor.fromTopPx(targetOffsetPx, currentAnchors))
            }
    }
}

/**
 * 다음 snap target 을 결정한다.
 *
 * - fling velocity 가 임계값을 넘으면 방향 극단 anchor 로 이동
 * - velocity 가 작아도 [directionHint] 로 드래그 방향이 유의미하면 현재 위치에서
 *   해당 방향의 다음 anchor 로 이동 (Expanded↔Peek 사이 느린 드래그 후 놓았을 때
 *   nearest 계산이 원위치로 되돌리는 문제를 회피)
 * - 그 외에는 가장 가까운 anchor
 */
internal fun resolveSnapTargetPx(
    currentTopPx: Float,
    velocityY: Float,
    anchors: HomeSheetAnchors,
    directionHint: Float = 0f,
): Float =
    when {
        velocityY < -SNAP_VELOCITY_THRESHOLD_PX -> anchors.expandedTopPx
        velocityY > SNAP_VELOCITY_THRESHOLD_PX -> anchors.collapsedTopPx
        abs(directionHint) >= SNAP_DIRECTION_THRESHOLD_PX ->
            nextAnchorInDirection(currentTopPx, directionHint, anchors)
        else ->
            anchors.snapTargetsPx.minByOrNull { abs(it - currentTopPx) }
                ?: anchors.peekTopPx
    }

private fun nextAnchorInDirection(
    currentTopPx: Float,
    direction: Float,
    anchors: HomeSheetAnchors,
): Float {
    val sorted = anchors.snapTargetsPx.sorted()
    return if (direction > 0f) {
        sorted.firstOrNull { it > currentTopPx + SNAP_POSITION_TOLERANCE_PX } ?: sorted.last()
    } else {
        sorted.lastOrNull { it < currentTopPx - SNAP_POSITION_TOLERANCE_PX } ?: sorted.first()
    }
}

private const val SNAP_VELOCITY_THRESHOLD_PX = 900f
private const val SNAP_POSITION_TOLERANCE_PX = 1f
private const val SNAP_DIRECTION_THRESHOLD_PX = 24f
