package com.dminus14.app.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

private val CardShapeRadius = 12.dp
private val CardHorizontalPadding = 20.dp
private val CardVerticalPadding = 24.dp

/**
 * 세션 시작 오버레이 안의 흰색 라운드 카드 컨테이너.
 * designsystem에 공용 카드 primitive가 아직 없어 홈 feature 로컬로 둔다.
 */
@Composable
internal fun HomeSessionStartCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CardShapeRadius))
                .background(HilitTheme.colors.hilitWhite)
                .padding(
                    horizontal = CardHorizontalPadding,
                    vertical = CardVerticalPadding,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

@Preview(
    name = "HomeSessionStartCard",
    showBackground = true,
    backgroundColor = 0xFFDDEEDD,
    widthDp = 335,
    heightDp = 220,
)
@Composable
private fun HomeSessionStartCardPreview() {
    HilitTheme {
        HomeSessionStartCard {
            Text(text = "카드 내용 슬롯", color = HilitTheme.colors.hilitBlack800)
        }
    }
}
