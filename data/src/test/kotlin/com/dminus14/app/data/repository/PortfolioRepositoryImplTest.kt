package com.dminus14.app.data.repository

import com.dminus14.app.data.remote.datasource.PortfolioRemoteDataSource
import com.dminus14.app.data.remote.dto.PortfolioDeleteResponseDto
import com.dminus14.app.data.remote.dto.PortfolioDto
import com.dminus14.app.data.remote.dto.PortfolioListResponseDto
import com.dminus14.app.data.remote.dto.PortfolioUploadResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.CustomException
import com.dminus14.app.domain.exception.DeleteLimitExceededException
import com.dminus14.app.domain.exception.FileTooLargeException
import com.dminus14.app.domain.exception.InvalidFileTypeException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.PortfolioAlreadyExistsException
import com.dminus14.app.domain.exception.PortfolioDeleteBlockedByInterviewException
import com.dminus14.app.domain.exception.PortfolioNotFoundException
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UnknownException
import com.dminus14.app.domain.model.PortfolioStatus
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.IOException

class PortfolioRepositoryImplTest {
    @Test
    fun `개요 조회를 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakePortfolioRemoteDataSource()
        val repository = PortfolioRepositoryImpl(dataSource)

        val actual = runBlocking { repository.getPortfolioOverview() }

        assertEquals("portfolio-1", actual.portfolio?.portfolioId)
        assertEquals(PortfolioStatus.READY, actual.portfolio?.status)
        assertEquals(1, dataSource.getPortfoliosCallCount)
    }

    @Test
    fun `업로드를 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakePortfolioRemoteDataSource()
        val repository = PortfolioRepositoryImpl(dataSource)

        val actual =
            runBlocking {
                repository.uploadPortfolio(
                    file = File("resume.pdf"),
                    fileName = "resume.pdf",
                    fileSize = 1_024L,
                    pageCount = 3,
                    contentType = "application/pdf",
                )
            }

        assertEquals("portfolio-1", actual.portfolioId)
        assertEquals(PortfolioStatus.PROCESSING, actual.status)
        assertEquals("resume.pdf", dataSource.uploadedFileName)
    }

    @Test
    fun `상태 조회를 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakePortfolioRemoteDataSource()
        val repository = PortfolioRepositoryImpl(dataSource)

        val actual = runBlocking { repository.getPortfolioStatus("portfolio-1") }

        assertEquals("portfolio-1", actual.portfolioId)
        assertEquals("portfolio-1", dataSource.requestedStatusId)
    }

    @Test
    fun `삭제를 위임하고 도메인 결과를 반환한다`() {
        val dataSource = FakePortfolioRemoteDataSource()
        val repository = PortfolioRepositoryImpl(dataSource)

        val actual = runBlocking { repository.deletePortfolio("portfolio-1") }

        assertEquals("portfolio-1", actual.portfolioId)
        assertEquals("portfolio-1", dataSource.requestedDeleteId)
    }

    @Test
    fun `업로드 비즈니스 오류 코드를 도메인 오류로 변환한다`() {
        val cases =
            listOf(
                ApiErrorCode.INVALID_FILE_TYPE to InvalidFileTypeException::class.java,
                ApiErrorCode.FILE_TOO_LARGE to FileTooLargeException::class.java,
                ApiErrorCode.PORTFOLIO_ALREADY_EXISTS to
                    PortfolioAlreadyExistsException::class.java,
            )

        cases.forEach { (code, expectedType) ->
            val httpError = httpException(400, code)
            val repository =
                PortfolioRepositoryImpl(FakePortfolioRemoteDataSource(failure = httpError))

            val actual =
                captureFailure {
                    repository.uploadPortfolio(
                        file = File("resume.pdf"),
                        fileName = "resume.pdf",
                        fileSize = null,
                        pageCount = null,
                        contentType = "application/pdf",
                    )
                }

            assertTrue(expectedType.isInstance(actual))
            assertEquals(code, (actual as CustomException).errCode)
            assertSame(httpError, actual.cause)
        }
    }

    @Test
    fun `상태 조회 not found 오류를 도메인 오류로 변환한다`() {
        val httpError = httpException(404, ApiErrorCode.PORTFOLIO_NOT_FOUND)
        val repository = PortfolioRepositoryImpl(FakePortfolioRemoteDataSource(failure = httpError))

        val actual = captureFailure { repository.getPortfolioStatus("portfolio-1") }

        assertTrue(actual is PortfolioNotFoundException)
        assertEquals(ApiErrorCode.PORTFOLIO_NOT_FOUND, (actual as CustomException).errCode)
        assertSame(httpError, actual.cause)
    }

    @Test
    fun `삭제 비즈니스 오류 코드를 도메인 오류로 변환한다`() {
        val cases =
            listOf(
                ApiErrorCode.PORTFOLIO_NOT_FOUND to PortfolioNotFoundException::class.java,
                ApiErrorCode.PORTFOLIO_DELETE_BLOCKED_BY_INTERVIEW to
                    PortfolioDeleteBlockedByInterviewException::class.java,
                ApiErrorCode.DELETE_LIMIT_EXCEEDED to DeleteLimitExceededException::class.java,
            )

        cases.forEach { (code, expectedType) ->
            val httpError = httpException(409, code)
            val repository =
                PortfolioRepositoryImpl(FakePortfolioRemoteDataSource(failure = httpError))

            val actual = captureFailure { repository.deletePortfolio("portfolio-1") }

            assertTrue(expectedType.isInstance(actual))
            assertEquals(code, (actual as CustomException).errCode)
            assertSame(httpError, actual.cause)
        }
    }

    @Test
    fun `공통 네트워크 서버 알 수 없는 오류 정책을 유지한다`() {
        val cases =
            listOf(
                IOException("synthetic offline") to NetworkUnavailableException::class.java,
                httpException(500, "SYNTHETIC_SERVER_ERROR") to ServerException::class.java,
                IllegalStateException("synthetic invalid state") to UnknownException::class.java,
            )

        cases.forEach { (failure, expectedType) ->
            val repository =
                PortfolioRepositoryImpl(FakePortfolioRemoteDataSource(failure = failure))

            val actual = captureFailure { repository.getPortfolioOverview() }

            assertTrue(expectedType.isInstance(actual))
            assertSame(failure, actual.cause)
        }
    }

    private fun captureFailure(block: suspend () -> Unit): Throwable {
        try {
            runBlocking { block() }
        } catch (error: Throwable) {
            return error
        }
        throw AssertionError("예외가 발생해야 합니다.")
    }

    private fun httpException(
        status: Int,
        code: String,
    ): HttpException {
        val body =
            """
            {"success":false,"code":"$code","message":"synthetic $code"}
            """.trimIndent()
                .toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Unit>(status, body))
    }

    private class FakePortfolioRemoteDataSource(
        private val failure: Throwable? = null,
    ) : PortfolioRemoteDataSource {
        var getPortfoliosCallCount = 0
            private set
        var uploadedFileName: String? = null
            private set
        var requestedStatusId: String? = null
            private set
        var requestedDeleteId: String? = null
            private set

        override suspend fun getPortfolios(): PortfolioListResponseDto {
            failure?.let { throw it }
            getPortfoliosCallCount += 1
            return PortfolioListResponseDto(
                portfolios =
                    listOf(
                        PortfolioDto(
                            portfolioId = "portfolio-1",
                            fileName = "resume.pdf",
                            fileSize = 1_024L,
                            pageCount = 3,
                            status = "READY",
                            uploadedAt = "2026-08-04T00:00:00Z",
                        ),
                    ),
            )
        }

        override suspend fun uploadPortfolio(
            file: File,
            fileName: String,
            fileSize: Long?,
            pageCount: Int?,
            contentType: String,
        ): PortfolioUploadResponseDto {
            failure?.let { throw it }
            uploadedFileName = fileName
            return PortfolioUploadResponseDto(
                portfolioId = "portfolio-1",
                status = "PROCESSING",
                message = null,
            )
        }

        override suspend fun getPortfolioStatus(portfolioId: String): PortfolioUploadResponseDto {
            failure?.let { throw it }
            requestedStatusId = portfolioId
            return PortfolioUploadResponseDto(
                portfolioId = "portfolio-1",
                status = "READY",
                message = null,
            )
        }

        override suspend fun deletePortfolio(portfolioId: String): PortfolioDeleteResponseDto {
            failure?.let { throw it }
            requestedDeleteId = portfolioId
            return PortfolioDeleteResponseDto(
                portfolioId = "portfolio-1",
                deletedAt = "2026-08-04T00:00:00Z",
            )
        }
    }
}
