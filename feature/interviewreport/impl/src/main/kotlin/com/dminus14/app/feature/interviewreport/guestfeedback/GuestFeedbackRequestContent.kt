package com.dminus14.app.feature.interviewreport.guestfeedback

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dminus14.app.domain.model.GuestFeedbackAxisCode
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.toggle.HilitToggle
import com.dminus14.designsystem.theme.HilitTheme

/** 시안(443:8088) 순서에 맞춘 평가 항목 행 정의. */
private data class AxisRow(
    val axis: GuestFeedbackAxisCode,
    val label: String,
    val icon: HilitIconAsset,
)

private val AXIS_ROWS =
    listOf(
        AxisRow(GuestFeedbackAxisCode.GAZE, "시선", HilitIconAsset.Eyes),
        AxisRow(GuestFeedbackAxisCode.EXPRESSION, "표정", HilitIconAsset.Face),
        AxisRow(GuestFeedbackAxisCode.POSTURE, "자세", HilitIconAsset.Body),
        AxisRow(GuestFeedbackAxisCode.GESTURE, "손동작", HilitIconAsset.Hand),
        AxisRow(GuestFeedbackAxisCode.VOICE, "목소리", HilitIconAsset.Voice),
    )

/**
 * 지인 피드백 항목선택 화면 순수 UI (Figma Node: 443:8006). State 만 받아 렌더한다.
 */
@Composable
internal fun GuestFeedbackRequestContent(
    state: GuestFeedbackRequestState,
    onIntent: (GuestFeedbackRequestIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HilitTheme.colors
    Box(modifier = modifier.fillMaxSize().background(colors.hilitBlack900)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HilitIcon(
                    asset = HilitIconAsset.Cancel,
                    contentDescription = "닫기",
                    tint = colors.hilitWhite,
                    modifier =
                        Modifier.clickable {
                            onIntent(
                                GuestFeedbackRequestIntent.ClickClose,
                            )
                        },
                )
            }
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
            ) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "지인에게 어떤 항목을\n평가받을까요?",
                    style = HilitTheme.typography.sub4,
                    color = colors.hilitWhite,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "지인은 면접 태도를 중심으로 평가합니다.\n평가받고 싶은 항목을 선택해 주세요.",
                    style = HilitTheme.typography.body7,
                    color = colors.gray300,
                )
                Spacer(Modifier.height(24.dp))
                AXIS_ROWS.forEachIndexed { index, row ->
                    AxisToggleRow(
                        row = row,
                        checked = row.axis in state.selectedAxes,
                        onToggle = { onIntent(GuestFeedbackRequestIntent.ToggleAxis(row.axis)) },
                    )
                    if (index != AXIS_ROWS.lastIndex) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .height(1.dp)
                                .background(colors.gray800),
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
            HilitFixedBottomButton(
                text = if (state.hasActiveShare) "피드백 종료하기" else "피드백 링크 생성",
                // Figma 443:8044 실제 버튼은 짙은 회색(gray900) 배경 + 흰 글자로, Dark 타입(흰 배경)이
                // 아니라 Light 타입(활성 시 hilitBlack800 배경, 누를 때 gray900)과 일치한다. 이 앱의
                // 다른 화면 CTA 버튼도 전부 Light 를 쓴다 — Dark 타입은 이 파일에서만 잘못 쓰이고 있었음.
                type = HilitButtonType.Light,
                enabled =
                    !state.submitting &&
                        (state.hasActiveShare || state.selectedAxes.isNotEmpty()),
                onClick = { onIntent(GuestFeedbackRequestIntent.ClickSubmit) },
            )
        }

        if (state.linkCopied) {
            LinkCopiedModal()
        } else if (state.shareLink != null) {
            LinkCreatedModal(onCopy = { onIntent(GuestFeedbackRequestIntent.ClickCopyLink) })
        }
    }
}

@Composable
private fun AxisToggleRow(
    row: AxisRow,
    checked: Boolean,
    onToggle: () -> Unit,
) {
    val colors = HilitTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HilitIcon(
            asset = row.icon,
            contentDescription = null,
            tint = colors.gray50,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = row.label,
            style = HilitTheme.typography.sub7,
            color = colors.hilitWhite,
            modifier = Modifier.weight(1f),
        )
        HilitToggle(checked = checked, onCheckedChange = { onToggle() })
    }
}

@Composable
private fun LinkCreatedModal(onCopy: () -> Unit) {
    ModalScrim {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .background(HilitTheme.colors.hilitWhite),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                HilitIcon(
                    asset = HilitIconAsset.Link,
                    contentDescription = null,
                    modifier = Modifier.size(74.dp),
                )
                Text(
                    text = "링크 생성 완료!\n지인에게 보내보세요.",
                    style = HilitTheme.typography.sub4,
                    // TODO(designsystem): Figma #262A30 대응 토큰 확정 필요, 임시 gray900.
                    color = HilitTheme.colors.gray900,
                    textAlign = TextAlign.Center,
                )
            }
            Text(
                text = "링크 복사하기",
                style = HilitTheme.typography.sub7,
                color = HilitTheme.colors.hilitWhite,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(HilitTheme.colors.hilitBlack800)
                        .clickable(onClick = onCopy)
                        .padding(vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun LinkCopiedModal() {
    ModalScrim {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .background(HilitTheme.colors.hilitWhite)
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HilitIcon(
                asset = HilitIconAsset.Link,
                contentDescription = null,
                modifier = Modifier.size(74.dp),
            )
            Text(
                text = "링크 복사 완료",
                style = HilitTheme.typography.body6,
                color = HilitTheme.colors.gray500,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "링크를 지인에게 보내보세요!",
                style = HilitTheme.typography.sub4,
                color = HilitTheme.colors.hilitBlack800,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ModalScrim(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.65f)),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
