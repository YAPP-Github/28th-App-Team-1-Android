package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.PortfolioApi
import com.dminus14.app.data.remote.dto.ApiResponseDto
import com.dminus14.app.data.remote.dto.PortfolioDeleteResponseDto
import com.dminus14.app.data.remote.dto.PortfolioDto
import com.dminus14.app.data.remote.dto.PortfolioListResponseDto
import com.dminus14.app.data.remote.dto.PortfolioUploadResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.data.remote.multipart.PortfolioPartFactory
import com.dminus14.app.domain.exception.ServerException
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.File

class PortfolioRemoteDataSourceTest {
    @Test
    fun `목록 응답을 그대로 반환한다`() {
        val expected = PortfolioListResponseDto(portfolios = listOf(samplePortfolioDto()))
        val api = FakePortfolioApi(listResponse = ApiResponseDto(success = true, data = expected))
        val dataSource = PortfolioRemoteDataSourceImpl(api, PortfolioPartFactory())

        val actual = runBlocking { dataSource.getPortfolios() }

        assertSame(expected, actual)
    }

    @Test
    fun `목록 data가 null이면 ServerException을 던진다`() {
        val api = FakePortfolioApi(listResponse = ApiResponseDto(success = true, data = null))
        val dataSource = PortfolioRemoteDataSourceImpl(api, PortfolioPartFactory())

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.getPortfolios() }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("포트폴리오 목록 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `업로드는 파트를 만들어 쿼리와 함께 전송하고 응답을 반환한다`() {
        val expected =
            PortfolioUploadResponseDto(
                portfolioId = "portfolio-1",
                status = "PROCESSING",
                message = null,
            )
        val api = FakePortfolioApi(uploadResponse = ApiResponseDto(success = true, data = expected))
        val dataSource = PortfolioRemoteDataSourceImpl(api, PortfolioPartFactory())
        val file = createTempPdf()

        val actual =
            runBlocking {
                dataSource.uploadPortfolio(
                    file = file,
                    fileName = "resume.pdf",
                    fileSize = 1_024L,
                    pageCount = 3,
                    contentType = "application/pdf",
                )
            }

        assertSame(expected, actual)
        assertEquals("resume.pdf", api.uploadedFileName)
        assertEquals(1_024L, api.uploadedFileSize)
        assertEquals(3, api.uploadedPageCount)
        assertEquals("application/pdf", api.uploadedContentType)
        assertEquals(
            "pdf",
            api.uploadedPart
                ?.body
                ?.contentType()
                ?.subtype,
        )
    }

    @Test
    fun `업로드 data가 null이면 ServerException을 던진다`() {
        val api = FakePortfolioApi(uploadResponse = ApiResponseDto(success = true, data = null))
        val dataSource = PortfolioRemoteDataSourceImpl(api, PortfolioPartFactory())

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking {
                    dataSource.uploadPortfolio(
                        file = createTempPdf(),
                        fileName = "resume.pdf",
                        fileSize = null,
                        pageCount = null,
                        contentType = "application/pdf",
                    )
                }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("포트폴리오 업로드 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `상태 응답을 그대로 반환한다`() {
        val expected =
            PortfolioUploadResponseDto(
                portfolioId = "portfolio-1",
                status = "READY",
                message = null,
            )
        val api = FakePortfolioApi(statusResponse = ApiResponseDto(success = true, data = expected))
        val dataSource = PortfolioRemoteDataSourceImpl(api, PortfolioPartFactory())

        val actual = runBlocking { dataSource.getPortfolioStatus("portfolio-1") }

        assertSame(expected, actual)
        assertEquals("portfolio-1", api.requestedStatusId)
    }

    @Test
    fun `상태 data가 null이면 ServerException을 던진다`() {
        val api = FakePortfolioApi(statusResponse = ApiResponseDto(success = true, data = null))
        val dataSource = PortfolioRemoteDataSourceImpl(api, PortfolioPartFactory())

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.getPortfolioStatus("portfolio-1") }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("포트폴리오 상태 응답이 비어 있습니다.", actual.message)
    }

    @Test
    fun `삭제 응답을 그대로 반환한다`() {
        val expected =
            PortfolioDeleteResponseDto(
                portfolioId = "portfolio-1",
                deletedAt = "2026-08-04T00:00:00Z",
            )
        val api = FakePortfolioApi(deleteResponse = ApiResponseDto(success = true, data = expected))
        val dataSource = PortfolioRemoteDataSourceImpl(api, PortfolioPartFactory())

        val actual = runBlocking { dataSource.deletePortfolio("portfolio-1") }

        assertSame(expected, actual)
        assertEquals("portfolio-1", api.requestedDeleteId)
    }

    @Test
    fun `삭제 data가 null이면 ServerException을 던진다`() {
        val api = FakePortfolioApi(deleteResponse = ApiResponseDto(success = true, data = null))
        val dataSource = PortfolioRemoteDataSourceImpl(api, PortfolioPartFactory())

        val actual =
            assertThrows(ServerException::class.java) {
                runBlocking { dataSource.deletePortfolio("portfolio-1") }
            }

        assertEquals(ApiErrorCode.SERVER_ERROR, actual.errCode)
        assertEquals("포트폴리오 삭제 응답이 비어 있습니다.", actual.message)
    }

    private class FakePortfolioApi(
        private val listResponse: ApiResponseDto<PortfolioListResponseDto> =
            ApiResponseDto(
                success = true,
                data = PortfolioListResponseDto(portfolios = emptyList()),
            ),
        private val uploadResponse: ApiResponseDto<PortfolioUploadResponseDto> =
            ApiResponseDto(
                success = true,
                data =
                    PortfolioUploadResponseDto(
                        portfolioId = "portfolio-1",
                        status = "PROCESSING",
                        message = null,
                    ),
            ),
        private val statusResponse: ApiResponseDto<PortfolioUploadResponseDto> =
            ApiResponseDto(
                success = true,
                data =
                    PortfolioUploadResponseDto(
                        portfolioId = "portfolio-1",
                        status = "PROCESSING",
                        message = null,
                    ),
            ),
        private val deleteResponse: ApiResponseDto<PortfolioDeleteResponseDto> =
            ApiResponseDto(
                success = true,
                data =
                    PortfolioDeleteResponseDto(
                        portfolioId = "portfolio-1",
                        deletedAt = "2026-08-04T00:00:00Z",
                    ),
            ),
    ) : PortfolioApi {
        var uploadedFileName: String? = null
            private set
        var uploadedFileSize: Long? = null
            private set
        var uploadedPageCount: Int? = null
            private set
        var uploadedContentType: String? = null
            private set
        var uploadedPart: MultipartBody.Part? = null
            private set
        var requestedStatusId: String? = null
            private set
        var requestedDeleteId: String? = null
            private set

        override suspend fun uploadPortfolio(
            fileName: String,
            fileSize: Long?,
            pageCount: Int?,
            contentType: String,
            file: MultipartBody.Part,
        ): ApiResponseDto<PortfolioUploadResponseDto> {
            uploadedFileName = fileName
            uploadedFileSize = fileSize
            uploadedPageCount = pageCount
            uploadedContentType = contentType
            uploadedPart = file
            return uploadResponse
        }

        override suspend fun getPortfolios(): ApiResponseDto<PortfolioListResponseDto> =
            listResponse

        override suspend fun getPortfolioStatus(
            portfolioId: String,
        ): ApiResponseDto<PortfolioUploadResponseDto> {
            requestedStatusId = portfolioId
            return statusResponse
        }

        override suspend fun deletePortfolio(
            portfolioId: String,
        ): ApiResponseDto<PortfolioDeleteResponseDto> {
            requestedDeleteId = portfolioId
            return deleteResponse
        }
    }

    private companion object {
        fun samplePortfolioDto(): PortfolioDto =
            PortfolioDto(
                portfolioId = "portfolio-1",
                fileName = "resume.pdf",
                fileSize = 1_024L,
                pageCount = 3,
                status = "READY",
                uploadedAt = "2026-08-04T00:00:00Z",
            )

        fun createTempPdf(): File =
            File.createTempFile("portfolio-test", ".pdf").apply {
                writeBytes(byteArrayOf(0x25, 0x50, 0x44, 0x46))
                deleteOnExit()
            }
    }
}
