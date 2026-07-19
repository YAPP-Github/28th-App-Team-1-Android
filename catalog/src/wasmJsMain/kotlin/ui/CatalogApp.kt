package ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import stories.CatalogStoryTree
import theme.CatalogTheme
import util.findFirstStoryLeaf
import util.findStoryLeafById

@Composable
internal fun CatalogApp() {
    val systemDark = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(systemDark) }

    val storyTree = CatalogStoryTree
    var selectedStoryId by remember { mutableStateOf(storyTree.findFirstStoryLeaf()?.id) }
    val selectedStory =
        storyTree.findStoryLeafById(selectedStoryId)
            ?: storyTree.findFirstStoryLeaf()

    CatalogTheme(darkTheme = isDarkMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
            ) {
                CatalogSidebar(
                    nodes = storyTree,
                    selectedStoryId = selectedStory?.id,
                    onStorySelected = { selectedStoryId = it.id },
                    isDarkMode = isDarkMode,
                    onDarkModeToggle = { isDarkMode = !isDarkMode },
                )

                StoryPreview(
                    selectedStory = selectedStory,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
