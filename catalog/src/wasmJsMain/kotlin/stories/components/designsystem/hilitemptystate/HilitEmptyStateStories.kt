package stories.components.designsystem.hilitemptystate

import type.Story
import type.StoryGroup

internal val HilitEmptyStateStories =
    StoryGroup(
        path = "Components/HilitEmptyState",
        description = "콘텐츠가 비어 있을 때 안내 문구를 표시하는 상태 영역.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "Controls로 안내 문구를 바꿀 수 있다.",
                ) {
                    HilitEmptyStateCatalogAdapterControls(
                        initialArgs =
                            HilitEmptyStateCatalogAdapterArgs(
                                text = "아직 첨부된 콘텐츠가 없어요",
                            ),
                    )
                },
            ),
    )
