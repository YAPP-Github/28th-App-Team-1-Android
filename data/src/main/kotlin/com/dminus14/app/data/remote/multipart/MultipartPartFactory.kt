package com.dminus14.app.data.remote.multipart

import com.dminus14.app.data.remote.config.UploadNetworkConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class MultipartPartFactory
    @Inject
    constructor() {
        fun createPdfPart(
            fieldName: String = UploadNetworkConfig.PDF_PART_NAME,
            file: File,
        ): MultipartBody.Part {
            require(file.exists()) { "PDF file does not exist: ${file.path}" }

            val requestBody = file.asRequestBody(UploadNetworkConfig.MIME_PDF.toMediaType())
            return MultipartBody.Part.createFormData(fieldName, file.name, requestBody)
        }

        fun createVideoPart(
            fieldName: String = UploadNetworkConfig.VIDEO_PART_NAME,
            file: File,
            mimeType: String = UploadNetworkConfig.MIME_MP4,
        ): MultipartBody.Part {
            require(file.exists()) { "Video file does not exist: ${file.path}" }

            val requestBody = file.asRequestBody(mimeType.toMediaType())
            return MultipartBody.Part.createFormData(fieldName, file.name, requestBody)
        }

        fun createMetadataPart(
            fieldName: String = UploadNetworkConfig.METADATA_PART_NAME,
            value: String,
        ): MultipartBody.Part {
            val requestBody = value.toRequestBody(UploadNetworkConfig.MIME_TEXT_PLAIN.toMediaType())
            return MultipartBody.Part.createFormData(fieldName, value, requestBody)
        }
    }
