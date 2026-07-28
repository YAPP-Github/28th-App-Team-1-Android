package stories.components.designsystem.bublefield

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.bubblefield.BubbleField
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailAlign
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailEdge
import com.dminus14.designsystem.component.bubblefield.BubleFieldTailShape
import com.dminus14.designsystem.component.bubblefield.BubleFieldType

@CatalogControls
@Composable
internal fun BubleFieldCatalogAdapter(
    text: String,
    type: BubleFieldType,
    tailEdge: BubleFieldTailEdge,
    tailAlign: BubleFieldTailAlign,
    tailShape: BubleFieldTailShape,
) {
    BubbleField(
        text = text,
        type = type,
        tailEdge = tailEdge,
        tailAlign = tailAlign,
        tailShape = tailShape,
    )
}
