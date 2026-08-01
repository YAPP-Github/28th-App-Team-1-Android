package com.dminus14.app.data.remote.dto.user

import com.google.gson.annotations.SerializedName

data class UserWithdrawRequestDto(
    @SerializedName("unused")
    val unused: Unit? = null,
)

data class UserWithdrawResponseDto(
    @SerializedName("unused")
    val unused: Unit? = null,
)
