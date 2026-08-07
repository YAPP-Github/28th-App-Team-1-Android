package com.dminus14.app.feature.mypage.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

@Composable
internal fun MyPageTopBar(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth().height(64.dp),
        contentAlignment = Alignment.Center,
    ) {
        HilitIcon(
            asset = HilitIconAsset.Cancel,
            contentDescription = "마이페이지 닫기",
            tint = HilitTheme.colors.hilitBlack800,
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp)
                    .size(24.dp)
                    .clickable(role = Role.Button, onClick = onCloseClick),
        )
        Text(
            text = "마이페이지",
            color = HilitTheme.colors.hilitBlack800,
            style = HilitTheme.typography.sub7,
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun MyPageTopBarPreview() {
    HilitTheme {
        MyPageTopBar(onCloseClick = {})
    }
}
