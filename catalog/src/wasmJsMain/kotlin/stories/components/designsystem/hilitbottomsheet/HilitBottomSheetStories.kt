package stories.components.designsystem.hilitbottomsheet

import type.Story
import type.StoryGroup

internal val HilitBottomSheetStories =
    StoryGroup(
        path = "Components/HilitBottomSheet",
        description = "핸들·스크림과 content 슬롯을 제공하는 공용 Modal Bottom Sheet.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "content 슬롯에 타이틀·본문을 채운 기본 Bottom Sheet.",
                ) {
                    HilitBottomSheetCatalogAdapterControls(
                        initialArgs =
                            HilitBottomSheetCatalogAdapterArgs(
                                title = "타이틀",
                                content = "본문 슬롯 내용입니다.",
                            ),
                    )
                },
                Story(
                    id = "long-content",
                    title = "긴 본문",
                    description = "본문이 길어 스크롤되는 Bottom Sheet.",
                ) {
                    HilitBottomSheetCatalogAdapterControls(
                        initialArgs =
                            HilitBottomSheetCatalogAdapterArgs(
                                title = "서비스 이용 약관",
                                content =
                                    "이 문구는 실제 사용자 데이터가 아닌 합성 예시입니다. ".repeat(24),
                            ),
                    )
                },
            ),
    )
