package stories.components.designsystem.hilittopbar

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.topbar.HilitTextTopBar
import com.dminus14.designsystem.component.topbar.TopBarType
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitTextTopBarCatalogAdapter(
    type: TopBarType,
    title: String,
    buttonText: String,
    leftIcon: HilitIconAsset,
) {
    HilitTheme {
        HilitTextTopBar(
            type = type,
            leftIcon = leftIcon,
            title = title,
            buttonText = buttonText,
            onLeftClick = {},
            onButtonClick = {},
        )
    }
}
