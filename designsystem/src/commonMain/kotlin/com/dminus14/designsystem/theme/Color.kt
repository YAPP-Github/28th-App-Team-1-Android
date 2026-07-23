package com.dminus14.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
class HilitColors internal constructor(
    val hilitWhite: Color,
    val hilitBlack800: Color,
    val hilitBlack900: Color,
    val hilitGreen500: Color,
    val hilitGreen600: Color,
    val hilitGreen800: Color,
    val gray50: Color,
    val gray100: Color,
    val gray200: Color,
    val gray300: Color,
    val gray400: Color,
    val gray500: Color,
    val gray600: Color,
    val gray700: Color,
    val gray800: Color,
    val gray900: Color,
    val error200: Color,
    val error300: Color,
    val error400: Color,
    val error500: Color,
    val positive200: Color,
    val positive500: Color,
    val positive800: Color,
)

internal val DefaultHilitColors =
    HilitColors(
        hilitWhite = Color(0xFFFFFFFF),
        hilitBlack800 = Color(0xFF1A1B1F),
        hilitBlack900 = Color(0xFF121316),
        hilitGreen500 = Color(0xFFACEBA0),
        hilitGreen600 = Color(0xFF88C97C),
        hilitGreen800 = Color(0xFF106100),
        gray50 = Color(0xFFF6F7F9),
        gray100 = Color(0xFFEBECF1),
        gray200 = Color(0xFFBCBEC6),
        gray300 = Color(0xFF9DA0AC),
        gray400 = Color(0xFF8A8D9C),
        gray500 = Color(0xFF6D7183),
        gray600 = Color(0xFF636777),
        gray700 = Color(0xFF494C58),
        gray800 = Color(0xFF31333B),
        gray900 = Color(0xFF27282F),
        error200 = Color(0xFFFFEBEB),
        error300 = Color(0xFFFFA6A6),
        error400 = Color(0xFFFF8383),
        error500 = Color(0xFFFF5757),
        positive200 = Color(0xFFDDFAFF),
        positive500 = Color(0xFF00CFEF),
        positive800 = Color(0xFF008A9F),
    )

internal val LocalHilitColors = staticCompositionLocalOf { DefaultHilitColors }
