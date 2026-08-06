@file:Suppress("MatchingDeclarationName", "ktlint:standard:filename")

package com.dminus14.app.data.remote.dto

import com.dminus14.app.domain.model.AppVersionPolicy
import com.dminus14.app.domain.model.AppVersionUpdateType
import com.google.gson.annotations.SerializedName

/** GET api/v1/app-versions/check 응답 `data` 본문이다. */
data class AppVersionCheckResponseDto(
    @SerializedName("updateType")
    val updateType: String,
    @SerializedName("latestVersion")
    val latestVersion: String,
    @SerializedName("minSupportedVersion")
    val minSupportedVersion: String,
    @SerializedName("storeUrl")
    val storeUrl: String,
    @SerializedName("title")
    val title: String?,
    @SerializedName("body")
    val body: String?,
) {
    fun toDomain(): AppVersionPolicy =
        AppVersionPolicy(
            updateType = updateType.toAppVersionUpdateType(),
            latestVersion = latestVersion,
            minSupportedVersion = minSupportedVersion,
            storeUrl = storeUrl,
            title = title,
            body = body,
        )
}

private fun String.toAppVersionUpdateType(): AppVersionUpdateType =
    AppVersionUpdateType.entries.firstOrNull { entry -> entry.name == this }
        ?: AppVersionUpdateType.UNKNOWN
