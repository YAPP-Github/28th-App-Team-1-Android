package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.model.GuestFeedbackUiModel
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 지인 피드백 요청 카드 (Figma Node: 435:1433).
 *
 * 좌측 green bar + (제목 · 참여수/정원) + 서브텍스트 + 우측 chevron 으로 구성한다.
 * 탭하면 지인 피드백 항목선택 플로우로 진입한다.
 */
@Composable
@Suppress("LongMethod")
internal fun FeedbackRequestCard(
    model: GuestFeedbackUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(colors.gray900)
                .clickable(onClick = onClick)
                .padding(end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .width(4.dp)
                    .height(70.dp)
                    .background(colors.hilitGreen500),
        )
        Row(
            modifier = Modifier.weight(1f).padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = model.title,
                        style = HilitTheme.typography.body2,
                        color = colors.hilitWhite,
                    )
                    Box(
                        modifier = Modifier.width(1.dp).height(16.dp).background(colors.gray700),
                    )
                    Text(
                        text = "${model.participantCount}/${model.capacity}",
                        style = HilitTheme.typography.body2,
                        color = colors.hilitWhite,
                    )
                }
                Text(
                    text = model.subtitle,
                    style = HilitTheme.typography.body7,
                    // TODO(designsystem): Figma #D2D6DE 대응 토큰 확정 필요, 임시 gray200.
                    color = colors.gray200,
                )
            }
            HilitIcon(
                asset = HilitIconAsset.Right,
                contentDescription = null,
                tint = colors.gray400,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
