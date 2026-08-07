package com.dminus14.app.feature.interview.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.modal.HilitModal
import com.dminus14.designsystem.component.modal.HilitModalPreviewHost
import com.dminus14.designsystem.component.modal.HilitModalType

@Composable
fun InterviewFinishModal(
    onContinueClick: () -> Unit,
    onFinishClick: () -> Unit,
) {
    HilitModal(
        type = HilitModalType.InvisibleInfo,
        title = "면접을 마칠까요?",
        subtitle = "마치기를 클릭하는 즉시 면접이 종료됩니다.\n지금까지 답변으로 분석을 시작해요.",
        graphic = {
            HilitIcon(
                asset = HilitIconAsset.Finish,
                contentDescription = "Finish",
            )
        },
        dismissible = false,
        buttons = {
            HilitFixedBottomDualButton(
                leftText = "면접 계속하기",
                rightText = "바로 마치기",
                type = HilitFixedBottomDualButtonType.TwoColor,
                onLeftClick = onContinueClick,
                onRightClick = onFinishClick,
            )
        },
    )
}

@Preview
@Composable
private fun InterviewAbortModalPreview() {
    HilitModalPreviewHost {
        InterviewFinishModal(
            onContinueClick = {},
            onFinishClick = {},
        )
    }
}
