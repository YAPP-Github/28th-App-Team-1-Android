package com.dminus14.app.feature.mypage.portfolio

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.dminus14.app.core.common.pdf.PdfInvalidReason
import com.dminus14.app.core.common.pdf.PdfValidationResult
import com.dminus14.app.core.common.pdf.validatePdf
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/** [PortfolioFileReader.read] 결과. */
sealed interface PortfolioFileResult {
    data class Valid(
        val file: File,
        val fileName: String,
        val pageCount: Int,
    ) : PortfolioFileResult

    data class Invalid(
        val fileName: String,
        val reason: PdfInvalidReason,
    ) : PortfolioFileResult

    /** 파일을 읽을 수 없거나 알 수 없는 오류가 발생한 경우다. */
    data class Unreadable(
        val fileName: String,
    ) : PortfolioFileResult
}

/**
 * SAF `Uri`로 선택한 PDF를 검증하고 앱 캐시 디렉터리에만 임시 복사한다.
 *
 * `Uri`를 문자열로 받아 ViewModel이 Android 타입에 의존하지 않고도 이 계약을 대역으로 바꿔
 * 테스트할 수 있게 한다.
 */
interface PortfolioFileReader {
    suspend fun read(uri: String): PortfolioFileResult

    /** 화면 진입 시 이전 세션에서 남은 임시 파일을 정리한다. */
    suspend fun clearCache()
}

class PortfolioFileReaderImpl
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) : PortfolioFileReader {
        override suspend fun read(uri: String): PortfolioFileResult =
            withContext(Dispatchers.IO) {
                val parsedUri = uri.toUri()
                val fileName = queryFileName(parsedUri) ?: DEFAULT_FILE_NAME
                when (val validation = validatePdf(context.contentResolver, parsedUri)) {
                    is PdfValidationResult.Valid -> {
                        runCatching {
                            copyToCache(parsedUri, fileName, validation.pageCount)
                        }.getOrDefault(PortfolioFileResult.Unreadable(fileName))
                    }

                    is PdfValidationResult.Invalid -> {
                        PortfolioFileResult.Invalid(fileName, validation.reason)
                    }

                    is PdfValidationResult.Error -> {
                        PortfolioFileResult.Unreadable(fileName)
                    }
                }
            }

        override suspend fun clearCache() =
            withContext(Dispatchers.IO) {
                cacheDir().listFiles()?.forEach(File::delete)
                Unit
            }

        private fun copyToCache(
            uri: Uri,
            fileName: String,
            pageCount: Int,
        ): PortfolioFileResult.Valid {
            val target = File(cacheDir(), "${System.currentTimeMillis()}_$fileName")
            context.contentResolver.openInputStream(uri)?.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            } ?: error("PDF 파일을 열 수 없습니다.")
            return PortfolioFileResult.Valid(
                file = target,
                fileName = fileName,
                pageCount = pageCount,
            )
        }

        private fun queryFileName(uri: Uri): String? =
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
            }

        private fun cacheDir(): File = File(context.cacheDir, CACHE_SUB_DIR).apply { mkdirs() }

        private companion object {
            const val CACHE_SUB_DIR = "mypage_portfolio"
            const val DEFAULT_FILE_NAME = "portfolio.pdf"
        }
    }

@Module
@InstallIn(SingletonComponent::class)
abstract class PortfolioFileReaderModule {
    @Binds
    @Singleton
    abstract fun bindPortfolioFileReader(impl: PortfolioFileReaderImpl): PortfolioFileReader
}
