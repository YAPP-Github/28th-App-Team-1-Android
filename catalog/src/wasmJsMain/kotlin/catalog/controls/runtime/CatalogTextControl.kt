package catalog.controls.runtime

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun CatalogTextControl(
    name: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    CatalogControlField(
        name = name,
        type = CatalogControlType.STRING,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
