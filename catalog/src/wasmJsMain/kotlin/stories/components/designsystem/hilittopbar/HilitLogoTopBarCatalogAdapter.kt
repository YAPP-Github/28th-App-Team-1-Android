package stories.components.designsystem.hilittopbar

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.topbar.HilitLogoTopBar
import com.dminus14.designsystem.component.topbar.TopBarType
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitLogoTopBarCatalogAdapter(
    type: TopBarType,
    showRightIcon: Boolean,
    rightIcon: HilitIconAsset,
) {
    HilitTheme {
        HilitLogoTopBar(
            type = type,
            rightIcon = if (showRightIcon) rightIcon else null,
            onRightClick = {},
        )
    }
}
