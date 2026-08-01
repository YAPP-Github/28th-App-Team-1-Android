package stories.components.designsystem.hilitfixedbottomdualbutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitFixedBottomDualButtonStories =
    StoryGroup(
        path = "Components/HilitFixedBottomDualButton",
        description =
            "하단 고정 듀얼 버튼. Default·Gray·TwoColor 타입과 좌·우 개별 활성 상태를 지원한다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "좌·우 문구, 활성 여부, 타입을 조작하는 기본 상태.",
                ) {
                    HilitFixedBottomDualButtonCatalogAdapterControls(
                        initialArgs =
                            HilitFixedBottomDualButtonCatalogAdapterArgs(
                                leftText = "버튼",
                                rightText = "버튼2",
                                leftEnabled = true,
                                rightEnabled = true,
                                type = HilitFixedBottomDualButtonType.Default,
                            ),
                    )
                },
                Story(
                    id = "variants",
                    title = "타입 · 상태",
                    description =
                        "Default, Gray, TwoColor와 한쪽 비활성(1 disabled) 상태를 비교한다.",
                ) {
                    HilitTheme {
                        Column(
                            modifier = Modifier.width(360.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            HilitFixedBottomDualButton(
                                leftText = "버튼",
                                rightText = "버튼2",
                                type = HilitFixedBottomDualButtonType.Default,
                                onLeftClick = {},
                                onRightClick = {},
                            )
                            HilitFixedBottomDualButton(
                                leftText = "버튼",
                                rightText = "버튼2",
                                type = HilitFixedBottomDualButtonType.Gray,
                                onLeftClick = {},
                                onRightClick = {},
                            )
                            HilitFixedBottomDualButton(
                                leftText = "버튼",
                                rightText = "버튼2",
                                type = HilitFixedBottomDualButtonType.TwoColor,
                                onLeftClick = {},
                                onRightClick = {},
                            )
                            HilitFixedBottomDualButton(
                                leftText = "버튼",
                                rightText = "버튼2",
                                leftEnabled = false,
                                rightEnabled = true,
                                type = HilitFixedBottomDualButtonType.Default,
                                onLeftClick = {},
                                onRightClick = {},
                            )
                        }
                    }
                },
            ),
    )
