package com.dminus14.app.feature.interview.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dminus14.designsystem.component.bubblefield.BubbleField
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailAlign
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailEdge
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailShape
import com.dminus14.designsystem.component.bubblefield.BubbleFieldType
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun InterviewCompletableBubble(modifier: Modifier = Modifier) {
    BubbleField(
        text = "8분이 경과되어 면접을 종료할 수 있어요",
        type = BubbleFieldType.Big,
        tailEdge = BubbleFieldTailEdge.Bottom,
        tailAlign = BubbleFieldTailAlign.Right,
        tailShape = BubbleFieldTailShape.Right,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun InterviewCompletableBubblePreview() {
    HilitTheme {
        InterviewCompletableBubble()
    }
}
