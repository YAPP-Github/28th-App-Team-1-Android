package com.dminus14.app.feature.login

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.feature.login.kakao.KakaoLoginClient
import com.dminus14.app.feature.main.api.MainHome
import com.dminus14.designsystem.theme.DMinusTheme
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigate: (Any) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()
    val kakaoLoginClient =
        EntryPointAccessors
            .fromApplication(
                context.applicationContext,
                KakaoLoginClientEntryPoint::class.java,
            ).kakaoLoginClient()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToHome -> onNavigate(MainHome)
            }
        }
    }

    LoginContent(
        state = state,
        onKakaoLoginClick = {
            viewModel.onIntent(LoginIntent.ClickKakaoLogin)
            scope.launch {
                runCatching { kakaoLoginClient.login(activity) }
                    .onSuccess { credential ->
                        viewModel.onIntent(LoginIntent.KakaoLoginSucceeded(credential))
                    }.onFailure { error ->
                        viewModel.onIntent(LoginIntent.KakaoLoginFailed(error))
                    }
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun LoginContent(
    state: LoginState,
    onKakaoLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "D-14",
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "면접 준비를 시작해 보세요",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onKakaoLoginClick,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = KakaoYellow,
                        contentColor = KakaoBrown,
                    ),
            ) {
                Text(text = "카카오 로그인")
            }

            state.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}

private val KakaoYellow = Color(0xFFFEE500)
private val KakaoBrown = Color(0xFF191919)

@EntryPoint
@InstallIn(SingletonComponent::class)
interface KakaoLoginClientEntryPoint {
    fun kakaoLoginClient(): KakaoLoginClient
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    DMinusTheme {
        LoginContent(
            state = LoginState(),
            onKakaoLoginClick = {},
        )
    }
}
