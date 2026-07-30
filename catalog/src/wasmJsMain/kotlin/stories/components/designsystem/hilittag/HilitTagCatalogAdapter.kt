package stories.components.designsystem.hilittag

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitTagCatalogAdapter(
    text: String,
    colorType: TagColorType,
    tagType: TagType,
) {
    HilitTheme {
        HilitTag(
            text = text,
            colorType = colorType,
            tagType = tagType,
        )
    }
}
