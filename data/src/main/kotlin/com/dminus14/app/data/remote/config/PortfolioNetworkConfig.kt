package com.dminus14.app.data.remote.config

internal object PortfolioNetworkConfig {
    const val BASE_URL = NetworkConfig.BASE_URL

    const val CONNECT_TIMEOUT_SECONDS = 60L
    const val READ_TIMEOUT_SECONDS = 300L
    const val WRITE_TIMEOUT_SECONDS = 300L

    const val MIME_PDF = "application/pdf"

    const val PDF_PART_NAME = "pdf"
}
