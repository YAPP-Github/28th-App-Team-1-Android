package stories.components.designsystem.hilitsubtext

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.subtext.HilitSubText
import com.dminus14.designsystem.component.subtext.HilitSubTextType
import type.Story
import type.StoryGroup

internal val HilitSubTextStories =
    StoryGroup(
        path = "Components/HilitSubText",
        description = "필드 하단 서브 텍스트. Default / Success / Error 타입.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "Controls로 문구와 타입을 바꿀 수 있다.",
                ) {
                    HilitSubTextCatalogAdapterControls(
                        initialArgs =
                            HilitSubTextCatalogAdapterArgs(
                                text = "서브 텍스트를 입력해주세요",
                                type = HilitSubTextType.Default,
                            ),
                    )
                },
                Story(
                    id = "all-types",
                    title = "전체 타입",
                    description = "Default, Success, Error를 한눈에 보기.",
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        HilitSubTextType.entries.forEach { type ->
                            HilitSubText(
                                text = "서브 텍스트를 입력해주세요",
                                type = type,
                            )
                        }
                    }
                },
            ),
    )
