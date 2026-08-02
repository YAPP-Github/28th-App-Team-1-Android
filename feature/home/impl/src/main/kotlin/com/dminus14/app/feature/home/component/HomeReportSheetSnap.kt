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

    fun snap(velocityY: Float = 0f) {
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

internal fun resolveSnapTargetPx(
    currentTopPx: Float,
    velocityY: Float,
    anchors: HomeSheetAnchors,
): Float =
    when {
        velocityY < -SNAP_VELOCITY_THRESHOLD_PX -> anchors.expandedTopPx
        velocityY > SNAP_VELOCITY_THRESHOLD_PX -> anchors.collapsedTopPx
        else ->
            anchors.snapTargetsPx.minByOrNull { abs(it - currentTopPx) }
                ?: anchors.peekTopPx
    }

private const val SNAP_VELOCITY_THRESHOLD_PX = 900f
private const val SNAP_POSITION_TOLERANCE_PX = 1f
