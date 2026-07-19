package ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val DarkMode: ImageVector
    get() {
        if (darkMode != null) {
            return darkMode!!
        }

        darkMode =
            ImageVector
                .Builder(
                    name = "dark_mode",
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
                        moveTo(20f, 35f)
                        quadTo(13.75f, 35f, 9.38f, 30.63f)
                        reflectiveQuadTo(5f, 20f)
                        reflectiveQuadTo(9.38f, 9.38f)
                        reflectiveQuadTo(20f, 5f)
                        quadToRelative(0.42f, 0f, 0.85f, 0.03f)
                        reflectiveQuadToRelative(1.01f, 0.08f)
                        quadTo(20.29f, 6.4f, 19.4f, 8.35f)
                        reflectiveQuadTo(18.5f, 12.5f)
                        quadToRelative(0f, 3.75f, 2.63f, 6.38f)
                        reflectiveQuadTo(27.5f, 21.5f)
                        quadToRelative(2.21f, 0f, 4.15f, -0.85f)
                        reflectiveQuadToRelative(3.24f, -2.34f)
                        quadToRelative(0.06f, 0.51f, 0.08f, 0.91f)
                        reflectiveQuadTo(35f, 20f)
                        quadToRelative(0f, 6.25f, -4.38f, 10.63f)
                        reflectiveQuadTo(20f, 35f)
                        close()
                        moveToRelative(0f, -2.78f)
                        quadToRelative(4.25f, 0f, 7.47f, -2.55f)
                        reflectiveQuadToRelative(4.22f, -6.16f)
                        quadToRelative(-0.97f, 0.38f, -2.05f, 0.57f)
                        reflectiveQuadTo(27.5f, 24.28f)
                        quadToRelative(-4.89f, 0f, -8.34f, -3.44f)
                        reflectiveQuadTo(15.72f, 12.5f)
                        quadToRelative(0f, -0.94f, 0.18f, -1.99f)
                        reflectiveQuadTo(16.51f, 8.22f)
                        quadToRelative(-3.81f, 1.19f, -6.27f, 4.46f)
                        reflectiveQuadTo(7.78f, 20f)
                        quadToRelative(0f, 5.08f, 3.57f, 8.65f)
                        reflectiveQuadTo(20f, 32.22f)
                        close()
                        moveToRelative(-0.25f, -12f)
                        close()
                    }
                }.build()

        return darkMode!!
    }

private var darkMode: ImageVector? = null
