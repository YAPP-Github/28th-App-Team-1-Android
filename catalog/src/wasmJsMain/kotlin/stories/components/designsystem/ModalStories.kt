package stories.components.designsystem

import type.Story
import type.StoryGroup

internal val ModalStories =
    StoryGroup(
        path = "Components/DMinusModal",
        description = "Material 기반 공용 Modal 상태.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "확인 버튼만 있는 기본 Modal.",
                ) {
                    DMinusModalCatalogAdapterControls(
                        initialArgs =
                            DMinusModalCatalogAdapterArgs(
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
                    description = "확인과 취소 선택을 제공하는 Modal.",
                ) {
                    DMinusModalWithCancelCatalogAdapterControls(
                        initialArgs =
                            DMinusModalWithCancelCatalogAdapterArgs(
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
                    description = "명시적인 버튼 선택으로만 닫을 수 있는 Modal.",
                ) {
                    DMinusModalWithCancelCatalogAdapterControls(
                        initialArgs =
                            DMinusModalWithCancelCatalogAdapterArgs(
                                title = "선택이 필요합니다",
                                message = "계속하려면 아래 버튼 중 하나를 선택해 주세요.",
                                confirmText = "동의",
                                cancelText = "취소",
                                dismissible = false,
                            ),
                    )
                },
                Story(
                    id = "long-content",
                    title = "긴 콘텐츠",
                    description = "긴 제목과 본문에서 레이아웃을 확인하는 Modal.",
                ) {
                    DMinusModalWithCancelCatalogAdapterControls(
                        initialArgs =
                            DMinusModalWithCancelCatalogAdapterArgs(
                                title = "긴 제목이 표시될 때 Modal 레이아웃을 확인합니다",
                                message =
                                    "이 문구는 실제 사용자 데이터가 아닌 합성 예시입니다. " +
                                        "본문이 여러 줄로 표시되는 상황에서 기본 Material Modal " +
                                        "간격과 버튼 배치를 검토하기 위해 사용합니다.",
                                confirmText = "확인",
                                cancelText = "취소",
                                dismissible = true,
                            ),
                    )
                },
            ),
    )
