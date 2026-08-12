package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.delay

private const val RED_FLAG_BUBBLE_VISIBLE_MS = 3_000L

/**
 * "상세 리포트" 섹션 타이틀 (Figma Node: 443:7204).
 *
 * [redFlagNotices] 가 있으면 타이틀 옆에 경고 아이콘을 노출한다. 화면 진입 시 안내 말풍선이 한 번
 * 자동으로 뜨고, 이후에도 아이콘을 탭하면 다시 뜬다. 말풍선은 뜬 지 3초 뒤 자동으로 사라진다
 * (재탭하면 3초 타이머가 리셋된다). 레드플래그 정보는 더 이상 질문 탭(QuestionTabRow)의 점으로
 * 표시하지 않는다.
 */
@Composable
internal fun DetailReportSectionHeader(
    redFlagNotices: List<String>,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    var showBubble by remember { mutableStateOf(false) }
    // 0보다 크면 노출을 "요청"한 것으로 본다. 레드플래그가 있으면 진입 즉시 1회 자동 요청되고,
    // 이후 아이콘 탭마다 값을 증가시켜 같은 이펙트를 다시 트리거(=3초 타이머 리셋)한다.
    var bubbleRequestCount by remember {
        mutableIntStateOf(if (redFlagNotices.isNotEmpty()) 1 else 0)
    }
    LaunchedEffect(bubbleRequestCount) {
        if (bubbleRequestCount > 0) {
            showBubble = true
            delay(RED_FLAG_BUBBLE_VISIBLE_MS)
            showBubble = false
        }
    }
    Box(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "상세 리포트",
                style = HilitTheme.typography.sub7,
                color = colors.hilitWhite,
            )
            if (redFlagNotices.isNotEmpty()) {
                HilitIcon(
                    asset = HilitIconAsset.FillWarning,
                    contentDescription = "리포트 안내",
                    modifier =
                        Modifier
                            .padding(start = 8.dp)
                            .size(16.dp)
                            .clickable { bubbleRequestCount += 1 },
                )
            }
        }
        if (showBubble && redFlagNotices.isNotEmpty()) {
            RedFlagBubble(
                text = redFlagNotices.joinToString(separator = "\n"),
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .offset(y = (-44).dp),
            )
        }
    }
}

@Composable
private fun RedFlagBubble(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Column(modifier = modifier) {
        Text(
            text = text,
            style = HilitTheme.typography.body5,
            color = colors.hilitWhite,
            modifier =
                Modifier
                    .background(colors.gray900)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
        )
        Box(
            modifier =
                Modifier
                    .padding(start = 12.dp)
                    .offset(y = (-4).dp)
                    .size(8.dp)
                    .rotate(45f)
                    .background(colors.gray900),
        )
    }
}
