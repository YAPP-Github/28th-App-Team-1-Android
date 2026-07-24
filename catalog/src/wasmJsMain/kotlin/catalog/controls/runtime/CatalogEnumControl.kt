package catalog.controls.runtime

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
internal fun <T : Enum<T>> CatalogEnumControl(
    name: String,
    value: T,
    options: List<T>,
    onValueChange: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    CatalogControlField(
        name = name,
        type = CatalogControlType.ENUM,
    ) {
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(value.name)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}
