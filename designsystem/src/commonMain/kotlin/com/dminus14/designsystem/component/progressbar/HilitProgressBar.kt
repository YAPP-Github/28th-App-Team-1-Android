package com.dminus14.designsystem.component.progressbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme

/** Onboarding에서만 사용할 예정이라 indicator 갯수를 확장가능하게 만들지 않음. 참고 부탁드립니다. */
enum class HilitStep {
    Step1,
    Step2,
    Step3,
    Step4,
    Step5,
}

/**
 * 온보딩 등에서 쓰는 5칸 dash progress bar.
 *
 * Figma: progress bar (`2044:4743`)
 *
 * @param step 현재까지 활성화할 단계. [HilitStep.Step1]이면 첫 칸만 활성
 * @param modifier 외부 레이아웃 Modifier
 */
@Composable
fun HilitProgressBar(
    step: HilitStep,
    modifier: Modifier = Modifier,
) {
    val activeCount = step.ordinal + 1
    val activeColor = HilitTheme.colors.hilitBlack800
    val inactiveColor = HilitTheme.colors.gray50

    Row(
        modifier =
            modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        repeat(HilitStep.entries.size) { index ->
            val isActive = index < activeCount
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(color = if (isActive) activeColor else inactiveColor),
            )
        }
    }
}

@Preview(
    name = "HilitProgressBar",
    showBackground = true,
    widthDp = 375,
)
@Composable
private fun HilitProgressBarPreview() {
    HilitTheme {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HilitStep.entries.forEach { step ->
                HilitProgressBar(step = step)
            }
        }
    }
}
