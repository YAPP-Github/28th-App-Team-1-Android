package stories.components.designsystem.hilitminibutton

import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.button.HilitMiniButton
import com.dminus14.designsystem.component.button.HilitMiniButtonColor
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitMiniButtonCatalogAdapter(
    text: String,
    color: HilitMiniButtonColor,
    showIcon: Boolean,
) {
    HilitTheme {
        HilitMiniButton(
            color = color,
            onClick = {},
        ) {
            HilitMiniButtonCatalogContent(text = text, showIcon = showIcon)
        }
    }
}

@Composable
internal fun HilitMiniButtonCatalogContent(
    text: String,
    showIcon: Boolean = true,
) {
    if (showIcon) {
        HilitIcon(
            asset = HilitIconAsset.Video,
            contentDescription = null,
            tint = LocalContentColor.current,
            modifier = Modifier.size(16.dp),
        )
    }
    Text(text = text)
}
