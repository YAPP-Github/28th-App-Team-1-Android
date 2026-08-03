package com.dminus14.app.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.mypage.MyPageProfileUiModel
import com.dminus14.app.feature.mypage.MyPageSocialAccountUiModel
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.theme.HilitTheme

@Composable
@Suppress("LongMethod", "LongParameterList")
internal fun MyPageProfileSection(
    profile: MyPageProfileUiModel?,
    remainingTicketCount: Int?,
    socialAccount: MyPageSocialAccountUiModel?,
    onProfileEditClick: () -> Unit,
    onTicketInfoClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (profile == null) {
                    EmptyText()
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = profile.name,
                            color = HilitTheme.colors.hilitBlack800,
                            style = HilitTheme.typography.body1,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            HilitTag(
                                colorType = TagColorType.Black,
                                tagType = TagType.Small,
                                text = profile.role,
                            )
                            Text(
                                text = profile.experience,
                                modifier =
                                    Modifier
                                        .background(HilitTheme.colors.hilitBlack800)
                                        .padding(horizontal = 4.dp),
                                style = HilitTheme.typography.body6,
                                color = HilitTheme.colors.hilitWhite,
                            )
                        }
                    }
                }
                HilitIcon(
                    asset = HilitIconAsset.Edit,
                    contentDescription = "프로필 편집",
                    tint = HilitTheme.colors.gray500,
                    modifier =
                        Modifier
                            .size(
                                24.dp,
                            ).clickable(role = Role.Button, onClick = onProfileEditClick),
                )
            }

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
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                        contentDescription = null,
                        tint = HilitTheme.colors.gray200,
                        modifier =
                            Modifier
                                .size(16.dp)
                                .clickable { onTicketInfoClick() },
                    )
                }
                HilitTag(
                    colorType = TagColorType.Green,
                    tagType = TagType.Small,
                    text = remainingTicketCount?.let { "${it}장" } ?: "내용 없음",
                )
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(HilitTheme.colors.gray50)
                    .border(1.dp, HilitTheme.colors.gray100)
                    .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (socialAccount == null) {
                EmptyText(modifier = Modifier.weight(1f))
            } else {
                HilitIcon(
                    asset = HilitIconAsset.KakaoLogo,
                    contentDescription = socialAccount.provider,
                    modifier = Modifier.size(24.dp),
                )
                Text(
                    text = socialAccount.displayAccount,
                    modifier = Modifier.weight(1f),
                    style = HilitTheme.typography.body6,
                    color = HilitTheme.colors.gray700,
                )
            }
            Text(
                text = "로그아웃",
                style = HilitTheme.typography.body6,
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
                    provider = "카카오",
                    displayAccount = "sample****@example.com",
                ),
            onProfileEditClick = {},
            onTicketInfoClick = {},
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
