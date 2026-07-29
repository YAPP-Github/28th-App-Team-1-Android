package ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import type.StoryLeafNode
import type.StoryTreeNode
import ui.icons.DarkMode
import ui.icons.LightMode

@Composable
internal fun CatalogSidebar(
    nodes: List<StoryTreeNode>,
    selectedStoryId: String?,
    onStorySelected: (StoryLeafNode) -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggle: () -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .width(280.dp)
                .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
        ) {
            Text(
                text = "Design System Catalog",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "for Hilit",
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onDarkModeToggle,
                modifier = Modifier.fillMaxWidth().padding(4.dp),
            ) {
                Icon(
                    imageVector = if (isDarkMode) DarkMode else LightMode,
                    contentDescription = "Toggle Dark Mode",
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isDarkMode) "Dark Mode" else "Light Mode")
            }

            Spacer(modifier = Modifier.height(16.dp))

            nodes.forEach { node ->
                StoryTreeItem(
                    node = node,
                    depth = 0,
                    selectedStoryId = selectedStoryId,
                    onStorySelected = onStorySelected,
                )
            }
        }
    }
}
