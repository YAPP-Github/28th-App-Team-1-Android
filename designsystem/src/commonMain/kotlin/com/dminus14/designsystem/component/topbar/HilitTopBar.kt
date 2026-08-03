package com.dminus14.designsystem.component.topbar

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

/**
 * 공용 TopBar chrome.
 *
 * 좌·중앙·우 슬롯만 제공하고 내용은 호출자가 채운다. 타이틀은 바 전체 기준 가로 중앙에 둔다.
 *
 * @param modifier 외부 Modifier
 * @param leading 좌측 슬롯. null이면 비운다
 * @param title 중앙 슬롯. null이면 비운다
 * @param trailing 우측 슬롯. null이면 비운다
 */
@Composable
fun HilitTopBar(
    modifier: Modifier = Modifier,
    leading: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(HilitTheme.colors.hilitWhite)
                .padding(
                    horizontal = HilitTopBarHorizontalPadding,
                    vertical = HilitTopBarVerticalPadding,
                ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column (
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.Start,
            ) {

                leading?.invoke()
            }


            Column (
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                title?.invoke()
            }

            Column (
                modifier = Modifier.width(40.dp),
                horizontalAlignment = Alignment.End,
            ) {
                trailing?.invoke()
            }

        }
    }
}

@Composable
internal fun TopBarIconButton(
    asset: HilitIconAsset,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    HilitIcon(
        asset = asset,
        contentDescription = contentDescription,
        tint = HilitTheme.colors.hilitBlack800,
        modifier =
            modifier
                .size(asset.defaultSize)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                ),
    )
}

private val HilitTopBarHorizontalPadding = 20.dp
private val HilitTopBarVerticalPadding = 14.dp

@Preview(name = "HilitTopBar", showBackground = true, widthDp = 375)
@Composable
private fun HilitTopBarPreview() {
    HilitTheme {
        HilitTopBar(
            leading = {
                TopBarIconButton(
                    asset = HilitIconAsset.Cancel,
                    onClick = {},
                    contentDescription = null,
                )
            },
            title = {
                Text(
                    text = "타이틀",
                    style = HilitTheme.typography.sub7,
                    color = HilitTheme.colors.hilitBlack800,
                )
            },
            trailing = {
                TopBarIconButton(
                    asset = HilitIconAsset.Plus,
                    onClick = {},
                    contentDescription = null,
                )
            },
        )
    }
}
