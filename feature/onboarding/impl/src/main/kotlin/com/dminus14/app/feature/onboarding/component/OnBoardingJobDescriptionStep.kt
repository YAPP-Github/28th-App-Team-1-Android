package com.dminus14.app.feature.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.onboarding.JdLinkStatus
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
private const val JOB_DESCRIPTION_TEXT_MIN_LENGTH = 200
private const val JOB_DESCRIPTION_TEXT_VALIDATION_ERROR = "공고 내용은 200자 이상으로 입력해 주세요"
private const val JOB_DESCRIPTION_TEXT_VALIDATION_SUCCESS = "공고 내용을 확인했어요"

@Composable
@Suppress("LongParameterList")
fun OnBoardingJobDescriptionStep(
    tab: JobDescriptionTab,
    link: String,
    linkStatus: JdLinkStatus,
    linkSubText: String,
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
            linkStatus = linkStatus,
            linkSubText = linkSubText,
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
@Suppress("LongParameterList")
private fun OnBoardingJobDescriptionField(
    tab: JobDescriptionTab,
    link: String,
    linkStatus: JdLinkStatus,
    linkSubText: String,
    text: String,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (tab) {
        JobDescriptionTab.Link -> {
            HilitAsyncTextField(
                value = link,
                onValueChange = {
                    onIntent(
                        OnBoardingInterviewIntent.JobDescriptionLinkChange(it),
                    )
                },
                type = link.toLinkFieldType(linkStatus),
                placeholder = "https://www.hilit.com/",
                processingText = "분석 중",
                subText = linkSubText,
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
                minLength = JOB_DESCRIPTION_TEXT_MIN_LENGTH,
                validationErrorText = JOB_DESCRIPTION_TEXT_VALIDATION_ERROR,
                validationSuccessText = JOB_DESCRIPTION_TEXT_VALIDATION_SUCCESS,
                modifier = modifier,
            )
        }
    }
}

private fun String.toLinkFieldType(status: JdLinkStatus): HilitAsyncTextFieldType =
    when {
        isEmpty() -> HilitAsyncTextFieldType.Ready
        status == JdLinkStatus.Validating -> HilitAsyncTextFieldType.Processing
        status == JdLinkStatus.Valid -> HilitAsyncTextFieldType.Complete
        status == JdLinkStatus.Invalid -> HilitAsyncTextFieldType.Error
        else -> HilitAsyncTextFieldType.Edit
    }

@Preview(name = "Link - Empty", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepLinkPreview() {
    HilitTheme {
        OnBoardingJobDescriptionStep(
            tab = JobDescriptionTab.Link,
            link = "",
            linkStatus = JdLinkStatus.Idle,
            linkSubText = "",
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
            linkStatus = JdLinkStatus.Idle,
            linkSubText = "",
            text = "",
            onIntent = {},
        )
    }
}

@Preview(name = "Link - Processing", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepLinkProcessingPreview() {
    HilitTheme {
        OnBoardingJobDescriptionStep(
            tab = JobDescriptionTab.Link,
            link = "https://www.hilit.com/jobs/123",
            linkStatus = JdLinkStatus.Validating,
            linkSubText = "",
            text = "",
            onIntent = {},
        )
    }
}

@Preview(name = "Link - Complete", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepLinkCompletePreview() {
    HilitTheme {
        OnBoardingJobDescriptionStep(
            tab = JobDescriptionTab.Link,
            link = "https://www.hilit.com/jobs/123",
            linkStatus = JdLinkStatus.Valid,
            linkSubText = "링크를 확인했어요",
            text = "",
            onIntent = {},
        )
    }
}

@Preview(name = "Link - Error", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepLinkErrorPreview() {
    HilitTheme {
        OnBoardingJobDescriptionStep(
            tab = JobDescriptionTab.Link,
            link = "http://www.hilit.com/jobs/123",
            linkStatus = JdLinkStatus.Invalid,
            linkSubText = "https:// 형식의 링크를 입력해주세요",
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
            linkStatus = JdLinkStatus.Idle,
            linkSubText = "",
            text = "",
            onIntent = {},
        )
    }
}

@Preview(name = "Text - Filled (under min)", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepTextFilledPreview() {
    HilitTheme {
        OnBoardingJobDescriptionStep(
            tab = JobDescriptionTab.Text,
            link = "",
            linkStatus = JdLinkStatus.Idle,
            linkSubText = "",
            text = "당사 서비스의 백엔드 API를 설계하고 운영하실 시니어 개발자를 찾고 있습니다.",
            onIntent = {},
        )
    }
}

@Preview(name = "Text - Valid", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnBoardingJobDescriptionStepTextValidPreview() {
    HilitTheme {
        OnBoardingJobDescriptionStep(
            tab = JobDescriptionTab.Text,
            link = "",
            linkStatus = JdLinkStatus.Idle,
            linkSubText = "",
            text =
                "당사 서비스의 백엔드 API를 설계하고 운영하실 시니어 개발자를 찾고 있습니다. " +
                    "Kotlin, Spring Boot 기반 마이크로서비스 경험이 필요하며, " +
                    "대용량 트래픽 처리와 데이터 파이프라인 구축 경험을 우대합니다. " +
                    "협업과 코드 리뷰 문화를 중시하는 팀에서 함께 성장할 분을 기다립니다.",
            onIntent = {},
        )
    }
}
