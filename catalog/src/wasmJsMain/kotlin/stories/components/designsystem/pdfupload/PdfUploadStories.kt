package stories.components.designsystem.pdfupload

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dminus14.designsystem.component.fileupload.PdfUpload
import com.dminus14.designsystem.component.fileupload.PdfUploadType
import type.Story
import type.StoryGroup

internal val PdfUploadStories =
    StoryGroup(
        path = "Components/PdfUpload",
        description = "PDF 업로드 상태. Ready / Processing / Completed / Failed.",
        stories =
            listOf(
                Story(
                    id = "default",
                    title = "기본",
                    description =
                        "Controls로 type·fileName·progress를 조절할 수 있다. " +
                            "Processing은 Gray200 배경 위를 HilitGreen500으로 왼쪽부터 채운다.",
                ) {
                    PdfUploadCatalogAdapterControls(
                        initialArgs =
                            PdfUploadCatalogAdapterArgs(
                                type = PdfUploadType.Processing,
                                fileName = "{파일명}.pdf",
                                progress = 50f,
                                buttonText = "버튼",
                            ),
                    )
                },
                Story(
                    id = "all-types",
                    title = "전체 상태",
                    description = "Ready / Processing / Completed / Failed를 한눈에 보기.",
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        PdfUpload(
                            type = PdfUploadType.Ready,
                            progress = 0f,
                        )
                        PdfUpload(
                            type = PdfUploadType.Processing,
                            fileName = "{파일명}.pdf",
                            progress = 50f,
                            onCloseClick = {},
                        )
                        PdfUpload(
                            type = PdfUploadType.Completed,
                            fileName = "{파일명}.pdf",
                            progress = 100f,
                            onCloseClick = {},
                        )
                        PdfUpload(
                            type = PdfUploadType.Failed,
                            fileName = "{파일명}.pdf",
                            progress = 0f,
                            onInfoClick = {},
                            onRetryClick = {},
                        )
                    }
                },
            ),
    )
