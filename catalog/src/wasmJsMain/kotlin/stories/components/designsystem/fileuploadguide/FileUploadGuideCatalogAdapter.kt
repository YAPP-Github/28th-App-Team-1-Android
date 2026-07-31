package stories.components.designsystem.fileuploadguide

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.fileupload.FileUploadGuide

@CatalogControls
@Composable
internal fun FileUploadGuideCatalogAdapter(
    title: String,
    description: String,
) {
    FileUploadGuide(
        title = title,
        description = description,
        onClick = {},
    )
}
