package stories.components.designsystem.hilitwheelpicker

import type.Story
import type.StoryGroup

internal val HilitWheelPickerStories =
    StoryGroup(
        path = "Components/HilitWheelPicker",
        description = "문장형 세로 휠 피커. 중앙 선택 항목에 midlined 하이라이트를 표시한다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "경력 선택 휠 피커 기본 상태. Controls로 선택 인덱스를 바꿀 수 있다.",
                ) {
                    HilitWheelPickerCatalogAdapterControls(
                        initialArgs =
                            HilitWheelPickerCatalogAdapterArgs(
                                selectedIndex = 2,
                            ),
                    )
                },
                Story(
                    id = "first-item",
                    title = "첫 항목",
                    description = "첫 번째 항목이 선택된 상태.",
                ) {
                    HilitWheelPickerCatalogAdapterControls(
                        initialArgs =
                            HilitWheelPickerCatalogAdapterArgs(
                                selectedIndex = 0,
                            ),
                    )
                },
                Story(
                    id = "last-item",
                    title = "마지막 항목",
                    description = "마지막 항목이 선택된 상태.",
                ) {
                    HilitWheelPickerCatalogAdapterControls(
                        initialArgs =
                            HilitWheelPickerCatalogAdapterArgs(
                                selectedIndex = 4,
                            ),
                    )
                },
            ),
    )
