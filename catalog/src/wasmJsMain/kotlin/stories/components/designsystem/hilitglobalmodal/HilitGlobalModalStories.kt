package stories.components.designsystem.hilitglobalmodal

import type.Story
import type.StoryGroup

internal val HilitGlobalModalStories =
    StoryGroup(
        path = "Components/HilitGlobalModal",
        description = "앱 전역 Modal에서 사용하는 title + message alert preset.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "확인 버튼만 있는 기본 Global Modal.",
                ) {
                    HilitGlobalModalCatalogAdapterControls(
                        initialArgs =
                            HilitGlobalModalCatalogAdapterArgs(
                                title = "안내",
                                message = "요청한 작업이 완료되었습니다.",
                                confirmText = "확인",
                                dismissible = true,
                            ),
                    )
                },
                Story(
                    id = "two-buttons",
                    title = "두 버튼",
                    description = "회색/검정 TwoColor 듀얼 버튼 Global Modal.",
                ) {
                    HilitGlobalModalWithCancelCatalogAdapterControls(
                        initialArgs =
                            HilitGlobalModalWithCancelCatalogAdapterArgs(
                                title = "작업을 진행할까요?",
                                message = "선택한 설정으로 작업을 계속합니다.",
                                confirmText = "진행",
                                cancelText = "취소",
                                dismissible = true,
                            ),
                    )
                },
                Story(
                    id = "non-dismissible",
                    title = "외부 닫기 차단",
                    description = "명시적인 종료 선택만 허용하는 Global Modal.",
                ) {
                    HilitGlobalModalCatalogAdapterControls(
                        initialArgs =
                            HilitGlobalModalCatalogAdapterArgs(
                                title = "오류가 발생했어요",
                                message = "앱을 종료한 뒤 링크를 다시 열어주세요.",
                                confirmText = "종료하기",
                                dismissible = false,
                            ),
                    )
                },
            ),
    )
