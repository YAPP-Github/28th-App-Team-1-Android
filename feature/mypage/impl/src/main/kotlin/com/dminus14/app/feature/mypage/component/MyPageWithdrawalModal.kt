package com.dminus14.app.feature.mypage.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dminus14.app.feature.mypage.MyPageModalType
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.component.modal.HilitModal
import com.dminus14.designsystem.component.modal.HilitModalType
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 회원 탈퇴 1·2단계 모달.
 *
 * 주의사항(`WithdrawalNotice`)은 삭제 대상 안내 info-field를 포함하고, 최종 확인
 * (`WithdrawalConfirm`)은 되돌릴 수 없다는 경고만 담는다. 실제 탈퇴 처리는 최종 확인의 확인
 * 버튼에서만 실행된다. [isSubmitting]인 동안에는 두 버튼 모두 비활성으로 잠근다.
 */
@Composable
internal fun MyPageWithdrawalModal(
    type: MyPageModalType,
    isSubmitting: Boolean,
    onConfirm: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val content =
        when (type) {
            MyPageModalType.WithdrawalNotice -> WithdrawalContentSpec.Notice
            MyPageModalType.WithdrawalConfirm -> WithdrawalContentSpec.Confirm
            else -> return
        }

    HilitModal(
        type = content.hilitModalType,
        title = content.title,
        subtitle = content.subtitle,
        infoText = content.infoText,
        dismissible = false,
        onDismiss = onClose,
        modifier = modifier,
        buttons = {
            HilitFixedBottomDualButton(
                leftText = "취소",
                rightText = content.confirmText,
                type = HilitFixedBottomDualButtonType.TwoColor,
                leftEnabled = !isSubmitting,
                rightEnabled = !isSubmitting,
                onLeftClick = onClose,
                onRightClick = onConfirm,
            )
        },
    )
}

private data class WithdrawalContentSpec(
    val hilitModalType: HilitModalType,
    val title: String,
    val subtitle: String?,
    val infoText: String?,
    val confirmText: String,
) {
    companion object {
        val Notice =
            WithdrawalContentSpec(
                hilitModalType = HilitModalType.InvisibleIcon,
                title = "정말 탈퇴하시겠어요?",
                subtitle = "탈퇴하면 아래 정보가 모두 삭제되고\n복구할 수 없어요.",
                infoText = "포트폴리오, 모든 면접 리포트, 지인 피드백 기록, 프로필, 잔여 면접 횟수",
                confirmText = "탈퇴하기",
            )
        val Confirm =
            WithdrawalContentSpec(
                hilitModalType = HilitModalType.Default,
                title = "탈퇴를 진행할까요?",
                subtitle = "이 동작은 되돌릴 수 없어요.",
                infoText = null,
                confirmText = "탈퇴",
            )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageWithdrawalNoticeModalPreview() {
    HilitTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MyPageWithdrawalModal(
                type = MyPageModalType.WithdrawalNotice,
                isSubmitting = false,
                onConfirm = {},
                onClose = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageWithdrawalConfirmModalPreview() {
    HilitTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MyPageWithdrawalModal(
                type = MyPageModalType.WithdrawalConfirm,
                isSubmitting = false,
                onConfirm = {},
                onClose = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MyPageWithdrawalConfirmSubmittingModalPreview() {
    HilitTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MyPageWithdrawalModal(
                type = MyPageModalType.WithdrawalConfirm,
                isSubmitting = true,
                onConfirm = {},
                onClose = {},
            )
        }
    }
}
