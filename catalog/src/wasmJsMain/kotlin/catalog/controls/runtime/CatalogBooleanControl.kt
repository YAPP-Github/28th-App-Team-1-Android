package catalog.controls.runtime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Switch(
                checked = value,
                onCheckedChange = onValueChange,
            )
        }
    }
}
