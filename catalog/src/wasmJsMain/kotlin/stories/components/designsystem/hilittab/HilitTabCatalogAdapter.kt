package stories.components.designsystem.hilittab

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.tab.HilitTab
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitTabCatalogAdapter(
    text: String,
    selected: Boolean,
    enabled: Boolean,
) {
    HilitTheme {
        HilitTab(
            text = text,
            selected = selected,
            enabled = enabled,
            onClick = {},
        )
    }
}
