package stories.components.designsystem.hilitbottomoutlinedtextfield

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.textfield.HilitBottomOutlinedTextField
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitBottomOutlinedTextFieldCatalogAdapter(
    placeholder: String,
    initialValue: String,
) {
    var value by remember(initialValue) { mutableStateOf(initialValue) }

    HilitTheme {
        HilitBottomOutlinedTextField(
            value = value,
            onValueChange = { value = it },
            placeholder = placeholder,
        )
    }
}
