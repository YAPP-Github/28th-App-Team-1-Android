package com.dminus14.app.feature.login.suspension

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.text.HilitText
import com.dminus14.designsystem.component.text.HilitTextHighlightColor
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 어뷰징 탐지로 면접 이용이 제한된 사용자에게 노출되는 안내 화면.
 *
 * Figma Node: 2211:10511 (Account_Suspension Notice)
 */
@Composable
fun SuspensionNoticeScreen(
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    SuspensionNoticeContent(
        onSendMailClick = {
            val intent =
                Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:$SUPPORT_EMAIL".toUri()
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL))
                }
            val launched = runCatching { context.startActivity(intent) }.isSuccess
            if (!launched) {
                clipboardManager.setText(AnnotatedString(SUPPORT_EMAIL))
                Toast
                    .makeText(
                        context,
                        "메일 앱을 열 수 없어 문의 이메일 주소를 복사했어요.",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
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
        verticalArrangement = Arrangement.spacedBy(SuspensionNoticeSectionSpacing),
        modifier = Modifier.padding(horizontal = SuspensionNoticeHorizontalPadding),
    ) {
        SuspensionNoticeIcon()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SuspensionNoticeTitleToBodySpacing),
        ) {
            HilitText(
                text =
                    buildAnnotatedString {
                        append("면접 이용이 ")
                        withHilitTextHighlight { append("제한") }
                        append(" 됐어요")
                    },
                style = HilitTheme.typography.head3.copy(textAlign = TextAlign.Center),
                color = HilitTheme.colors.hilitBlack800,
                highlightColor = HilitTextHighlightColor.Red,
            )
            Text(
                text = "비정상적인 이용 패턴이 반복 확인되어 면접 시작이\n제한되었어요. 궁금한 점은 아래로 문의해주세요.",
                style = HilitTheme.typography.body4,
                color = HilitTheme.colors.gray500,
                textAlign = TextAlign.Center,
            )
        }

        SuspensionNoticeInquiryField()
    }
}

/**
 * 상단 아이콘. Figma `problem/54px` — [HilitTheme.colors.hilitBlack800] 타일에
 * [HilitIconAsset.AnalyzeProblem]("!" 말풍선) 글리프를 얹은 형태다.
 */
@Composable
private fun SuspensionNoticeIcon() {
    Box(
        modifier =
            Modifier
                .size(SuspensionNoticeIconTileSize)
                .background(HilitTheme.colors.hilitBlack800),
        contentAlignment = Alignment.Center,
    ) {
        HilitIcon(
            asset = HilitIconAsset.AnalyzeProblem,
            contentDescription = null,
            modifier = Modifier.size(HilitIconAsset.AnalyzeProblem.defaultSize),
        )
    }
}

/**
 * "문의 : [이메일]" 안내 박스. `feature:mypage:impl` 의 `ModalNotice`와 사실상 동일한 시안이라
 * Row + [HilitIconAsset.Info] + 본문 텍스트 구성을 따른다.
 */
@Composable
private fun SuspensionNoticeInquiryField() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SuspensionNoticeInquiryGap),
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .background(HilitTheme.colors.gray100)
                .padding(
                    horizontal = SuspensionNoticeInquiryHorizontalPadding,
                    vertical = SuspensionNoticeInquiryVerticalPadding,
                ),
    ) {
        HilitIcon(
            asset = HilitIconAsset.Info,
            contentDescription = "안내",
            tint = HilitTheme.colors.hilitBlack800,
            modifier = Modifier.size(HilitIconAsset.Info.defaultSize),
        )
        Text(
            text = "문의 : [$SUPPORT_EMAIL]",
            style = HilitTheme.typography.body9,
            color = HilitTheme.colors.gray700,
        )
    }
}

/** 정지 안내 화면 문의 수신 이메일. */
private const val SUPPORT_EMAIL = "team@hilit.my"

private val SuspensionNoticeHorizontalPadding = 20.dp
private val SuspensionNoticeSectionSpacing = 24.dp
private val SuspensionNoticeTitleToBodySpacing = 4.dp
private val SuspensionNoticeIconTileSize = 54.dp
private val SuspensionNoticeInquiryGap = 8.dp
private val SuspensionNoticeInquiryHorizontalPadding = 14.dp
private val SuspensionNoticeInquiryVerticalPadding = 12.dp

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
