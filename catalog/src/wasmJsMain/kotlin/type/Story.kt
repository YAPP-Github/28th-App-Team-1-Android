package type

import androidx.compose.runtime.Composable

internal data class Story(
    val id: String,
    val title: String,
    val description: String? = null,
    val content: @Composable () -> Unit,
)
