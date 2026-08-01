package com.dminus14.app.feature.feedback.feedback

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.app.feature.feedback.component.GuestFeedbackCommentModal
import com.dminus14.app.feature.feedback.component.GuestFeedbackVideoPlayer
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.button.HilitMediumButton
import com.dminus14.designsystem.component.button.HilitMediumButtonColor
import com.dminus14.designsystem.component.button.HilitOptionalButton
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.loading.HilitLoadingIndicator
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun FeedbackScreen(
    onReviewReady: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedbackViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(FeedbackIntent.LoadSession)
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                FeedbackEffect.ReviewReady -> onReviewReady()
                FeedbackEffect.ExitRequested -> onExit()
            }
        }
    }
    BackHandler { viewModel.onIntent(FeedbackIntent.BackPressed) }

    FeedbackContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

/** ViewModel 없이 Feedback 영상과 현재 축 평가 상태를 렌더링한다. */
@Composable
fun FeedbackContent(
    state: FeedbackState,
    onIntent: (FeedbackIntent) -> Unit,
    modifier: Modifier = Modifier,
    showVideoPlayer: Boolean = true,
) {
    if (!state.hasLoaded || state.videoUrl.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            HilitLoadingIndicator()
        }
        return
    }

    val selectedAxis = state.axes.firstOrNull { it.code == state.selectedAxis }
    val isSplit = selectedAxis != null && !state.isVideoExpanded

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(HilitTheme.colors.hilitBlack800),
    ) {
        FeedbackVideoArea(
            state = state,
            isSplit = isSplit,
            showVideoPlayer = showVideoPlayer,
            onIntent = onIntent,
            modifier = Modifier.weight(if (isSplit) VIDEO_SPLIT_WEIGHT else 1f),
        )
        if (isSplit) {
            FeedbackAxisPanel(
                requesterName = state.requesterName,
                axis = selectedAxis,
                canReview = state.canReview,
                isPlaybackBlocked = state.isPlaybackBlocked,
                onIntent = onIntent,
                modifier = Modifier.weight(PANEL_SPLIT_WEIGHT),
            )
        }
    }

    if (state.isCommentEditorVisible) {
        GuestFeedbackCommentModal(
            value = state.editingValue,
            onValueChange = { onIntent(FeedbackIntent.CommentChanged(it)) },
            onConfirm = { onIntent(FeedbackIntent.CommentConfirmed) },
            onDismiss = { onIntent(FeedbackIntent.CommentDismissed) },
        )
    }
}

@Composable
@Suppress("LongMethod")
private fun FeedbackVideoArea(
    state: FeedbackState,
    isSplit: Boolean,
    showVideoPlayer: Boolean,
    onIntent: (FeedbackIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        if (showVideoPlayer) {
            GuestFeedbackVideoPlayer(
                videoUrl = state.videoUrl,
                isIntroVisible = state.isVideoIntroVisible,
                requesterName = state.requesterName,
                showBlurredBackdrop = isSplit,
                onIntroCompleted = { onIntent(FeedbackIntent.VideoIntroCompleted) },
                onExpand = { onIntent(FeedbackIntent.VideoExpanded) },
                onFatalPlaybackError = { onIntent(FeedbackIntent.VideoPlaybackFailed) },
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(HilitTheme.colors.hilitBlack800),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "합성 영상 자리",
                    color = HilitTheme.colors.hilitWhite,
                    style = HilitTheme.typography.body4,
                )
            }
            if (isSplit) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 76.dp)
                            .size(30.dp)
                            .background(HilitTheme.colors.gray700)
                            .clickable(
                                role = Role.Button,
                                onClick = { onIntent(FeedbackIntent.VideoExpanded) },
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    HilitIcon(
                        asset = HilitIconAsset.Expand,
                        contentDescription = "영상 확대",
                        tint = HilitTheme.colors.hilitWhite,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(VIDEO_GRADIENT_HEIGHT)
                    .background(
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    HilitTheme.colors.hilitBlack800.copy(alpha = 0f),
                                    HilitTheme.colors.hilitBlack800.copy(alpha = 0.88f),
                                ),
                        ),
                    ),
        )
        FeedbackAxisSelector(
            axes = state.axes,
            selectedAxis = state.selectedAxis,
            enabled = !state.isPlaybackBlocked,
            onSelected = { onIntent(FeedbackIntent.AxisSelected(it)) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun FeedbackAxisSelector(
    axes: List<FeedbackAxisUiModel>,
    selectedAxis: GuestFeedbackAxisCode?,
    enabled: Boolean,
    onSelected: (GuestFeedbackAxisCode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        axes.forEach { axis ->
            val selected = axis.code == selectedAxis

            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .background(
                            if (selected) {
                                HilitTheme.colors.hilitGreen500
                            } else {
                                Color.Transparent
                            },
                        ).clickable(
                            enabled = enabled,
                            role = Role.Button,
                            onClick = { onSelected(axis.code) },
                        ).padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = axis.title,
                    color =
                        if (selected) {
                            HilitTheme.colors.hilitGreen800
                        } else {
                            HilitTheme.colors.hilitWhite
                        },
                    style =
                        if (selected) {
                            HilitTheme.typography.body1
                        } else {
                            HilitTheme.typography.body3
                        },
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
@Suppress("LongMethod", "LongParameterList")
private fun FeedbackAxisPanel(
    requesterName: String,
    axis: FeedbackAxisUiModel,
    canReview: Boolean,
    isPlaybackBlocked: Boolean,
    onIntent: (FeedbackIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(HilitTheme.colors.hilitWhite),
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = axis.code.question(requesterName),
                color = HilitTheme.colors.hilitBlack800,
                style = HilitTheme.typography.sub4,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                HilitTag(
                    colorType = TagColorType.Blue,
                    tagType = TagType.Small,
                    text = "좋았어요",
                )
                HilitTag(
                    colorType = TagColorType.Red,
                    tagType = TagType.Small,
                    text = "아쉬웠어요",
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                axis.code.ratingOptions().forEach { option ->
                    HilitMediumButton(
                        text = option.label,
                        color =
                            when (axis.level) {
                                option.level if option.isPositive -> {
                                    HilitMediumButtonColor.Blue
                                }

                                option.level -> {
                                    HilitMediumButtonColor.Red
                                }

                                else -> {
                                    HilitMediumButtonColor.Gray
                                }
                            },
                        enabled = !isPlaybackBlocked,
                        onClick = {
                            onIntent(FeedbackIntent.RatingSelected(axis.code, option.level))
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            if (axis.comment.isEmpty()) {
                HilitOptionalButton(
                    onClick = {
                        if (!isPlaybackBlocked) {
                            onIntent(FeedbackIntent.CommentEditorClicked(axis.code))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(modifier = Modifier.size(16.dp), contentAlignment = Alignment.Center) {
                        HilitIcon(
                            asset = HilitIconAsset.Plus,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                    Text("왜 그렇게 느꼈나요?")
                    HilitTag(
                        colorType = TagColorType.Gray,
                        tagType = TagType.Small,
                        text = "선택",
                    )
                }
            } else {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .background(HilitTheme.colors.gray50),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(HilitTheme.colors.hilitGreen500),
                    )
                    Text(
                        text = axis.comment,
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(12.dp),
                        color = HilitTheme.colors.hilitBlack800,
                        style = HilitTheme.typography.body6,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "수정",
                        modifier =
                            Modifier
                                .clickable(
                                    enabled = !isPlaybackBlocked,
                                    role = Role.Button,
                                    onClick = {
                                        onIntent(FeedbackIntent.CommentEditorClicked(axis.code))
                                    },
                                ).padding(12.dp),
                        color = HilitTheme.colors.hilitBlack800,
                        style = HilitTheme.typography.body6,
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }
        }
        HilitFixedBottomButton(
            text = "피드백 종료하기",
            enabled = canReview && !isPlaybackBlocked,
            onClick = { onIntent(FeedbackIntent.ReviewClicked) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedbackFullVideoPreview() {
    HilitTheme {
        FeedbackContent(
            state =
                previewState(
                    selectedAxis = GuestFeedbackAxisCode.GESTURE,
                    isVideoExpanded = true,
                ),
            onIntent = {},
            showVideoPlayer = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedbackSplitPreview() {
    HilitTheme {
        FeedbackContent(
            state =
                previewState(
                    selectedAxis = GuestFeedbackAxisCode.GAZE,
                    isVideoExpanded = false,
                ),
            onIntent = {},
            showVideoPlayer = false,
        )
    }
}

private fun previewState(
    selectedAxis: GuestFeedbackAxisCode?,
    isVideoExpanded: Boolean,
) = FeedbackState(
    requesterName = "합성 요청자",
    videoUrl = "https://example.invalid/synthetic.mp4",
    axes =
        listOf(
            FeedbackAxisUiModel(GuestFeedbackAxisCode.GAZE, "시선", MAX_RATING_LEVEL),
            FeedbackAxisUiModel(GuestFeedbackAxisCode.EXPRESSION, "표정"),
            FeedbackAxisUiModel(GuestFeedbackAxisCode.POSTURE, "자세"),
            FeedbackAxisUiModel(GuestFeedbackAxisCode.GESTURE, "손동작"),
            FeedbackAxisUiModel(GuestFeedbackAxisCode.VOICE, "목소리"),
        ),
    selectedAxis = selectedAxis,
    isVideoExpanded = isVideoExpanded,
    isVideoIntroVisible = false,
    hasLoaded = true,
)

private const val MAX_RATING_LEVEL = 4
private const val VIDEO_SPLIT_WEIGHT = 3f
private const val PANEL_SPLIT_WEIGHT = 2f
private val VIDEO_GRADIENT_HEIGHT = 104.dp
