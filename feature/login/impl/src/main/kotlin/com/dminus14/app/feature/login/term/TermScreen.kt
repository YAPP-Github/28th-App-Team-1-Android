package com.dminus14.app.feature.login.term

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
import com.dminus14.app.feature.login.api.Onboarding
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun TermScreen(
    onNavigate: (Any) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TermViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(TermIntent.Load)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TermEffect.Agreed -> onNavigate(Onboarding)
            }
        }
    }

    TermContent(
        state = state,
        onAgreeClick = { viewModel.onIntent(TermIntent.ClickAgree) },
        modifier = modifier,
    )
}

@Composable
private fun TermContent(
    state: TermState,
    onAgreeClick: () -> Unit,
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
        Text(text = "약관 동의")
        Button(
            onClick = onAgreeClick,
            enabled = !state.isLoading,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
        ) {
            Text(text = "동의하고 계속")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermContentPreview() {
    HilitTheme {
        TermContent(
            state = TermState(),
            onAgreeClick = {},
        )
    }
}
