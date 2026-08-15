package com.dminus14.app.data.remote.dto.consent

import com.dminus14.app.domain.model.ConsentItem
import com.dminus14.app.domain.model.ConsentItemCode
import com.dminus14.app.domain.model.ConsentPendingStatus
import com.dminus14.app.domain.model.PendingConsentList
import com.google.gson.annotations.SerializedName

/**
 * GET api/v1/consents/pending
 *
 * 요청 본문은 없다.
 */
data class ConsentPendingItemsDto(
    @SerializedName("consentStatus")
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

private fun String.toConsentPendingStatus(): ConsentPendingStatus =
    when (this) {
        "NOT_SUBMITTED" -> ConsentPendingStatus.NOT_SUBMITTED
        "STALE" -> ConsentPendingStatus.STALE
        "UP_TO_DATE" -> ConsentPendingStatus.UP_TO_DATE
        else -> ConsentPendingStatus.UNKNOWN
    }
