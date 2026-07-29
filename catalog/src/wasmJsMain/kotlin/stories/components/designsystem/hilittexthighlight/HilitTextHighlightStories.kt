package stories.components.designsystem.hilittexthighlight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.text.HilitText
import com.dminus14.designsystem.component.text.HilitTextHighlightColor
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitTextHighlightStories =
    StoryGroup(
        path = "Components/HilitTextHighlight",
        description = "한 Text 안에서 특정 구간에 적용하는 초록, 빨강, 파랑 텍스트 하이라이트.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "앞 문장, 강조 문장, 뒤 문장을 직접 편집하는 하이라이트.",
                ) {
                    HilitTextHighlightCatalogAdapterControls(
                        initialArgs =
                            HilitTextHighlightCatalogAdapterArgs(
                                prefix = "면접에서 ",
                                highlight = "핵심 경험",
                                suffix = "을 설명해 주세요.",
                                highlightColor = HilitTextHighlightColor.Green,
                            ),
                    )
                },
                Story(
                    id = "figma-reference",
                    title = "Figma 기준",
                    description = "head3 글꼴 속성으로 세 가지 하이라이트 색상을 비교하는 기준 상태.",
                ) {
                    HilitTheme {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            HilitTextHighlightColor.entries.forEach { highlightColor ->
                                HilitText(
                                    text =
                                        buildAnnotatedString {
                                            withHilitTextHighlight {
                                                append("텍스트")
                                            }
                                        },
                                    style = HilitTheme.typography.head3,
                                    highlightColor = highlightColor,
                                )
                            }
                        }
                    }
                },
                Story(
                    id = "multiple-ranges",
                    title = "복수 강조",
                    description = "서로 다른 부모 크기와 여러 강조 구간을 같이 확인하는 상태.",
                ) {
                    HilitTheme {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            HilitText(
                                text = multipleHighlightText(),
                                style = HilitTheme.typography.body10,
                            )
                            HilitText(
                                text = multipleHighlightText(),
                                style = HilitTheme.typography.body4,
                            )
                            HilitText(
                                text = multipleHighlightText(),
                                style = HilitTheme.typography.head3,
                            )
                        }
                    }
                },
                Story(
                    id = "line-move",
                    title = "줄 이동",
                    description = "강조 요소가 남은 너비보다 넓으면 요소 전체가 다음 줄로 이동.",
                ) {
                    HilitTheme {
                        HilitText(
                            text =
                                buildAnnotatedString {
                                    append("줄 끝에 가까운 일반 문장 뒤의 ")
                                    withHilitTextHighlight {
                                        append("하이라이트")
                                    }
                                },
                            modifier = Modifier.width(240.dp),
                            style = HilitTheme.typography.body4,
                        )
                    }
                },
            ),
    )

private fun multipleHighlightText() =
    buildAnnotatedString {
        withHilitTextHighlight {
            append("문제 상황")
        }
        append("과 ")
        withHilitTextHighlight {
            append("해결 과정")
        }
        append("을 설명해 주세요.")
    }
