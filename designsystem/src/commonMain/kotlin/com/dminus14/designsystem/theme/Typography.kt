package com.dminus14.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

private val DefaultLetterSpacing = (-0.025).em

@Immutable
data class DMinusTypography(
    val head1Sb32: TextStyle,
    val head2M32: TextStyle,
    val head3Sb24: TextStyle,
    val head4M24: TextStyle,
    val head5R24: TextStyle,
    val sub1Sb22: TextStyle,
    val sub2M22: TextStyle,
    val sub3R22: TextStyle,
    val sub4Sb20: TextStyle,
    val sub5M20: TextStyle,
    val sub6R20: TextStyle,
    val sub7Sb18: TextStyle,
    val sub8M18: TextStyle,
    val sub9R18: TextStyle,
    val body1Sb16: TextStyle,
    val body2M16: TextStyle,
    val body3R16: TextStyle,
    val body4Sb14: TextStyle,
    val body5M14: TextStyle,
    val body6R14: TextStyle,
    val body7Sb12: TextStyle,
    val body8M12: TextStyle,
    val body9R12: TextStyle,
)

val DefaultDMinusTypography =
    DMinusTypography(
        head1Sb32 = textStyle(FontWeight.SemiBold, 32.sp, 38.sp),
        head2M32 = textStyle(FontWeight.Medium, 32.sp, 38.sp),
        head3Sb24 = textStyle(FontWeight.SemiBold, 24.sp, 31.sp),
        head4M24 = textStyle(FontWeight.Medium, 24.sp, 31.sp),
        head5R24 = textStyle(FontWeight.Normal, 24.sp, 31.sp),
        sub1Sb22 = textStyle(FontWeight.SemiBold, 22.sp, 29.sp),
        sub2M22 = textStyle(FontWeight.Medium, 22.sp, 29.sp),
        sub3R22 = textStyle(FontWeight.Normal, 22.sp, 29.sp),
        sub4Sb20 = textStyle(FontWeight.SemiBold, 20.sp, 26.sp),
        sub5M20 = textStyle(FontWeight.Medium, 20.sp, 26.sp),
        sub6R20 = textStyle(FontWeight.Normal, 20.sp, 26.sp),
        sub7Sb18 = textStyle(FontWeight.SemiBold, 18.sp, 23.sp),
        sub8M18 = textStyle(FontWeight.Medium, 18.sp, 23.sp),
        sub9R18 = textStyle(FontWeight.Normal, 18.sp, 23.sp),
        body1Sb16 = textStyle(FontWeight.SemiBold, 16.sp, 21.sp),
        body2M16 = textStyle(FontWeight.Medium, 16.sp, 21.sp),
        body3R16 = textStyle(FontWeight.Normal, 16.sp, 21.sp),
        body4Sb14 = textStyle(FontWeight.SemiBold, 14.sp, 18.sp),
        body5M14 = textStyle(FontWeight.Medium, 14.sp, 18.sp),
        body6R14 = textStyle(FontWeight.Normal, 14.sp, 18.sp),
        body7Sb12 = textStyle(FontWeight.SemiBold, 12.sp, 16.sp),
        body8M12 = textStyle(FontWeight.Medium, 12.sp, 16.sp),
        body9R12 = textStyle(FontWeight.Normal, 12.sp, 16.sp),
    )

private fun textStyle(
    weight: FontWeight,
    fontSize: TextUnit,
    lineHeight: TextUnit,
): TextStyle =
    TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = weight,
        fontSize = fontSize,
        lineHeight = lineHeight,
        letterSpacing = DefaultLetterSpacing,
    )
