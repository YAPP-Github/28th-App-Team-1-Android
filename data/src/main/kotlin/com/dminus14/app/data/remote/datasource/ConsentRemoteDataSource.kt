package com.dminus14.app.data.remote.datasource

import com.dminus14.app.data.remote.dto.ConsentDocumentDto
import com.dminus14.app.data.remote.dto.ConsentPendingItemsDto
import com.dminus14.app.data.remote.dto.ConsentSubmitRequestDto

interface ConsentRemoteDataSource {
    suspend fun getPendingConsentList(): ConsentPendingItemsDto

    suspend fun getConsentDocument(
        rawCode: String,
        version: Int,
    ): ConsentDocumentDto

    suspend fun submitConsent(request: ConsentSubmitRequestDto)
}
