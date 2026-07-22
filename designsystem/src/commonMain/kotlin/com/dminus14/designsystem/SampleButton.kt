package com.dminus14.designsystem

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun SampleButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier.padding(4.dp),
    ) {
        Text(text)
    }
}

@Preview
@Composable
private fun SampleButtonPreview() {
    HilitTheme {
        SampleButton(
            text = "D-14",
            enabled = true,
            onClick = {},
        )
    }
}
