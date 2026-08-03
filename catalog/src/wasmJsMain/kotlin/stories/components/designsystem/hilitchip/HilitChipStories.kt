package stories.components.designsystem.hilitchip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.chip.HilitChip
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitChipStories =
    StoryGroup(
        path = "Components/HilitChip",
        description = "선택 가능한 Chip의 selected / enabled 상태.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "text, selected, enabled를 조작하는 HilitChip 기본 상태.",
                ) {
                    HilitChipCatalogAdapterControls(
                        initialArgs =
                            HilitChipCatalogAdapterArgs(
                                text = "백엔드",
                                selected = false,
                                enabled = true,
                            ),
                    )
                },
                Story(
                    id = "states",
                    title = "상태",
                    description = "미선택 / 선택 / Disabled를 한눈에 보기.",
                ) {
                    HilitTheme {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            HilitChip(text = "백엔드", selected = false, onClick = {})
                            HilitChip(text = "iOS", selected = true, onClick = {})
                            HilitChip(
                                text = "Disabled",
                                selected = false,
                                enabled = false,
                                onClick = {},
                            )
                        }
                    }
                },
            ),
    )
