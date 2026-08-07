package stories.components.designsystem.gradienttext

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.text.GradientText
import com.dminus14.designsystem.theme.HilitTheme

/**
 * GradientText Controls용 어댑터.
 *
 * [Color]는 CatalogControls 지원 타입이 아니라 hex 문자열로 받아 어댑터 안에서 파싱한다.
 * 잘못된 hex가 들어와도 카탈로그가 죽지 않도록 default 색으로 fallback한다.
 */
@CatalogControls
@Composable
internal fun GradientTextCatalogAdapter(
    text: String,
    startColorHex: String,
    endColorHex: String,
) {
    HilitTheme {
        GradientText(
            text = text,
            startColor = parseHexColorOrDefault(startColorHex, HilitTheme.colors.hilitGreen600),
            endColor = parseHexColorOrDefault(endColorHex, HilitTheme.colors.hilitGreen800),
        )
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
