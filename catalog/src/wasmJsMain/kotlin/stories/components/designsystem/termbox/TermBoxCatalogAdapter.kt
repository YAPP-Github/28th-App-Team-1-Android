package stories.components.designsystem.termbox

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.term.TermBox
import com.dminus14.designsystem.component.term.TermBoxType
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun TermBoxCatalogAdapter(
    type: TermBoxType,
    text: String,
    checked: Boolean,
) {
    HilitTheme {
        TermBox(
            type = type,
            text = text,
            checked = checked,
            onClick = {},
            onViewClick = {},
        )
    }
}
