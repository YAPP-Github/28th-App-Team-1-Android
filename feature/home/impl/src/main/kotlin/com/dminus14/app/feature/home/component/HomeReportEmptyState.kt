package com.dminus14.app.feature.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.home_report_empty
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.painterResource

private val IconSize = 100.dp
private val IconToTextSpacing = 12.dp

@Composable
fun HomeReportEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(IconToTextSpacing),
    ) {
        Icon(
            painter = painterResource(Res.drawable.home_report_empty),
            contentDescription = null,
            tint = HilitTheme.colors.gray400,
            modifier = Modifier.size(IconSize),
        )
        Text(
            text = "면접을 보고\n레포트를 받아보세요",
            style = HilitTheme.typography.body2,
            color = HilitTheme.colors.gray400,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable
private fun HomeReportEmptyStatePreview() {
    HilitTheme {
        HomeReportEmptyState()
    }
}
