package com.app.mlkit.domain.useCase

import com.app.mlkit.domain.model.ScannedDocument
import com.app.mlkit.domain.repository.DocumentRepository
import javax.inject.Inject

class SaveDocumentUseCase @Inject constructor(
    private val documentRepository: DocumentRepository
) {
    suspend operator fun invoke(document: ScannedDocument): Long {
        return documentRepository.saveDocument(document)
    }
}