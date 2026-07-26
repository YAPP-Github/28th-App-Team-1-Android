package stories

import stories.components.designsystem.dminusmodal.ModalStories
import stories.components.designsystem.samplebutton.ButtonStories
import stories.foundations.ColorStories
import stories.foundations.TypographyStories
import type.StoryGroup
import type.StoryTreeNode
import util.buildStoryTree
import util.validateStories

internal val CatalogStories: List<StoryGroup> =
    listOf(
        ColorStories,
        TypographyStories,
        ButtonStories,
        ModalStories,
    ).validateStories()

internal val CatalogStoryTree: List<StoryTreeNode> = CatalogStories.buildStoryTree()
