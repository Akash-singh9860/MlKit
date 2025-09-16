package com.app.mlkit.data.repository

import com.app.mlkit.data.dataSource.DocumentLocalDataSource
import com.app.mlkit.data.dataSource.MlKitTextRecognizer
import com.app.mlkit.domain.model.ScannedDocument
import com.app.mlkit.domain.repository.DocumentRepository
import javax.inject.Inject

class DocumentRepositoryImpl @Inject constructor(
    private val mlKitTextRecognizer: MlKitTextRecognizer,
    private val documentLocalDataSource: DocumentLocalDataSource
): DocumentRepository {
    override suspend fun recognizeText(imagePath: String): ScannedDocument {
        val recognizedText = mlKitTextRecognizer.recognizeText(imagePath)
        return ScannedDocument(
            imagePath = imagePath,
            text = recognizedText
        )
    }

    override suspend fun saveDocument(document: ScannedDocument): Long {
        return documentLocalDataSource.saveDocument(document)
    }
}