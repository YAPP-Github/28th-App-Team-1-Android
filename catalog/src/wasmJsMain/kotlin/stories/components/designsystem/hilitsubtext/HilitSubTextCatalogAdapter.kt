package stories.components.designsystem.hilitsubtext

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.subtext.HilitSubText
import com.dminus14.designsystem.component.subtext.HilitSubTextType

@CatalogControls
@Composable
internal fun HilitSubTextCatalogAdapter(
    text: String,
    type: HilitSubTextType,
) {
    HilitSubText(
        text = text,
        type = type,
    )
}
