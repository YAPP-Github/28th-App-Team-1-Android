package com.dminus14.app.feature.login.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.dminus14.designsystem.theme.HilitTheme

private const val PREVIEW_SELECTED_JOB_INDEX = 3
private const val PREVIEW_SELECTED_EXPERIENCE_INDEX = 2

@Preview(
    name = "Naming",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun OnboardingNamingPreview() {
    HilitTheme {
        OnboardingContent(
            state = OnboardingState(step = OnboardingStep.Naming),
            onIntent = {},
        )
    }
}

@Preview(
    name = "Naming",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun OnboardingInputNamingPreview() {
    HilitTheme {
        OnboardingContent(
            state =
                OnboardingState(
                    step = OnboardingStep.Naming,
                    name = "재원",
                    isContinueEnabled = true,
                ),
            onIntent = {},
        )
    }
}

@Preview(
    name = "JobUnSelection",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun OnboardingJobUnSelectionPreview() {
    HilitTheme {
        OnboardingContent(
            state =
                OnboardingState(
                    step = OnboardingStep.JobSelection,
                    name = "재원",
                    selectedJobIndex = null,
                    isContinueEnabled = true,
                ),
            onIntent = {},
        )
    }
}

@Preview(
    name = "JobSelection",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun OnboardingJobSelectionPreview() {
    HilitTheme {
        OnboardingContent(
            state =
                OnboardingState(
                    step = OnboardingStep.JobSelection,
                    name = "재원",
                    selectedJobIndex = PREVIEW_SELECTED_JOB_INDEX,
                    isContinueEnabled = true,
                ),
            onIntent = {},
        )
    }
}

@Preview(
    name = "ExperienceSelection",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun OnboardingExperienceSelectionPreview() {
    HilitTheme {
        OnboardingContent(
            state =
                OnboardingState(
                    step = OnboardingStep.ExperienceSelection,
                    name = "재원",
                    selectedJobIndex = PREVIEW_SELECTED_JOB_INDEX,
                    selectedExperienceIndex = PREVIEW_SELECTED_EXPERIENCE_INDEX,
                    isContinueEnabled = true,
                ),
            onIntent = {},
        )
    }
}

@Preview(
    name = "RegisterDone",
    showBackground = true,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun OnboardingRegisterDonePreview() {
    HilitTheme {
        OnboardingContent(
            state = OnboardingState(step = OnboardingStep.RegisterDone),
            onIntent = {},
        )
    }
}
