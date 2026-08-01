package com.dminus14.app.data.remote.dto

import com.dminus14.app.domain.model.ConsentItemCode
import com.dminus14.app.domain.model.ConsentPendingStatus
import com.dminus14.app.domain.model.ConsentSubmission
import com.dminus14.app.domain.model.ConsentSubmissionItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsentDtoTest {
    @Test
    fun `pending 목록을 도메인 모델로 변환한다`() {
        val actual =
            ConsentPendingItemsDto(
                status = "NOT_SUBMITTED",
                items =
                    listOf(
                        ConsentItemDto(
                            code = "TERMS_OF_SERVICE",
                            label = "서비스 이용약관",
                            required = true,
                            version = 1,
                            hasDocument = true,
                        ),
                        ConsentItemDto(
                            code = "OVERSEAS_TRANSFER",
                            label = "개인정보 국외 이전",
                            required = true,
                            version = 1,
                            hasDocument = false,
                        ),
                        ConsentItemDto(
                            code = "FUTURE_ITEM",
                            label = "알 수 없는 항목",
                            required = false,
                            version = 3,
                            hasDocument = true,
                        ),
                    ),
            ).toDomain()

        assertEquals(ConsentPendingStatus.NOT_SUBMITTED, actual.status)
        assertEquals(3, actual.items.size)
        assertEquals(ConsentItemCode.TERMS_OF_SERVICE, actual.items[0].code)
        assertEquals("TERMS_OF_SERVICE", actual.items[0].rawCode)
        assertTrue(actual.items[0].isRequired)
        assertTrue(actual.items[0].hasDocument)
        assertFalse(actual.items[1].hasDocument)
        assertEquals(ConsentItemCode.UNKNOWN, actual.items[2].code)
        assertEquals("FUTURE_ITEM", actual.items[2].rawCode)
    }

    @Test
    fun `알 수 없는 status는 UNKNOWN으로 흡수한다`() {
        val actual =
            ConsentPendingItemsDto(
                status = "SOMETHING_NEW",
                items = emptyList(),
            ).toDomain()

        assertEquals(ConsentPendingStatus.UNKNOWN, actual.status)
        assertTrue(actual.items.isEmpty())
    }

    @Test
    fun `null items는 빈 목록으로 변환한다`() {
        val actual =
            ConsentPendingItemsDto(
                status = "UP_TO_DATE",
                items = null,
            ).toDomain()

        assertEquals(ConsentPendingStatus.UP_TO_DATE, actual.status)
        assertTrue(actual.items.isEmpty())
    }

    @Test
    fun `문서 응답을 도메인 모델로 변환한다`() {
        val actual =
            ConsentDocumentDto(
                item = "PERSONAL_INFO_COLLECTION",
                title = "개인정보 수집·이용",
                version = 2,
                content = "합성 예시 본문",
            ).toDomain()

        assertEquals(ConsentItemCode.PERSONAL_INFO_COLLECTION, actual.code)
        assertEquals("PERSONAL_INFO_COLLECTION", actual.rawCode)
        assertEquals("개인정보 수집·이용", actual.title)
        assertEquals(2, actual.version)
        assertEquals("합성 예시 본문", actual.contentMarkdown)
    }

    @Test
    fun `제출 모델을 DTO로 변환한다`() {
        val actual =
            ConsentSubmitRequestDto.from(
                ConsentSubmission(
                    items =
                        listOf(
                            ConsentSubmissionItem(
                                rawCode = "AGE_OVER_14",
                                version = 1,
                                agreed = true,
                            ),
                        ),
                ),
            )

        assertEquals(1, actual.items.size)
        assertEquals("AGE_OVER_14", actual.items.single().item)
        assertEquals(1, actual.items.single().version)
        assertTrue(actual.items.single().agreed)
    }
}
