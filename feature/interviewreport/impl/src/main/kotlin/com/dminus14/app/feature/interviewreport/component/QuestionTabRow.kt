package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.model.CardUiModel
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 상세 리포트 질문 탭 행 (Figma Node: 443:6906).
 *
 * 카드 목록을 미니 칩으로 늘어놓고 선택된 질문만 강조한다. 레드플래그가 걸린 카드는
 * 칩 우상단에 빨간 점으로 표시한다. 화면 폭을 넘으면 가로 스크롤한다.
 */
@Composable
internal fun QuestionTabRow(
    cards: List<CardUiModel>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        cards.forEachIndexed { index, card ->
            QuestionTab(
                label = card.label,
                selected = index == selectedIndex,
                hasRedFlag = card.cardRedFlagNotices.isNotEmpty(),
                onClick = { onSelect(index) },
            )
        }
    }
}

@Composable
private fun QuestionTab(
    label: String,
    selected: Boolean,
    hasRedFlag: Boolean,
    onClick: () -> Unit,
) {
    val colors = HilitTheme.colors
    Box {
        Text(
            text = label,
            style = if (selected) HilitTheme.typography.body2 else HilitTheme.typography.body3,
            color = if (selected) colors.hilitGreen800 else colors.gray300,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected) colors.hilitGreen500 else colors.gray900)
                    .clickable(onClick = onClick)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
        )
        if (hasRedFlag) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(colors.error500),
            )
        }
    }
}
