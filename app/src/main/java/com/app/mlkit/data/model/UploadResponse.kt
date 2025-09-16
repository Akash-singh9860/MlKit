package com.app.mlkit.data.model

data class UploadResponse(
    val success: Boolean,
    val message: String,
    val fileUrl: String
)