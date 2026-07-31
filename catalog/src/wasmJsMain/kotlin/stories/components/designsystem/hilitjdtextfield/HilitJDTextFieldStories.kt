package stories.components.designsystem.hilitjdtextfield

import type.Story
import type.StoryGroup

internal val HilitJDTextFieldStories =
    StoryGroup(
        path = "Components/HilitJDTextField",
        description = "JD 입력용 멀티라인 텍스트 필드. 글자 수 카운터를 포함한다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "비어 있는 기본 상태. Controls로 값·placeholder·maxLength를 조절할 수 있다.",
                ) {
                    HilitJDTextFieldCatalogAdapterControls(
                        initialArgs =
                            HilitJDTextFieldCatalogAdapterArgs(
                                value = "",
                                placeholder = "텍스트를 입력해주세요",
                                maxLength = 300,
                            ),
                    )
                },
                Story(
                    id = "filled",
                    title = "입력됨",
                    description = "텍스트가 채워진 상태.",
                ) {
                    HilitJDTextFieldCatalogAdapterControls(
                        initialArgs =
                            HilitJDTextFieldCatalogAdapterArgs(
                                value = "백엔드 개발자 JD를 입력하는 예시입니다.",
                                placeholder = "텍스트를 입력해주세요",
                                maxLength = 300,
                            ),
                    )
                },
            ),
    )
