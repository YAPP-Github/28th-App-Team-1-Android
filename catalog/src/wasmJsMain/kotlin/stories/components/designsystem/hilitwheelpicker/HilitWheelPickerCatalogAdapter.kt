package stories.components.designsystem.hilitwheelpicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.wheelpicker.HilitWheelPicker

private val CareerItems =
    listOf(
        "경력 없음",
        "신입",
        "1년 이상",
        "2년 이상",
        "3년 이상",
    )

@CatalogControls
@Composable
internal fun HilitWheelPickerCatalogAdapter(selectedIndex: Int) {
    val safeIndex = selectedIndex.coerceIn(0, CareerItems.lastIndex)
    var selectedItem by remember(safeIndex) {
        mutableStateOf(CareerItems[safeIndex])
    }

    HilitWheelPicker(
        items = CareerItems,
        selectedItem = selectedItem,
        onSelectedItemChange = { selectedItem = it },
    )
}
