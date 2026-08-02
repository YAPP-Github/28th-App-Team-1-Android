package com.dminus14.app.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

private val CardHorizontalPadding = 20.dp
private val CardVerticalPadding = 24.dp
private val DateToTitleSpacing = 8.dp
private val TitleToActionSpacing = 16.dp
private val ActionPadding = 10.dp

@Composable
fun HomeReportCardOpen(
    date: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(HilitTheme.colors.hilitBlack800)
                .padding(
                    horizontal = CardHorizontalPadding,
                    vertical = CardVerticalPadding,
                ),
    ) {
        Text(
            text = date,
            style = HilitTheme.typography.body3,
            color = HilitTheme.colors.hilitGreen500,
        )
        Text(
            text = title,
            style = HilitTheme.typography.sub4,
            color = HilitTheme.colors.hilitWhite,
            modifier =
                Modifier
                    .padding(top = DateToTitleSpacing)
                    .fillMaxWidth(),
        )
        Row(
            modifier =
                Modifier
                    .padding(top = TitleToActionSpacing)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Row(
                modifier =
                    Modifier
                        .background(HilitTheme.colors.hilitGreen500)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        )
                        .padding(ActionPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HilitIcon(
                    asset = HilitIconAsset.Right,
                    contentDescription = null,
                    tint = HilitTheme.colors.hilitBlack800,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun HomeReportCardOpenPreview() {
    HilitTheme {
        HomeReportCardOpen(
            date = "7월 11일 월",
            title = "캐시 도입 결정의 이유와 한계까지 구체적인 수치로 설명해 주셨어요",
            onClick = {},
        )
    }
}
