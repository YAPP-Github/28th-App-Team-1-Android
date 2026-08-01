package com.dminus14.app.data.remote.dto

import com.dminus14.app.domain.model.ConsentDocument
import com.dminus14.app.domain.model.ConsentItem
import com.dminus14.app.domain.model.ConsentItemCode
import com.dminus14.app.domain.model.ConsentPendingStatus
import com.dminus14.app.domain.model.ConsentSubmission
import com.dminus14.app.domain.model.PendingConsentList
import com.google.gson.annotations.SerializedName

/** pending 동의 목록 응답의 `data` 본문이다. */
data class ConsentPendingItemsDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("items")
    val items: List<ConsentItemDto>?,
) {
    fun toDomain(): PendingConsentList =
        PendingConsentList(
            status = status.toConsentPendingStatus(),
            items = items.orEmpty().map(ConsentItemDto::toDomain),
        )
}

/** pending 목록의 동의 항목 하나다. */
data class ConsentItemDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("label")
    val label: String,
    @SerializedName("required")
    val required: Boolean,
    @SerializedName("version")
    val version: Int,
    @SerializedName("hasDocument")
    val hasDocument: Boolean,
) {
    fun toDomain(): ConsentItem =
        ConsentItem(
            code = ConsentItemCode.fromRaw(code),
            rawCode = code,
            label = label,
            version = version,
            isRequired = required,
            hasDocument = hasDocument,
        )
}

/** 동의 문서 본문 조회 응답의 `data` 본문이다. */
data class ConsentDocumentDto(
    @SerializedName("item")
    val item: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("version")
    val version: Int,
    @SerializedName("content")
    val content: String,
) {
    fun toDomain(): ConsentDocument =
        ConsentDocument(
            code = ConsentItemCode.fromRaw(item),
            rawCode = item,
            title = title,
            version = version,
            contentMarkdown = content,
        )
}

/** 동의 제출 요청 본문이다. */
data class ConsentSubmitRequestDto(
    @SerializedName("items")
    val items: List<ConsentItemSubmissionDto>,
) {
    companion object {
        fun from(submission: ConsentSubmission): ConsentSubmitRequestDto =
            ConsentSubmitRequestDto(
                items =
                    submission.items.map { item ->
                        ConsentItemSubmissionDto(
                            item = item.rawCode,
                            version = item.version,
                            agreed = item.agreed,
                        )
                    },
            )
    }
}

/** 동의 제출 요청의 항목 하나다. */
data class ConsentItemSubmissionDto(
    @SerializedName("item")
    val item: String,
    @SerializedName("version")
    val version: Int,
    @SerializedName("agreed")
    val agreed: Boolean,
)

private fun String.toConsentPendingStatus(): ConsentPendingStatus =
    when (this) {
        "NOT_SUBMITTED" -> ConsentPendingStatus.NOT_SUBMITTED
        "STALE" -> ConsentPendingStatus.STALE
        "UP_TO_DATE" -> ConsentPendingStatus.UP_TO_DATE
        else -> ConsentPendingStatus.UNKNOWN
    }
