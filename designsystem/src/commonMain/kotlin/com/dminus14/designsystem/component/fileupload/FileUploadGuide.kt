package com.dminus14.designsystem.component.fileupload

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.upload
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.painterResource

private val FileUploadGuideShape = RectangleShape
private val FileUploadGuideHeight = 150.dp
private val FileUploadGuideHorizontalPadding = 16.dp
private val FileUploadGuideVerticalPadding = 14.dp
private val FileUploadGuideIconSize = 44.dp
private val FileUploadGuideIconSpacing = 11.dp
private val FileUploadGuideTextSpacing = 4.dp
private val FileUploadGuideBorderWidth = 1.dp
private const val DEFAULT_TITLE = "파일을 업로드해주세요"
private const val DEFAULT_DESCRIPTION = "1개 파일, 최대 20Mb까지 가능합니다"

/**
 * 파일 업로드 유도 영역. 업로드 전(before) 상태를 표시한다.
 *
 * Figma: FileUpload status=before (`2280:10097`)
 *
 * @param modifier 외부 레이아웃 Modifier
 * @param title 상단 안내 문구
 * @param description 하단 보조 문구(용량·개수 제한 등)
 * @param onClick 영역 클릭 콜백. null이면 클릭 불가
 */
@Composable
fun FileUploadGuide(
    modifier: Modifier = Modifier,
    title: String = DEFAULT_TITLE,
    description: String = DEFAULT_DESCRIPTION,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .height(FileUploadGuideHeight)
                .background(
                    color = HilitTheme.colors.gray50,
                    shape = FileUploadGuideShape,
                )
                .border(
                    width = FileUploadGuideBorderWidth,
                    color = HilitTheme.colors.gray100,
                    shape = FileUploadGuideShape,
                )
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    },
                )
                .padding(
                    horizontal = FileUploadGuideHorizontalPadding,
                    vertical = FileUploadGuideVerticalPadding,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        HilitIcon(
            asset = HilitIconAsset.Upload,
            contentDescription = ""
        )

        Text(
            text = title,
            style = HilitTheme.typography.body2,
            color = HilitTheme.colors.gray900,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = FileUploadGuideIconSpacing),
        )

        Text(
            text = description,
            style = HilitTheme.typography.body9,
            color = HilitTheme.colors.gray600,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = FileUploadGuideTextSpacing),
        )
    }
}

@Preview(
    name = "FileUploadGuide",
    showBackground = true,
    widthDp = 360,
)
@Composable
private fun FileUploadGuidePreview() {
    HilitTheme {
        FileUploadGuide(
            modifier = Modifier.padding(16.dp),
            onClick = {},
        )
    }
}
