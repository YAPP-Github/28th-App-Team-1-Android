package stories.components.designsystem

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.SampleButton

@CatalogControls
@Composable
internal fun SampleButtonCatalogAdapter(
    text: String,
    enabled: Boolean,
) {
    SampleButton(
        text = text,
        enabled = enabled,
        onClick = {},
    )
}
