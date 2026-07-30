package stories.components.designsystem.hilitloadingindicator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.loading.HilitLoadingIndicator
import com.dminus14.designsystem.theme.HilitTheme
import type.Story
import type.StoryGroup

internal val HilitLoadingIndicatorStories =
    StoryGroup(
        path = "Components/HilitLoadingIndicator",
        description = "Figma 원본 그래픽을 시계 방향으로 1.5초에 한 바퀴, 멈춤 없이 속도를 바꾸며 회전하는 로딩 표시.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "크기와 접근성 설명을 조작한다.",
                ) {
                    HilitLoadingIndicatorCatalogAdapterControls(
                        initialArgs =
                            HilitLoadingIndicatorCatalogAdapterArgs(
                                size = 74,
                                contentDescription = "불러오는 중",
                            ),
                    )
                },
                Story(
                    id = "sizes",
                    title = "크기",
                    description = "원본 비율을 유지하는 48/74/96dp 크기를 비교한다.",
                ) {
                    HilitTheme {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            HilitLoadingIndicator(size = 48.dp)
                            HilitLoadingIndicator(size = 74.dp)
                            HilitLoadingIndicator(size = 96.dp)
                        }
                    }
                },
            ),
    )
