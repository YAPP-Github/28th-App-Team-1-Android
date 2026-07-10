package com.dminus14.app.core.common.pdf

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfValidationRulesTest {
    /** 최대 허용 크기와 최대 허용 페이지 수에 해당하는 PDF 메타데이터를 승인하는지 확인합니다. */
    @Test
    fun validMetadata_returnsTrue() {
        assertTrue(
            isValidPdfMetadata(
                fileSize = 20L * 1024L * 1024L,
                pageCount = 30,
            ),
        )
    }

    /** 파일 크기가 0 이하인 PDF 메타데이터를 거부하는지 확인합니다. */
    @Test
    fun nonPositiveSize_returnsFalse() {
        assertFalse(isValidPdfMetadata(fileSize = 0L, pageCount = 1))
    }

    /** 최대 허용 크기인 20MiB를 1바이트 초과한 PDF 메타데이터를 거부하는지 확인합니다. */
    @Test
    fun sizeOverTwentyMebibytes_returnsFalse() {
        assertFalse(
            isValidPdfMetadata(
                fileSize = 20L * 1024L * 1024L + 1L,
                pageCount = 1,
            ),
        )
    }

    /** 페이지 수가 0인 PDF 메타데이터를 거부하는지 확인합니다. */
    @Test
    fun zeroPages_returnsFalse() {
        assertFalse(isValidPdfMetadata(fileSize = 1L, pageCount = 0))
    }

    /** 최대 허용 페이지 수를 초과한 31쪽 PDF 메타데이터를 거부하는지 확인합니다. */
    @Test
    fun thirtyOnePages_returnsFalse() {
        assertFalse(isValidPdfMetadata(fileSize = 1L, pageCount = 31))
    }
}
