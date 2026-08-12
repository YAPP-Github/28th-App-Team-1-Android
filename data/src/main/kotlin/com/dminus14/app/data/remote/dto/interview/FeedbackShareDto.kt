package com.dminus14.app.data.remote.dto.interview

import com.google.gson.annotations.SerializedName

/** 지인 피드백 공유 링크 생성 요청. 평가받을 태도 항목(1~5개)을 서버 코드 이름으로 전송한다. */
data class FeedbackShareCreateRequestDto(
    @SerializedName("axes")
    val axes: List<String>,
)

/** 공유 링크 생성 응답. 클라이언트는 이 token 으로 공유 딥링크를 조립한다. */
data class FeedbackShareCreateResponseDto(
    @SerializedName("token")
    val token: String?,
)
