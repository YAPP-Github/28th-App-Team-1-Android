package com.dminus14.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val BaseMaterialColorScheme = lightColorScheme()
private val BaseMaterialTypography = Typography()
private val BaseMaterialShapes = Shapes()

object HilitTheme {
    val colors: HilitColors
        @Composable
        @ReadOnlyComposable
        get() = LocalHilitColors.current

    val typography: HilitTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalHilitTypography.current
}

@Composable
fun HilitTheme(content: @Composable () -> Unit) {
    val typography = rememberHilitTypography()

    MaterialTheme(
        colorScheme = BaseMaterialColorScheme,
        typography = BaseMaterialTypography,
        shapes = BaseMaterialShapes,
    ) {
        CompositionLocalProvider(
            LocalHilitColors provides DefaultHilitColors,
            LocalHilitTypography provides typography,
            content = content,
        )
    }
}
