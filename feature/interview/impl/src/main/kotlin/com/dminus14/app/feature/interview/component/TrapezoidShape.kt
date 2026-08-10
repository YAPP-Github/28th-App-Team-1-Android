package com.dminus14.app.feature.interview.component

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class TrapezoidShape(
    private val slant: Dp = 4.dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val slantPx = with(density) { slant.toPx() }
        val path =
            Path().apply {
                moveTo(slantPx, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width - slantPx, size.height)
                lineTo(0f, size.height)
                close()
            }
        return Outline.Generic(path)
    }
}
