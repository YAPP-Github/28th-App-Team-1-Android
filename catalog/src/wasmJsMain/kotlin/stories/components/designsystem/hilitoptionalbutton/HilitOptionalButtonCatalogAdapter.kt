package stories.components.designsystem.hilitoptionalbutton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.button.HilitOptionalButton
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitOptionalButtonCatalogAdapter(
    text: String,
    tagText: String,
    showIcon: Boolean,
    showTag: Boolean,
) {
    HilitTheme {
        HilitOptionalButton(onClick = {}) {
            HilitOptionalButtonCatalogContent(
                text = text,
                tagText = tagText,
                showIcon = showIcon,
                showTag = showTag,
            )
        }
    }
}

@Composable
internal fun HilitOptionalButtonCatalogContent(
    text: String,
    tagText: String,
    showIcon: Boolean = true,
    showTag: Boolean = true,
) {
    if (showIcon) {
        Box(
            modifier = Modifier.size(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            HilitIcon(
                asset = HilitIconAsset.Plus,
                contentDescription = null,
                tint = LocalContentColor.current,
                modifier = Modifier.size(12.dp),
            )
        }
    }
    Text(text = text)
    if (showTag) {
        HilitTag(
            colorType = TagColorType.Gray,
            tagType = TagType.Small,
            text = tagText,
        )
    }
}
