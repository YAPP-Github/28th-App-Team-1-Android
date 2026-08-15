package com.dminus14.app.data.remote.api

import com.dminus14.app.data.remote.dto.common.ApiResponseDto
import com.dminus14.app.data.remote.dto.consent.ConsentDocumentDto
import com.dminus14.app.data.remote.dto.consent.ConsentPendingItemsDto
import com.dminus14.app.data.remote.dto.consent.ConsentSubmitRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/** 회원 동의 pending 목록, 문서 본문, 제출을 담당하는 Retrofit API다. */
interface ConsentApi {
    /** 지금 동의가 필요한 항목과 게이트 상태를 조회한다. */
    @GET("api/v1/consents/pending")
    suspend fun getPendingConsentList(): ApiResponseDto<ConsentPendingItemsDto>

    /** 특정 항목·버전의 동의 문서 본문을 조회한다. */
    @GET("api/v1/consents/{item}/versions/{version}")
    suspend fun getConsentDocument(
        @Path("item") item: String,
        @Path("version") version: Int,
    ): ApiResponseDto<ConsentDocumentDto>

    /** 동의 항목을 제출한다. 성공 응답은 `data` 없이 `success`만 올 수 있다. */
    @POST("api/v1/consents")
    suspend fun submitConsent(
        @Body request: ConsentSubmitRequestDto,
    ): ApiResponseDto<Unit?>
}
