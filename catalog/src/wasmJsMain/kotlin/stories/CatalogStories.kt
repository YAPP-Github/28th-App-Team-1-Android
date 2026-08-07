package stories

import stories.components.designsystem.bubblefield.BubbleFieldStories
import stories.components.designsystem.fileuploadguide.FileUploadGuideStories
import stories.components.designsystem.hilitasynctextfield.HilitAsyncTextFieldStories
import stories.components.designsystem.hilitbottomoutlinedtextfield.HilitBottomOutlinedTextFieldStories
import stories.components.designsystem.hilitbottomsheet.HilitBottomSheetStories
import stories.components.designsystem.hilitchip.HilitChipStories
import stories.components.designsystem.hilitfixedbottombutton.HilitFixedBottomButtonStories
import stories.components.designsystem.hilitfixedbottomdualbutton.HilitFixedBottomDualButtonStories
import stories.components.designsystem.hilitglobalmodal.HilitGlobalModalStories
import stories.components.designsystem.hilitjdtextfield.HilitJDTextFieldStories
import stories.components.designsystem.hilitloadingindicator.HilitLoadingIndicatorStories
import stories.components.designsystem.hilitmediumbutton.HilitMediumButtonStories
import stories.components.designsystem.hilitminibutton.HilitMiniButtonStories
import stories.components.designsystem.hilitmodal.HilitModalStories
import stories.components.designsystem.hilitoptionalbutton.HilitOptionalButtonStories
import stories.components.designsystem.hilitprogressbar.HilitProgressBarStories
import stories.components.designsystem.hilitreportcard.HilitReportCardStories
import stories.components.designsystem.hilitsubtext.HilitSubTextStories
import stories.components.designsystem.hilittab.HilitTabStories
import stories.components.designsystem.hilittag.HilitTagStories
import stories.components.designsystem.hilittexthighlight.HilitTextHighlightStories
import stories.components.designsystem.hilittoggle.HilitToggleStories
import stories.components.designsystem.hilittopbar.HilitTopBarStories
import stories.components.designsystem.hilitwheelpicker.HilitWheelPickerStories
import stories.components.designsystem.kakaologinbutton.KakaoLoginButtonStories
import stories.components.designsystem.pdfupload.PdfUploadStories
import stories.components.designsystem.previousinfotext.PreviousInfoTextStories
import stories.components.designsystem.termbox.TermBoxStories
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
        HilitFixedBottomDualButtonStories,
        HilitLoadingIndicatorStories,
        HilitMediumButtonStories,
        HilitChipStories,
        HilitMiniButtonStories,
        HilitOptionalButtonStories,
        KakaoLoginButtonStories,
        TermBoxStories,
        HilitBottomSheetStories,
        HilitTopBarStories,
        HilitTagStories,
        HilitTabStories,
        HilitTextHighlightStories,
        HilitToggleStories,
        HilitBottomOutlinedTextFieldStories,
        HilitGlobalModalStories,
        HilitModalStories,
        HilitWheelPickerStories,
        HilitProgressBarStories,
        HilitReportCardStories,
        HilitJDTextFieldStories,
        HilitAsyncTextFieldStories,
        HilitSubTextStories,
        PreviousInfoTextStories,
        BubbleFieldStories,
        FileUploadGuideStories,
        PdfUploadStories,
    ).validateStories()

internal val CatalogStoryTree: List<StoryTreeNode> = CatalogStories.buildStoryTree()
