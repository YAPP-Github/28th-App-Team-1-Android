package com.dminus14.app.feature.interview.component

import androidx.compose.runtime.Composable
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.component.modal.HilitModal
import com.dminus14.designsystem.component.modal.HilitModalType

@Composable
fun InterviewMeteredUploadModal(
    onUseMobileData: () -> Unit,
    onWaitForWifi: () -> Unit,
) {
    HilitModal(
        type = HilitModalType.Default,
        title = "모바일 데이터로 업로드할까요?",
        subtitle = "영상 업로드에 데이터가 사용될 수 있어요.",
        dismissible = false,
        buttons = {
            HilitFixedBottomDualButton(
                leftText = "Wi-Fi 기다리기",
                rightText = "계속하기",
                type = HilitFixedBottomDualButtonType.TwoColor,
                onLeftClick = onWaitForWifi,
                onRightClick = onUseMobileData,
            )
        },
    )
}
