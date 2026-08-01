package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.api.ConsentApi
import com.dminus14.app.data.remote.dto.ConsentDocumentDto
import com.dminus14.app.data.remote.dto.ConsentPendingItemsDto
import com.dminus14.app.data.remote.dto.ConsentSubmitRequestDto
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
            return response.data ?: error("동의 pending 목록 응답이 비어 있습니다.")
        }

        override suspend fun getConsentDocument(
            rawCode: String,
            version: Int,
        ): ConsentDocumentDto {
            val response = consentApi.getConsentDocument(item = rawCode, version = version)
            return response.data ?: error("동의 문서 응답이 비어 있습니다.")
        }

        override suspend fun submitConsent(request: ConsentSubmitRequestDto) {
            consentApi.submitConsent(request)
        }
    }
