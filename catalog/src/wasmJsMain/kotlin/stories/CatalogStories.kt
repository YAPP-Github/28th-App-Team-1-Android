package stories

import stories.components.designsystem.hilitfixedbottombutton.HilitFixedBottomButtonStories
import stories.components.designsystem.hilitmodal.HilitModalStories
import stories.components.designsystem.hilittag.HilitTagStories
import stories.foundations.ColorStories
import stories.foundations.IconStories
import stories.foundations.TypographyStories
import type.StoryGroup
import type.StoryTreeNode
import util.buildStoryTree
import util.validateStories

internal val CatalogStories: List<StoryGroup> =
    listOf(
        ColorStories,
        IconStories,
        TypographyStories,
        HilitFixedBottomButtonStories,
        HilitTagStories,
        HilitModalStories,
    ).validateStories()

internal val CatalogStoryTree: List<StoryTreeNode> = CatalogStories.buildStoryTree()
