package catalog.controls.runtime

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun CatalogNumberControl(
    name: String,
    type: CatalogControlType,
    rawValue: String,
    errorMessage: String?,
    onValueChange: (String) -> Unit,
) {
    CatalogControlField(
        name = name,
        type = type,
    ) {
        OutlinedTextField(
            value = rawValue,
            onValueChange = onValueChange,
            supportingText = errorMessage?.let { message -> { Text(message) } },
            isError = errorMessage != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
