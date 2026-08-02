package com.dminus14.designsystem.component.reportcard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 면접 리포트 카드 한 항목.
 *
 * Figma: report-card-open (`649:6611`), report-card-close (`649:6645`)
 *
 * @param date 날짜 라벨
 * @param title 펼친 상태에서 표시할 제목. 접힌 상태에서는 사용하지 않는다
 * @param expanded 펼침 여부. 목록에서는 [resolveHilitReportCardExpansion]으로 단일 expand를 유지한다
 * @param onExpandClick 접힌 카드 전체 클릭 시 호출된다
 * @param onActionClick 펼친 카드의 화살표 버튼 클릭 시 호출된다
 */
@Composable
fun HilitReportCard(
    date: String,
    title: String,
    expanded: Boolean,
    onExpandClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = expanded,
        modifier = modifier.fillMaxWidth(),
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "hilitReportCardExpanded",
    ) { isExpanded ->
        if (isExpanded) {
            HilitReportCardExpandedContent(
                date = date,
                title = title,
                onActionClick = onActionClick,
            )
        } else {
            HilitReportCardCollapsedContent(
                date = date,
                onExpandClick = onExpandClick,
            )
        }
    }
}

/**
 * 단일 expand 정책.
 *
 * - 다른 항목을 펼치면 [requestedId]만 반환되어 기존 expand는 해제된다.
 * - 이미 펼쳐진 항목을 다시 요청하면 접힌다(`null`).
 */
fun resolveHilitReportCardExpansion(
    currentExpandedId: String?,
    requestedId: String,
): String? = if (currentExpandedId == requestedId) null else requestedId

@Composable
private fun HilitReportCardCollapsedContent(
    date: String,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = date,
        style = HilitTheme.typography.body3,
        color = HilitTheme.colors.hilitBlack800,
        modifier =
            modifier
                .fillMaxWidth()
                .background(HilitReportCardCollapsedBackground)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClick = onExpandClick,
                )
                .padding(CollapsedPadding),
    )
}

@Composable
private fun HilitReportCardExpandedContent(
    date: String,
    title: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(HilitTheme.colors.hilitBlack800)
                .padding(
                    horizontal = ExpandedHorizontalPadding,
                    vertical = ExpandedVerticalPadding,
                ),
    ) {
        Text(
            text = date,
            style = HilitTheme.typography.body3,
            color = HilitTheme.colors.hilitGreen500,
        )
        Text(
            text = title,
            style = HilitTheme.typography.sub4,
            color = HilitTheme.colors.hilitWhite,
            modifier =
                Modifier
                    .padding(top = DateToTitleSpacing)
                    .fillMaxWidth(),
        )
        Row(
            modifier =
                Modifier
                    .padding(top = TitleToActionSpacing)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Row(
                modifier =
                    Modifier
                        .background(HilitTheme.colors.hilitGreen500)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = onActionClick,
                        )
                        .padding(ActionPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HilitIcon(
                    asset = HilitIconAsset.Right,
                    contentDescription = null,
                    tint = HilitTheme.colors.hilitBlack800,
                )
            }
        }
    }
}

// Figma hilit green/200 (#D2EFCC). 제품 token 미정의 — spec 확정 시 HilitColors로 이전.
private val HilitReportCardCollapsedBackground = Color(0xFFD2EFCC)

private val CollapsedPadding = 20.dp
private val ExpandedHorizontalPadding = 20.dp
private val ExpandedVerticalPadding = 24.dp
private val DateToTitleSpacing = 8.dp
private val TitleToActionSpacing = 16.dp
private val ActionPadding = 10.dp

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun HilitReportCardCollapsedPreview() {
    HilitTheme {
        HilitReportCard(
            date = "7월 10일 월",
            title = "",
            expanded = false,
            onExpandClick = {},
            onActionClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun HilitReportCardExpandedPreview() {
    HilitTheme {
        HilitReportCard(
            date = "7월 11일 월",
            title = "캐시 도입 결정의 이유와 한계까지 구체적인 수치로 설명해 주셨어요",
            expanded = true,
            onExpandClick = {},
            onActionClick = {},
        )
    }
}
