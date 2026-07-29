package stories.components.designsystem.hilittexthighlight

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.buildAnnotatedString
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.text.HilitText
import com.dminus14.designsystem.component.text.HilitTextHighlightColor
import com.dminus14.designsystem.component.text.withHilitTextHighlight
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitTextHighlightCatalogAdapter(
    prefix: String,
    highlight: String,
    suffix: String,
    highlightColor: HilitTextHighlightColor,
) {
    HilitTheme {
        HilitText(
            text =
                buildAnnotatedString {
                    append(prefix)
                    withHilitTextHighlight {
                        append(highlight)
                    }
                    append(suffix)
                },
            style = HilitTheme.typography.body4,
            highlightColor = highlightColor,
        )
    }
}
