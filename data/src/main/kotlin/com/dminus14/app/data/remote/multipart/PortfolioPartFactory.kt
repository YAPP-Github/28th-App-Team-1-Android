package com.dminus14.app.data.remote.multipart

import com.dminus14.app.data.remote.config.PortfolioNetworkConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class PortfolioPartFactory
    @Inject
    constructor() {
        fun createPdfPart(
            fieldName: String = PortfolioNetworkConfig.PDF_PART_NAME,
            file: File,
        ): MultipartBody.Part {
            require(file.exists()) { "PDF file does not exist: ${file.path}" }

            val requestBody = file.asRequestBody(PortfolioNetworkConfig.MIME_PDF.toMediaType())
            return MultipartBody.Part.createFormData(fieldName, file.name, requestBody)
        }
    }
