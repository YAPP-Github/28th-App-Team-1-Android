package catalog.controls.runtime

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun CatalogPreviewUnavailable() {
    Text(
        text = "초기 인자가 올바르지 않아 Preview를 표시할 수 없습니다.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
    )
}
