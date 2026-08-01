package com.dminus14.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 카카오 로그인 전용 Large 버튼.
 *
 * Figma: ButtonLargeLogin (kakao)
 * - 배경: kakao `#FEE500`
 * - 텍스트: `hilitBlack900` + `sub7`
 * - 아이콘: [HilitIconAsset.KakaoLogo] 24dp
 */
@Composable
fun KakaoLoginButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = KakaoYellow, shape = RectangleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                ).padding(
                    horizontal = KakaoLoginButtonHorizontalPadding,
                    vertical = KakaoLoginButtonVerticalPadding,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(KakaoLoginButtonContentGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HilitIcon(
                asset = HilitIconAsset.KakaoLogo,
                contentDescription = null,
                modifier = Modifier.size(HilitIconAsset.KakaoLogo.defaultSize),
            )
            Text(
                text = "카카오톡으로 로그인",
                style = HilitTheme.typography.sub7,
                color = HilitTheme.colors.hilitBlack900,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** 카카오 브랜드 옐로우. Figma `kakao` 토큰. */
private val KakaoYellow = Color(0xFFFEE500)

private val KakaoLoginButtonHorizontalPadding = 8.dp
private val KakaoLoginButtonVerticalPadding = (16.5).dp
private val KakaoLoginButtonContentGap = 8.dp

@Preview(name = "KakaoLoginButton")
@Composable
private fun KakaoLoginButtonPreview() {
    HilitTheme {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            KakaoLoginButton(
                onClick = {},
            )
        }
    }
}
