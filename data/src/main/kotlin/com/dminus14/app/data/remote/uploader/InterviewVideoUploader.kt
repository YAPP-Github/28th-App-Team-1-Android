package com.dminus14.app.data.remote.uploader

import com.dminus14.app.data.di.remote.interview.InterviewUploadOkHttpClient
import com.dminus14.app.data.remote.mapper.ApiErrorCode
import com.dminus14.app.domain.exception.ServerException
import com.dminus14.app.domain.exception.UnknownException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterviewVideoUploader
    @Inject
    constructor(
        @InterviewUploadOkHttpClient private val client: OkHttpClient,
    ) {
        fun upload(
            uploadUrl: String,
            contentType: String,
            file: File,
        ) {
            val request =
                Request
                    .Builder()
                    .url(uploadUrl)
                    .header("Content-Type", contentType)
                    .put(file.asRequestBody(contentType.toMediaType()))
                    .build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) return
                if (response.code in SERVER_ERROR_MIN..SERVER_ERROR_MAX) {
                    throw ServerException(
                        errCode = ApiErrorCode.SERVER_ERROR,
                        message = "영상 업로드 서버가 요청을 처리하지 못했습니다.",
                    )
                }
                throw UnknownException(
                    errCode = ApiErrorCode.UNKNOWN,
                    message = "영상 업로드를 완료하지 못했습니다.",
                )
            }
        }

        private companion object {
            const val SERVER_ERROR_MIN = 500
            const val SERVER_ERROR_MAX = 599
        }
    }
