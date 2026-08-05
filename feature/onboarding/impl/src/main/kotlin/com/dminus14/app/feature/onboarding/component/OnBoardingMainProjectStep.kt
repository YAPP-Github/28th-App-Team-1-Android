package com.dminus14.app.feature.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.onboarding.OnBoardingInterviewIntent
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.component.textfield.HilitJDTextField
import com.dminus14.designsystem.theme.HilitTheme

private val SubtitleToLabelSpacing = 30.dp
private val LabelToFieldSpacing = 10.dp
private val BubbleBottomPadding = 16.dp
private const val MAIN_PROJECT_MAX_LENGTH = 300
private const val MAIN_PROJECT_PLACEHOLDER =
    "프로젝트에서 담당하신 주요 내용을 담아 10글자 이상 작성해 주세요. " +
        "AI가 프로젝트를 더 좋은 면접 질문을 만들 수 있어요."

@Composable
fun OnBoardingMainProjectStep(
    text: String,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OnBoardingStepHeader(
            tagText = "선택",
            tagColorType = TagColorType.Gray,
            title =
                buildAnnotatedString {
                    append("집중적으로 보고싶은\n")
                    withHilitTextHighlight { append("프로젝트 내용") }
                    append("을 알려주세요")
                },
            subtitle = "해당 프로젝트를 중점으로 면접이 진행돼요.",
        )

        Column(modifier = Modifier.padding(top = SubtitleToLabelSpacing)) {
            Text(
                text = "프로젝트 내용 입력",
                style = HilitTheme.typography.body2,
                color = HilitTheme.colors.hilitBlack800,
                modifier = Modifier.fillMaxWidth(),
            )

            HilitJDTextField(
                value = text,
                onValueChange = { onIntent(OnBoardingInterviewIntent.MainProjectTextChange(it)) },
                placeholder = MAIN_PROJECT_PLACEHOLDER,
                maxLength = MAIN_PROJECT_MAX_LENGTH,
                modifier = Modifier.padding(top = LabelToFieldSpacing),
            )
        }

        OnBoardingHintBubble(
            text = "나중에 등록해도 괜찮아요!",
            modifier =
                Modifier
                    .weight(1f)
                    .padding(bottom = BubbleBottomPadding),
        )
    }
}

@Preview(name = "Empty", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingMainProjectStepPreview() {
    HilitTheme {
        OnBoardingMainProjectStep(
            text = "",
            onIntent = {},
        )
    }
}

@Preview(name = "Filled", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingMainProjectStepFilledPreview() {
    HilitTheme {
        OnBoardingMainProjectStep(
            text = "결제 시스템 리뉴얼 프로젝트에서 백엔드 아키텍처 설계와 API 개발을 담당했습니다.",
            onIntent = {},
        )
    }
}
