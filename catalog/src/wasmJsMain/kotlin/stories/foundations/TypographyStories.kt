package stories.foundations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val TypographyStories =
    StoryGroup(
        path = "Foundations/Typography",
        description = "Pretendard를 사용하는 25개 제품 텍스트 스타일입니다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "전체 타이포그래피",
                    description = "스타일별 크기, 굵기, 행간과 공통 자간을 확인합니다.",
                    content = { TypographyScale() },
                ),
            ),
    )

@Composable
private fun TypographyScale() {
    val typography = HilitTheme.typography
    val tokens =
        listOf(
            TypographyToken("head1", "32sp / 38sp / SemiBold", typography.head1),
            TypographyToken("head2", "32sp / 38sp / Medium", typography.head2),
            TypographyToken("head3", "24sp / 32sp / Bold", typography.head3),
            TypographyToken("head4", "24sp / 32sp / SemiBold", typography.head4),
            TypographyToken("head5", "24sp / 31sp / Medium", typography.head5),
            TypographyToken("head6", "24sp / 31sp / Regular", typography.head6),
            TypographyToken("sub1", "22sp / 29sp / SemiBold", typography.sub1),
            TypographyToken("sub2", "22sp / 29sp / Medium", typography.sub2),
            TypographyToken("sub3", "22sp / 29sp / Regular", typography.sub3),
            TypographyToken("sub4", "20sp / 26sp / SemiBold", typography.sub4),
            TypographyToken("sub5", "20sp / 26sp / Medium", typography.sub5),
            TypographyToken("sub6", "20sp / 26sp / Regular", typography.sub6),
            TypographyToken("sub7", "18sp / 23sp / SemiBold", typography.sub7),
            TypographyToken("sub8", "18sp / 23sp / Medium", typography.sub8),
            TypographyToken("sub9", "18sp / 23sp / Regular", typography.sub9),
            TypographyToken("body1", "16sp / 21sp / Bold", typography.body1),
            TypographyToken("body2", "16sp / 21sp / SemiBold", typography.body2),
            TypographyToken("body3", "16sp / 21sp / Medium", typography.body3),
            TypographyToken("body4", "16sp / 21sp / Regular", typography.body4),
            TypographyToken("body5", "14sp / 18sp / SemiBold", typography.body5),
            TypographyToken("body6", "14sp / 18sp / Medium", typography.body6),
            TypographyToken("body7", "14sp / 18sp / Regular", typography.body7),
            TypographyToken("body8", "12sp / 16sp / SemiBold", typography.body8),
            TypographyToken("body9", "12sp / 16sp / Medium", typography.body9),
            TypographyToken("body10", "12sp / 16sp / Regular", typography.body10),
        )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        tokens.forEach { token -> TypographyTokenItem(token) }
    }
}

@Composable
private fun TypographyTokenItem(token: TypographyToken) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "${token.name} · ${token.spec} · letter spacing -2.5%",
            style = HilitTheme.typography.body10,
            color = HilitTheme.colors.gray600,
        )
        Text(
            text = "면접 준비를 시작해 보세요 ABC 123",
            style = token.style,
            color = HilitTheme.colors.hilitBlack900,
        )
    }
}

private data class TypographyToken(
    val name: String,
    val spec: String,
    val style: TextStyle,
)
