package com.dminus14.app.feature.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.designsystem.component.topbar.HilitIconTopBar
import com.dminus14.designsystem.component.topbar.TopBarType
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun OnBoardingInterviewScreen(
    onNavigate: (Any) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnBoardingInterviewViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(OnBoardingInterviewIntent.Load)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnBoardingInterviewEffect.CloseRequested -> onClose()
            }
        }
    }

    OnBoardingInterviewContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
private fun OnBoardingInterviewContent(
    state: OnBoardingInterviewState,
    onIntent: (OnBoardingInterviewIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        HilitIconTopBar(
            type = TopBarType.HideRight,
            title = "",
            onLeftClick = { onIntent(OnBoardingInterviewIntent.CloseClick) },
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "OnBoarding Interview",
                style = HilitTheme.typography.head3,
                color = HilitTheme.colors.hilitBlack800,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnBoardingInterviewContentPreview() {
    HilitTheme {
        OnBoardingInterviewContent(
            state = OnBoardingInterviewState(),
            onIntent = {},
        )
    }
}
