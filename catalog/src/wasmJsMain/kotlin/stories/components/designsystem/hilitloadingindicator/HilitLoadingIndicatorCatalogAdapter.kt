package stories.components.designsystem.hilitloadingindicator

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.loading.HilitLoadingIndicator
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitLoadingIndicatorCatalogAdapter(
    size: Int,
    contentDescription: String,
) {
    HilitTheme {
        HilitLoadingIndicator(
            size = size.dp,
            contentDescription = contentDescription.ifBlank { null },
        )
    }
}
