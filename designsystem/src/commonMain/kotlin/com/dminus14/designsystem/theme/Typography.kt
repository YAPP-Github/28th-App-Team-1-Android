package com.dminus14.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.Pretendard_Medium
import com.dminus14.app.core.resources.Pretendard_Regular
import com.dminus14.app.core.resources.Pretendard_SemiBold
import org.jetbrains.compose.resources.Font

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

val DefaultDMinusTypography: DMinusTypography =
    dMinusTypography(FontFamily.Default)

@Composable
fun rememberDMinusTypography(): DMinusTypography {
    val regular = Font(Res.font.Pretendard_Regular, weight = FontWeight.Normal)
    val medium = Font(Res.font.Pretendard_Medium, weight = FontWeight.Medium)
    val semiBold = Font(Res.font.Pretendard_SemiBold, weight = FontWeight.SemiBold)
    val fontFamily =
        remember(regular, medium, semiBold) {
            FontFamily(regular, medium, semiBold)
        }

    return remember(fontFamily) {
        dMinusTypography(fontFamily)
    }
}

private fun dMinusTypography(fontFamily: FontFamily): DMinusTypography =
    DMinusTypography(
        head1Sb32 = textStyle(fontFamily, FontWeight.SemiBold, 32.sp, 38.sp),
        head2M32 = textStyle(fontFamily, FontWeight.Medium, 32.sp, 38.sp),
        head3Sb24 = textStyle(fontFamily, FontWeight.SemiBold, 24.sp, 31.sp),
        head4M24 = textStyle(fontFamily, FontWeight.Medium, 24.sp, 31.sp),
        head5R24 = textStyle(fontFamily, FontWeight.Normal, 24.sp, 31.sp),
        sub1Sb22 = textStyle(fontFamily, FontWeight.SemiBold, 22.sp, 29.sp),
        sub2M22 = textStyle(fontFamily, FontWeight.Medium, 22.sp, 29.sp),
        sub3R22 = textStyle(fontFamily, FontWeight.Normal, 22.sp, 29.sp),
        sub4Sb20 = textStyle(fontFamily, FontWeight.SemiBold, 20.sp, 26.sp),
        sub5M20 = textStyle(fontFamily, FontWeight.Medium, 20.sp, 26.sp),
        sub6R20 = textStyle(fontFamily, FontWeight.Normal, 20.sp, 26.sp),
        sub7Sb18 = textStyle(fontFamily, FontWeight.SemiBold, 18.sp, 23.sp),
        sub8M18 = textStyle(fontFamily, FontWeight.Medium, 18.sp, 23.sp),
        sub9R18 = textStyle(fontFamily, FontWeight.Normal, 18.sp, 23.sp),
        body1Sb16 = textStyle(fontFamily, FontWeight.SemiBold, 16.sp, 21.sp),
        body2M16 = textStyle(fontFamily, FontWeight.Medium, 16.sp, 21.sp),
        body3R16 = textStyle(fontFamily, FontWeight.Normal, 16.sp, 21.sp),
        body4Sb14 = textStyle(fontFamily, FontWeight.SemiBold, 14.sp, 18.sp),
        body5M14 = textStyle(fontFamily, FontWeight.Medium, 14.sp, 18.sp),
        body6R14 = textStyle(fontFamily, FontWeight.Normal, 14.sp, 18.sp),
        body7Sb12 = textStyle(fontFamily, FontWeight.SemiBold, 12.sp, 16.sp),
        body8M12 = textStyle(fontFamily, FontWeight.Medium, 12.sp, 16.sp),
        body9R12 = textStyle(fontFamily, FontWeight.Normal, 12.sp, 16.sp),
    )

private fun textStyle(
    fontFamily: FontFamily,
    weight: FontWeight,
    fontSize: TextUnit,
    lineHeight: TextUnit,
): TextStyle =
    TextStyle(
        fontFamily = fontFamily,
        fontWeight = weight,
        fontSize = fontSize,
        lineHeight = lineHeight,
        letterSpacing = DefaultLetterSpacing,
    )
