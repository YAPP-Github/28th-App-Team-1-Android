package com.dminus14.app.feature.feedback.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

@Composable
@Suppress("LongMethod")
fun GuestFeedbackCommentModal(
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        Column(
            modifier =
                modifier
                    .fillMaxWidth()
                    .background(HilitTheme.colors.hilitWhite),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "왜 그렇게 느꼈나요?",
                    color = HilitTheme.colors.hilitBlack800,
                    style = HilitTheme.typography.head5,
                )
                HilitIcon(
                    asset = HilitIconAsset.Cancel,
                    contentDescription = "코멘트 입력 닫기",
                    modifier =
                        Modifier.clickable(
                            role = Role.Button,
                            onClick = onDismiss,
                        ),
                )
            }

            BasicTextField(
                value = value,
                onValueChange = { changed -> onValueChange(changed.take(MAX_COMMENT_LENGTH)) },
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .heightIn(min = 96.dp, max = 120.dp)
                        .border(1.dp, HilitTheme.colors.gray200)
                        .padding(12.dp)
                        .semantics {
                            contentDescription = "선택 코멘트"
                        },
                textStyle =
                    HilitTheme.typography.body4.copy(
                        color = HilitTheme.colors.hilitBlack800,
                    ),
                cursorBrush = SolidColor(HilitTheme.colors.hilitBlack800),
                minLines = 3,
                maxLines = 3,
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = "선택 사항이에요",
                                color = HilitTheme.colors.gray400,
                                style = HilitTheme.typography.body4,
                            )
                        }
                        innerTextField()
                    }
                },
            )

            Text(
                text = "${value.length}/$MAX_COMMENT_LENGTH",
                modifier =
                    Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                color = HilitTheme.colors.gray500,
                style = HilitTheme.typography.body7,
            )

            HilitFixedBottomButton(
                text = "다음",
                onClick = onConfirm,
            )
        }
    }
}

private const val MAX_COMMENT_LENGTH = 100
