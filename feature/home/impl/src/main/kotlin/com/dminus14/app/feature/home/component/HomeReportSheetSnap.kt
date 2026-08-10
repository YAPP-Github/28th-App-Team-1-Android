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
            val targetPx = resolveSnapTargetPx(topPx, anchors)
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

    fun snap() {
        snapJob?.cancel()
        snapJob =
            scope.launch {
                val currentAnchors = anchors()
                val startOffsetPx = getOffsetPx()
                val targetOffsetPx = resolveSnapTargetPx(startOffsetPx, currentAnchors)
                if (abs(targetOffsetPx - startOffsetPx) < SNAP_POSITION_TOLERANCE_PX) {
                    setOffsetPx(targetOffsetPx)
                    onAnchorSettled(HomeSheetAnchor.fromTopPx(targetOffsetPx, currentAnchors))
                    return@launch
                }
                animate(
                    initialValue = startOffsetPx,
                    targetValue = targetOffsetPx,
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
 * 다음 snap target을 결정한다. 현재 위치에서 가장 가까운 anchor로 이동.
 * velocity·방향과 무관하게 오직 위치만 기준.
 */
internal fun resolveSnapTargetPx(
    currentTopPx: Float,
    anchors: HomeSheetAnchors,
): Float = anchors.snapTargetsPx.minByOrNull { abs(it - currentTopPx) } ?: anchors.peekTopPx

private const val SNAP_POSITION_TOLERANCE_PX = 1f
