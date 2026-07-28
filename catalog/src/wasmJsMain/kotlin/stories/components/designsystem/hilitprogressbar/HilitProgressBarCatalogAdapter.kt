package stories.components.designsystem.hilitprogressbar

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.progressbar.HilitProgressBar
import com.dminus14.designsystem.component.progressbar.HilitStep

@CatalogControls
@Composable
internal fun HilitProgressBarCatalogAdapter(step: HilitStep) {
    HilitProgressBar(step = step)
}
