package com.dminus14.app.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dminus14.app.feature.mypage.MyPageModalType
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.theme.HilitTheme

@Composable
@Suppress("LongMethod")
internal fun MyPageModal(
    type: MyPageModalType,
    onConfirm: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val content = type.content()
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        Column(
            modifier =
                modifier
                    .widthIn(
                        max = 327.dp,
                    ).fillMaxWidth()
                    .background(HilitTheme.colors.hilitWhite),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = content.title,
                    modifier = Modifier.fillMaxWidth(),
                    style = HilitTheme.typography.head4,
                    color = HilitTheme.colors.hilitBlack800,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = content.message,
                    modifier = Modifier.fillMaxWidth(),
                    style = HilitTheme.typography.body4,
                    color = HilitTheme.colors.gray600,
                    textAlign = TextAlign.Center,
                )
                content.notice?.let { notice ->
                    val noticeBackground =
                        if (content.isErrorNotice) {
                            HilitTheme.colors.error200
                        } else {
                            HilitTheme.colors.gray50
                        }
                    val noticeBorder =
                        if (content.isErrorNotice) {
                            HilitTheme.colors.error300
                        } else {
                            HilitTheme.colors.gray100
                        }
                    val noticeText =
                        if (content.isErrorNotice) {
                            HilitTheme.colors.error500
                        } else {
                            HilitTheme.colors.gray700
                        }
                    Text(
                        text = notice,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(noticeBackground)
                                .border(
                                    1.dp,
                                    noticeBorder,
                                ).padding(12.dp),
                        style = HilitTheme.typography.body6,
                        color = noticeText,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            HilitFixedBottomDualButton(
                leftText = content.leftText,
                rightText = content.rightText,
                type = HilitFixedBottomDualButtonType.TwoColor,
                onLeftClick = {
                    onConfirm()
                    onClose()
                },
                onRightClick = onClose,
            )
        }
    }
}

private data class ModalContent(
    val title: String,
    val message: String,
    val notice: String? = null,
    val isErrorNotice: Boolean = false,
    val leftText: String = "닫기",
    val rightText: String = "확인",
)

private fun MyPageModalType.content(): ModalContent =
    when (this) {
        MyPageModalType.PortfolioReupload -> {
            ModalContent(
                title = "포트폴리오를\n새로 업로드하시겠어요?",
                message = "한 달에 한 번만 포트폴리오를\n새로 업로드할 수 있어요.",
                notice = "이번달 남은 기회 1번",
            )
        }

        MyPageModalType.PortfolioDelete -> {
            ModalContent(
                title = "포트폴리오를 삭제하시겠어요?",
                message = "포트폴리오 파일이 삭제되어도\n지난 면접 레포트는 그대로 남아요.",
                notice = "이번달 남은 삭제 기회 1번",
            )
        }

        MyPageModalType.PortfolioDeleteUnavailable -> {
            ModalContent(
                title = "포트폴리오를 삭제할 수 없어요",
                message = "현재 면접이 진행되고 있어요.\n면접이 끝나면 다시 삭제를 시도해주세요.",
            )
        }

        MyPageModalType.PortfolioReuploadUnavailable -> {
            ModalContent(
                title = "포트폴리오를\n새로 업로드할 수 없어요.",
                message = "한 달에 한 번만 포트폴리오를 업로드할 수 있어요.",
                notice = "이번달 남은 기회 0번",
                isErrorNotice = true,
            )
        }

        MyPageModalType.Logout -> {
            ModalContent(
                title = "로그아웃하시겠어요?",
                message = "언제든 다시 로그인할 수 있어요.",
                leftText = "취소",
                rightText = "로그아웃",
            )
        }
    }

@Preview(showBackground = true)
@Composable
private fun MyPagePortfolioModalPreview() {
    HilitTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MyPageModal(type = MyPageModalType.PortfolioReupload, onConfirm = {}, onClose = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPagePortfolioDeleteModalPreview() {
    HilitTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MyPageModal(type = MyPageModalType.PortfolioDelete, onConfirm = {}, onClose = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageDeleteUnavailableModalPreview() {
    HilitTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MyPageModal(
                type = MyPageModalType.PortfolioDeleteUnavailable,
                onConfirm = {},
                onClose = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageReuploadUnavailableModalPreview() {
    HilitTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MyPageModal(
                type = MyPageModalType.PortfolioReuploadUnavailable,
                onConfirm = {},
                onClose = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageLogoutModalPreview() {
    HilitTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MyPageModal(type = MyPageModalType.Logout, onConfirm = {}, onClose = {})
        }
    }
}
