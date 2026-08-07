package stories.components.designsystem.hilittag

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitTagStories =
    StoryGroup(
        path = "Components/HilitTag",
        description = "태그 colorType / tagType 상태.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "text / colorType / tagType을 조작하는 HilitTag.",
                ) {
                    HilitTagCatalogAdapterControls(
                        initialArgs =
                            HilitTagCatalogAdapterArgs(
                                text = "Tag",
                                colorType = TagColorType.BlackGreen,
                                tagType = TagType.Small,
                            ),
                    )
                },
                Story(
                    id = "variants",
                    title = "타입 · 크기",
                    description = "Small/Large × colorType을 같이 보기.",
                ) {
                    HilitTheme {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                TagColorType.entries.forEach { colorType ->
                                    HilitTag(
                                        text = colorType.name,
                                        colorType = colorType,
                                        tagType = TagType.Small,
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                TagColorType.entries.forEach { colorType ->
                                    HilitTag(
                                        text = colorType.name,
                                        colorType = colorType,
                                        tagType = TagType.Large,
                                    )
                                }
                            }
                        }
                    }
                },
            ),
    )
