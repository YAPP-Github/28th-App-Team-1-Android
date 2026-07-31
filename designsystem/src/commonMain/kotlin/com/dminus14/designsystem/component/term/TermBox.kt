package com.dminus14.designsystem.component.term

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 약관 동의 행 타입.
 */
enum class TermBoxType {
    /** 전체 동의. 체크박스 + `sub7` 문구. */
    AllAgree,

    /** 일반 문구 항목. 체크박스 + `body3` 문구. */
    Text,

    /** 약관 상세 보기 항목. 체크박스 + `body3` 문구 + `보기`. */
    Term,
}

/**
 * 약관 동의 행.
 *
 * @param type 행 타입. 타이포와 `보기` 노출 여부를 결정한다
 * @param text 체크박스 옆에 표시할 문구
 * @param checked 체크 여부
 * @param onClick 체크박스 클릭 콜백. 체크 토글은 호출자가 상태를 갱신한다
 * @param modifier 외부 레이아웃 Modifier
 * @param onViewClick [TermBoxType.Term]의 문구와 `보기` 영역 클릭 콜백. 다른 타입에서는 사용되지 않는다
 */
@Composable
fun TermBox(
    type: TermBoxType,
    text: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onViewClick: () -> Unit = {},
) {
    val textStyle = termBoxTextStyle(type)
    val showViewLabel = type == TermBoxType.Term

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TermBoxContentGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HilitIcon(
            asset =
                if (checked) {
                    HilitIconAsset.CheckboxCheck
                } else {
                    HilitIconAsset.CheckboxUncheck
                },
            contentDescription = null,
            modifier =
                modifier
                    .size(TermBoxCheckboxSize)
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        role = Role.Checkbox,
                        onClick = onClick,
                    ),
        )

        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .then(
                        if (showViewLabel) {
                            Modifier.clickable(
                                interactionSource = null,
                                indication = null,
                                role = Role.Button,
                                onClick = onViewClick,
                            )
                        } else {
                            Modifier
                        },
                    ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = textStyle,
                color = HilitTheme.colors.gray900,
                modifier = Modifier.weight(1f),
            )

            if (showViewLabel) {
                TermBoxViewLabel()
            }
        }
    }
}

@Composable
private fun termBoxTextStyle(type: TermBoxType): TextStyle =
    when (type) {
        TermBoxType.AllAgree -> HilitTheme.typography.sub7
        TermBoxType.Text,
        TermBoxType.Term,
        -> HilitTheme.typography.body3
    }

@Composable
private fun TermBoxViewLabel(modifier: Modifier = Modifier) {
    Text(
        text = "보기",
        style = HilitTheme.typography.body5,
        color = HilitTheme.colors.gray400,
        modifier =
            modifier.padding(
                horizontal = TermBoxViewButtonHorizontalPadding,
                vertical = TermBoxViewButtonVerticalPadding,
            ),
    )
}

private val TermBoxContentGap = 8.dp
private val TermBoxCheckboxSize = 24.dp
private val TermBoxViewButtonHorizontalPadding = 8.dp
private val TermBoxViewButtonVerticalPadding = 4.dp

@Preview(name = "TermBox")
@Composable
private fun TermBoxPreview() {
    HilitTheme {
        Column(
            modifier =
                Modifier
                    .background(Color.White)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TermBox(
                type = TermBoxType.AllAgree,
                text = "모두 동의합니다.",
                checked = false,
                onClick = {},
            )
            TermBox(
                type = TermBoxType.AllAgree,
                text = "모두 동의합니다.",
                checked = true,
                onClick = {},
            )
            TermBox(
                type = TermBoxType.Text,
                text = "(필수) 만 14세 이상입니다.",
                checked = false,
                onClick = {},
            )
            TermBox(
                type = TermBoxType.Text,
                text = "(필수) 만 14세 이상입니다.",
                checked = true,
                onClick = {},
            )
            TermBox(
                type = TermBoxType.Term,
                text = "(필수) 서비스 이용약관 동의",
                checked = false,
                onClick = {},
            )
            TermBox(
                type = TermBoxType.Term,
                text = "(필수) 서비스 이용약관 동의",
                checked = true,
                onClick = {},
            )
        }
    }
}
