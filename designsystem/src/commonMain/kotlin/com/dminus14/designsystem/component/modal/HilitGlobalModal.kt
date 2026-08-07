package com.dminus14.designsystem.component.modal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 앱 전역 Modal(Global Modal)용 alert preset.
 *
 * title + message + single/dual([HilitFixedBottomDualButtonType.TwoColor]) 버튼을 제공한다.
 *
 * Figma 노드 번호: 439-10403, 439-10404, 439-10405, 439-10406.
 */
@Composable
fun HilitGlobalModal(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String? = null,
    dismissible: Boolean = true,
    onConfirm: () -> Unit,
    onCancel: () -> Unit = {},
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    HilitModalScaffold(
        dismissible = dismissible,
        onDismiss = onDismiss,
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    color = HilitTheme.colors.hilitBlack800,
                    style = HilitTheme.typography.head4,
                    textAlign = TextAlign.Center,
                )
                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        modifier = Modifier.fillMaxWidth(),
                        color = HilitTheme.colors.gray600,
                        style = HilitTheme.typography.body4,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
        buttons = {
            if (cancelText == null) {
                HilitFixedBottomButton(
                    text = confirmText,
                    type = HilitButtonType.Light,
                    onClick = onConfirm,
                )
            } else {
                HilitFixedBottomDualButton(
                    modifier = Modifier.fillMaxWidth(),
                    leftText = cancelText,
                    rightText = confirmText,
                    type = HilitFixedBottomDualButtonType.TwoColor,
                    onLeftClick = onCancel,
                    onRightClick = onConfirm,
                )
            }
        },
    )
}
