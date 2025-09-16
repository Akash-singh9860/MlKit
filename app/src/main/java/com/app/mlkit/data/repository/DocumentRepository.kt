package com.app.mlkit.data.repository

import com.app.mlkit.data.apiService.DocumentUploadApi
import com.app.mlkit.data.model.UploadResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


class DocumentRepository @Inject constructor(
    private val api: DocumentUploadApi
) {
    fun uploadDocument(imageFile: File): Flow<Result<UploadResponse>> = flow {
        try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("document", imageFile.name, requestFile)
            val response = api.uploadDocument(body)

            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Upload failed: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }


     fun uploadPdfFile(pdfFile: File): Flow<Result<UploadResponse>> = flow {
        val requestFile = pdfFile.asRequestBody("application/pdf".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("document", pdfFile.name, requestFile)

        try {
            val response = api.uploadDocument(body)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("PDF Upload failed: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.message ?: "Unknown error")))
        }
    }

}
