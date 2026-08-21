package com.dminus14.app.data.remote.datasource

import android.util.Log
import com.dminus14.app.data.remote.api.GuestFeedbackApi
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackEntryResponseDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackRatingDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackSubmitRequestDto
import com.dminus14.app.data.remote.dto.feedback.GuestFeedbackSubmitResponseDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ServerException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Guest Feedback Retrofit 호출과 제출 요청 조립만 담당하는 원격 데이터 소스다.
 *
 * 응답 계약 검증은 Gson 응답 adapter가 `{"success", "data"}` 봉투의 `data`를 DTO로 생성하기
 * 전에 완료하므로, 여기서는 그 봉투를 벗겨 [ApiResponseDto.data]만 상위 계층에 전달하고 별도
 * 재검증은 하지 않는다.
 */
@Singleton
class GuestFeedbackRemoteDataSourceImpl
@Inject
constructor(
    private val guestFeedbackApi: GuestFeedbackApi,
) : GuestFeedbackRemoteDataSource {
    /** 공유 token의 진입 정보를 요청하고 adapter가 검증한 DTO를 반환한다. */
    override suspend fun enter(token: String): GuestFeedbackEntryResponseDto {
        Log.d("interview", "${token}")
        val response = guestFeedbackApi.enter(token)
        return response.data
            ?: throw ServerException(
                errCode = ApiErrorCode.SERVER_ERROR,
                message = "Guest Feedback 진입 응답이 비어 있습니다.",
            )
    }

    /** nullable nickname과 comment 키를 보존하는 제출 요청을 조립해 전송한다. */
    override suspend fun submit(
        token: String,
        nickname: String?,
        ratings: List<GuestFeedbackRatingDto>,
    ): GuestFeedbackSubmitResponseDto {
        val response =
            guestFeedbackApi.submit(
                token = token,
                request =
                    GuestFeedbackSubmitRequestDto(
                        nickname = nickname,
                        ratings = ratings,
                    ),
            )
        return response.data
            ?: throw ServerException(
                errCode = ApiErrorCode.SERVER_ERROR,
                message = "Guest Feedback 제출 응답이 비어 있습니다.",
            )
    }
}
