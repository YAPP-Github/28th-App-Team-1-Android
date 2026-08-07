package com.dminus14.designsystem.component.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dminus14.designsystem.theme.HilitTheme

private const val DEFAULT_TEXT = "오랜만이에요\n재원님!"

/** 가로 그라디언트가 적용된 텍스트. */
@Composable
fun GradientText(
    modifier: Modifier = Modifier,
    text: String = DEFAULT_TEXT,
    startColor: Color = HilitTheme.colors.hilitGreen600,
    endColor: Color = HilitTheme.colors.hilitGreen800,
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        style =
            HilitTheme.typography.sub1.copy(
                brush = Brush.horizontalGradient(colors = listOf(startColor, endColor)),
            ),
    )
}

@Preview(name = "GradientText")
@Composable
private fun GradientTextPreview() {
    HilitTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(HilitTheme.colors.hilitWhite),
        ) {
            GradientText()
        }
    }
}
