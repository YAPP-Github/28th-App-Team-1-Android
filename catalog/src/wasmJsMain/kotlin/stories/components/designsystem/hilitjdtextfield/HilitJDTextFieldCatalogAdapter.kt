package stories.components.designsystem.hilitjdtextfield

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.textfield.HilitJDTextField

@CatalogControls
@Composable
internal fun HilitJDTextFieldCatalogAdapter(
    value: String,
    placeholder: String,
    maxLength: Int,
) {
    val safeMaxLength = maxLength.coerceAtLeast(0)
    var text by remember(value, safeMaxLength) {
        mutableStateOf(value.take(safeMaxLength))
    }

    HilitJDTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = placeholder,
        maxLength = safeMaxLength,
    )
}
