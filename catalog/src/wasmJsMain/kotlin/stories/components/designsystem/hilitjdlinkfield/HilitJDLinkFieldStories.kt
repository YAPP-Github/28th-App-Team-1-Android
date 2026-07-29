package stories.components.designsystem.hilitjdlinkfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.textfield.HilitJDLinkField
import com.dminus14.designsystem.component.textfield.HilitJDLinkFieldType
import type.Story
import type.StoryGroup

private val PreviewSurfaceColor = Color(0xFF4A4B50)

internal val HilitJDLinkFieldStories =
    StoryGroup(
        path = "Components/HilitJDLinkField",
        description = "JD 링크 입력 필드. Ready/Focus/Edit/Processing/Complete/Error 상태.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "Controls로 value·placeholder·subText·type을 조절할 수 있다.",
                ) {
                    HilitJDLinkFieldCatalogAdapterControls(
                        initialArgs =
                            HilitJDLinkFieldCatalogAdapterArgs(
                                value = "",
                                placeholder = "텍스트를 입력해주세요",
                                subText = "서브 텍스트를 입력해주세요",
                                type = HilitJDLinkFieldType.Ready,
                            ),
                    )
                },
                Story(
                    id = "all-types",
                    title = "전체 상태",
                    description = "6가지 상태를 한눈에 보기.",
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(PreviewSurfaceColor)
                                .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        HilitJDLinkFieldType.entries.forEach { type ->
                            HilitJDLinkField(
                                value =
                                    when (type) {
                                        HilitJDLinkFieldType.Edit,
                                        HilitJDLinkFieldType.Processing,
                                        HilitJDLinkFieldType.Complete,
                                        HilitJDLinkFieldType.Error,
                                        -> "https://company.com/jobs/123"

                                        else -> ""
                                    },
                                onValueChange = {},
                                type = type,
                                onClearClick = {},
                                subText = "서브 텍스트를 입력해주세요",
                            )
                        }
                    }
                },
            ),
    )
