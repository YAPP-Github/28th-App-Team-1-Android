package stories.components.designsystem.hilitbottomsheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.bottomsheet.HilitBottomSheet
import com.dminus14.designsystem.theme.HilitTheme

@OptIn(ExperimentalMaterial3Api::class)
@CatalogControls
@Composable
internal fun HilitBottomSheetCatalogAdapter(
    title: String,
    content: String,
) {
    CatalogBottomSheetPreview { closeSheet ->
        HilitTheme {
            HilitBottomSheet(
                onDismissRequest = closeSheet,
                content = {
                    Text(
                        text = title,
                        style = HilitTheme.typography.sub7,
                        color = HilitTheme.colors.hilitBlack800,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                    )
                    Text(
                        text = content,
                        style = HilitTheme.typography.body4,
                        color = HilitTheme.colors.gray800,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp)
                                .padding(bottom = 20.dp),
                    )
                },
            )
        }
    }
}

@Composable
private fun CatalogBottomSheetPreview(content: @Composable (() -> Unit) -> Unit) {
    var visible by remember { mutableStateOf(true) }
    if (visible) {
        content { visible = false }
    } else {
        Button(onClick = { visible = true }) {
            Text("Bottom Sheet 열기")
        }
    }
}
