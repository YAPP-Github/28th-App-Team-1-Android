package theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun CatalogTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = getCatalogColorScheme(darkTheme),
        typography = CatalogTypography(),
        content = content,
    )
}
