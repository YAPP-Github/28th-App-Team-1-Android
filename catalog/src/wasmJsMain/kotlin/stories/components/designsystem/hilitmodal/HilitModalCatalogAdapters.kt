package stories.components.designsystem.hilitmodal

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButton
import com.dminus14.designsystem.component.button.HilitFixedBottomDualButtonType
import com.dminus14.designsystem.component.modal.HilitBookIllustration
import com.dminus14.designsystem.component.modal.HilitModal
import com.dminus14.designsystem.component.modal.HilitModalType
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitModalCatalogAdapter(
    type: HilitModalType,
    title: String,
    subtitle: String,
    infoText: String,
    confirmText: String,
    showGraphic: Boolean,
    dismissible: Boolean,
) {
    CatalogModalPreview { closeModal ->
        HilitTheme {
            HilitModal(
                type = type,
                title = title,
                subtitle = subtitle.takeIf { it.isNotEmpty() },
                infoText = infoText.takeIf { it.isNotEmpty() },
                confirmText = confirmText,
                dismissible = dismissible,
                onConfirm = closeModal,
                onDismiss = closeModal,
                graphic =
                    if (showGraphic) {
                        { HilitBookIllustration() }
                    } else {
                        null
                    },
            )
        }
    }
}

@CatalogControls
@Composable
internal fun HilitModalDualButtonCatalogAdapter(
    title: String,
    leftText: String,
    rightText: String,
    dismissible: Boolean,
) {
    CatalogModalPreview { closeModal ->
        HilitTheme {
            HilitModal(
                type = HilitModalType.InvisibleInfo,
                title = title,
                dismissible = dismissible,
                onDismiss = closeModal,
                graphic = { HilitBookIllustration() },
                buttons = {
                    HilitFixedBottomDualButton(
                        leftText = leftText,
                        rightText = rightText,
                        type = HilitFixedBottomDualButtonType.Default,
                        onLeftClick = closeModal,
                        onRightClick = closeModal,
                    )
                },
            )
        }
    }
}

@Composable
private fun CatalogModalPreview(content: @Composable (() -> Unit) -> Unit) {
    var visible by remember { mutableStateOf(true) }
    if (visible) {
        content { visible = false }
    } else {
        Button(onClick = { visible = true }) {
            Text("Modal 열기")
        }
    }
}
