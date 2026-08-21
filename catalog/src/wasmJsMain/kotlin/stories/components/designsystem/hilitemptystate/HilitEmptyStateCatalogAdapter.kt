package stories.components.designsystem.hilitemptystate

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.emptystate.HilitEmptyState

@CatalogControls
@Composable
internal fun HilitEmptyStateCatalogAdapter(text: String) {
    HilitEmptyState(text = text)
}
