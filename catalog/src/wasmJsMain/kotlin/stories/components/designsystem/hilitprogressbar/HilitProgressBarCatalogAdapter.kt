package stories.components.designsystem.hilitprogressbar

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.progressbar.HilitProgressBar

@CatalogControls
@Composable
internal fun HilitProgressBarCatalogAdapter(
    step: Int,
    maxStep: Int,
) {
    HilitProgressBar(step = step, maxStep = maxStep)
}
