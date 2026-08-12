package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.model.CardUiModel
import com.dminus14.designsystem.component.button.HilitMiniButton
import com.dminus14.designsystem.component.button.HilitMiniButtonColor
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 상세 리포트 질문 탭 행 (Figma Node: 443:6906).
 *
 * 카드 목록을 [HilitMiniButton] 칩으로 늘어놓고 선택된 질문만 강조한다. 화면 폭을 넘으면 가로
 * 스크롤한다. 레드플래그 안내는 탭이 아니라 "상세 리포트" 타이틀 옆 아이콘/말풍선으로 노출한다
 * ([DetailReportSectionHeader], Figma Node: 443:7204).
 */
@Composable
internal fun QuestionTabRow(
    cards: List<CardUiModel>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Row(
        modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        cards.forEachIndexed { index, card ->
            val selected = index == selectedIndex
            HilitMiniButton(
                onClick = { onSelect(index) },
                color = if (selected) HilitMiniButtonColor.Green else HilitMiniButtonColor.Dark,
            ) {
                Text(
                    text = card.label,
                    style =
                        if (selected) {
                            HilitTheme.typography.body2
                        } else {
                            HilitTheme.typography.body3
                        },
                    color = if (selected) colors.hilitGreen800 else colors.gray300,
                )
            }
        }
    }
}
