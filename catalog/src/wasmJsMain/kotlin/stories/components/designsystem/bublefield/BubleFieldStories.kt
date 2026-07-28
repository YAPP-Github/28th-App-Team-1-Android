package stories.components.designsystem.bublefield

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.bubblefield.BubbleField
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailAlign
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailEdge
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailShape
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val BubleFieldStories =
    StoryGroup(
        path = "Components/BubleField",
        description = "말풍선 필드. 꼬리 Edge(Top/Bottom) × Align(Left/Right) × Shape(Left/Right).",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "Controls로 문구·꼬리 위치·삼각형 방향을 조절할 수 있다.",
                ) {
                    BubleFieldCatalogAdapterControls(
                        initialArgs =
                            BubleFieldCatalogAdapterArgs(
                                text = "텍스트를 입력해주세요",
                                tailEdge = BubleFieldTailEdge.Bottom,
                                tailAlign = BubleFieldTailAlign.Right,
                                tailShape = BubleFieldTailShape.Right,
                            ),
                    )
                },
                Story(
                    id = "all-variants",
                    title = "전체 조합",
                    description = "Edge × Align × Shape 8가지를 한눈에 보기.",
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        BubleFieldTailEdge.entries.forEach { edge ->
                            BubleFieldTailAlign.entries.forEach { align ->
                                BubleFieldTailShape.entries.forEach { shape ->
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "${edge.name} / ${align.name} / shape=${shape.name}",
                                            style = HilitTheme.typography.body9,
                                            color = HilitTheme.colors.gray500,
                                        )
                                        BubbleField(
                                            text = "텍스트를 입력해주세요",
                                            tailEdge = edge,
                                            tailAlign = align,
                                            tailShape = shape,
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
            ),
    )
