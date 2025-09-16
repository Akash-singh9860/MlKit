package com.app.mlkit.domain.repository

import com.app.mlkit.domain.model.ScannedDocument

interface DocumentRepository {
    suspend fun recognizeText(imagePath: String): ScannedDocument
    suspend fun saveDocument(document: ScannedDocument): Long
}