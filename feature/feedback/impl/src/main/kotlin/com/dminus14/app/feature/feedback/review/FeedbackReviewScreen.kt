package com.dminus14.app.feature.feedback.review

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.feature.feedback.component.GuestFeedbackCommentModal
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.button.HilitMiniButton
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.loading.HilitLoadingIndicator
import com.dminus14.designsystem.component.text.HilitText
import com.dminus14.designsystem.component.text.HilitTextHighlightColor
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun FeedbackReviewScreen(
    onReplayRequested: () -> Unit,
    onCompleted: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedbackReviewViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.onIntent(FeedbackReviewIntent.LoadSession)
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                FeedbackReviewEffect.ReplayRequested -> {
                    onReplayRequested()
                }

                FeedbackReviewEffect.SubmissionCompleted -> {
                    onCompleted()
                }

                FeedbackReviewEffect.ExitRequested -> {
                    onExit()
                }

                is FeedbackReviewEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    BackHandler { viewModel.onIntent(FeedbackReviewIntent.ReplayVideoClicked) }

    FeedbackReviewContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
@Suppress("LongMethod")
fun FeedbackReviewContent(
    state: FeedbackReviewState,
    onIntent: (FeedbackReviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(HilitTheme.colors.gray50),
    ) {
        if (!state.hasLoaded) {
            HilitLoadingIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(top = 42.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        HilitText(
                            text =
                                buildAnnotatedString {
                                    append("${state.nickname}님,\n정성스러운 ")
                                    withHilitTextHighlight { append("피드백") }
                                    append(" 감사해요")
                                },
                            modifier =
                                Modifier
                                    .padding(top = 24.dp)
                                    .padding(bottom = 4.dp),
                            color = HilitTheme.colors.hilitBlack800,
                            style = HilitTheme.typography.head3,
                        )
                    }

                    item {
                        Text(
                            text = "${state.requesterName}님에게 다음 피드백을 전달할게요",
                            style = HilitTheme.typography.body4,
                            color = HilitTheme.colors.gray500,
                            modifier =
                                Modifier.padding(bottom = 16.dp),
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "평가 항목",
                                fontWeight = FontWeight.SemiBold,
                                style = HilitTheme.typography.body2,
                            )

                            HilitMiniButton(
                                onClick = { onIntent(FeedbackReviewIntent.ReplayVideoClicked) },
                            ) {
                                HilitIcon(
                                    asset = HilitIconAsset.Video,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text("영상 다시보기")
                            }
                        }
                    }
                    items(state.axes, key = { it.code }) { axis ->
                        FeedbackReviewCard(
                            axis = axis,
                            onEdit = {
                                onIntent(FeedbackReviewIntent.EditCommentClicked(axis.code))
                            },
                        )
                    }
                }

                HilitFixedBottomButton(
                    text = "피드백 전송하기",
                    enabled = !state.isSubmitting,
                    onClick = { onIntent(FeedbackReviewIntent.SubmitClicked) },
                )
            }
        }
    }

    if (state.isCommentEditorVisible) {
        GuestFeedbackCommentModal(
            value = state.editingValue,
            onValueChange = { onIntent(FeedbackReviewIntent.CommentChanged(it)) },
            onConfirm = { onIntent(FeedbackReviewIntent.CommentConfirmed) },
            onDismiss = { onIntent(FeedbackReviewIntent.CommentDismissed) },
        )
    }
}

@Composable
private fun FeedbackReviewCard(
    axis: FeedbackReviewAxisUiModel,
    onEdit: () -> Unit,
) {
    val highlightColor =
        if (axis.isPositive) HilitTextHighlightColor.Blue else HilitTextHighlightColor.Red
    val borderColor = HilitTheme.colors.gray100

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(HilitTheme.colors.hilitWhite)
                .border((1.2f).dp, borderColor)
                .drawBehind {
                    val leftWidthPx = 6.dp.toPx()
                    drawLine(
                        color = borderColor,
                        start = Offset(leftWidthPx / 2, 0f),
                        end = Offset(leftWidthPx / 2, size.height),
                        strokeWidth = leftWidthPx,
                    )
                }.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = axis.title,
                color = HilitTheme.colors.gray400,
                style = HilitTheme.typography.body9,
            )

            HilitIcon(
                asset = HilitIconAsset.Edit,
                contentDescription = "${axis.title} 코멘트 수정",
                tint = HilitTheme.colors.gray200,
                modifier =
                    Modifier
                        .size(16.dp)
                        .clip(
                            RoundedCornerShape(4.dp),
                        ).clickable(onClick = onEdit),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // if (axis.isPositive) TagColorType.Blue else TagColorType.Red
            HilitText(
                text =
                    buildAnnotatedString {
                        withHilitTextHighlight { append(axis.levelLabel) }
                        append("(이)라고 평가했어요")
                    },
                highlightColor = highlightColor,
                color = HilitTheme.colors.hilitBlack800,
                style = HilitTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold),
            )
        }

        if (axis.comment.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Surface(
                    color = HilitTheme.colors.gray100,
                    modifier =
                        Modifier
                            .width(2.dp)
                            .height(12.dp),
                ) { }

                Text(
                    text = "“${axis.comment}”",
                    color = HilitTheme.colors.gray500,
                    style = HilitTheme.typography.body10,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedbackReviewContentPreview() {
    HilitTheme {
        FeedbackReviewContent(
            state =
                FeedbackReviewState(
                    requesterName = "합성 요청자",
                    nickname = "합성 지인",
                    axes =
                        listOf(
                            FeedbackReviewAxisUiModel(
                                code = GuestFeedbackAxisCode.GAZE,
                                title = "시선",
                                level = 4,
                                levelLabel = "잘 맞춤",
                                comment = "비식별 합성 코멘트",
                            ),
                            FeedbackReviewAxisUiModel(
                                code = GuestFeedbackAxisCode.VOICE,
                                title = "목소리",
                                level = 1,
                                levelLabel = "너무 작음",
                                comment = "비식별 합성 코멘트",
                            ),
                        ),
                    hasLoaded = true,
                ),
            onIntent = {},
        )
    }
}
