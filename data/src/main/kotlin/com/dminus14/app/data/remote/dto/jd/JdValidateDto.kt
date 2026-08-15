package com.dminus14.app.data.remote.dto.jd

import com.dminus14.app.domain.model.JdValidationResult
import com.google.gson.annotations.SerializedName

/**
 * POST api/v1/jd/validate
 */
data class JdValidateRequestDto(
    @SerializedName("jdUrl")
    val jdUrl: String,
)

data class JdValidateResponseDto(
    @SerializedName("valid")
    val valid: Boolean,
    @SerializedName("reason")
    val reason: String?,
    @SerializedName("message")
    val message: String?,
) {
    fun toDomain(): JdValidationResult =
        JdValidationResult(
            valid = valid,
            reason = reason,
            message = message,
        )
}
