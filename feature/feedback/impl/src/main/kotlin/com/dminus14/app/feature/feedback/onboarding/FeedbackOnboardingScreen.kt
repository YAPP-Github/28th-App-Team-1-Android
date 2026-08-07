package com.dminus14.app.feature.feedback.onboarding

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.feedback
import com.dminus14.app.domain.exception.GuestFeedbackValidationException
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.loading.HilitLoadingIndicator
import com.dminus14.designsystem.component.text.HilitText
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.component.textfield.HilitBottomOutlinedTextField
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun FeedbackOnboardingScreen(
    token: String,
    onFeedbackReady: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedbackOnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(token) {
        viewModel.onIntent(FeedbackOnboardingIntent.Load(token))
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                FeedbackOnboardingEffect.FeedbackReady -> {
                    onFeedbackReady()
                }

                FeedbackOnboardingEffect.ExitRequested -> {
                    onExit()
                }

                FeedbackOnboardingEffect.ShowExitHint -> {
                    Toast.makeText(context, "한 번 더 뒤로 가면 종료돼요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    BackHandler { viewModel.onIntent(FeedbackOnboardingIntent.BackPressed) }

    FeedbackOnboardingContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongMethod")
fun FeedbackOnboardingContent(
    state: FeedbackOnboardingState,
    onIntent: (FeedbackOnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(HilitTheme.colors.hilitWhite),
    ) {
        if (state.loadState != FeedbackOnboardingLoadState.Ready) {
            if (state.loadState != FeedbackOnboardingLoadState.Failed) {
                HilitLoadingIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 34.dp),
            ) {
                HilitText(
                    text =
                        buildAnnotatedString {
                            append(state.requesterName.ifEmpty { "요청자" })
                            append("님이 당신께\n")
                            withHilitTextHighlight {
                                append("피드백")
                            }
                            append("을 요청했어요")
                        },
                    modifier =
                        Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 4.dp),
                    color = HilitTheme.colors.hilitBlack800,
                    style = HilitTheme.typography.head3,
                )
                Text(
                    text = "이동 시간 중 딱 10분만 빌려줄 수 있나요?",
                    style = HilitTheme.typography.body4,
                    color = HilitTheme.colors.gray500,
                    modifier =
                        Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 16.dp),
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(color = HilitTheme.colors.gray100)
                            .padding(horizontal = 40.dp),
                ) {
                    Image(
                        painter = painterResource(Res.drawable.feedback),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .padding(bottom = 91.dp)
                                .size(214.dp),
                    )

                    OnboardingGuideRow(
                        icon = HilitIconAsset.Profile,
                        title = "AI가 못 보는 것도 있어요",
                        description = "눈빛, 말투 같은 순간은 당신만 알아요",
                    )

                    OnboardingGuideRow(
                        icon = HilitIconAsset.Play,
                        title = "면접은 외우기가 아니라 대화예요",
                        description = "정해진 답보다 얼마나 자연스러운지 봐주세요",
                    )
                }

                HilitFixedBottomButton(
                    text = "피드백 시작하기",
                    onClick = { onIntent(FeedbackOnboardingIntent.StartClicked) },
                )
            }
        }
    }

    if (state.isNameEditorVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { onIntent(FeedbackOnboardingIntent.NameEditorDismissed) },
            sheetState = sheetState,
            containerColor = HilitTheme.colors.hilitWhite,
            dragHandle = null,
        ) {
            NameEditorBottomSheetContent(
                state = state,
                onIntent = onIntent,
            )
        }
    }
}

@Composable
private fun NameEditorBottomSheetContent(
    state: FeedbackOnboardingState,
    onIntent: (FeedbackOnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .height(578.dp)
                .fillMaxWidth(),
    ) {
        HilitText(
            text =
                buildAnnotatedString {
                    append("리포트에 표시될 당신의\n")
                    withHilitTextHighlight { append("이름") }
                    append("을 입력해주세요")
                },
            modifier =
                Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp)
                    .padding(bottom = 4.dp),
            color = HilitTheme.colors.hilitBlack800,
            style = HilitTheme.typography.head3,
        )
        Text(
            text = "이름은 피드백 리포트에만 반영이 됩니다",
            style = HilitTheme.typography.body4,
            color = HilitTheme.colors.gray500,
            modifier =
                Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            HilitBottomOutlinedTextField(
                value = state.nickname,
                onValueChange = {
                    onIntent(
                        FeedbackOnboardingIntent.NicknameChanged(
                            it.take(GuestFeedbackValidationException.MAX_NICKNAME_LENGTH),
                        ),
                    )
                },
                placeholder = "이름",
                modifier = Modifier.semantics { contentDescription = "피드백 작성자 이름" },
            )
        }

        HilitFixedBottomButton(
            text = "다음",
            enabled = state.canContinue,
            onClick = { onIntent(FeedbackOnboardingIntent.NicknameConfirmed) },
        )
    }
}

@Composable
private fun OnboardingGuideRow(
    icon: HilitIconAsset,
    title: String,
    description: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .background(color = HilitTheme.colors.hilitBlack800),
            contentAlignment = Alignment.Center,
        ) {
            HilitIcon(
                asset = icon,
                tint = HilitTheme.colors.hilitGreen500,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }

        Column {
            Text(
                text = title,
                style = HilitTheme.typography.body2,
                modifier = Modifier.padding(bottom = 2.dp),
            )
            Text(
                text = description,
                color = HilitTheme.colors.gray500,
                style = HilitTheme.typography.body7,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedbackOnboardingContentPreview() {
    HilitTheme {
        FeedbackOnboardingContent(
            state =
                FeedbackOnboardingState(
                    requesterName = "합성 요청자",
                    loadState = FeedbackOnboardingLoadState.Ready,
                ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NameEditorBottomSheetContentPreview() {
    HilitTheme {
        NameEditorBottomSheetContent(
            state = FeedbackOnboardingState(nickname = "홍길동"),
            onIntent = {},
        )
    }
}
