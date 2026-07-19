package ui

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import type.Story
import type.StoryGroup
import type.StoryLeafNode
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class StoryPreviewTest {
    @Test
    fun `Story 변경 시 기억된 콘텐츠 상태를 폐기한다`() =
        runComposeUiTest {
            val first = storyLeaf("first")
            val second = storyLeaf("second")
            var selectedStory by mutableStateOf(first)

            setContent {
                MaterialTheme {
                    StoryPreview(selectedStory = selectedStory)
                }
            }

            onNodeWithText("first:0").performClick()
            onNodeWithText("first:1").assertExists()

            runOnIdle { selectedStory = second }
            onNodeWithText("second:0").assertExists()

            runOnIdle { selectedStory = first }
            onNodeWithText("first:0").assertExists()
        }

    @Test
    fun `낮은 화면에서도 Story 콘텐츠까지 스크롤할 수 있다`() =
        runComposeUiTest {
            val story = storyLeaf("scroll target")

            setContent {
                MaterialTheme {
                    StoryPreview(
                        selectedStory = story,
                        modifier = Modifier.height(240.dp),
                    )
                }
            }

            onNodeWithText("scroll target:0")
                .performScrollTo()
                .assertIsDisplayed()
        }

    private fun storyLeaf(id: String): StoryLeafNode {
        val story =
            Story(
                id = id,
                title = id,
            ) {
                var count by remember { mutableStateOf(0) }
                Button(onClick = { count += 1 }) {
                    Text("$id:$count")
                }
            }
        val group =
            StoryGroup(
                path = "Components/StateReset",
                stories = listOf(story),
            )

        return StoryLeafNode(
            id = "story:${group.path}#${story.id}",
            title = story.title,
            path = "${group.path}/${story.id}",
            group = group,
            story = story,
        )
    }
}
