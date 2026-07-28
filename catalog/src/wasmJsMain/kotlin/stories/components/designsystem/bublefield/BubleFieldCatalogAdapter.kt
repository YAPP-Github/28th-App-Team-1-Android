package stories.components.designsystem.bublefield

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.bubblefield.BubbleField
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailAlign
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailEdge
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailShape

@CatalogControls
@Composable
internal fun BubleFieldCatalogAdapter(
    text: String,
    tailEdge: BubleFieldTailEdge,
    tailAlign: BubleFieldTailAlign,
    tailShape: BubleFieldTailShape,
) {
    BubbleField(
        text = text,
        tailEdge = tailEdge,
        tailAlign = tailAlign,
        tailShape = tailShape,
    )
}
