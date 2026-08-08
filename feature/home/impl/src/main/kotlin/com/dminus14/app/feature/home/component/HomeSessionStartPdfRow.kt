package com.dminus14.app.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

private val RowShape = RoundedCornerShape(8.dp)
private val RowBorderWidth = 1.dp
private val RowHorizontalPadding = 12.dp
private val RowVerticalPadding = 12.dp
private val BadgeSize = 36.dp
private val BadgeIconSize = 20.dp
private val BadgeToTextGap = 12.dp

/**
 * 등록된 포트폴리오 파일을 표시하는 행. 파일 배지 + 파일명 + 서브텍스트(업로드 일자·크기).
 *
 * designsystem `PdfUpload`의 `PdfFileBadge`가 private이라 유사 룩을 홈 feature 로컬로 재현한다.
 */
@Composable
internal fun HomeSessionStartPdfRow(
    fileName: String,
    subText: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .border(width = RowBorderWidth, color = HilitTheme.colors.gray100, shape = RowShape)
                .padding(horizontal = RowHorizontalPadding, vertical = RowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BadgeToTextGap),
    ) {
        Box(
            modifier =
                Modifier
                    .size(BadgeSize)
                    .background(color = HilitTheme.colors.hilitBlack800, shape = RowShape),
            contentAlignment = Alignment.Center,
        ) {
            HilitIcon(
                asset = HilitIconAsset.File,
                contentDescription = null,
                tint = HilitTheme.colors.hilitGreen500,
                modifier = Modifier.size(BadgeIconSize),
            )
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = fileName,
                style = HilitTheme.typography.body2,
                color = HilitTheme.colors.hilitBlack800,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subText,
                style = HilitTheme.typography.body9,
                color = HilitTheme.colors.gray500,
            )
        }
    }
}

@Preview(name = "HomeSessionStartPdfRow", showBackground = true, widthDp = 335, heightDp = 80)
@Composable
private fun HomeSessionStartPdfRowPreview() {
    HilitTheme {
        HomeSessionStartPdfRow(
            fileName = "{파일명}.pdf",
            subText = "20xx.xx.xx | {0}mb",
        )
    }
}
