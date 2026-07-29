package stories.components.designsystem.hilitfixedbottombutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitFixedBottomButtonStories =
    StoryGroup(
        path = "Components/HilitFixedBottomButton",
        description =
            "화면 모드에 따른 하단 고정 버튼. 활성 상태에서 Light는 검정 배경과 흰 글자, " +
                "Dark는 흰 배경과 검정 글자를 사용한다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "문구, 활성 여부, 버튼이 표시되는 화면 모드를 조작하는 기본 상태.",
                ) {
                    HilitFixedBottomButtonCatalogAdapterControls(
                        initialArgs =
                            HilitFixedBottomButtonCatalogAdapterArgs(
                                text = "계속하기",
                                enabled = true,
                                type = HilitButtonType.Light,
                            ),
                    )
                },
                Story(
                    id = "variants",
                    title = "타입 · 상태",
                    description =
                        "Light(검정 배경·흰 글자)와 Dark(흰 배경·검정 글자)의 " +
                            "활성·비활성 상태를 비교하며, 눌림 상태는 직접 눌러 확인한다.",
                ) {
                    HilitTheme {
                        Column(
                            modifier = Modifier.width(360.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            HilitFixedBottomButton(
                                text = "Light · 활성",
                                type = HilitButtonType.Light,
                                enabled = true,
                                onClick = {},
                            )
                            HilitFixedBottomButton(
                                text = "Light · 비활성",
                                type = HilitButtonType.Light,
                                enabled = false,
                                onClick = {},
                            )
                            HilitFixedBottomButton(
                                text = "Dark · 활성",
                                type = HilitButtonType.Dark,
                                enabled = true,
                                onClick = {},
                            )
                            HilitFixedBottomButton(
                                text = "Dark · 비활성",
                                type = HilitButtonType.Dark,
                                enabled = false,
                                onClick = {},
                            )
                        }
                    }
                },
            ),
    )
