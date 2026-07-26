package stories.components.designsystem.hilitfixedbottombutton

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.button.HilitButtonType
import com.dminus14.designsystem.component.button.HilitFixedBottomButton
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitFixedBottomButtonCatalogAdapter(
    text: String,
    enabled: Boolean,
    type: HilitButtonType,
) {
    HilitTheme {
        HilitFixedBottomButton(
            text = text,
            enabled = enabled,
            type = type,
            onClick = {},
        )
    }
}
