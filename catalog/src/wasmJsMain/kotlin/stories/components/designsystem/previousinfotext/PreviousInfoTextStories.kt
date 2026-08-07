package stories.components.designsystem.previousinfotext

import type.Story
import type.StoryGroup

internal val PreviousInfoTextStories =
    StoryGroup(
        path = "Components/PreviousInfoText",
        description = "가로 그라디언트 텍스트. 재방문 사용자 인사 등에 사용.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "Controls로 문구와 시작·끝 hex 색상을 편집한다.",
                ) {
                    PreviousInfoTextCatalogAdapterControls(
                        initialArgs =
                            PreviousInfoTextCatalogAdapterArgs(
                                text = "오랜만이에요\n재원님!",
                                startColorHex = "88C97C", // hilitGreen600
                                endColorHex = "106100", // hilitGreen800
                            ),
                    )
                },
            ),
    )
