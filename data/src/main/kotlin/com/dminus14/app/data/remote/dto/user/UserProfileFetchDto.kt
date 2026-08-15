@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto.user

import com.google.gson.annotations.SerializedName

/**
 * GET api/v1/users/me/profile
 *
 * 요청 본문은 없다.
 */
data class UserProfileFetchResponseDto(
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("provider")
    val provider: String? = null,
    @SerializedName("jobRole")
    val jobRole: String? = null,
    @SerializedName("jobRoleLabel")
    val jobRoleLabel: String? = null,
    @SerializedName("careerYears")
    val careerYears: Int? = null,
    @SerializedName("remainingTicketCount")
    val remainingTicketCount: Int? = null,
)
