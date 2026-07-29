package stories.components.designsystem.bublefield

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.bubblefield.BubbleField
import com.dminus14.designsystem.component.bubblefield.BubbleFieldType
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailAlign
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailEdge
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailShape
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val BubleFieldStories =
    StoryGroup(
        path = "Components/BubleField",
        description = "말풍선 필드. Type(Small/Big) × Edge × Align × Shape.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "Controls로 타입·문구·꼬리 옵션을 조절할 수 있다.",
                ) {
                    BubleFieldCatalogAdapterControls(
                        initialArgs =
                            BubleFieldCatalogAdapterArgs(
                                text = "텍스트를 입력해주세요",
                                type = BubbleFieldType.Big,
                                tailEdge = BubleFieldTailEdge.Bottom,
                                tailAlign = BubleFieldTailAlign.Right,
                                tailShape = BubleFieldTailShape.Right,
                            ),
                    )
                },
                Story(
                    id = "types",
                    title = "Small / Big",
                    description = "Small(콘텐츠 너비)과 Big(최소 274dp) 비교.",
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        BubbleFieldType.entries.forEach { type ->
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = type.name,
                                    style = HilitTheme.typography.body9,
                                    color = HilitTheme.colors.gray500,
                                )
                                BubbleField(
                                    text = "텍스트를 입력해주세요",
                                    type = type,
                                    tailEdge = BubleFieldTailEdge.Bottom,
                                    tailAlign = BubleFieldTailAlign.Right,
                                    tailShape = BubleFieldTailShape.Right,
                                )
                            }
                        }
                    }
                },
            ),
    )
