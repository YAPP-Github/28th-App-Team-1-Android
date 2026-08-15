package com.dminus14.app.data.remote.dto.consent

import com.dminus14.app.domain.model.ConsentDocument
import com.dminus14.app.domain.model.ConsentItemCode
import com.google.gson.annotations.SerializedName

/**
 * GET api/v1/consents/{item}/versions/{version}
 *
 * 요청 본문은 없다.
 */
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
