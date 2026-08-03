package com.dminus14.app.feature.login.onboarding.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.dminus14.app.core.resources.success_register_done
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.painterResource

private val IconSize = 100.dp
private val IconToTitleSpacing = 20.dp
private val TitleToSubtitleSpacing = 4.dp

@Composable
fun OnboardingRegisterDoneStep(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.success_register_done),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(IconSize),
        )

        Text(
            text = "등록이 완료됐어요!",
            style = HilitTheme.typography.sub1,
            color = HilitTheme.colors.hilitBlack800,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = IconToTitleSpacing),
        )

        Text(
            text = "Hilit과 면접 여정을 시작해보세요",
            style = HilitTheme.typography.body4,
            color = HilitTheme.colors.gray500,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = TitleToSubtitleSpacing),
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun OnboardingRegisterDoneStepPreview() {
    HilitTheme {
        OnboardingRegisterDoneStep()
    }
}
