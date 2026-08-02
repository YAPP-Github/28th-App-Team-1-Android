package stories.components.designsystem.hilitmodal

import com.dminus14.designsystem.component.modal.HilitModalType
import type.Story
import type.StoryGroup

internal val HilitModalStories =
    StoryGroup(
        path = "Components/HilitModal",
        description = "Figma modal preset. type별 아이콘·info-field 노출과 하단 버튼 슬롯.",
        stories =
            listOf(
                Story(
                    id = "max",
                    title = "Max",
                    description = "아이콘, 타이틀, 서브텍스트, info-field, 단일 버튼.",
                ) {
                    HilitModalCatalogAdapterControls(
                        initialArgs =
                            HilitModalCatalogAdapterArgs(
                                type = HilitModalType.Max,
                                title = "텍스트를 입력해주세요",
                                subtitle = "서브텍스트를 입력해주세요",
                                infoText = "합성 안내 문구입니다.",
                                confirmText = "확인",
                                showGraphic = true,
                                dismissible = true,
                            ),
                    )
                },
                Story(
                    id = "invisible-info",
                    title = "InvisibleInfo",
                    description = "아이콘과 타이틀·서브텍스트, info-field 없음.",
                ) {
                    HilitModalCatalogAdapterControls(
                        initialArgs =
                            HilitModalCatalogAdapterArgs(
                                type = HilitModalType.InvisibleInfo,
                                title = "텍스트를 입력해주세요",
                                subtitle = "서브텍스트를 입력해주세요",
                                infoText = "",
                                confirmText = "확인",
                                showGraphic = true,
                                dismissible = true,
                            ),
                    )
                },
                Story(
                    id = "invisible-icon",
                    title = "InvisibleIcon",
                    description = "아이콘 없이 타이틀, 서브텍스트, info-field.",
                ) {
                    HilitModalCatalogAdapterControls(
                        initialArgs =
                            HilitModalCatalogAdapterArgs(
                                type = HilitModalType.InvisibleIcon,
                                title = "텍스트를 입력해주세요",
                                subtitle = "서브텍스트를 입력해주세요",
                                infoText = "합성 안내 문구입니다.",
                                confirmText = "확인",
                                showGraphic = false,
                                dismissible = true,
                            ),
                    )
                },
                Story(
                    id = "default",
                    title = "Default",
                    description = "타이틀, 서브텍스트, 단일 버튼만.",
                ) {
                    HilitModalCatalogAdapterControls(
                        initialArgs =
                            HilitModalCatalogAdapterArgs(
                                type = HilitModalType.Default,
                                title = "텍스트를 입력해주세요",
                                subtitle = "서브텍스트를 입력해주세요",
                                infoText = "",
                                confirmText = "확인",
                                showGraphic = false,
                                dismissible = true,
                            ),
                    )
                },
                Story(
                    id = "dual-button",
                    title = "듀얼 버튼",
                    description = "InvisibleInfo + book 일러스트 + Default 듀얼 버튼.",
                ) {
                    HilitModalDualButtonCatalogAdapterControls(
                        initialArgs =
                            HilitModalDualButtonCatalogAdapterArgs(
                                title = "기존에 있는 포트폴리오로\n진행할까요?",
                                leftText = "새로 업로드",
                                rightText = "기존 포트폴리오 사용",
                                dismissible = false,
                            ),
                    )
                },
            ),
    )
