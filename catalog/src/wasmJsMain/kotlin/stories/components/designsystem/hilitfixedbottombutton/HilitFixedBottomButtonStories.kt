package stories.components.designsystem.hilitfixedbottombutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitFixedBottomButtonStories =
    StoryGroup(
        path = "Components/HilitFixedBottomButton",
        description = "하단 고정 버튼 Dark/Light 타입과 enable/disable 상태.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "text / enabled / type을 조작하는 HilitFixedBottomButton.",
                ) {
                    HilitFixedBottomButtonCatalogAdapterControls(
                        initialArgs =
                            HilitFixedBottomButtonCatalogAdapterArgs(
                                text = "계속하기",
                                enabled = true,
                                type = HilitButtonType.Dark,
                            ),
                    )
                },
                Story(
                    id = "variants",
                    title = "타입 · 상태",
                    description = "Dark/Light × enabled/disabled를 같이 보기. press는 직접 눌러 확인.",
                ) {
                    HilitTheme {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            HilitFixedBottomButton(
                                text = "Dark · Enable",
                                type = HilitButtonType.Dark,
                                enabled = true,
                                onClick = {},
                            )
                            HilitFixedBottomButton(
                                text = "Dark · Disable",
                                type = HilitButtonType.Dark,
                                enabled = false,
                                onClick = {},
                            )
                            HilitFixedBottomButton(
                                text = "Light · Enable",
                                type = HilitButtonType.Light,
                                enabled = true,
                                onClick = {},
                            )
                            HilitFixedBottomButton(
                                text = "Light · Disable",
                                type = HilitButtonType.Light,
                                enabled = false,
                                onClick = {},
                            )
                        }
                    }
                },
            ),
    )
