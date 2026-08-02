package com.dminus14.app.feature.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.onboarding.JobDescriptionTab
import com.dminus14.app.feature.onboarding.OnBoardingInterviewIntent
import com.dminus14.designsystem.component.tab.HilitTabItem
import com.dminus14.designsystem.component.tab.HilitTabRow
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.component.textfield.HilitAsyncTextField
import com.dminus14.designsystem.component.textfield.HilitAsyncTextFieldType
import com.dminus14.designsystem.component.textfield.HilitJDTextField
import com.dminus14.designsystem.theme.HilitTheme

private val SubtitleToTabSpacing = 34.dp
private val TabToFieldSpacing = 16.dp
private val BubbleBottomPadding = 16.dp
private const val JOB_DESCRIPTION_TEXT_MAX_LENGTH = 3000

@Composable
fun OnBoardingJobDescriptionStep(
    tab: JobDescriptionTab,
    link: String,
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
                    withHilitTextHighlight { append("채용공고 링크") }
                    append("를\n업로드해 주세요.")
                },
            subtitle = "채용 페이지에 직접 올라온 공고 링크를 넣어주세요.",
        )

        HilitTabRow(
            items =
                listOf(
                    HilitTabItem(text = "링크 붙여넣기"),
                    HilitTabItem(text = "직접 입력하기"),
                ),
            selectedIndex = tab.ordinal,
            onTabSelected = { onIntent(OnBoardingInterviewIntent.JobDescriptionTabChange(it)) },
            modifier = Modifier.padding(top = SubtitleToTabSpacing),
        )

        OnBoardingJobDescriptionField(
            tab = tab,
            link = link,
            text = text,
            onIntent = onIntent,
            modifier = Modifier.padding(top = TabToFieldSpacing),
        )

        OnBoardingHintBubble(
            text = "링크 입력을 원하지 않으면 넘어가도 괜찮아요.",
            modifier =
                Modifier
                    .weight(1f)
                    .padding(bottom = BubbleBottomPadding),
        )
    }
}

@Composable
private fun OnBoardingJobDescriptionField(
    tab: JobDescriptionTab,
    link: String,
    text: String,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (tab) {
        JobDescriptionTab.Link -> {
            val linkFieldType =
                if (link.isEmpty()) {
                    HilitAsyncTextFieldType.Ready
                } else {
                    HilitAsyncTextFieldType.Edit
                }
            HilitAsyncTextField(
                value = link,
                onValueChange = {
                    onIntent(
                        OnBoardingInterviewIntent.JobDescriptionLinkChange(it),
                    )
                },
                type = linkFieldType,
                placeholder = "https://www.hilit.com/",
                subText = "서브 텍스트를 입력해주세요",
                onClearClick = { onIntent(OnBoardingInterviewIntent.JobDescriptionLinkChange("")) },
                modifier = modifier,
            )
        }

        JobDescriptionTab.Text -> {
            HilitJDTextField(
                value = text,
                onValueChange = {
                    onIntent(
                        OnBoardingInterviewIntent.JobDescriptionTextChange(it),
                    )
                },
                maxLength = JOB_DESCRIPTION_TEXT_MAX_LENGTH,
                modifier = modifier,
            )
        }
    }
}

@Preview(name = "Link - Empty", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepLinkPreview() {
    HilitTheme {
        OnBoardingJobDescriptionStep(
            tab = JobDescriptionTab.Link,
            link = "",
            text = "",
            onIntent = {},
        )
    }
}

@Preview(name = "Link - Filled", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepLinkFilledPreview() {
    HilitTheme {
        OnBoardingJobDescriptionStep(
            tab = JobDescriptionTab.Link,
            link = "https://www.hilit.com/jobs/123",
            text = "",
            onIntent = {},
        )
    }
}

@Preview(name = "Text - Empty", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepTextPreview() {
    HilitTheme {
        OnBoardingJobDescriptionStep(
            tab = JobDescriptionTab.Text,
            link = "",
            text = "",
            onIntent = {},
        )
    }
}

@Preview(name = "Text - Filled", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepTextFilledPreview() {
    HilitTheme {
        OnBoardingJobDescriptionStep(
            tab = JobDescriptionTab.Text,
            link = "",
            text = "당사 서비스의 백엔드 API를 설계하고 운영하실 시니어 개발자를 찾고 있습니다.",
            onIntent = {},
        )
    }
}

/**
 * [HilitAsyncTextField]의 Processing/Error/Complete 상태는 링크 검증 API 연동 이후에나 실제
 * 화면에서 발생한다. 아직 연동 전이라 프로덕션 스텝 시그니처(`link: String`)로는 표현할 수
 * 없어서, 디자인 검수용으로 헤더+탭+필드를 직접 조립해 미리보기만 제공한다.
 */
@Composable
private fun OnBoardingJobDescriptionLinkStatusPreviewContent(
    fieldType: HilitAsyncTextFieldType,
    subText: String,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OnBoardingStepHeader(
            tagText = "선택",
            tagColorType = TagColorType.Gray,
            title =
                buildAnnotatedString {
                    withHilitTextHighlight { append("채용공고 링크") }
                    append("를\n업로드해 주세요.")
                },
            subtitle = "채용 페이지에 직접 올라온 공고 링크를 넣어주세요.",
        )

        HilitTabRow(
            items =
                listOf(
                    HilitTabItem(text = "링크 붙여넣기"),
                    HilitTabItem(text = "직접 입력하기"),
                ),
            selectedIndex = JobDescriptionTab.Link.ordinal,
            onTabSelected = {},
            modifier = Modifier.padding(top = SubtitleToTabSpacing),
        )

        HilitAsyncTextField(
            value = "https://www.hilit.com/jobs/123",
            onValueChange = {},
            type = fieldType,
            placeholder = "https://www.hilit.com/",
            subText = subText,
            onClearClick = {},
            modifier = Modifier.padding(top = TabToFieldSpacing),
        )
    }
}

@Preview(name = "Link - Processing", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepLinkProcessingPreview() {
    HilitTheme {
        OnBoardingJobDescriptionLinkStatusPreviewContent(
            fieldType = HilitAsyncTextFieldType.Processing,
            subText = "분석 중",
        )
    }
}

@Preview(name = "Link - Error", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepLinkErrorPreview() {
    HilitTheme {
        OnBoardingJobDescriptionLinkStatusPreviewContent(
            fieldType = HilitAsyncTextFieldType.Error,
            subText = "링크 형식을 확인해주세요",
        )
    }
}

@Preview(name = "Link - Complete", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepLinkCompletePreview() {
    HilitTheme {
        OnBoardingJobDescriptionLinkStatusPreviewContent(
            fieldType = HilitAsyncTextFieldType.Complete,
            subText = "링크를 확인했어요",
        )
    }
}
