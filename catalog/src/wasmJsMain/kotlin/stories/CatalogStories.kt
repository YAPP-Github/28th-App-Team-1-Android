package stories

import stories.components.designsystem.bublefield.BubleFieldStories
import stories.components.designsystem.fileuploadguide.FileUploadGuideStories
import stories.components.designsystem.hilitbottomoutlinedtextfield.HilitBottomOutlinedTextFieldStories
import stories.components.designsystem.hilitfixedbottombutton.HilitFixedBottomButtonStories
import stories.components.designsystem.hilitjdlinkfield.HilitJDLinkFieldStories
import stories.components.designsystem.hilitjdtextfield.HilitJDTextFieldStories
import stories.components.designsystem.hilitmediumbutton.HilitMediumButtonStories
import stories.components.designsystem.hilitmodal.HilitModalStories
import stories.components.designsystem.hilitprogressbar.HilitProgressBarStories
import stories.components.designsystem.hilitsubtext.HilitSubTextStories
import stories.components.designsystem.hilittag.HilitTagStories
import stories.components.designsystem.hilittexthighlight.HilitTextHighlightStories
import stories.components.designsystem.hilitwheelpicker.HilitWheelPickerStories
import stories.components.designsystem.pdfupload.PdfUploadStories
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
        HilitMediumButtonStories,
        HilitTagStories,
        HilitTextHighlightStories,
        HilitBottomOutlinedTextFieldStories,
        HilitModalStories,
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
