package theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalCatalogDarkTheme = staticCompositionLocalOf { false }

@Composable
internal fun CatalogTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalCatalogDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = getCatalogColorScheme(darkTheme),
            typography = CatalogTypography(),
            content = content,
        )
    }
}
