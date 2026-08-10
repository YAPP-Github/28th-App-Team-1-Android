package stories.components.designsystem.hilitreportcard

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.reportcard.HilitReportCard
import com.dminus14.designsystem.theme.HilitTheme

@CatalogControls
@Composable
internal fun HilitReportCardCatalogAdapter(
    date: String,
    title: String,
    expanded: Boolean,
) {
    HilitTheme {
        HilitReportCard(
            date = date,
            title = title,
            expanded = expanded,
            onExpandClick = {},
            onActionClick = {},
        )
    }
}
