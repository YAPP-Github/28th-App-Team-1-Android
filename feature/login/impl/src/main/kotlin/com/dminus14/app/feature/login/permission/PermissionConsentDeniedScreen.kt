package com.dminus14.app.feature.login.permission

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.opp_o
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun PermissionConsentDeniedScreen(
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PermissionConsentDeniedContent(
        onHomeClick = onHomeClick,
        modifier = modifier,
    )
}

@Composable
private fun PermissionConsentDeniedContent(
    onHomeClick: () -> Unit,
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
                Image(
                    painter = painterResource(Res.drawable.opp_o),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(PermissionConsentDeniedIconSize),
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "권한 없이도 둘러볼 수 있어요",
                        style = HilitTheme.typography.sub1,
                        color = HilitTheme.colors.hilitBlack800,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = "지금은 권한 없이 홈으로 이동해요.\n면접을 시작할 때 카메라·마이크 권한을 다시 요청할게요",
                        style = HilitTheme.typography.body4,
                        color = HilitTheme.colors.gray500,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        HilitFixedBottomButton(
            text = "홈으로",
            type = HilitButtonType.Light,
            onClick = onHomeClick,
        )
    }
}

private val PermissionConsentDeniedIconSize = 74.dp

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun PermissionConsentDeniedContentPreview() {
    HilitTheme {
        PermissionConsentDeniedContent(onHomeClick = {})
    }
}
