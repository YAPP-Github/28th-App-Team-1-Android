package stories

import stories.components.designsystem.dminusmodal.ModalStories
import stories.components.designsystem.samplebutton.ButtonStories
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
