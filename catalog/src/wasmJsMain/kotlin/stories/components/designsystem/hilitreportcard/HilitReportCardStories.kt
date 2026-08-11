package stories.components.designsystem.hilitreportcard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.reportcard.HilitReportCard
import com.dminus14.designsystem.component.reportcard.resolveHilitReportCardExpansion
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

private val PreviewReportItems =
    listOf(
        PreviewReportItem(
            id = "1",
            date = "7월 11일 월",
            title = "캐시 도입 결정의 이유와 한계까지 구체적인 수치로 설명해 주셨어요",
        ),
        PreviewReportItem(
            id = "2",
            date = "7월 10일 월",
            title = "서비스 아키텍처 변경 배경을 명확히 설명해 주셨어요",
        ),
        PreviewReportItem(
            id = "3",
            date = "7월 9일 금",
            title = "팀 협업 경험을 구체적인 사례로 설명해 주셨어요",
        ),
    )

private data class PreviewReportItem(
    val id: String,
    val date: String,
    val title: String,
)

internal val HilitReportCardStories =
    StoryGroup(
        path = "Components/HilitReportCard",
        description = "면접 리포트 카드. 목록에서는 단일 expand만 유지한다.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "date, title, expanded 상태를 조작하는 단일 카드.",
                ) {
                    HilitReportCardCatalogAdapterControls(
                        initialArgs =
                            HilitReportCardCatalogAdapterArgs(
                                date = "7월 11일 월",
                                title = "캐시 도입 결정의 이유와 한계까지 구체적인 수치로 설명해 주셨어요",
                                expanded = true,
                            ),
                    )
                },
                Story(
                    id = "single-expand-list",
                    title = "단일 expand 목록",
                    description = "한 번에 하나만 펼쳐지는 리포트 카드 목록.",
                ) {
                    HilitTheme {
                        var expandedItemId by remember { mutableStateOf<String?>("1") }

                        Column {
                            PreviewReportItems.forEach { item ->
                                HilitReportCard(
                                    date = item.date,
                                    title = item.title,
                                    expanded = expandedItemId == item.id,
                                    onExpandClick = {
                                        expandedItemId =
                                            resolveHilitReportCardExpansion(
                                                currentExpandedId = expandedItemId,
                                                requestedId = item.id,
                                            )
                                    },
                                    onActionClick = {},
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                            }
                        }
                    }
                },
            ),
    )
