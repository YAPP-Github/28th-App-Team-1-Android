package ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.theme.HilitTheme
import type.StoryLeafNode

private val StoryPreviewPadding = 32.dp
private val StoryContentTopPadding = 24.dp
private val MinimumStoryContentHeight = 320.dp

@Composable
internal fun StoryPreview(
    selectedStory: StoryLeafNode?,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (selectedStory == null) {
            EmptyStoryPreview()
            return@BoxWithConstraints
        }

        key(selectedStory.id) {
            StoryPreviewContent(
                selectedStory = selectedStory,
                viewportHeight = maxHeight,
            )
        }
    }
}

@Composable
private fun StoryPreviewContent(
    selectedStory: StoryLeafNode,
    viewportHeight: Dp,
) {
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val headerHeight = with(LocalDensity.current) { headerHeightPx.toDp() }
    val storyContentHeight =
        maxOf(
            MinimumStoryContentHeight,
            viewportHeight - StoryPreviewPadding * 2 - StoryContentTopPadding - headerHeight,
        )

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(StoryPreviewPadding),
        ) {
            StoryPreviewHeader(
                selectedStory = selectedStory,
                modifier = Modifier.onSizeChanged { headerHeightPx = it.height },
            )

            Box(
                modifier =
                    Modifier
                        .padding(top = StoryContentTopPadding)
                        .fillMaxWidth()
                        .height(storyContentHeight)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(16.dp),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    selectedStory.story.content()
                }
            }
        }
    }
}

@Composable
private fun StoryPreviewHeader(
    selectedStory: StoryLeafNode,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = selectedStory.group.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = selectedStory.group.path,
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        selectedStory.group.description?.let { description ->
            Text(
                text = description,
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        Text(
            text = selectedStory.story.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        selectedStory.story.description?.let { description ->
            Text(
                text = description,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Box(
            modifier =
                Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(16.dp),
                    ).padding(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            HilitTheme {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    selectedStory.story.content()
                }
            }
        }
    }
}
