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
        /**
         * 서버로 보낼 PDF 멀티파트를 만든다.
         *
         * @param file 실제 바이너리(캐시된 로컬 파일). 파일명은 저장 로직상 랜덤일 수 있다.
         * @param fileName 서버가 canonical 파일명으로 저장할 원본 이름. Content-Disposition의
         *   `filename` 값으로 실려 로그·디버깅과 서버 저장 이름이 일치하게 한다.
         * @param fieldName 파트 필드명(기본값 사용 권장).
         */
        fun createPdfPart(
            file: File,
            fileName: String,
            fieldName: String = PortfolioNetworkConfig.PDF_PART_NAME,
        ): MultipartBody.Part {
            require(file.exists()) { "PDF file does not exist: ${file.path}" }

            val requestBody = file.asRequestBody(PortfolioNetworkConfig.MIME_PDF.toMediaType())
            return MultipartBody.Part.createFormData(fieldName, fileName, requestBody)
        }
    }
