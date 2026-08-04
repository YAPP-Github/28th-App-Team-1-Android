package com.dminus14.app.data.remote.dto.interview

import com.google.gson.annotations.SerializedName

/**
 * GET api/v1/interview/sessions/{sessionId}/questions/{questionId}/audio/stream
 */
data class StreamAudioRequestDto(
    @SerializedName("dummy")
    val dummy: String? = null,
)

/**
 * GET api/v1/interview/sessions/{sessionId}/questions/{questionId}/audio/stream 응답용 더미 DTO.
 * 실제 API에서는 ResponseBody 바이너리 스트림을 반환한다.
 */
data class StreamAudioResponseDto(
    @SerializedName("dummy")
    val dummy: String? = null,
)
