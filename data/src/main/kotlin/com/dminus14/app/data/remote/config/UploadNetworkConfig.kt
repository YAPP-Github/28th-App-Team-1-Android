package com.dminus14.app.data.remote.config

internal object UploadNetworkConfig {
    const val BASE_URL = NetworkConfig.BASE_URL

    const val CONNECT_TIMEOUT_SECONDS = 60L
    const val READ_TIMEOUT_SECONDS = 300L
    const val WRITE_TIMEOUT_SECONDS = 300L

    const val MIME_PDF = "application/pdf"
    const val MIME_MP4 = "video/mp4"
    const val MIME_TEXT_PLAIN = "text/plain"

    const val PDF_PART_NAME = "pdf"
    const val VIDEO_PART_NAME = "video"
    const val METADATA_PART_NAME = "metadata"
}
