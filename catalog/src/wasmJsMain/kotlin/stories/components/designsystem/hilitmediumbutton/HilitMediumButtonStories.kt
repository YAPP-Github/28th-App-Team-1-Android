package stories.components.designsystem.hilitmediumbutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.button.HilitMediumButton
import com.dminus14.designsystem.component.button.HilitMediumButtonColor
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitMediumButtonStories =
    StoryGroup(
        path = "Components/HilitMediumButton",
        description = "콘텐츠 폭에 맞춰지는 Medium 버튼의 색상과 Disabled 상태.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "text, color, enabled를 조작하는 HilitMediumButton 기본 상태.",
                ) {
                    HilitMediumButtonCatalogAdapterControls(
                        initialArgs =
                            HilitMediumButtonCatalogAdapterArgs(
                                text = "버튼",
                                color = HilitMediumButtonColor.Default,
                                enabled = true,
                            ),
                    )
                },
                Story(
                    id = "colors",
                    title = "색상 · 상태",
                    description = "6가지 활성 색상과 공통 Disabled 상태를 Figma 기준으로 비교.",
                ) {
                    HilitTheme {
                        Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                                MediumButton(color = HilitMediumButtonColor.Default)
                                MediumButton(color = HilitMediumButtonColor.Green)
                                MediumButton(enabled = false)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                                MediumButton(color = HilitMediumButtonColor.Blue)
                                MediumButton(color = HilitMediumButtonColor.Black)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                                MediumButton(color = HilitMediumButtonColor.Red)
                                MediumButton(color = HilitMediumButtonColor.Gray)
                            }
                        }
                    }
                },
                Story(
                    id = "content-width",
                    title = "콘텐츠 너비",
                    description = "문구 길이가 달라도 좌우 24dp 여백을 유지하는지 비교.",
                ) {
                    HilitTheme {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            HilitMediumButton(text = "버튼", onClick = {})
                            HilitMediumButton(text = "조금 더 긴 버튼 문구", onClick = {})
                        }
                    }
                },
            ),
    )

@Composable
private fun MediumButton(
    color: HilitMediumButtonColor = HilitMediumButtonColor.Default,
    enabled: Boolean = true,
) {
    HilitMediumButton(
        text = "버튼",
        color = color,
        enabled = enabled,
        onClick = {},
    )
}
