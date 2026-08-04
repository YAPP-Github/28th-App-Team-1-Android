package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.model.PortfolioUploadResult
import com.dminus14.app.domain.repository.PortfolioRepository
import com.dminus14.app.domain.util.runCatchingCancellable
import java.io.File
import javax.inject.Inject

class UploadPortfolioUseCase
    @Inject
    constructor(
        private val portfolioRepository: PortfolioRepository,
    ) {
        suspend operator fun invoke(
            file: File,
            fileName: String,
            fileSize: Long? = file.length().takeIf { it > 0L },
            pageCount: Int? = null,
            contentType: String = DEFAULT_CONTENT_TYPE,
        ): Result<PortfolioUploadResult> =
            runCatchingCancellable {
                portfolioRepository.uploadPortfolio(
                    file = file,
                    fileName = fileName,
                    fileSize = fileSize,
                    pageCount = pageCount,
                    contentType = contentType,
                )
            }

        private companion object {
            const val DEFAULT_CONTENT_TYPE = "application/pdf"
        }
    }
