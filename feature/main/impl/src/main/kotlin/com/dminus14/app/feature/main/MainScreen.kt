package com.dminus14.app.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(MainIntent.Load)
    }

    MainContent(
        state = state,
        modifier = modifier,
    )
}

@Composable
private fun MainContent(
    state: MainState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = state.title,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainContentPreview() {
    HilitTheme {
        MainContent(
            state = MainState(title = "Main Home"),
        )
    }
}
