package com.dminus14.app.feature.login.suspension

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.fill_warning
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun SuspensionNoticeScreen(
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    SuspensionNoticeContent(
        onSendMailClick = {
            val intent =
                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                }
            runCatching { context.startActivity(intent) }
        },
        onHomeClick = onHomeClick,
        modifier = modifier,
    )
}

@Composable
private fun SuspensionNoticeContent(
    onSendMailClick: () -> Unit,
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
            SuspensionNoticeBody()
        }

        HilitFixedBottomDualButton(
            leftText = "메일 보내기",
            rightText = "홈으로",
            onLeftClick = onSendMailClick,
            onRightClick = onHomeClick,
        )
    }
}

@Composable
private fun SuspensionNoticeBody() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.padding(horizontal = 20.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.fill_warning),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(SuspensionNoticeIconSize),
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "면접 이용이 제한되었어요",
                style = HilitTheme.typography.sub1,
                color = HilitTheme.colors.hilitBlack800,
                textAlign = TextAlign.Center,
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "비정상적인 이용 패턴이 반복 확인되어 면접 시작이\n제한되었어요. 궁금한 점은 아래로 문의해주세요.",
                    style = HilitTheme.typography.body4,
                    color = HilitTheme.colors.gray500,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = "문의 : [aiinterview.hilit@gmail.com]",
                    style = HilitTheme.typography.body4,
                    color = HilitTheme.colors.gray500,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private val SuspensionNoticeIconSize = 24.dp

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun SuspensionNoticeContentPreview() {
    HilitTheme {
        SuspensionNoticeContent(
            onSendMailClick = {},
            onHomeClick = {},
        )
    }
}
