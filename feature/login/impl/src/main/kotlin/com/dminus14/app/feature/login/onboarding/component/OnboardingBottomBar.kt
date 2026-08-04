package com.dminus14.app.feature.login.onboarding.component

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dminus14.app.feature.login.onboarding.OnboardingIntent
import com.dminus14.app.feature.login.onboarding.OnboardingStep
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType

@Composable
fun OnboardingBottomBar(
    step: OnboardingStep,
    isContinueEnabled: Boolean,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (step) {
        OnboardingStep.Naming -> {
            HilitFixedBottomButton(
                text = "다음",
                enabled = isContinueEnabled,
                type = HilitButtonType.Light,
                onClick = { onIntent(OnboardingIntent.ContinueClick) },
                modifier = modifier.navigationBarsPadding(),
            )
        }

        OnboardingStep.JobSelection -> {
            HilitFixedBottomDualButton(
                leftText = "이전으로",
                rightText = "계속하기",
                leftEnabled = true,
                rightEnabled = isContinueEnabled,
                type = HilitFixedBottomDualButtonType.Default,
                onLeftClick = { onIntent(OnboardingIntent.PreviousClick) },
                onRightClick = { onIntent(OnboardingIntent.ContinueClick) },
                modifier = modifier.navigationBarsPadding(),
            )
        }

        OnboardingStep.ExperienceSelection -> {
            HilitFixedBottomDualButton(
                leftText = "이전으로",
                rightText = "계속하기",
                leftEnabled = true,
                rightEnabled = isContinueEnabled,
                type = HilitFixedBottomDualButtonType.Default,
                onLeftClick = { onIntent(OnboardingIntent.PreviousClick) },
                onRightClick = { onIntent(OnboardingIntent.ContinueClick) },
                modifier = modifier.navigationBarsPadding(),
            )
        }

        OnboardingStep.RegisterDone -> {
            HilitFixedBottomButton(
                text = "시작하기",
                enabled = true,
                type = HilitButtonType.Light,
                onClick = { onIntent(OnboardingIntent.ContinueClick) },
                modifier = modifier.navigationBarsPadding(),
            )
        }
    }
}
