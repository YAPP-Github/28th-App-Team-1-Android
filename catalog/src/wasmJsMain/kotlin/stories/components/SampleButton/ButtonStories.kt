package stories.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.SampleButton
import type.Story
import type.StoryGroup

internal val ButtonStories =
    StoryGroup(
        path = "Components/SampleButton",
        description = "Button component states.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "기본 SampleButton 상태.",
                ) {
                    SampleButtonCatalogAdapterControls(
                        initialArgs =
                            SampleButtonCatalogAdapterArgs(
                                text = "샘플 버튼",
                                enabled = true,
                            ),
                    )
                },
                Story(
                    id = "disabled",
                    title = "비활성화",
                    description = "비활성화된 SampleButton 상태.",
                ) {
                    SampleButtonCatalogAdapterControls(
                        initialArgs =
                            SampleButtonCatalogAdapterArgs(
                                text = "샘플 버튼",
                                enabled = false,
                            ),
                    )
                },
                Story(
                    id = "variants",
                    title = "같이 보기",
                    description = "활성화 및 비활성화 상태를 같이 보기.",
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SampleButton(
                            text = "샘플 버튼",
                            enabled = true,
                            onClick = {},
                        )

                        SampleButton(
                            text = "샘플 버튼",
                            enabled = false,
                            onClick = {},
                        )
                    }
                },
            ),
    )
