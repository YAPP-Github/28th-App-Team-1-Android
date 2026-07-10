package com.dminus14.app.core.common.pdf

import android.content.ContentResolver
import android.content.res.AssetFileDescriptor
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

@RunWith(AndroidJUnit4::class)
class PdfValidationInstrumentedTest {
    /** 한 페이지로 구성된 정상 PDF가 유효한 것으로 판정되는지 확인합니다. */
    @Test
    fun validPdf_returnsTrue() {
        val pdf = createPdf(pageCount = 1)

        withPdfResolver(pdf) { contentResolver, uri ->
            assertTrue(isValidPdf(contentResolver, uri))
        }
    }

    /** 허용되는 최대 페이지 수인 30쪽 PDF가 유효한 것으로 판정되는지 확인합니다. */
    @Test
    fun thirtyPagePdf_returnsTrue() {
        val pdf = createPdf(pageCount = 30)

        withPdfResolver(pdf) { contentResolver, uri ->
            assertTrue(isValidPdf(contentResolver, uri))
        }
    }

    /** 허용 범위를 초과한 31쪽 PDF가 유효하지 않은 것으로 판정되는지 확인합니다. */
    @Test
    fun thirtyOnePagePdf_returnsFalse() {
        val pdf = createPdf(pageCount = 31)

        withPdfResolver(pdf) { contentResolver, uri ->
            assertFalse(isValidPdf(contentResolver, uri))
        }
    }

    /** 암호가 있어야 열 수 있는 PDF가 유효하지 않은 것으로 판정되는지 확인합니다. */
    @Test
    fun encryptedPdfRequiringPassword_returnsFalse() {
        val pdf = createPasswordProtectedPdf()

        withPdfResolver(pdf) { contentResolver, uri ->
            assertFalse(isValidPdf(contentResolver, uri))
        }
    }

    /** PDF 헤더만 흉내 낸 손상 데이터가 유효하지 않은 것으로 판정되는지 확인합니다. */
    @Test
    fun malformedPdf_returnsFalse() {
        val malformedPdf = "%PDF-1.7\nsynthetic-invalid-data".encodeToByteArray()

        withPdfResolver(malformedPdf) { contentResolver, uri ->
            assertFalse(isValidPdf(contentResolver, uri))
        }
    }

    /** 길이를 알 수 없는 파일을 거부하고 획득한 디스크립터를 닫는지 확인합니다. */
    @Test
    fun unknownLength_returnsFalseAndClosesDescriptor() {
        val contentResolver = mockk<ContentResolver>()
        val assetFileDescriptor = mockk<AssetFileDescriptor>()
        every { contentResolver.openAssetFileDescriptor(TEST_URI, "r") } returns assetFileDescriptor
        every { assetFileDescriptor.length } returns AssetFileDescriptor.UNKNOWN_LENGTH
        every { assetFileDescriptor.close() } returns Unit

        assertFalse(isValidPdf(contentResolver, TEST_URI))
        verify(exactly = 1) { assetFileDescriptor.close() }
    }

    /** 접근할 수 없는 URI를 예외로 노출하지 않고 유효하지 않은 것으로 판정하는지 확인합니다. */
    @Test
    fun inaccessibleUri_returnsFalse() {
        val contentResolver = mockk<ContentResolver>()
        every {
            contentResolver.openAssetFileDescriptor(TEST_URI, "r")
        } throws FileNotFoundException()

        assertFalse(isValidPdf(contentResolver, TEST_URI))
    }

    /** 위치 이동을 지원하지 않는 파이프 디스크립터를 유효하지 않은 것으로 판정하는지 확인합니다. */
    @Test
    fun unseekableDescriptor_returnsFalse() {
        val contentResolver = mockk<ContentResolver>()
        val pipe = ParcelFileDescriptor.createPipe()
        val assetFileDescriptor = AssetFileDescriptor(pipe[0], 0L, 1L)
        every { contentResolver.openAssetFileDescriptor(TEST_URI, "r") } returns assetFileDescriptor

        try {
            assertFalse(isValidPdf(contentResolver, TEST_URI))
        } finally {
            pipe[1].close()
        }
    }

    /** Android `PdfDocument`로 식별 정보가 없는 지정 페이지 수의 합성 PDF를 생성합니다. */
    private fun createPdf(pageCount: Int): ByteArray {
        val document = PdfDocument()
        return try {
            repeat(pageCount) { pageIndex ->
                val pageInfo = PdfDocument.PageInfo.Builder(1, 1, pageIndex + 1).create()
                val page = document.startPage(pageInfo)
                document.finishPage(page)
            }

            ByteArrayOutputStream().use { output ->
                document.writeTo(output)
                output.toByteArray()
            }
        } finally {
            document.close()
        }
    }

    /** PDFBox로 식별 정보가 없는 암호 보호 합성 PDF를 생성합니다. */
    private fun createPasswordProtectedPdf(): ByteArray =
        PDDocument().use { document ->
            document.addPage(PDPage())
            document.protect(
                StandardProtectionPolicy(
                    "synthetic-owner-password",
                    "synthetic-user-password",
                    AccessPermission(),
                ),
            )

            ByteArrayOutputStream().use { output ->
                document.save(output)
                output.toByteArray()
            }
        }

    /**
     * 합성 PDF를 앱 캐시에 임시 저장하고 읽기 전용 디스크립터를 제공해 [assertion]을 실행합니다.
     *
     * 테스트가 끝나면 성공 여부와 관계없이 임시 파일을 삭제합니다.
     */
    private fun withPdfResolver(
        pdf: ByteArray,
        assertion: (ContentResolver, Uri) -> Unit,
    ) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val temporaryPdf = File.createTempFile("synthetic-pdf-", ".pdf", context.cacheDir)
        try {
            temporaryPdf.writeBytes(pdf)
            val descriptor =
                ParcelFileDescriptor.open(temporaryPdf, ParcelFileDescriptor.MODE_READ_ONLY)
            val assetFileDescriptor =
                AssetFileDescriptor(descriptor, 0L, temporaryPdf.length())
            val contentResolver = mockk<ContentResolver>()
            every {
                contentResolver.openAssetFileDescriptor(TEST_URI, "r")
            } returns assetFileDescriptor

            assertion(contentResolver, TEST_URI)
        } finally {
            temporaryPdf.delete()
        }
    }

    companion object {
        private val TEST_URI = Uri.parse("content://com.dminus14.test/synthetic.pdf")
    }
}
