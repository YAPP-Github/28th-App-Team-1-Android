package stories.components.designsystem.hilitprogressbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.progressbar.HilitProgressBar
import com.dminus14.designsystem.component.progressbar.HilitStep
import type.Story
import type.StoryGroup

internal val HilitProgressBarStories =
    StoryGroup(
        path = "Components/HilitProgressBar",
        description = "5칸 dash progress bar. 활성 단계는 hilitBlack800, 비활성은 gray50.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description = "Controls로 현재 step을 바꿀 수 있다.",
                ) {
                    HilitProgressBarCatalogAdapterControls(
                        initialArgs =
                            HilitProgressBarCatalogAdapterArgs(
                                step = HilitStep.Step3,
                            ),
                    )
                },
                Story(
                    id = "all-steps",
                    title = "전체 단계",
                    description = "Step1부터 Step5까지 한눈에 보기.",
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        HilitStep.entries.forEach { step ->
                            HilitProgressBar(step = step)
                        }
                    }
                },
            ),
    )
