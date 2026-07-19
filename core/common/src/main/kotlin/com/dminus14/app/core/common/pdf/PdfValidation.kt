package com.dminus14.app.core.common.pdf

import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.system.ErrnoException
import android.system.Os
import android.system.OsConstants
import java.io.FileNotFoundException
import java.io.IOException

private const val MAX_PDF_SIZE_BYTES = 20L * 1024L * 1024L
private const val MIN_PDF_PAGE_COUNT = 1
private const val MAX_PDF_PAGE_COUNT = 30

/**
 * [uri]가 가리키는 PDF의 구조와 메타데이터가 유효한지 검사합니다.
 *
 * 이 함수는 블로킹 방식으로 동작하므로 작업자 스레드에서 호출해야 합니다. URI 권한을 유지하거나 문서를
 * 저장소에 복사하거나 문서 데이터를 기록하지 않고 로컬에서 읽기 전용으로 검사합니다.
 */
fun validatePdf(
    contentResolver: ContentResolver,
    uri: Uri,
): PdfValidationResult =
    try {
        contentResolver.openAssetFileDescriptor(uri, "r")?.use(::validateOpenedPdf)
            ?: PdfValidationResult.Error(PdfValidationErrorReason.FILE_NOT_ACCESSIBLE)
    } catch (_: FileNotFoundException) {
        PdfValidationResult.Error(PdfValidationErrorReason.FILE_NOT_ACCESSIBLE)
    } catch (_: SecurityException) {
        PdfValidationResult.Error(PdfValidationErrorReason.FILE_NOT_ACCESSIBLE)
    } catch (_: Exception) {
        PdfValidationResult.Error(PdfValidationErrorReason.UNKNOWN_ERROR)
    }

private fun validateOpenedPdf(assetFileDescriptor: AssetFileDescriptor): PdfValidationResult {
    val fileSizeResult = validatePdfFileSize(assetFileDescriptor.length)

    return if (fileSizeResult != PdfValidationResult.Valid) {
        fileSizeResult
    } else {
        assetFileDescriptor.parcelFileDescriptor.validatePdfFileDescriptor(
            fileSize = assetFileDescriptor.length,
        )
    }
}

private fun ParcelFileDescriptor.validatePdfFileDescriptor(fileSize: Long): PdfValidationResult =
    if (isSeekable()) {
        validatePdfPages(fileSize = fileSize)
    } else {
        PdfValidationResult.Invalid(PdfInvalidReason.NOT_SEEKABLE)
    }

/**
 * PDF 메타데이터가 유효한지 확인합니다.
 *
 * 파일 크기가 1바이트 이상 20MiB 이하이고 페이지 수가 1쪽 이상 30쪽 이하일 때만 [PdfValidationResult.Valid]을 반환합니다.
 * PDF 구조와 현재 조건에서의 열기 가능 여부는 호출 전에 `PdfRenderer`가 문서를 여는 과정에서 검증합니다.
 */
internal fun validatePdfMetadata(
    fileSize: Long,
    pageCount: Int,
): PdfValidationResult {
    val fileSizeResult = validatePdfFileSize(fileSize)
    if (fileSizeResult != PdfValidationResult.Valid) {
        return fileSizeResult
    }

    return if (pageCount in MIN_PDF_PAGE_COUNT..MAX_PDF_PAGE_COUNT) {
        PdfValidationResult.Valid
    } else {
        PdfValidationResult.Invalid(PdfInvalidReason.INVALID_PAGE_COUNT)
    }
}

private fun validatePdfFileSize(fileSize: Long): PdfValidationResult =
    when {
        fileSize == AssetFileDescriptor.UNKNOWN_LENGTH -> {
            PdfValidationResult.Invalid(PdfInvalidReason.UNKNOWN_FILE_SIZE)
        }

        fileSize !in 1L..MAX_PDF_SIZE_BYTES -> {
            PdfValidationResult.Invalid(PdfInvalidReason.INVALID_FILE_SIZE)
        }

        else -> {
            PdfValidationResult.Valid
        }
    }

/**
 * 현재 파일 디스크립터가 위치 이동을 지원하는지 확인합니다.
 *
 * 현재 위치를 바꾸지 않는 탐색을 시도하고, 탐색할 수 없는 디스크립터이면 `false`를 반환합니다.
 */
private fun ParcelFileDescriptor.isSeekable(): Boolean =
    try {
        Os.lseek(fileDescriptor, 0L, OsConstants.SEEK_CUR)
        true
    } catch (_: ErrnoException) {
        false
    }

/**
 * 파일 디스크립터를 복제해 PDF를 연 뒤 전체 페이지 수를 읽습니다.
 *
 * 복제한 디스크립터와 `PdfRenderer`는 읽기가 끝나면 즉시 닫습니다.
 */
private fun ParcelFileDescriptor.validatePdfPages(fileSize: Long): PdfValidationResult =
    try {
        ParcelFileDescriptor.dup(fileDescriptor).use { duplicate ->
            PdfRenderer(duplicate).use { renderer ->
                validatePdfMetadata(
                    fileSize = fileSize,
                    pageCount = renderer.pageCount,
                )
            }
        }
    } catch (_: SecurityException) {
        PdfValidationResult.Invalid(PdfInvalidReason.PASSWORD_REQUIRED)
    } catch (_: IOException) {
        PdfValidationResult.Invalid(PdfInvalidReason.INVALID_PDF_FORMAT)
    } catch (_: Exception) {
        PdfValidationResult.Error(PdfValidationErrorReason.UNKNOWN_ERROR)
    }
