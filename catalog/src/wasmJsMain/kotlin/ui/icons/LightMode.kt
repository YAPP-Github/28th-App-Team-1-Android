package ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// From "https://fonts.gstatic.com/render/v1/Material+Symbols+Outlined/40dp/light_mode.kt?var=opsz,wght,FILL,GRAD,ROND@40,400,0,0,50"
@Suppress("CheckReturnValue")
internal val LightMode: ImageVector
    get() {
        if (lightMode != null) {
            return lightMode!!
        }
        lightMode =
            ImageVector
                .Builder(
                    name = "light_mode",
                    defaultWidth = 40.dp,
                    defaultHeight = 40.dp,
                    viewportWidth = 40f,
                    viewportHeight = 40f,
                ).apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Bevel,
                        strokeLineMiter = 1f,
                        pathFillType = PathFillType.Companion.NonZero,
                    ) {
                        moveTo(23.93f, 23.93f)
                        quadTo(25.56f, 22.31f, 25.56f, 20f)
                        reflectiveQuadTo(23.93f, 16.07f)
                        reflectiveQuadTo(20f, 14.44f)
                        reflectiveQuadToRelative(-3.93f, 1.63f)
                        reflectiveQuadTo(14.44f, 20f)
                        reflectiveQuadToRelative(1.63f, 3.93f)
                        reflectiveQuadTo(20f, 25.56f)
                        reflectiveQuadToRelative(3.93f, -1.63f)
                        close()
                        moveTo(14.1f, 25.9f)
                        quadTo(11.67f, 23.46f, 11.67f, 20f)
                        reflectiveQuadTo(14.1f, 14.1f)
                        reflectiveQuadTo(20f, 11.67f)
                        reflectiveQuadToRelative(5.9f, 2.44f)
                        reflectiveQuadTo(28.33f, 20f)
                        reflectiveQuadTo(25.9f, 25.9f)
                        reflectiveQuadTo(20f, 28.33f)
                        reflectiveQuadTo(14.1f, 25.9f)
                        close()
                        moveTo(8.33f, 21.39f)
                        horizontalLineTo(1.67f)
                        verticalLineTo(18.61f)
                        horizontalLineTo(8.33f)
                        verticalLineToRelative(2.78f)
                        close()
                        moveToRelative(30f, 0f)
                        horizontalLineTo(31.67f)
                        verticalLineTo(18.61f)
                        horizontalLineToRelative(6.67f)
                        verticalLineToRelative(2.78f)
                        close()
                        moveTo(18.61f, 8.33f)
                        verticalLineTo(1.67f)
                        horizontalLineToRelative(2.78f)
                        verticalLineTo(8.33f)
                        horizontalLineTo(18.61f)
                        close()
                        moveToRelative(0f, 30f)
                        verticalLineTo(31.67f)
                        horizontalLineToRelative(2.78f)
                        verticalLineToRelative(6.67f)
                        horizontalLineTo(18.61f)
                        close()
                        moveTo(10.83f, 12.69f)
                        lineTo(6.65f, 8.65f)
                        lineTo(8.64f, 6.61f)
                        lineToRelative(4f, 4.17f)
                        lineToRelative(-1.81f, 1.92f)
                        close()
                        moveTo(31.39f, 33.36f)
                        lineTo(27.32f, 29.18f)
                        lineToRelative(1.87f, -1.9f)
                        lineToRelative(4.15f, 4.07f)
                        lineToRelative(-1.96f, 2.01f)
                        close()
                        moveTo(27.28f, 10.81f)
                        lineTo(31.35f, 6.65f)
                        lineToRelative(2.04f, 1.96f)
                        lineToRelative(-4.14f, 4.06f)
                        lineTo(27.28f, 10.81f)
                        close()
                        moveTo(6.64f, 31.36f)
                        lineToRelative(4.15f, -4.07f)
                        lineToRelative(1.93f, 1.9f)
                        lineTo(8.65f, 33.35f)
                        lineTo(6.64f, 31.36f)
                        close()
                        moveTo(20f, 20f)
                        close()
                    }
                }.build()
        return lightMode!!
    }

private var lightMode: ImageVector? = null
