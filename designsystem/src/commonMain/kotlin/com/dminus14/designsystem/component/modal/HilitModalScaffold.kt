package com.dminus14.designsystem.component.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dminus14.designsystem.theme.HilitTheme

internal val HilitModalMaxWidth = 327.dp
internal val HilitModalContentPaddingHorizontal = 24.dp
internal val HilitModalContentPaddingVertical = 40.dp

/**
 * 공용 Modal shell.
 *
 * [Dialog], 최대 너비, 흰 배경과 content·buttons 슬롯만 제공한다.
 * Global alert는 [HilitGlobalModal], Figma modal preset은 [HilitModal]이 이 scaffold 위에 구성된다.
 */
@Composable
fun HilitModalScaffold(
    dismissible: Boolean = true,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
    buttons: @Composable () -> Unit,
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
                    .widthIn(max = HilitModalMaxWidth)
                    .fillMaxWidth()
                    .background(HilitTheme.colors.hilitWhite),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = HilitModalContentPaddingHorizontal,
                            vertical = HilitModalContentPaddingVertical,
                        ),
                content = content,
            )
            buttons()
        }
    }
}
