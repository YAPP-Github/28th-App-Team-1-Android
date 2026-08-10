package com.dminus14.app.feature.interview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.interview.interview.InterviewSpeaker
import com.dminus14.designsystem.component.text.HilitText
import com.dminus14.designsystem.component.text.HilitTextHighlightColor
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun InterviewSpeakerIndicator(
    speaker: InterviewSpeaker,
    modifier: Modifier = Modifier,
) {
    val highlightColor =
        if (speaker == InterviewSpeaker.AI) {
            HilitTextHighlightColor.Green
        } else {
            HilitTextHighlightColor.Black
        }
    val titleText = if (speaker == InterviewSpeaker.AI) "질문 듣는 중" else "답변 녹음 중"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier =
            Modifier
                .then(modifier)
                .heightIn(min = 60.dp),
    ) {
        HilitText(
            text =
                buildAnnotatedString {
                    withHilitTextHighlight {
                        append(titleText)
                    }
                },
            style = HilitTheme.typography.body2,
            highlightColor = highlightColor,
        )

        if (speaker == InterviewSpeaker.AI) {
            Text(
                text = "끝까지 듣고 대답해주세요",
                style = HilitTheme.typography.sub7,
                color = HilitTheme.colors.hilitWhite,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

@Preview
@Composable
private fun InterviewSpeakerIndicatorAIPreview() {
    HilitTheme {
        InterviewSpeakerIndicator(
            speaker = InterviewSpeaker.AI,
        )
    }
}

@Preview
@Composable
private fun InterviewSpeakerIndicatorUserPreview() {
    HilitTheme {
        InterviewSpeakerIndicator(
            speaker = InterviewSpeaker.User,
        )
    }
}
