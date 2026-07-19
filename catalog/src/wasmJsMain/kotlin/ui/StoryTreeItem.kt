package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import type.StoryDirectoryNode
import type.StoryGroupNode
import type.StoryLeafNode
import type.StoryTreeNode

@Composable
internal fun StoryTreeItem(
    node: StoryTreeNode,
    depth: Int,
    selectedStoryId: String?,
    onStorySelected: (StoryLeafNode) -> Unit,
) {
    when (node) {
        is StoryDirectoryNode -> {
            Text(
                text = node.title,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = (depth * 12).dp,
                            top = 12.dp,
                            bottom = 6.dp,
                        ),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            node.children.forEach { child ->
                StoryTreeItem(
                    node = child,
                    depth = depth + 1,
                    selectedStoryId = selectedStoryId,
                    onStorySelected = onStorySelected,
                )
            }
        }

        is StoryGroupNode -> {
            Text(
                text = node.title,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = (depth * 12).dp,
                            top = 8.dp,
                            bottom = 4.dp,
                        ),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )

            node.children.forEach { child ->
                StoryTreeItem(
                    node = child,
                    depth = depth + 1,
                    selectedStoryId = selectedStoryId,
                    onStorySelected = onStorySelected,
                )
            }
        }

        is StoryLeafNode -> {
            val selected = node.id == selectedStoryId
            val backgroundColor =
                if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Transparent
                }

            Text(
                text = node.title,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = backgroundColor)
                        .clickable { onStorySelected(node) }
                        .padding(
                            start = (depth * 12).dp + 8.dp,
                            top = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp,
                        ),
                style = MaterialTheme.typography.bodySmall,
                color =
                    if (selected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
            )
        }
    }
}
