package com.dminus14.app.core.common.pdf

import android.content.res.AssetFileDescriptor
import org.junit.Assert.assertEquals
import org.junit.Test

class PdfValidationRulesTest {
    /** 최대 허용 크기와 최대 허용 페이지 수에 해당하는 PDF 메타데이터를 승인하는지 확인합니다. */
    @Test
    fun `유효한 메타데이터는 유효함을 반환한다`() {
        assertEquals(
            PdfValidationResult.Valid(pageCount = 30),
            validatePdfMetadata(
                fileSize = 20L * 1024L * 1024L,
                pageCount = 30,
            ),
        )
    }

    /** 파일 크기가 0 이하인 PDF 메타데이터를 거부하는지 확인합니다. */
    @Test
    fun `0 이하의 크기는 유효하지 않은 파일 크기를 반환한다`() {
        assertEquals(
            PdfValidationResult.Invalid(PdfInvalidReason.INVALID_FILE_SIZE),
            validatePdfMetadata(fileSize = 0L, pageCount = 1),
        )
    }

    /** 최대 허용 크기인 20MiB를 1바이트 초과한 PDF 메타데이터를 거부하는지 확인합니다. */
    @Test
    fun `20메비바이트를 초과하는 크기는 유효하지 않은 파일 크기를 반환한다`() {
        assertEquals(
            PdfValidationResult.Invalid(PdfInvalidReason.INVALID_FILE_SIZE),
            validatePdfMetadata(
                fileSize = 20L * 1024L * 1024L + 1L,
                pageCount = 1,
            ),
        )
    }

    /** 파일 크기를 알 수 없는 PDF 메타데이터에 별도 결과를 반환하는지 확인합니다. */
    @Test
    fun `알 수 없는 크기는 알 수 없는 파일 크기를 반환한다`() {
        assertEquals(
            PdfValidationResult.Invalid(PdfInvalidReason.UNKNOWN_FILE_SIZE),
            validatePdfMetadata(
                fileSize = AssetFileDescriptor.UNKNOWN_LENGTH,
                pageCount = 1,
            ),
        )
    }

    /** 페이지 수가 0인 PDF 메타데이터를 거부하는지 확인합니다. */
    @Test
    fun `0페이지는 유효하지 않은 페이지 수를 반환한다`() {
        assertEquals(
            PdfValidationResult.Invalid(PdfInvalidReason.INVALID_PAGE_COUNT),
            validatePdfMetadata(fileSize = 1L, pageCount = 0),
        )
    }

    /** 최대 허용 페이지 수를 초과한 31쪽 PDF 메타데이터를 거부하는지 확인합니다. */
    @Test
    fun `31페이지는 유효하지 않은 페이지 수를 반환한다`() {
        assertEquals(
            PdfValidationResult.Invalid(PdfInvalidReason.INVALID_PAGE_COUNT),
            validatePdfMetadata(fileSize = 1L, pageCount = 31),
        )
    }
}
