package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.ConsentApi
import com.dminus14.app.data.remote.dto.ConsentDocumentDto
import com.dminus14.app.data.remote.dto.ConsentPendingItemsDto
import com.dminus14.app.data.remote.dto.ConsentSubmitRequestDto
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ServerException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsentRemoteDataSourceImpl
    @Inject
    constructor(
        private val consentApi: ConsentApi,
    ) : ConsentRemoteDataSource {
        override suspend fun getPendingConsentList(): ConsentPendingItemsDto {
            val response = consentApi.getPendingConsentList()
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "동의 pending 목록 응답이 비어 있습니다.",
                )
        }

        override suspend fun getConsentDocument(
            rawCode: String,
            version: Int,
        ): ConsentDocumentDto {
            val response = consentApi.getConsentDocument(item = rawCode, version = version)
            return response.data
                ?: throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "동의 문서 응답이 비어 있습니다.",
                )
        }

        override suspend fun submitConsent(request: ConsentSubmitRequestDto) {
            val response = consentApi.submitConsent(request)
            // 성공 응답은 data 없이 success만 올 수 있으므로 success를 반드시 확인한다.
            // HTTP non-2xx는 Retrofit이 HttpException으로 던지고 Repository에서 매핑한다.
            if (!response.success) {
                throw ServerException(
                    errCode = ApiErrorCode.SERVER_ERROR,
                    message = "동의 제출에 실패했습니다.",
                )
            }
        }
    }
