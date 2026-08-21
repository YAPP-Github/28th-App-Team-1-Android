package com.dminus14.app.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.mypage.MyPageProfileUiModel
import com.dminus14.app.feature.mypage.MyPageSocialAccountUiModel
import com.dminus14.designsystem.component.bubblefield.BubbleField
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailAlign
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailEdge
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailShape
import com.dminus14.designsystem.component.bubblefield.BubbleFieldType
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.theme.HilitTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * 사용자 프로필과 계정 정보를 표시한다.
 *
 * Figma Node: 1855:6830
 */
@Composable
@Suppress("LongMethod", "LongParameterList", "UnusedParameter")
internal fun MyPageProfileSection(
    profile: MyPageProfileUiModel?,
    remainingTicketCount: Int?,
    socialAccount: MyPageSocialAccountUiModel?,
    isTicketInfoTooltipVisible: Boolean,
    onProfileEditClick: () -> Unit,
    onTicketInfoClick: () -> Unit,
    onTicketInfoTooltipDismiss: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(isTicketInfoTooltipVisible) {
        if (isTicketInfoTooltipVisible) {
            delay(TICKET_INFO_TOOLTIP_DURATION_MS.milliseconds)
            onTicketInfoTooltipDismiss()
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(HilitTheme.colors.hilitWhite)
                    .border(1.5.dp, HilitTheme.colors.gray100)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (profile == null) {
                    EmptyText()
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = profile.name,
                            color = HilitTheme.colors.hilitBlack800,
                            style = HilitTheme.typography.body1,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = profile.role,
                                style = HilitTheme.typography.body7,
                                color = HilitTheme.colors.gray500,
                            )
                            Text(
                                text = profile.experience,
                                style = HilitTheme.typography.body7,
                                color = HilitTheme.colors.gray500,
                            )
                        }
                    }
                }
                // 2026. 08. 13. 나중에 프로필 편집 UI 생기면 재활성화 필요
//                HilitIcon(
//                    asset = HilitIconAsset.Edit,
//                    contentDescription = "프로필 편집",
//                    tint = HilitTheme.colors.gray500,
//                    modifier =
//                        Modifier
//                            .size(
//                                16.dp,
//                            ).clickable(role = Role.Button, onClick = onProfileEditClick),
//                )
            }

            Box {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                HilitTheme.colors.gray50,
                            ).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        HilitIcon(
                            asset = HilitIconAsset.Coupon,
                            contentDescription = null,
                            tint = HilitTheme.colors.hilitBlack800,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "남은 면접 티켓",
                            style = HilitTheme.typography.body6,
                            color = HilitTheme.colors.gray700,
                        )
                        HilitIcon(
                            asset = HilitIconAsset.Info,
                            contentDescription = "남은 면접 티켓 개수",
                            tint = HilitTheme.colors.gray200,
                            modifier =
                                Modifier
                                    .size(16.dp)
                                    .clickable(role = Role.Button, onClick = onTicketInfoClick),
                        )
                    }
                    HilitTag(
                        colorType = TagColorType.Green,
                        tagType = TagType.Small,
                        text = remainingTicketCount?.let { "${it}회" } ?: "내용 없음",
                    )
                }
                if (isTicketInfoTooltipVisible && remainingTicketCount != null) {
                    BubbleField(
                        text = "${remainingTicketCount}개",
                        type = BubbleFieldType.Small,
                        tailEdge = BubbleFieldTailEdge.Bottom,
                        tailAlign = BubbleFieldTailAlign.Left,
                        tailShape = BubbleFieldTailShape.Left,
                        modifier =
                            Modifier
                                .align(Alignment.TopStart)
                                .offset(x = 60.dp, y = (-44).dp),
                    )
                }
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(HilitTheme.colors.gray50)
                    .border(1.dp, HilitTheme.colors.gray100)
                    .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (socialAccount == null) {
                EmptyText(modifier = Modifier.weight(1f))
            } else {
                if (socialAccount.isKakaoProvider) {
                    HilitIcon(
                        asset = HilitIconAsset.KakaoLogo,
                        contentDescription = socialAccount.providerLabel,
                        modifier = Modifier.size(16.dp),
                    )
                }
                Text(
                    text = socialAccount.displayAccount,
                    modifier = Modifier.weight(1f),
                    style = HilitTheme.typography.body7,
                    color = HilitTheme.colors.gray700,
                )
            }
            Text(
                text = "로그아웃",
                style = HilitTheme.typography.body5,
                color = HilitTheme.colors.gray500,
                modifier =
                    Modifier
                        .clickable(
                            role = Role.Button,
                            onClick = onLogoutClick,
                        ).padding(4.dp),
            )
        }
    }
}

@Composable
private fun EmptyText(modifier: Modifier = Modifier) {
    Text(
        text = "내용 없음",
        modifier = modifier,
        style = HilitTheme.typography.body6,
        color = HilitTheme.colors.gray400,
    )
}

private const val TICKET_INFO_TOOLTIP_DURATION_MS = 3_000L

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun MyPageProfileSectionPreview() {
    HilitTheme {
        MyPageProfileSection(
            profile =
                MyPageProfileUiModel(
                    name = "사용자",
                    role = "Android",
                    experience = "3년차",
                ),
            remainingTicketCount = 3,
            socialAccount =
                MyPageSocialAccountUiModel(
                    providerLabel = "카카오",
                    displayAccount = "sam****@example.com",
                    isKakaoProvider = true,
                ),
            isTicketInfoTooltipVisible = false,
            onProfileEditClick = {},
            onTicketInfoClick = {},
            onTicketInfoTooltipDismiss = {},
            onLogoutClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun MyPageProfileSectionUnknownProviderPreview() {
    HilitTheme {
        MyPageProfileSection(
            profile =
                MyPageProfileUiModel(
                    name = "사용자",
                    role = "Android",
                    experience = "3년차",
                ),
            remainingTicketCount = 3,
            socialAccount =
                MyPageSocialAccountUiModel(
                    providerLabel = "APPLE",
                    displayAccount = "sam****@example.com",
                    isKakaoProvider = false,
                ),
            isTicketInfoTooltipVisible = false,
            onProfileEditClick = {},
            onTicketInfoClick = {},
            onTicketInfoTooltipDismiss = {},
            onLogoutClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun MyPageProfileSectionTicketTooltipPreview() {
    HilitTheme {
        MyPageProfileSection(
            profile =
                MyPageProfileUiModel(
                    name = "사용자",
                    role = "Android",
                    experience = "3년차",
                ),
            remainingTicketCount = 3,
            socialAccount =
                MyPageSocialAccountUiModel(
                    providerLabel = "카카오",
                    displayAccount = "sam****@example.com",
                    isKakaoProvider = true,
                ),
            isTicketInfoTooltipVisible = true,
            onProfileEditClick = {},
            onTicketInfoClick = {},
            onTicketInfoTooltipDismiss = {},
            onLogoutClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyTextPreview() {
    HilitTheme {
        EmptyText(modifier = Modifier.padding(20.dp))
    }
}
