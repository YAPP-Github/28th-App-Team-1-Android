package stories.components.designsystem.hilitmediumbutton

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.button.HilitMediumButton
import com.dminus14.designsystem.component.button.HilitMediumButtonColor
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitMediumButtonCatalogAdapter(
    text: String,
    color: HilitMediumButtonColor,
    enabled: Boolean,
) {
    HilitTheme {
        HilitMediumButton(
            text = text,
            color = color,
            enabled = enabled,
            onClick = {},
        )
    }
}
