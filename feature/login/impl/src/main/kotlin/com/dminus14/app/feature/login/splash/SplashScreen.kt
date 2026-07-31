package com.dminus14.app.feature.login.splash

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.hiiii_logo
import com.dminus14.app.feature.home.api.Home
import com.dminus14.app.feature.login.api.Login
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(
    onNavigate: (Any) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.onIntent(SplashIntent.Load)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SplashEffect.SessionExists -> onNavigate(Home)
                SplashEffect.SessionNotFound -> onNavigate(Login)
                SplashEffect.UnknownError -> (context as Activity).finish()
            }
        }
    }

    SplashContent(modifier = modifier)
}

@Composable
private fun SplashContent(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(HilitTheme.colors.hilitWhite),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.hiiii_logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier =
                Modifier
                    .offset(y = (-50).dp)
                    .size(
                        width = 171.dp,
                        height = 72.dp,
                    ),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun SplashContentPreview() {
    HilitTheme {
        SplashContent()
    }
}
