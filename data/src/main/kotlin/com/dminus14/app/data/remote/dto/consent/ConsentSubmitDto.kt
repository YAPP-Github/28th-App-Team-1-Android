@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.consent

import com.dminus14.app.domain.model.ConsentSubmission
import com.google.gson.annotations.SerializedName

/**
 * POST api/v1/consents
 *
 * 응답 본문은 없다.
 */
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

data class ConsentItemSubmissionDto(
    @SerializedName("item")
    val item: String,
    @SerializedName("version")
    val version: Int,
    @SerializedName("agreed")
    val agreed: Boolean,
)
