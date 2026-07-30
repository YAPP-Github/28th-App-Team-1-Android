package stories.components.designsystem.hilitminibutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.button.HilitMiniButton
import com.dminus14.designsystem.component.button.HilitMiniButtonColor
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitMiniButtonStories =
    StoryGroup(
        path = "Components/HilitMiniButton",
        description = "light/dark 색상과 호출자 구성 콘텐츠를 제공하는 Mini 버튼.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "문구, 색상과 아이콘 표시 여부를 조작한다.",
                ) {
                    HilitMiniButtonCatalogAdapterControls(
                        initialArgs =
                            HilitMiniButtonCatalogAdapterArgs(
                                text = "버튼",
                                color = HilitMiniButtonColor.Light,
                                showIcon = true,
                            ),
                    )
                },
                Story(
                    id = "variants",
                    title = "색상 · 콘텐츠",
                    description = "두 색상과 아이콘 유무를 비교한다.",
                ) {
                    HilitTheme {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                MiniButton(color = HilitMiniButtonColor.Light)
                                MiniButton(color = HilitMiniButtonColor.Dark)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                MiniButton(color = HilitMiniButtonColor.Light, showIcon = false)
                                MiniButton(color = HilitMiniButtonColor.Dark, showIcon = false)
                            }
                        }
                    }
                },
            ),
    )

@androidx.compose.runtime.Composable
private fun MiniButton(
    color: HilitMiniButtonColor,
    showIcon: Boolean = true,
) {
    HilitMiniButton(color = color, onClick = {}) {
        HilitMiniButtonCatalogContent(text = "버튼", showIcon = showIcon)
    }
}
