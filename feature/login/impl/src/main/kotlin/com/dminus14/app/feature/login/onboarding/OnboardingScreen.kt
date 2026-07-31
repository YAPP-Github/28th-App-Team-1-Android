package com.dminus14.app.feature.login.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.feature.login.api.Login
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun OnboardingScreen(
    onNavigate: (Any) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(OnboardingIntent.Load)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                OnboardingEffect.Completed -> onNavigate(Login)
            }
        }
    }

    OnboardingContent(
        state = state,
        onCompleteClick = { viewModel.onIntent(OnboardingIntent.ClickComplete) },
        modifier = modifier,
    )
}

@Composable
private fun OnboardingContent(
    state: OnboardingState,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "온보딩")
        Button(
            onClick = onCompleteClick,
            enabled = !state.isLoading,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
        ) {
            Text(text = "시작하기")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingContentPreview() {
    HilitTheme {
        OnboardingContent(
            state = OnboardingState(),
            onCompleteClick = {},
        )
    }
}
