package com.dminus14.app.feature.interview.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.component.modal.HilitModal
import com.dminus14.designsystem.component.modal.HilitModalPreviewHost
import com.dminus14.designsystem.component.modal.HilitModalType

@Composable
fun InterviewAbortModal(
    onExitClick: () -> Unit,
    onContinueClick: () -> Unit,
) {
    HilitModal(
        type = HilitModalType.Default,
        title = "다음에 면접을 다시 진행할까요?",
        subtitle = "지금 나가면 방금 쓴 이용권 한장이 사라져요.",
        dismissible = false,
        buttons = {
            HilitFixedBottomDualButton(
                leftText = "그대로 나가기",
                rightText = "면접 계속하기",
                type = HilitFixedBottomDualButtonType.TwoColor,
                onLeftClick = onExitClick,
                onRightClick = onContinueClick,
            )
        },
    )
}

@Preview
@Composable
private fun InterviewAbortModalPreview() {
    HilitModalPreviewHost {
        InterviewAbortModal(
            onExitClick = {},
            onContinueClick = {},
        )
    }
}
