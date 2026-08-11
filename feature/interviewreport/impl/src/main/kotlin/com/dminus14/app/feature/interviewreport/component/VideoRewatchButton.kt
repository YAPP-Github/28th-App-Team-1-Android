package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.model.VideoUiModel
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 영상 다시보기 진입 버튼.
 *
 * - [VideoUiModel.expired] 이면 비활성으로 렌더하고 만료 안내 문구를 함께 노출한다 (기획서 §3-2).
 * - `video == null` 이면 렌더하지 않는다.
 */
@Composable
internal fun VideoRewatchButton(
    video: VideoUiModel?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (video == null) return
    val colors = HilitTheme.colors
    val disabled = video.expired || video.url.isNullOrBlank()
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (disabled) colors.gray100 else colors.hilitGreen500)
                .let { m -> if (disabled) m else m.clickable(onClick = onClick) }
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = if (disabled) "영상 다시 보기 (만료됨)" else "영상 다시 보기",
            style = HilitTheme.typography.sub7,
            color = if (disabled) colors.gray500 else colors.hilitGreen800,
        )
        video.expiredNotice?.let { notice ->
            Text(
                text = notice,
                style = HilitTheme.typography.body9,
                color = colors.gray600,
            )
        }
    }
}
