package stories.components.designsystem.hilitmodal

import type.Story
import type.StoryGroup

internal val HilitModalStories =
    StoryGroup(
        path = "Components/HilitModal",
        description = "Hilit 제품 시각과 고정 하단 버튼을 사용하는 공용 Modal 상태.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "확인 버튼만 있는 기본 Modal.",
                ) {
                    HilitModalCatalogAdapterControls(
                        initialArgs =
                            HilitModalCatalogAdapterArgs(
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
                    description = "같은 너비의 검정 버튼 두 개와 중앙 구분선을 제공하는 Modal.",
                ) {
                    HilitModalWithCancelCatalogAdapterControls(
                        initialArgs =
                            HilitModalWithCancelCatalogAdapterArgs(
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
                    description = "Guest Feedback 오류에서 명시적인 종료 선택만 허용하는 Modal.",
                ) {
                    HilitModalCatalogAdapterControls(
                        initialArgs =
                            HilitModalCatalogAdapterArgs(
                                title = "오류가 발생했어요",
                                message = "앱을 종료한 뒤 링크를 다시 열어주세요.",
                                confirmText = "종료하기",
                                dismissible = false,
                            ),
                    )
                },
                Story(
                    id = "long-content",
                    title = "긴 콘텐츠",
                    description = "긴 제목과 본문에서 레이아웃을 확인하는 Modal.",
                ) {
                    HilitModalWithCancelCatalogAdapterControls(
                        initialArgs =
                            HilitModalWithCancelCatalogAdapterArgs(
                                title = "긴 제목이 표시될 때 Modal 레이아웃을 확인합니다",
                                message =
                                    "이 문구는 실제 사용자 데이터가 아닌 합성 예시입니다. " +
                                        "본문이 여러 줄로 표시되는 상황에서 제품 Modal " +
                                        "간격과 버튼 배치를 검토하기 위해 사용합니다.",
                                confirmText = "확인",
                                cancelText = "취소",
                                dismissible = true,
                            ),
                    )
                },
            ),
    )
