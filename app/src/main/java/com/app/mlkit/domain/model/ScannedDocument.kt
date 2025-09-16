package com.app.mlkit.domain.model

data class ScannedDocument(
    val id: Long = 0,
    val imagePath: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

