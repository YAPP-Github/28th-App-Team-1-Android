package com.dminus14.app.feature.login.onboarding.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.chip.HilitChip
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.component.text.HilitText
import com.dminus14.designsystem.component.text.HilitTextHighlightColor
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.theme.HilitTheme

private val TagToTitleSpacing = 8.dp
private val TitleToSubtitleSpacing = 4.dp
private val HeaderToChipsSpacing = 34.dp
private val ChipSpacing = 8.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingJobStep(
    userName: String,
    jobs: List<String>,
    selectedJobIndex: Int?,
    onJobClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayName = userName.ifBlank { "회원" }
    val title =
        buildAnnotatedString {
            append("${displayName}님의 ")
            withHilitTextHighlight { append("직군") }
            append("을 선택해 주세요.")
        }

    Column(modifier = modifier.fillMaxWidth()) {
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
            text = "${displayName}님의 현재 직군을 선택해 주세요.",
            style = HilitTheme.typography.body4,
            color = HilitTheme.colors.gray500,
            modifier =
                Modifier
                    .padding(top = TitleToSubtitleSpacing)
                    .fillMaxWidth(),
        )

        FlowRow(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = HeaderToChipsSpacing),
            horizontalArrangement = Arrangement.spacedBy(ChipSpacing),
            verticalArrangement = Arrangement.spacedBy(ChipSpacing),
        ) {
            jobs.forEachIndexed { index, job ->
                HilitChip(
                    text = job,
                    selected = index == selectedJobIndex,
                    onClick = { onJobClick(index) },
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnboardingJobStepPreview() {
    HilitTheme {
        OnboardingJobStep(
            userName = "재원",
            jobs =
                listOf(
                    "백엔드",
                    "데이터 엔지니어",
                    "Android",
                    "iOS",
                    "프론트엔드",
                    "인프라 · SRE",
                ),
            selectedJobIndex = 3,
            onJobClick = {},
        )
    }
}
