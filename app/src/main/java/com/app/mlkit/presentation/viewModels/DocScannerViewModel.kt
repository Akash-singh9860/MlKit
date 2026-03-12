package com.app.mlkit.presentation.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mlkit.data.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
data class DocScannerState(
    val isLoading: Boolean = false,
    val imageUris: List<String> = emptyList(),
    val pdfUri: String? = null,
    val error: String? = null,
    val message: String? = null
)

@HiltViewModel
class DocScannerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DocScannerState())
    val state: StateFlow<DocScannerState> = _state

    fun setDocumentData(imageUris: List<String>, pdfUri: String?) {
        _state.update {
            it.copy(imageUris = imageUris, pdfUri = pdfUri)
        }
    }
    fun uploadScannedImage() {
        val uris = _state.value.imageUris
        if (uris.isEmpty()) {
            _state.update { it.copy(error = "No images available to upload") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, message = null) }
            var successCount = 0
            var lastError: String? = null
            for ((index, uriString) in uris.withIndex()) {
                val tempFile = copyUriToTempFile(uriString, "upload_img_${index}_", ".jpg")
                if (tempFile != null) {
                    documentRepository.uploadDocument(tempFile).collect { result ->
                        result.onSuccess {
                            successCount++
                        }.onFailure { error ->
                            lastError = error.message
                        }
                    }
                }
            }
            if (successCount == uris.size) {
                _state.update { it.copy(isLoading = false, message = "Successfully uploaded $successCount images!") }
            } else {
                _state.update { it.copy(isLoading = false, error = "Uploaded $successCount/${uris.size}. Last error: $lastError") }
            }
        }
    }

    fun uploadGeneratedPdf() {
        val uriString = _state.value.pdfUri
        if (uriString == null) {
            _state.update { it.copy(error = "No PDF available to upload") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, message = null) }
            val tempFile = copyUriToTempFile(uriString, "upload_pdf_", ".pdf")
            if (tempFile != null) {
                documentRepository.uploadPdfFile(tempFile).collect { result ->
                    result.onSuccess { response ->
                        _state.update { it.copy(isLoading = false, message = "PDF Uploaded: ${response.message}") }
                    }.onFailure { error ->
                        _state.update { it.copy(isLoading = false, error = error.message) }
                    }
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "Failed to process PDF URI") }
            }
        }
    }

    private fun copyUriToTempFile(uriString: String, prefix: String, suffix: String): File? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile(prefix, suffix, context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}