package com.app.mlkit.data.apiService

import com.app.mlkit.data.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DocumentUploadApi {
        @Multipart
        @POST("upload")
        suspend fun uploadDocument(
            @Part image: MultipartBody.Part
        ): Response<UploadResponse>
}