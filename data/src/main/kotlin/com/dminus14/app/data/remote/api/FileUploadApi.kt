package com.dminus14.app.data.remote.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileUploadApi {
    @Multipart
    @POST("upload/pdf")
    suspend fun uploadPdf(
        @Part file: MultipartBody.Part,
    )

    @Multipart
    @POST("upload/video")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part,
    )
}
