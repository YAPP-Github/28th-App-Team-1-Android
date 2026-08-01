package com.dminus14.app.domain.usecase

import com.dminus14.app.domain.exception.ConsentValidationException
import com.dminus14.app.domain.model.ConsentDocument
import com.dminus14.app.domain.model.ConsentItem
import com.dminus14.app.domain.model.ConsentItemCode
import com.dminus14.app.domain.model.ConsentPendingStatus
import com.dminus14.app.domain.model.ConsentSubmission
import com.dminus14.app.domain.model.ConsentSubmissionItem
import com.dminus14.app.domain.model.PendingConsentList
import com.dminus14.app.domain.repository.ConsentRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsentUseCaseTest {
    @Test
    fun `pending 목록 조회는 저장소를 한 번 호출한다`() =
        runTest {
            val repository = FakeConsentRepository()
            val useCase = GetPendingConsentListUseCase(repository)

            val result = useCase()

            assertSame(repository.pendingList, result.getOrThrow())
            assertEquals(1, repository.getPendingCallCount)
        }

    @Test
    fun `문서 조회 시 코드 양끝 공백을 제거하고 저장소를 호출한다`() =
        runTest {
            val repository = FakeConsentRepository()
            val useCase = GetConsentDocumentUseCase(repository)

            val result = useCase(rawCode = "  TERMS_OF_SERVICE  ", version = 2)

            assertSame(repository.document, result.getOrThrow())
            assertEquals("TERMS_OF_SERVICE", repository.requestedDocumentCode)
            assertEquals(2, repository.requestedDocumentVersion)
            assertEquals(1, repository.getDocumentCallCount)
        }

    @Test
    fun `빈 코드나 잘못된 버전이면 문서 저장소를 호출하지 않는다`() =
        runTest {
            val repository = FakeConsentRepository()
            val useCase = GetConsentDocumentUseCase(repository)

            val emptyCode = useCase(rawCode = "  ", version = 1)
            val invalidVersion = useCase(rawCode = "TERMS_OF_SERVICE", version = 0)

            assertTrue(emptyCode.exceptionOrNull() is ConsentValidationException)
            assertTrue(invalidVersion.exceptionOrNull() is ConsentValidationException)
            assertEquals(0, repository.getDocumentCallCount)
        }

    @Test
    fun `제출 항목을 정규화해 저장소에 전달한다`() =
        runTest {
            val repository = FakeConsentRepository()
            val useCase = SubmitConsentUseCase(repository)
            val submission =
                ConsentSubmission(
                    items =
                        listOf(
                            ConsentSubmissionItem(
                                rawCode = "  TERMS_OF_SERVICE  ",
                                version = 1,
                                agreed = true,
                            ),
                        ),
                )

            val result = useCase(submission)

            assertTrue(result.isSuccess)
            assertEquals(1, repository.submitCallCount)
            assertEquals(
                listOf(
                    ConsentSubmissionItem(
                        rawCode = "TERMS_OF_SERVICE",
                        version = 1,
                        agreed = true,
                    ),
                ),
                repository.submitted?.items,
            )
        }

    @Test
    fun `빈 제출이면 저장소를 호출하지 않는다`() =
        runTest {
            val repository = FakeConsentRepository()
            val useCase = SubmitConsentUseCase(repository)

            val result = useCase(ConsentSubmission(items = emptyList()))

            assertTrue(result.exceptionOrNull() is ConsentValidationException)
            assertEquals(0, repository.submitCallCount)
        }

    @Test
    fun `필수 항목만 requiredItems로 분리한다`() {
        val list =
            PendingConsentList(
                status = ConsentPendingStatus.NOT_SUBMITTED,
                items =
                    listOf(
                        consentItem(rawCode = "TERMS_OF_SERVICE", isRequired = true),
                        consentItem(rawCode = "OPTIONAL_ITEM", isRequired = false),
                    ),
            )

        assertEquals(listOf("TERMS_OF_SERVICE"), list.requiredItems.map { it.rawCode })
    }

    private fun consentItem(
        rawCode: String,
        isRequired: Boolean,
    ): ConsentItem =
        ConsentItem(
            code = ConsentItemCode.fromRaw(rawCode),
            rawCode = rawCode,
            label = "합성 라벨",
            version = 1,
            isRequired = isRequired,
            hasDocument = true,
        )

    private class FakeConsentRepository(
        val pendingList: PendingConsentList =
            PendingConsentList(
                status = ConsentPendingStatus.NOT_SUBMITTED,
                items =
                    listOf(
                        ConsentItem(
                            code = ConsentItemCode.TERMS_OF_SERVICE,
                            rawCode = "TERMS_OF_SERVICE",
                            label = "서비스 이용약관",
                            version = 1,
                            isRequired = true,
                            hasDocument = true,
                        ),
                    ),
            ),
        val document: ConsentDocument =
            ConsentDocument(
                code = ConsentItemCode.TERMS_OF_SERVICE,
                rawCode = "TERMS_OF_SERVICE",
                title = "서비스 이용약관",
                version = 2,
                contentMarkdown = "합성 예시 본문",
            ),
    ) : ConsentRepository {
        var getPendingCallCount = 0
            private set
        var getDocumentCallCount = 0
            private set
        var submitCallCount = 0
            private set
        var requestedDocumentCode: String? = null
            private set
        var requestedDocumentVersion: Int? = null
            private set
        var submitted: ConsentSubmission? = null
            private set

        override suspend fun getPendingConsentList(): PendingConsentList {
            getPendingCallCount += 1
            return pendingList
        }

        override suspend fun getConsentDocument(
            rawCode: String,
            version: Int,
        ): ConsentDocument {
            getDocumentCallCount += 1
            requestedDocumentCode = rawCode
            requestedDocumentVersion = version
            return document
        }

        override suspend fun submitConsent(submission: ConsentSubmission) {
            submitCallCount += 1
            submitted = submission
        }
    }
}
