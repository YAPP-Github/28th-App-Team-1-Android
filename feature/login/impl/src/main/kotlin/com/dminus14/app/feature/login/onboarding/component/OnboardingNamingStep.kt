package com.dminus14.app.feature.login.onboarding.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.component.textfield.HilitBottomOutlinedTextField
import com.dminus14.designsystem.theme.HilitTheme

private val TagToTitleSpacing = 8.dp
private val TitleToSubtitleSpacing = 4.dp

@Composable
fun OnboardingNamingStep(
    name: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        HilitTag(
            colorType = TagColorType.Black,
            tagType = TagType.Small,
            text = "필수",
        )

        Text(
            text = "반가워요!\n이름을 입력해주세요",
            style = HilitTheme.typography.head3,
            color = HilitTheme.colors.hilitBlack800,
            modifier =
                Modifier
                    .padding(top = TagToTitleSpacing)
                    .fillMaxWidth(),
        )

        Text(
            text = "이름은 5글자까지 가능합니다.",
            style = HilitTheme.typography.body6,
            color = HilitTheme.colors.gray500,
            modifier =
                Modifier
                    .padding(top = TitleToSubtitleSpacing)
                    .fillMaxWidth(),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            HilitBottomOutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = "이름을 알려주세요",
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnboardingNamingStepPreview() {
    HilitTheme {
        OnboardingNamingStep(
            name = "",
            onNameChange = {},
        )
    }
}
