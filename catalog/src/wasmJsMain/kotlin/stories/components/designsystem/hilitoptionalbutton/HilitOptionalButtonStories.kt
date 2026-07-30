package stories.components.designsystem.hilitoptionalbutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.button.HilitOptionalButton
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitOptionalButtonStories =
    StoryGroup(
        path = "Components/HilitOptionalButton",
        description = "너비를 호출자가 정하고 내부 Composable을 직접 구성하는 Optional 버튼.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "본문·태그 문구와 아이콘·태그 표시 여부를 조작한다.",
                ) {
                    HilitOptionalButtonCatalogAdapterControls(
                        initialArgs =
                            HilitOptionalButtonCatalogAdapterArgs(
                                text = "버튼",
                                tagText = "선택",
                                showIcon = true,
                                showTag = true,
                            ),
                    )
                },
                Story(
                    id = "content-width",
                    title = "콘텐츠 · 너비",
                    description = "문구 길이와 호출자 지정 너비에 따른 배치를 비교한다.",
                ) {
                    HilitTheme {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            OptionalButton(text = "버튼")
                            OptionalButton(text = "조금 더 긴 버튼 문구")
                            OptionalButton(text = "버튼", modifier = Modifier.width(334.dp))
                        }
                    }
                },
            ),
    )

@androidx.compose.runtime.Composable
private fun OptionalButton(
    text: String,
    modifier: Modifier = Modifier,
) {
    HilitOptionalButton(
        modifier = modifier,
        onClick = {},
    ) {
        HilitOptionalButtonCatalogContent(text = text, tagText = "선택")
    }
}
