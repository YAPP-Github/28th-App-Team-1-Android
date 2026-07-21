package com.dminus14.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalDMinusColors =
    staticCompositionLocalOf {
        DefaultDMinusColors
    }

val LocalDMinusTypography =
    staticCompositionLocalOf {
        DefaultDMinusTypography
    }

@Composable
fun DMinusTheme(content: @Composable () -> Unit) {
    val typography = rememberDMinusTypography()

    CompositionLocalProvider(
        LocalDMinusColors provides DefaultDMinusColors,
        LocalDMinusTypography provides typography,
        content = content,
    )
}

object DMinusTheme {
    val colors: DMinusColors
        @Composable
        @ReadOnlyComposable
        get() = LocalDMinusColors.current

    val typography: DMinusTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalDMinusTypography.current
}
