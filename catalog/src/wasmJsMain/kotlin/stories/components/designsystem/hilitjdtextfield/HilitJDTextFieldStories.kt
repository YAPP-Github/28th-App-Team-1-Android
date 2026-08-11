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
                                minLength = 0,
                                validationErrorText = "",
                                validationSuccessText = "",
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
                                minLength = 0,
                                validationErrorText = "",
                                validationSuccessText = "",
                            ),
                    )
                },
                Story(
                    id = "validation-error",
                    title = "조건 미충족",
                    description = "minLength 미만 입력 시 Error 서브텍스트가 노출된다.",
                ) {
                    HilitJDTextFieldCatalogAdapterControls(
                        initialArgs =
                            HilitJDTextFieldCatalogAdapterArgs(
                                value = "JD 입력 예시입니다.",
                                placeholder = "텍스트를 입력해주세요",
                                maxLength = 3000,
                                minLength = 200,
                                validationErrorText = "공고 내용은 200자 이상으로 입력해 주세요",
                                validationSuccessText = "공고 내용을 확인했어요",
                            ),
                    )
                },
                Story(
                    id = "validation-success",
                    title = "조건 충족",
                    description = "minLength 이상 입력 시 Success 서브텍스트가 노출된다.",
                ) {
                    HilitJDTextFieldCatalogAdapterControls(
                        initialArgs =
                            HilitJDTextFieldCatalogAdapterArgs(
                                value =
                                    "당사 서비스의 백엔드 API를 설계하고 운영하실 시니어 개발자를 찾고 있습니다. " +
                                        "Kotlin, Spring Boot 기반 마이크로서비스 경험이 필요하며, " +
                                        "대용량 트래픽 처리와 데이터 파이프라인 구축 경험을 우대합니다. " +
                                        "협업과 코드 리뷰 문화를 중시하는 팀에서 함께 성장할 분을 기다립니다.",
                                placeholder = "텍스트를 입력해주세요",
                                maxLength = 3000,
                                minLength = 200,
                                validationErrorText = "공고 내용은 200자 이상으로 입력해 주세요",
                                validationSuccessText = "공고 내용을 확인했어요",
                            ),
                    )
                },
            ),
    )
