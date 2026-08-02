package com.dminus14.app.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

private val CardPadding = 20.dp
private val ReportCardCloseBackground = Color(0xFFD2EFCC)

@Composable
fun HomeReportCardClose(
    date: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = date,
        style = HilitTheme.typography.body3,
        color = HilitTheme.colors.hilitBlack800,
        modifier =
            modifier
                .fillMaxWidth()
                .background(ReportCardCloseBackground)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(CardPadding),
    )
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun HomeReportCardClosePreview() {
    HilitTheme {
        HomeReportCardClose(
            date = "7월 10일 월",
            onClick = {},
        )
    }
}
