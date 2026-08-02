package com.dminus14.app.feature.login.onboarding.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.login.onboarding.DefaultExperienceOptions
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.component.text.HilitText
import com.dminus14.designsystem.component.text.HilitTextHighlightColor
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.component.wheelpicker.HilitWheelPicker
import com.dminus14.designsystem.theme.HilitTheme

private val TagToTitleSpacing = 12.dp
private val TitleToSubtitleSpacing = 8.dp
private val WheelWidth = 104.dp
private val SentenceSpacing = 12.dp

@Composable
fun OnboardingExperienceStep(
    options: List<String>,
    selectedOption: Int,
    onOptionChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        ExperienceHeader()

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            ExperienceWheelSentence(
                options = options,
                selectedOption = selectedOption,
                onOptionChange = onOptionChange,
            )
        }
    }
}

@Composable
private fun ExperienceHeader() {
    val title =
        buildAnnotatedString {
            withHilitTextHighlight { append("연차") }
            append("를 입력해 주세요")
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        HilitTag(
            colorType = TagColorType.Black,
            tagType = TagType.Small,
            text = "필수",
        )

        HilitText(
            text = title,
            color = HilitTheme.colors.hilitBlack800,
            highlightColor = HilitTextHighlightColor.Green,
            style = HilitTheme.typography.head3,
            modifier =
                Modifier
                    .padding(top = TagToTitleSpacing)
                    .fillMaxWidth(),
        )

        Text(
            text = "지금까지 근무한 모든 기간의 합\n(정규직·계약직·프리랜서 포함, 인턴)입니다.",
            style = HilitTheme.typography.body4,
            color = HilitTheme.colors.gray400,
            modifier =
                Modifier
                    .padding(top = TitleToSubtitleSpacing)
                    .fillMaxWidth(),
        )
    }
}

@Composable
private fun ExperienceWheelSentence(
    options: List<String>,
    selectedOption: Int,
    onOptionChange: (Int) -> Unit,
) {
    val safeIndex = selectedOption.coerceIn(0, options.lastIndex.coerceAtLeast(0))
    val selectedItem = options.getOrElse(safeIndex) { "" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "내 경력은",
            style = HilitTheme.typography.sub4,
            color = HilitTheme.colors.hilitBlack800,
            modifier = Modifier.padding(end = SentenceSpacing),
        )
        HilitWheelPicker(
            items = options,
            selectedItem = selectedItem,
            onSelectedItemChange = { item ->
                val index = options.indexOf(item)
                if (index >= 0) {
                    onOptionChange(index)
                }
            },
            modifier = Modifier.width(WheelWidth),
        )
        Text(
            text = "이다",
            style = HilitTheme.typography.sub4,
            color = HilitTheme.colors.hilitBlack800,
            modifier = Modifier.padding(start = SentenceSpacing),
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnboardingExperienceStepPreview() {
    HilitTheme {
        OnboardingExperienceStep(
            options = DefaultExperienceOptions,
            selectedOption = PREVIEW_SELECTED_OPTION_INDEX,
            onOptionChange = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

private const val PREVIEW_SELECTED_OPTION_INDEX = 2
