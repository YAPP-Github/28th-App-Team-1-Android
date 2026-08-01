package com.dminus14.designsystem.component.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun HilitModal(
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
    Dialog(
        onDismissRequest = {
            if (dismissible) {
                onDismiss()
            }
        },
        properties =
            DialogProperties(
                dismissOnBackPress = dismissible,
                dismissOnClickOutside = dismissible,
            ),
    ) {
        Column(
            modifier =
                modifier
                    .widthIn(max = 327.dp)
                    .fillMaxWidth()
                    .background(HilitTheme.colors.hilitWhite),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
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

            if (cancelText == null) {
                HilitFixedBottomButton(
                    text = confirmText,
                    type = HilitButtonType.Light,
                    onClick = onConfirm,
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth()) {
                    HilitFixedBottomButton(
                        text = cancelText,
                        type = HilitButtonType.Light,
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.CenterVertically)
                                .width(1.dp)
                                .height(25.dp)
                                .background(HilitTheme.colors.gray800),
                    )
                    HilitFixedBottomButton(
                        text = confirmText,
                        type = HilitButtonType.Light,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
