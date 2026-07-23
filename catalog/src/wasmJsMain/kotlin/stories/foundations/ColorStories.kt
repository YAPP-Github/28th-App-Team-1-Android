package stories.foundations

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val ColorStories =
    StoryGroup(
        path = "Foundations/Color",
        description = "Figma에서 전달된 23개 제품 컬러 토큰입니다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "전체 컬러",
                    description = "토큰 이름과 HEX 값을 함께 확인합니다.",
                    content = { ColorPalette() },
                ),
            ),
    )

@Composable
private fun ColorPalette() {
    val colors = HilitTheme.colors
    val tokens =
        listOf(
            ColorToken("hilit-white", "#FFFFFF", colors.hilitWhite),
            ColorToken("hilit-black-800", "#1A1B1F", colors.hilitBlack800),
            ColorToken("hilit-black-900", "#121316", colors.hilitBlack900),
            ColorToken("hilit-green-500", "#ACEBA0", colors.hilitGreen500),
            ColorToken("hilit-green-600", "#88C97C", colors.hilitGreen600),
            ColorToken("hilit-green-800", "#106100", colors.hilitGreen800),
            ColorToken("gray-50", "#F6F7F9", colors.gray50),
            ColorToken("gray-100", "#EBECF1", colors.gray100),
            ColorToken("gray-200", "#BCBEC6", colors.gray200),
            ColorToken("gray-300", "#9DA0AC", colors.gray300),
            ColorToken("gray-400", "#8A8D9C", colors.gray400),
            ColorToken("gray-500", "#6D7183", colors.gray500),
            ColorToken("gray-600", "#636777", colors.gray600),
            ColorToken("gray-700", "#494C58", colors.gray700),
            ColorToken("gray-800", "#31333B", colors.gray800),
            ColorToken("gray-900", "#27282F", colors.gray900),
            ColorToken("error-200", "#FFEBEB", colors.error200),
            ColorToken("error-300", "#FFA6A6", colors.error300),
            ColorToken("error-400", "#FF8383", colors.error400),
            ColorToken("error-500", "#FF5757", colors.error500),
            ColorToken("positive-200", "#DDFAFF", colors.positive200),
            ColorToken("positive-500", "#00CFEF", colors.positive500),
            ColorToken("positive-800", "#008A9F", colors.positive800),
        )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        tokens.forEach { token -> ColorTokenRow(token) }
    }
}

@Composable
private fun ColorTokenRow(token: ColorToken) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(width = 72.dp, height = 48.dp)
                    .background(token.color, RoundedCornerShape(8.dp))
                    .border(1.dp, HilitTheme.colors.gray200, RoundedCornerShape(8.dp)),
        )

        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Text(
                text = token.name,
                style = HilitTheme.typography.body5,
            )
            Text(
                text = token.hex,
                style = HilitTheme.typography.body10,
                color = HilitTheme.colors.gray600,
            )
        }
    }
}

private data class ColorToken(
    val name: String,
    val hex: String,
    val color: Color,
)
