package com.dminus14.designsystem.component.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

/** Figma `modal` 컴포넌트 variant. */
enum class HilitModalType {
    /** 아이콘 + 타이틀 + 서브텍스트 + info-field + 버튼 */
    Max,

    /** 아이콘 + 타이틀 + 서브텍스트 + 버튼 */
    InvisibleInfo,

    /** 타이틀 + 서브텍스트 + info-field + 버튼 */
    InvisibleIcon,

    /** 타이틀 + 서브텍스트 + 버튼 */
    Default,
}

private val ContentSectionSpacing = 20.dp
private val TitleSubtitleSpacing = 4.dp
private val InfoFieldPaddingHorizontal = 14.dp
private val InfoFieldPaddingVertical = 12.dp
private val InfoFieldIconSpacing = 8.dp
private val BookIllustrationSize = 74.dp
private val BookIllustrationBadgeWidth = 55.dp
private val BookIllustrationBadgeHeight = 46.dp
private val BookIllustrationBadgeOffsetX = 17.dp
private val BookIllustrationIconWidth = 63.dp
private val BookIllustrationIconHeight = 67.dp
private val BookIllustrationIconOffsetX = 4.dp
private val BookIllustrationIconOffsetY = 10.dp

/**
 * Figma `modal` preset.
 *
 * [HilitModalType]에 따라 아이콘·info-field 노출을 제어하고, 하단 버튼은 [buttons] 슬롯으로 받는다.
 */
@Composable
fun HilitModal(
    type: HilitModalType,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    infoText: String? = null,
    dismissible: Boolean = true,
    onDismiss: () -> Unit = {},
    graphic: @Composable (() -> Unit)? = null,
    buttons: @Composable () -> Unit,
) {
    HilitModalScaffold(
        dismissible = dismissible,
        onDismiss = onDismiss,
        modifier = modifier,
        content = {
            HilitModalContent(
                type = type,
                title = title,
                subtitle = subtitle,
                infoText = infoText,
                graphic = graphic,
            )
        },
        buttons = buttons,
    )
}

/**
 * 단일 확인 버튼 [HilitModal] convenience API.
 */
@Composable
fun HilitModal(
    type: HilitModalType,
    title: String,
    confirmText: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    infoText: String? = null,
    dismissible: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit = {},
    graphic: @Composable (() -> Unit)? = null,
) {
    HilitModal(
        type = type,
        title = title,
        subtitle = subtitle,
        infoText = infoText,
        dismissible = dismissible,
        onDismiss = onDismiss,
        graphic = graphic,
        modifier = modifier,
        buttons = {
            HilitFixedBottomButton(
                text = confirmText,
                type = HilitButtonType.Light,
                onClick = onConfirm,
            )
        },
    )
}

/** book/74px(Figma `435:487`): 초록 배지 위에 책 라인아트를 겹친 일러스트. */
@Composable
fun HilitBookIllustration(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(BookIllustrationSize)) {
        Box(
            modifier =
                Modifier
                    .offset(x = BookIllustrationBadgeOffsetX)
                    .size(width = BookIllustrationBadgeWidth, height = BookIllustrationBadgeHeight)
                    .background(HilitTheme.colors.hilitGreen500),
        )

        HilitIcon(
            asset = HilitIconAsset.Book,
            contentDescription = null,
            modifier =
                Modifier
                    .offset(x = BookIllustrationIconOffsetX, y = BookIllustrationIconOffsetY)
                    .size(width = BookIllustrationIconWidth, height = BookIllustrationIconHeight),
        )
    }
}

/** Figma `info-field`: 회색 배경에 info 아이콘과 보조 텍스트. */
@Composable
private fun HilitModalInfoField(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(HilitTheme.colors.gray100)
                .padding(
                    horizontal = InfoFieldPaddingHorizontal,
                    vertical = InfoFieldPaddingVertical,
                ),
        horizontalArrangement = Arrangement.spacedBy(InfoFieldIconSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HilitIcon(
            asset = HilitIconAsset.Info,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = HilitTheme.colors.gray700,
            style = HilitTheme.typography.body9,
        )
    }
}

@Composable
private fun HilitModalContent(
    type: HilitModalType,
    title: String,
    subtitle: String?,
    infoText: String?,
    graphic: @Composable (() -> Unit)?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ContentSectionSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (type.showsGraphic()) {
            graphic?.invoke()
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(TitleSubtitleSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                color = HilitTheme.colors.gray800,
                style = HilitTheme.typography.sub4,
                textAlign = TextAlign.Center,
            )
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    modifier = Modifier.fillMaxWidth(),
                    color = HilitTheme.colors.gray500,
                    style = HilitTheme.typography.body6,
                    textAlign = TextAlign.Center,
                )
            }
        }

        if (type.showsInfoBox() && !infoText.isNullOrEmpty()) {
            HilitModalInfoField(text = infoText)
        }
    }
}

private fun HilitModalType.showsGraphic(): Boolean =
    when (this) {
        HilitModalType.Max,
        HilitModalType.InvisibleInfo,
        -> true

        HilitModalType.InvisibleIcon,
        HilitModalType.Default,
        -> false
    }

private fun HilitModalType.showsInfoBox(): Boolean =
    when (this) {
        HilitModalType.Max,
        HilitModalType.InvisibleIcon,
        -> true

        HilitModalType.InvisibleInfo,
        HilitModalType.Default,
        -> false
    }

@Preview(name = "HilitModal Max", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HilitModalMaxPreview() {
    HilitModalPreviewHost {
        HilitModal(
            type = HilitModalType.Max,
            title = "텍스트를 입력해주세요",
            subtitle = "서브텍스트를 입력해주세요",
            infoText = "합성 안내 문구입니다.",
            confirmText = "확인",
            graphic = { HilitBookIllustration() },
            onConfirm = {},
        )
    }
}

@Preview(name = "HilitModal InvisibleInfo", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HilitModalInvisibleInfoPreview() {
    HilitModalPreviewHost {
        HilitModal(
            type = HilitModalType.InvisibleInfo,
            title = "기존에 있는 포트폴리오로\n진행할까요?",
            confirmText = "확인",
            dismissible = false,
            graphic = { HilitBookIllustration() },
            onConfirm = {},
        )
    }
}

@Preview(name = "HilitModal InvisibleIcon", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HilitModalInvisibleIconPreview() {
    HilitModalPreviewHost {
        HilitModal(
            type = HilitModalType.InvisibleIcon,
            title = "텍스트를 입력해주세요",
            subtitle = "서브텍스트를 입력해주세요",
            infoText = "합성 안내 문구입니다.",
            confirmText = "확인",
            onConfirm = {},
        )
    }
}

@Preview(name = "HilitModal Default", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HilitModalDefaultPreview() {
    HilitModalPreviewHost {
        HilitModal(
            type = HilitModalType.Default,
            title = "텍스트를 입력해주세요",
            subtitle = "서브텍스트를 입력해주세요",
            confirmText = "확인",
            onConfirm = {},
        )
    }
}

@Preview(name = "HilitModal DualButton", showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HilitModalDualButtonPreview() {
    HilitModalPreviewHost {
        HilitModal(
            type = HilitModalType.InvisibleInfo,
            title = "기존에 있는 포트폴리오로\n진행할까요?",
            dismissible = false,
            graphic = { HilitBookIllustration() },
            buttons = {
                HilitFixedBottomDualButton(
                    leftText = "새로 업로드",
                    rightText = "기존 포트폴리오 사용",
                    type = HilitFixedBottomDualButtonType.Default,
                    onLeftClick = {},
                    onRightClick = {},
                )
            },
        )
    }
}

@Composable
fun HilitModalPreviewHost(content: @Composable () -> Unit) {
    HilitTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}
