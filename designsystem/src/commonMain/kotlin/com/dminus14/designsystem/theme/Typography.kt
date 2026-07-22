package com.dminus14.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.dminus14.app.core.resources.Pretendard_Bold
import com.dminus14.app.core.resources.Pretendard_Medium
import com.dminus14.app.core.resources.Pretendard_Regular
import com.dminus14.app.core.resources.Pretendard_SemiBold
import com.dminus14.app.core.resources.Res
import org.jetbrains.compose.resources.Font

@Immutable
class HilitTypography internal constructor(
    val head1: TextStyle,
    val head2: TextStyle,
    val head3: TextStyle,
    val head4: TextStyle,
    val head5: TextStyle,
    val head6: TextStyle,
    val sub1: TextStyle,
    val sub2: TextStyle,
    val sub3: TextStyle,
    val sub4: TextStyle,
    val sub5: TextStyle,
    val sub6: TextStyle,
    val sub7: TextStyle,
    val sub8: TextStyle,
    val sub9: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val body3: TextStyle,
    val body4: TextStyle,
    val body5: TextStyle,
    val body6: TextStyle,
    val body7: TextStyle,
    val body8: TextStyle,
    val body9: TextStyle,
    val body10: TextStyle,
)

@Composable
internal fun rememberHilitTypography(): HilitTypography {
    val regular = Font(Res.font.Pretendard_Regular, FontWeight.Normal)
    val medium = Font(Res.font.Pretendard_Medium, FontWeight.Medium)
    val semiBold = Font(Res.font.Pretendard_SemiBold, FontWeight.SemiBold)
    val bold = Font(Res.font.Pretendard_Bold, FontWeight.Bold)
    val fontFamily =
        remember(regular, medium, semiBold, bold) {
            FontFamily(regular, medium, semiBold, bold)
        }

    return remember(fontFamily) {
        HilitTypography(
            head1 = textStyle(fontFamily, FontWeight.SemiBold, 32.sp, 38.sp),
            head2 = textStyle(fontFamily, FontWeight.Medium, 32.sp, 38.sp),
            head3 = textStyle(fontFamily, FontWeight.Bold, 24.sp, 32.sp),
            head4 = textStyle(fontFamily, FontWeight.SemiBold, 24.sp, 32.sp),
            head5 = textStyle(fontFamily, FontWeight.Medium, 24.sp, 31.sp),
            head6 = textStyle(fontFamily, FontWeight.Normal, 24.sp, 31.sp),
            sub1 = textStyle(fontFamily, FontWeight.SemiBold, 22.sp, 29.sp),
            sub2 = textStyle(fontFamily, FontWeight.Medium, 22.sp, 29.sp),
            sub3 = textStyle(fontFamily, FontWeight.Normal, 22.sp, 29.sp),
            sub4 = textStyle(fontFamily, FontWeight.SemiBold, 20.sp, 26.sp),
            sub5 = textStyle(fontFamily, FontWeight.Medium, 20.sp, 26.sp),
            sub6 = textStyle(fontFamily, FontWeight.Normal, 20.sp, 26.sp),
            sub7 = textStyle(fontFamily, FontWeight.SemiBold, 18.sp, 23.sp),
            sub8 = textStyle(fontFamily, FontWeight.Medium, 18.sp, 23.sp),
            sub9 = textStyle(fontFamily, FontWeight.Normal, 18.sp, 23.sp),
            body1 = textStyle(fontFamily, FontWeight.Bold, 16.sp, 21.sp),
            body2 = textStyle(fontFamily, FontWeight.SemiBold, 16.sp, 21.sp),
            body3 = textStyle(fontFamily, FontWeight.Medium, 16.sp, 21.sp),
            body4 = textStyle(fontFamily, FontWeight.Normal, 16.sp, 21.sp),
            body5 = textStyle(fontFamily, FontWeight.SemiBold, 14.sp, 18.sp),
            body6 = textStyle(fontFamily, FontWeight.Medium, 14.sp, 18.sp),
            body7 = textStyle(fontFamily, FontWeight.Normal, 14.sp, 18.sp),
            body8 = textStyle(fontFamily, FontWeight.SemiBold, 12.sp, 16.sp),
            body9 = textStyle(fontFamily, FontWeight.Medium, 12.sp, 16.sp),
            body10 = textStyle(fontFamily, FontWeight.Normal, 12.sp, 16.sp),
        )
    }
}

private fun textStyle(
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    lineHeight: TextUnit,
): TextStyle =
    TextStyle(
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSize,
        lineHeight = lineHeight,
        letterSpacing = (-0.025).em,
    )

internal val LocalHilitTypography =
    staticCompositionLocalOf<HilitTypography> {
        error("HilitTypography is not provided. Wrap content with DMinusTheme.")
    }
