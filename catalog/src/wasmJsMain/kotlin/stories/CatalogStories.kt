package stories

import stories.components.designsystem.bublefield.BubleFieldStories
import stories.components.designsystem.dminusmodal.ModalStories
import stories.components.designsystem.fileuploadguide.FileUploadGuideStories
import stories.components.designsystem.hilitjdlinkfield.HilitJDLinkFieldStories
import stories.components.designsystem.hilitjdtextfield.HilitJDTextFieldStories
import stories.components.designsystem.hilitprogressbar.HilitProgressBarStories
import stories.components.designsystem.hilitsubtext.HilitSubTextStories
import stories.components.designsystem.hilitwheelpicker.HilitWheelPickerStories
import stories.components.designsystem.pdfupload.PdfUploadStories
import stories.components.designsystem.samplebutton.ButtonStories
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
        ButtonStories,
        ModalStories,
        HilitWheelPickerStories,
        HilitProgressBarStories,
        HilitJDTextFieldStories,
        HilitJDLinkFieldStories,
        HilitSubTextStories,
        BubleFieldStories,
        FileUploadGuideStories,
        PdfUploadStories,
    ).validateStories()

internal val CatalogStoryTree: List<StoryTreeNode> = CatalogStories.buildStoryTree()
