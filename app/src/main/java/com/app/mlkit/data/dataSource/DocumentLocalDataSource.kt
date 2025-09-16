package com.app.mlkit.data.dataSource

import android.content.Context
import com.app.mlkit.domain.model.ScannedDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DocumentLocalDataSource @Inject constructor(
    private val context: Context
) {
    suspend fun saveDocument(document: ScannedDocument): Long = withContext(Dispatchers.IO) {
        document.id
    }
}