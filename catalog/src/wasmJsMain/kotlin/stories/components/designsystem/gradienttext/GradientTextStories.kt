package stories.components.designsystem.gradienttext

import type.Story
import type.StoryGroup

internal val GradientTextStories =
    StoryGroup(
        path = "Components/GradientText",
        description = "가로 그라디언트 텍스트.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "Controls로 문구·typography·시작·끝 hex 색상을 편집한다.",
                ) {
                    GradientTextCatalogAdapterControls(
                        initialArgs =
                            GradientTextCatalogAdapterArgs(
                                text = "오랜만이에요\n재원님!",
                                typography = GradientTextTypography.Head1,
                                startColorHex = "88C97C", // hilitGreen600
                                endColorHex = "106100", // hilitGreen800
                            ),
                    )
                },
            ),
    )
