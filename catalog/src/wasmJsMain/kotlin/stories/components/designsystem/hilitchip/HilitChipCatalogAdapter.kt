package stories.components.designsystem.hilitchip

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.chip.HilitChip
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitChipCatalogAdapter(
    text: String,
    selected: Boolean,
    enabled: Boolean,
) {
    HilitTheme {
        HilitChip(
            text = text,
            selected = selected,
            enabled = enabled,
            onClick = {},
        )
    }
}
