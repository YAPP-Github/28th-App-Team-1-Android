package stories.foundations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

private const val ICONS_PER_ROW = 4

internal val IconStories =
    StoryGroup(
        path = "Foundations/Icon",
        description = "공용 벡터 아이콘 21개의 기본 크기와 색상을 확인합니다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "전체 아이콘",
                    description = "일반 아이콘은 Hilit Black 800, 소셜 로고는 원본 색상으로 표시합니다.",
                    content = { IconGallery() },
                ),
            ),
    )

@Composable
private fun IconGallery() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HilitIconAsset.entries.chunked(ICONS_PER_ROW).forEach { rowIcons ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowIcons.forEach { asset ->
                    IconItem(
                        asset = asset,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(ICONS_PER_ROW - rowIcons.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun IconItem(
    asset: HilitIconAsset,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(56.dp)
                    .background(HilitTheme.colors.gray50, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            HilitIcon(
                asset = asset,
                contentDescription = asset.resourceName,
                modifier = Modifier.size(asset.defaultSize),
            )
        }
        Text(
            text = asset.resourceName,
            style = HilitTheme.typography.body8,
            color = HilitTheme.colors.hilitBlack900,
        )
    }
}
