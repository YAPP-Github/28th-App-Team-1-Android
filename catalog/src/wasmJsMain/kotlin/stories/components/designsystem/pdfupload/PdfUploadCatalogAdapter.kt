package stories.components.designsystem.pdfupload

import androidx.compose.runtime.Composable
import com.dminus14.catalog.controls.CatalogControls
import com.dminus14.designsystem.component.fileupload.PdfUpload
import com.dminus14.designsystem.component.fileupload.PdfUploadType

@CatalogControls
@Composable
internal fun PdfUploadCatalogAdapter(
    type: PdfUploadType,
    fileName: String,
    progress: Float,
) {
    PdfUpload(
        type = type,
        fileName = fileName,
        progress = progress,
        onCloseClick = {},
        onInfoClick = {},
        onRetryClick = {},
    )
}
