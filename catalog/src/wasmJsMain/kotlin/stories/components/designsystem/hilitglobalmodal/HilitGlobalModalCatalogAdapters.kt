package stories.components.designsystem.hilitglobalmodal

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.modal.HilitGlobalModal
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitGlobalModalCatalogAdapter(
    title: String,
    message: String,
    confirmText: String,
    dismissible: Boolean,
) {
    CatalogGlobalModalPreview { closeModal ->
        HilitTheme {
            HilitGlobalModal(
                title = title,
                message = message,
                confirmText = confirmText,
                cancelText = null,
                dismissible = dismissible,
                onConfirm = closeModal,
                onCancel = closeModal,
                onDismiss = closeModal,
            )
        }
    }
}

@CatalogControls
@Composable
internal fun HilitGlobalModalWithCancelCatalogAdapter(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    dismissible: Boolean,
) {
    CatalogGlobalModalPreview { closeModal ->
        HilitTheme {
            HilitGlobalModal(
                title = title,
                message = message,
                confirmText = confirmText,
                cancelText = cancelText,
                dismissible = dismissible,
                onConfirm = closeModal,
                onCancel = closeModal,
                onDismiss = closeModal,
            )
        }
    }
}

@Composable
private fun CatalogGlobalModalPreview(content: @Composable (() -> Unit) -> Unit) {
    var visible by remember { mutableStateOf(true) }
    if (visible) {
        content { visible = false }
    } else {
        Button(onClick = { visible = true }) {
            Text("Modal 열기")
        }
    }
}
