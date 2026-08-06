package com.dminus14.app.feature.interview.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun InterviewStartButton(
    isReady: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HilitFixedBottomButton(
        type = HilitButtonType.Light,
        text = "면접 시작하기",
        enabled = isReady,
        onClick = onClick,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun InterviewStartButtonEnabledPreview() {
    HilitTheme {
        InterviewStartButton(
            isReady = true,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun InterviewStartButtonDisabledPreview() {
    HilitTheme {
        InterviewStartButton(
            isReady = false,
            onClick = {},
        )
    }
}
