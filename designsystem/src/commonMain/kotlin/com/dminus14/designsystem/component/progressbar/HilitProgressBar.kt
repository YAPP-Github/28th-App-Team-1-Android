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

/**
 * 온보딩 등에서 쓰는 dash progress bar.
 *
 * @param step 현재까지 활성화할 단계(1부터 [maxStep]까지). 1이면 첫 칸만 활성
 * @param maxStep 전체 칸 수. 1 이상이어야 한다
 * @param modifier 외부 레이아웃 Modifier
 */
@Composable
fun HilitProgressBar(
    step: Int,
    maxStep: Int,
    modifier: Modifier = Modifier,
) {
    val totalSteps = maxStep.coerceAtLeast(1)
    val activeCount = step.coerceIn(1, totalSteps)
    val activeColor = HilitTheme.colors.hilitBlack800
    val inactiveColor = HilitTheme.colors.gray50

    Row(
        modifier =
            modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        repeat(totalSteps) { index ->
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
            (1..5).forEach { step ->
                HilitProgressBar(step = step, maxStep = 5)
            }
        }
    }
}
