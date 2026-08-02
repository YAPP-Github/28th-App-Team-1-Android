package stories.components.designsystem.hilitfixedbottomdualbutton

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitFixedBottomDualButtonCatalogAdapter(
    leftText: String,
    rightText: String,
    leftEnabled: Boolean,
    rightEnabled: Boolean,
    type: HilitFixedBottomDualButtonType,
) {
    HilitTheme {
        HilitFixedBottomDualButton(
            modifier = Modifier.width(360.dp),
            leftText = leftText,
            rightText = rightText,
            leftEnabled = leftEnabled,
            rightEnabled = rightEnabled,
            type = type,
            onLeftClick = {},
            onRightClick = {},
        )
    }
}
