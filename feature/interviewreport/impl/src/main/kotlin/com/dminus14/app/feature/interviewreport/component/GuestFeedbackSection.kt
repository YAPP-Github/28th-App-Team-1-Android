package com.dminus14.app.feature.interviewreport.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interviewreport.model.GuestFeedbackAxisUi
import com.dminus14.app.feature.interviewreport.model.GuestFeedbackPersonUiModel
import com.dminus14.app.feature.interviewreport.model.GuestFeedbackRatingUiModel
import com.dminus14.app.feature.interviewreport.model.GuestFeedbackUiModel
import com.dminus14.designsystem.component.button.HilitMiniButton
import com.dminus14.designsystem.component.button.HilitMiniButtonColor
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 지인 피드백 섹션 (Figma Node: 443:7050, 443:7149).
 *
 * 제목 아래로 제출한 지인 탭([GuestTabRow]) · 선택된 지인의 평가 목록([GuestRatingList])을
 * 차례로 보여준다. 아직 아무도 제출하지 않았으면 탭과 목록을 숨긴다. 정원(4명)이 차면
 * [FeedbackRequestCard] 초대 카드도 숨긴다 (Figma Node: 443:7102 에는 카드가 없다).
 */
@Composable
internal fun GuestFeedbackSection(
    model: GuestFeedbackUiModel,
    selectedGuestIndex: Int,
    onSelectGuest: (Int) -> Unit,
    onClickInvite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "지인 피드백",
            style = HilitTheme.typography.sub7,
            color = colors.hilitWhite,
        )
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (model.guests.isNotEmpty()) {
                val safeIndex = selectedGuestIndex.coerceIn(0, model.guests.lastIndex)
                GuestTabRow(
                    guests = model.guests,
                    selectedIndex = safeIndex,
                    onSelect = onSelectGuest,
                )
                GuestRatingList(ratings = model.guests[safeIndex].ratings)
            }
            if (model.participantCount < model.capacity) {
                FeedbackRequestCard(model = model, onClick = onClickInvite)
            }
        }
    }
}

@Composable
private fun GuestTabRow(
    guests: List<GuestFeedbackPersonUiModel>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Row(
        modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        guests.forEachIndexed { index, guest ->
            val selected = index == selectedIndex
            HilitMiniButton(
                onClick = { onSelect(index) },
                color = if (selected) HilitMiniButtonColor.Green else HilitMiniButtonColor.Dark,
            ) {
                Text(
                    text = guest.alias,
                    style =
                        if (selected) HilitTheme.typography.body2 else HilitTheme.typography.body3,
                    color = if (selected) colors.hilitGreen800 else colors.gray300,
                )
            }
        }
    }
}

@Composable
private fun GuestRatingList(
    ratings: List<GuestFeedbackRatingUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(19.dp),
    ) {
        ratings.forEachIndexed { index, rating ->
            GuestFeedbackRatingRow(rating = rating)
            if (index != ratings.lastIndex) {
                GuestFeedbackDivider()
            }
        }
    }
}

@Composable
private fun GuestFeedbackRatingRow(
    rating: GuestFeedbackRatingUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(20.dp).background(colors.gray900),
                contentAlignment = Alignment.Center,
            ) {
                HilitIcon(
                    asset = rating.axis.icon(),
                    contentDescription = null,
                    tint = colors.gray200,
                    modifier = Modifier.size(14.dp),
                )
            }
            Text(
                text = rating.axisLabel,
                style = HilitTheme.typography.body2,
                color = colors.hilitWhite,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = rating.levelLabel,
                style = HilitTheme.typography.sub7,
                color = colors.hilitWhite,
            )
            if (rating.comment.isNotBlank()) {
                GuestFeedbackCommentText(comment = rating.comment)
            }
        }
    }
}

private fun GuestFeedbackAxisUi.icon(): HilitIconAsset =
    when (this) {
        GuestFeedbackAxisUi.GAZE -> HilitIconAsset.Eyes
        GuestFeedbackAxisUi.EXPRESSION -> HilitIconAsset.Face
        GuestFeedbackAxisUi.POSTURE -> HilitIconAsset.Body
        GuestFeedbackAxisUi.GESTURE -> HilitIconAsset.Hand
        GuestFeedbackAxisUi.VOICE -> HilitIconAsset.Voice
    }

/**
 * 코멘트 한 줄. 한 줄을 넘어가는 코멘트만 펼치기/접기 chevron 을 붙인다
 * (Figma Node: 443:7074, "두줄로 갈 경우 아코디언" 주석).
 */
@Composable
private fun GuestFeedbackCommentText(
    comment: String,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    var expanded by remember(comment) { mutableStateOf(false) }
    var canExpand by remember(comment) { mutableStateOf(false) }
    Row(
        modifier =
            modifier.fillMaxWidth().let { base ->
                if (canExpand) {
                    base.clickable { expanded = !expanded }
                } else {
                    base
                }
            },
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = comment,
            style = HilitTheme.typography.body7,
            color = colors.gray200,
            maxLines = if (expanded) Int.MAX_VALUE else 1,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result -> if (!expanded) canExpand = result.hasVisualOverflow },
            modifier = Modifier.weight(1f),
        )
        if (canExpand) {
            HilitIcon(
                asset = if (expanded) HilitIconAsset.Up else HilitIconAsset.Down,
                contentDescription = if (expanded) "코멘트 접기" else "코멘트 펼치기",
                tint = colors.hilitWhite,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun GuestFeedbackDivider(modifier: Modifier = Modifier) {
    val colors = HilitTheme.colors
    Box(modifier = modifier.fillMaxWidth().height(1.dp).background(colors.gray800))
}
