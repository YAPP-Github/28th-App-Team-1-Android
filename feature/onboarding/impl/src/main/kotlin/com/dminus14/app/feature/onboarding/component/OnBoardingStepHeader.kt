package com.dminus14.app.feature.onboarding.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.component.text.HilitText
import com.dminus14.designsystem.theme.HilitTheme

private val HeaderTopSpacing = 20.dp
private val TagToTitleSpacing = 8.dp
private val TitleToSubtitleSpacing = 4.dp

/** 온보딩 인터뷰 각 스텝 상단에 쓰는 태그·타이틀(하이라이트)·서브타이틀 묶음. */
@Composable
fun OnBoardingStepHeader(
    tagText: String,
    tagColorType: TagColorType,
    title: AnnotatedString,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(top = HeaderTopSpacing)) {
        HilitTag(
            colorType = tagColorType,
            tagType = TagType.Small,
            text = tagText,
        )

        HilitText(
            text = title,
            style = HilitTheme.typography.head3,
            color = HilitTheme.colors.hilitBlack800,
            modifier =
                Modifier
                    .padding(top = TagToTitleSpacing)
                    .fillMaxWidth(),
        )

        Text(
            text = subtitle,
            style = HilitTheme.typography.body4,
            color = HilitTheme.colors.gray500,
            modifier =
                Modifier
                    .padding(top = TitleToSubtitleSpacing)
                    .fillMaxWidth(),
        )
    }
}
