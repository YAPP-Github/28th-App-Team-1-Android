package catalog.controls.runtime

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable

@Composable
internal fun CatalogBooleanControl(
    name: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    CatalogControlField(
        name = name,
        type = CatalogControlType.BOOLEAN,
    ) {
        Switch(
            checked = value,
            onCheckedChange = onValueChange,
        )
    }
}
