package com.dminus14.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

private val Gray900 = Color(0xFF1A1B1F)
private val Gray700 = Color(0xFF4A4B50)
private val Gray500 = Color(0xFF8A8B90)
private val Gray300 = Color(0xFFC4C5C9)
private val Gray100 = Color(0xFFF2F3F5)
private val White = Color(0xFFFFFFFF)
private val BrandBlue = Color(0xFF3B5BDB)
private val ErrorRed = Color(0xFFD92D20)

@Immutable
data class DMinusColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textInverse: Color,
    val textDisabled: Color,
    val background: Color,
    val surface: Color,
    val brand: Color,
    val brandOn: Color,
    val error: Color,
    val errorOn: Color,
)

val DefaultDMinusColors =
    DMinusColors(
        textPrimary = Gray900,
        textSecondary = Gray700,
        textTertiary = Gray500,
        textInverse = White,
        textDisabled = Gray300,
        background = White,
        surface = Gray100,
        brand = BrandBlue,
        brandOn = White,
        error = ErrorRed,
        errorOn = White,
    )
