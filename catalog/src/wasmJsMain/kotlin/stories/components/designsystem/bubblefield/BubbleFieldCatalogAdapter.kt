package stories.components.designsystem.bubblefield

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.bubblefield.BubbleField
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailAlign
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailEdge
import com.dminus14.designsystem.component.bubblefield.BubbleFieldTailShape
import com.dminus14.designsystem.component.bubblefield.BubbleFieldType

@CatalogControls
@Composable
internal fun BubbleFieldCatalogAdapter(
    text: String,
    type: BubbleFieldType,
    tailEdge: BubbleFieldTailEdge,
    tailAlign: BubbleFieldTailAlign,
    tailShape: BubbleFieldTailShape,
) {
    BubbleField(
        text = text,
        type = type,
        tailEdge = tailEdge,
        tailAlign = tailAlign,
        tailShape = tailShape,
    )
}
