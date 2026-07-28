package com.dminus14.designsystem.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

/** figma spec확인에 이편이 편해보여서 이렇게 했는데, 보다가 어떤지 코멘트 주세용 */
private val HilitJDTextFieldShape = RectangleShape
private val HilitJDTextFieldBorderWidth = 1.dp
private val HilitJDTextFieldHorizontalPadding = 16.dp
private val HilitJDTextFieldVerticalPadding = 14.dp
private val HilitJDTextFieldHeight = 158.dp
private val HilitJDTextFieldCounterSpacing = 8.dp
private const val DEFAULT_MAX_LENGTH = 300
private const val DEFAULT_PLACEHOLDER = "텍스트를 입력해주세요"

/**
 * JD 입력용 멀티라인 텍스트 필드. 하단에 글자 수 카운터를 표시한다.
 *
 * Figma: text-field status=large (`2091:806`)
 *
 * @param value 현재 입력 값
 * @param onValueChange 입력이 바뀔 때 호출된다. [maxLength]를 넘는 값은 반영되지 않는다
 * @param modifier 외부 레이아웃 Modifier
 * @param placeholder 값이 비어 있을 때 표시할 placeholder
 * @param maxLength 최대 입력 글자 수. 카운터에도 사용된다
 */
@Composable
fun HilitJDTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = DEFAULT_PLACEHOLDER,
    maxLength: Int = DEFAULT_MAX_LENGTH,
) {
    val scrollState = rememberScrollState()
    val textStyle = HilitTheme.typography.body4
    val counterStyle = HilitTheme.typography.body9
    val textColor = HilitTheme.colors.hilitBlack800
    val placeholderColor = HilitTheme.colors.gray500
    val borderColor = HilitTheme.colors.gray100
    val surfaceColor = HilitTheme.colors.hilitWhite

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(HilitJDTextFieldHeight)
                    .background(
                        color = surfaceColor,
                        shape = HilitJDTextFieldShape,
                    ).border(
                        width = HilitJDTextFieldBorderWidth,
                        color = borderColor,
                        shape = HilitJDTextFieldShape,
                    ).clip(HilitJDTextFieldShape)
                    .padding(
                        horizontal = HilitJDTextFieldHorizontalPadding,
                        vertical = HilitJDTextFieldVerticalPadding,
                    ),
        ) {
            BasicTextField(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.length <= maxLength) {
                        onValueChange(newValue)
                    }
                },
                textStyle = textStyle.copy(color = textColor),
                cursorBrush = SolidColor(textColor),
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (value.isEmpty()) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = placeholder,
                                style = textStyle,
                                color = placeholderColor,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }

        Text(
            text = "${value.length}/$maxLength",
            style = counterStyle,
            color = placeholderColor,
            modifier =
                Modifier
                    .align(Alignment.End)
                    .padding(top = HilitJDTextFieldCounterSpacing),
        )
    }
}

@Preview(
    name = "HilitJDTextField",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun HilitJDTextFieldPreview() {
    HilitTheme {
        HilitJDTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
