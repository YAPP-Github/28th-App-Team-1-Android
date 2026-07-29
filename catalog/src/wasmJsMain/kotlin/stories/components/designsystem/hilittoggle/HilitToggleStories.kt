package stories.components.designsystem.hilittoggle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.toggle.HilitToggle
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitToggleStories =
    StoryGroup(
        path = "Components/HilitToggle",
        description = "50×28 크기의 상태 제어형 토글. 켜짐과 꺼짐 상태를 제공한다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "checked 값을 조작하고 클릭으로 상태 변경을 확인한다.",
                ) {
                    HilitToggleCatalogAdapterControls(
                        initialArgs = HilitToggleCatalogAdapterArgs(checked = true),
                    )
                },
                Story(
                    id = "states",
                    title = "전체 상태",
                    description = "켜짐과 꺼짐의 손잡이 위치와 색상을 비교한다.",
                ) {
                    HilitTheme {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            HilitToggle(checked = true, onCheckedChange = {})
                            HilitToggle(checked = false, onCheckedChange = {})
                        }
                    }
                },
            ),
    )
