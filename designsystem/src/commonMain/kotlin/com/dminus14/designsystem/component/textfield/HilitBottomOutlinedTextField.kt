package com.dminus14.designsystem.component.textfield

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme
import kotlin.math.ceil

internal const val HILIT_BOTTOM_OUTLINED_MINIMUM_WIDTH_TEXT = "가"

private val BottomPadding = 8.dp
private val BottomOutlineThickness = 4.dp

@Composable
fun HilitBottomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    val textStyle = HilitTheme.typography.head4
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    var isFocused by remember { mutableStateOf(false) }
    val measurementText = bottomOutlinedTextFieldMeasurementText(value, placeholder)
    val measuredText =
        textMeasurer.measure(
            text = measurementText,
            style = textStyle,
            softWrap = false,
            maxLines = 1,
        )
    val measuredWidth =
        with(density) {
            ceilTextWidthInPixels(measuredText.multiParagraph.width).toDp()
        }
    val outlineColor =
        if (isBottomOutlinedTextFieldActive(isFocused, value)) {
            colors.hilitGreen600
        } else {
            colors.gray100
        }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier =
            modifier
                .width(measuredWidth)
                .onFocusChanged { isFocused = it.isFocused }
                .drawBehind {
                    val outlineHeight = BottomOutlineThickness.toPx()
                    drawRect(
                        color = outlineColor,
                        topLeft = Offset(x = 0f, y = size.height - outlineHeight),
                        size = Size(width = size.width, height = outlineHeight),
                    )
                }.padding(bottom = BottomPadding),
        textStyle = textStyle.copy(color = colors.hilitBlack800),
        singleLine = true,
        cursorBrush = SolidColor(colors.hilitBlack800),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        color = colors.gray500,
                        style = textStyle,
                        maxLines = 1,
                    )
                }
                innerTextField()
            }
        },
    )
}

internal fun bottomOutlinedTextFieldMeasurementText(
    value: String,
    placeholder: String,
): String =
    when {
        value.isNotEmpty() -> value
        placeholder.isNotEmpty() -> placeholder
        else -> HILIT_BOTTOM_OUTLINED_MINIMUM_WIDTH_TEXT
    }

internal fun isBottomOutlinedTextFieldActive(
    isFocused: Boolean,
    value: String,
): Boolean = isFocused || value.isNotEmpty()

internal fun ceilTextWidthInPixels(width: Float): Int = ceil(width).toInt()

@Preview(name = "HilitBottomOutlinedTextField")
@Composable
private fun HilitBottomOutlinedTextFieldPreview() {
    HilitTheme {
        var value by remember { mutableStateOf("") }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            HilitBottomOutlinedTextField(
                value = value,
                onValueChange = { value = it },
                placeholder = "이름을 알려주세요",
            )
            HilitBottomOutlinedTextField(
                value = "박민",
                onValueChange = {},
                placeholder = "이름을 알려주세요",
            )
        }
    }
}
