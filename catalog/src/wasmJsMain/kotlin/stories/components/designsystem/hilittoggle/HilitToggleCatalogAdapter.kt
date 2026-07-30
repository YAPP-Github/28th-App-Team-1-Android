package stories.components.designsystem.hilittoggle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.toggle.HilitToggle
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitToggleCatalogAdapter(checked: Boolean) {
    var currentChecked by remember(checked) { mutableStateOf(checked) }

    HilitTheme {
        HilitToggle(
            checked = currentChecked,
            onCheckedChange = { currentChecked = it },
        )
    }
}
