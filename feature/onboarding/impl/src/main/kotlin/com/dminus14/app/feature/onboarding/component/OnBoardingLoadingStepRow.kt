package com.dminus14.app.feature.onboarding.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.onboarding.OnBoardingLoadingStepStatus
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.loading.HilitLoadingIndicator
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 온보딩 인터뷰 로딩 화면에서 쓰는 "대기 중/진행 중/완료" 체크리스트 한 줄.
 *
 * 진행 중은 [HilitLoadingIndicator], 완료는 [HilitIconAsset.Success](Figma `success/16px/black`,
 * 443:9823)를 재사용한다. 대기 중은 designsystem에 없는 정적 회색 링이라 이 화면 전용으로 둔다.
 */
private val IndicatorSize = 16.dp
private val WaitingIndicatorStrokeWidth = 1.5.dp
private val IndicatorTextGap = 10.dp

@Composable
fun OnBoardingLoadingStepRow(
    text: String,
    status: OnBoardingLoadingStepStatus,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(IndicatorTextGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OnBoardingLoadingStepIndicator(status = status)

        Text(
            text = text,
            style = HilitTheme.typography.body3,
            color = HilitTheme.colors.hilitWhite,
            maxLines = 1,
        )
    }
}

@Composable
private fun OnBoardingLoadingStepIndicator(status: OnBoardingLoadingStepStatus) {
    when (status) {
        OnBoardingLoadingStepStatus.Waiting -> {
            LoadingStepIndicatorWaiting()
        }

        OnBoardingLoadingStepStatus.InProgress -> {
            HilitLoadingIndicator(size = IndicatorSize)
        }

        OnBoardingLoadingStepStatus.Completed -> {
            HilitIcon(
                asset = HilitIconAsset.Success,
                contentDescription = null,
                tint = HilitTheme.colors.hilitGreen500,
                modifier = Modifier.size(IndicatorSize),
            )
        }
    }
}

@Composable
private fun LoadingStepIndicatorWaiting(modifier: Modifier = Modifier) {
    val trackColor = HilitTheme.colors.gray700
    Canvas(modifier = modifier.size(IndicatorSize)) {
        drawCircle(
            color = trackColor,
            style = Stroke(width = WaitingIndicatorStrokeWidth.toPx()),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1B1F, widthDp = 250)
@Composable
private fun OnBoardingLoadingStepRowPreview() {
    HilitTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OnBoardingLoadingStepRow(
                text = "기본정보 분석 중",
                status = OnBoardingLoadingStepStatus.Completed,
            )
            OnBoardingLoadingStepRow(
                text = "채용 정보 분석 중",
                status = OnBoardingLoadingStepStatus.InProgress,
            )
            OnBoardingLoadingStepRow(
                text = "나의 포폴 분석 중",
                status = OnBoardingLoadingStepStatus.Waiting,
            )
        }
    }
}
