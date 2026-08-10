package stories.components.designsystem.gradienttext

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.text.GradientText
import com.dminus14.designsystem.theme.HilitTheme

/**
 * GradientText Controls용 어댑터.
 *
 * [Color]는 CatalogControls 지원 타입이 아니라 hex 문자열로 받아 어댑터 안에서 파싱한다.
 * 잘못된 hex가 들어와도 카탈로그가 죽지 않도록 default 색으로 fallback한다.
 * typography(`TextStyle`) 역시 지원 타입이 아니라 [GradientTextTypography] enum으로 노출하고,
 * 어댑터 안에서 실제 [HilitTheme] 토큰으로 매핑한다.
 */
@CatalogControls
@Composable
internal fun GradientTextCatalogAdapter(
    text: String,
    typography: GradientTextTypography,
    startColorHex: String,
    endColorHex: String,
) {
    HilitTheme {
        GradientText(
            text = text,
            typo = typography.toTextStyle(),
            startColor = parseHexColorOrDefault(startColorHex, HilitTheme.colors.hilitGreen600),
            endColor = parseHexColorOrDefault(endColorHex, HilitTheme.colors.hilitGreen800),
        )
    }
}

/** GradientText에 적용할 typography 토큰. Controls에서 드롭다운으로 선택하도록 enum으로 노출한다. */
internal enum class GradientTextTypography {
    Head1,
    Head2,
    Head3,
    Head4,
    Head5,
    Head6,
    Sub1,
    Sub2,
    Sub3,
    Sub4,
    Sub5,
    Sub6,
    Sub7,
    Sub8,
    Sub9,
    Body1,
    Body2,
    Body3,
    Body4,
    Body5,
    Body6,
    Body7,
    Body8,
    Body9,
    Body10,
}

@Composable
private fun GradientTextTypography.toTextStyle(): TextStyle {
    val typography = HilitTheme.typography
    return when (this) {
        GradientTextTypography.Head1 -> typography.head1
        GradientTextTypography.Head2 -> typography.head2
        GradientTextTypography.Head3 -> typography.head3
        GradientTextTypography.Head4 -> typography.head4
        GradientTextTypography.Head5 -> typography.head5
        GradientTextTypography.Head6 -> typography.head6
        GradientTextTypography.Sub1 -> typography.sub1
        GradientTextTypography.Sub2 -> typography.sub2
        GradientTextTypography.Sub3 -> typography.sub3
        GradientTextTypography.Sub4 -> typography.sub4
        GradientTextTypography.Sub5 -> typography.sub5
        GradientTextTypography.Sub6 -> typography.sub6
        GradientTextTypography.Sub7 -> typography.sub7
        GradientTextTypography.Sub8 -> typography.sub8
        GradientTextTypography.Sub9 -> typography.sub9
        GradientTextTypography.Body1 -> typography.body1
        GradientTextTypography.Body2 -> typography.body2
        GradientTextTypography.Body3 -> typography.body3
        GradientTextTypography.Body4 -> typography.body4
        GradientTextTypography.Body5 -> typography.body5
        GradientTextTypography.Body6 -> typography.body6
        GradientTextTypography.Body7 -> typography.body7
        GradientTextTypography.Body8 -> typography.body8
        GradientTextTypography.Body9 -> typography.body9
        GradientTextTypography.Body10 -> typography.body10
    }
}

private const val HEX_LENGTH = 6
private const val ALPHA_PREFIX = "ff"
private const val HEX_RADIX = 16

private fun parseHexColorOrDefault(
    hex: String,
    fallback: Color,
): Color =
    runCatching {
        val normalized = hex.removePrefix("#").trim()
        require(normalized.length == HEX_LENGTH)
        Color(("$ALPHA_PREFIX$normalized").toLong(HEX_RADIX))
    }.getOrDefault(fallback)
