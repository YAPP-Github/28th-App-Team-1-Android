package stories

import stories.components.designsystem.ButtonStories
import stories.components.designsystem.ModalStories
import type.StoryGroup
import type.StoryTreeNode
import util.buildStoryTree
import util.validateStories

internal val CatalogStories: List<StoryGroup> =
    listOf(
        ButtonStories,
        ModalStories,
    ).validateStories()

internal val CatalogStoryTree: List<StoryTreeNode> = CatalogStories.buildStoryTree()
