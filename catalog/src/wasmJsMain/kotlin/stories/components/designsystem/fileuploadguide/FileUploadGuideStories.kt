package stories.components.designsystem.fileuploadguide

import type.Story
import type.StoryGroup

internal val FileUploadGuideStories =
    StoryGroup(
        path = "Components/FileUploadGuide",
        description = "파일 업로드 유도 영역. 업로드 전(before) 상태.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "Controls로 title·description을 조절할 수 있다.",
                ) {
                    FileUploadGuideCatalogAdapterControls(
                        initialArgs =
                            FileUploadGuideCatalogAdapterArgs(
                                title = "파일을 업로드해주세요",
                                description = "1개 파일, 최대 20Mb까지 가능합니다",
                            ),
                    )
                },
            ),
    )
