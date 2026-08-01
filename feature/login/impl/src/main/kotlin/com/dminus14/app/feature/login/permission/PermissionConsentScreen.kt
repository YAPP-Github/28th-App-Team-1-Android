package com.dminus14.app.feature.login.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dminus14.app.feature.login.api.Onboarding
import com.dminus14.app.feature.login.api.PermissionConsentDenied
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

@Composable
fun PermissionConsentScreen(
    onNavigate: (Any) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PermissionConsentViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PermissionConsentEffect.LaterSelected -> onNavigate(PermissionConsentDenied)
                PermissionConsentEffect.AllowSelected -> onNavigate(Onboarding)
            }
        }
    }

    PermissionConsentContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Suppress("UnusedParameter")
@Composable
private fun PermissionConsentContent(
    state: PermissionConsentState,
    onIntent: (PermissionConsentIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(HilitTheme.colors.hilitWhite),
    ) {
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                HilitIcon(
                    asset = HilitIconAsset.Voice,
                    contentDescription = null,
                    modifier = Modifier.size(HilitIconAsset.Voice.defaultSize),
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "면접은 영상과 음성으로 진행돼요",
                        style = HilitTheme.typography.sub1,
                        color = HilitTheme.colors.hilitBlack800,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "면접 답변을 녹화·녹음해 레포트를 만들어요.\n카메라와 마이크 권한이 필요해요.",
                        style = HilitTheme.typography.body4,
                        color = HilitTheme.colors.gray500,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        HilitFixedBottomDualButton(
            leftText = "나중에 하기",
            rightText = "권한 허용하기",
            onLeftClick = { onIntent(PermissionConsentIntent.ClickLater) },
            onRightClick = { onIntent(PermissionConsentIntent.ClickAllow) },
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun PermissionConsentContentPreview() {
    HilitTheme {
        PermissionConsentContent(
            state = PermissionConsentState(),
            onIntent = {},
        )
    }
}
