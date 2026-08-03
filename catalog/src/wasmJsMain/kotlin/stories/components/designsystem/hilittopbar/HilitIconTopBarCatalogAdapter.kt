package stories.components.designsystem.hilittopbar

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.topbar.HilitIconTopBar
import com.dminus14.designsystem.component.topbar.TopBarType
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitIconTopBarCatalogAdapter(
    type: TopBarType,
    title: String,
    leftIcon: HilitIconAsset,
    rightIcon: HilitIconAsset,
) {
    HilitTheme {
        HilitIconTopBar(
            type = type,
            leftIcon = leftIcon,
            title = title,
            rightIcon = rightIcon,
            onLeftClick = {},
            onRightClick = {},
        )
    }
}
