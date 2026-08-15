package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.consent.ConsentDocumentDto
import com.dminus14.app.data.remote.dto.consent.ConsentPendingItemsDto
import com.dminus14.app.data.remote.dto.consent.ConsentSubmitRequestDto

interface ConsentRemoteDataSource {
    suspend fun getPendingConsentList(): ConsentPendingItemsDto

    suspend fun getConsentDocument(
        rawCode: String,
        version: Int,
    ): ConsentDocumentDto

    suspend fun submitConsent(request: ConsentSubmitRequestDto)
}
