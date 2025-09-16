package com.app.mlkit.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mlkit.data.repository.DocumentRepository
import com.app.mlkit.domain.model.ResultState
import com.app.mlkit.domain.useCase.CreatePdfUseCase
import com.app.mlkit.domain.useCase.RecognizeTextUseCase
import com.app.mlkit.domain.useCase.SaveDocumentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val recognizeTextUseCase: RecognizeTextUseCase,
    private val saveDocumentUseCase: SaveDocumentUseCase,
    private val createPdfUseCase: CreatePdfUseCase,
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ResultState())
    val state: StateFlow<ResultState> = _state

    private val _pdfPath = MutableStateFlow<String?>(null)
    val pdfPath: StateFlow<String?> = _pdfPath

    fun processImage(imagePath: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val cleanPath = imagePath.replace("file:", "")
                val document = recognizeTextUseCase(cleanPath)
                _state.update {
                    it.copy(
                        isLoading = false,
                        document = document,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An error occurred during text recognition"
                    )
                }
            }
        }
    }

    /*fun updateDocumentText(newText: String) {
        _state.update { currentState ->
            currentState.document?.let { document ->
                currentState.copy(
                    document = document.copy(text = newText)
                )
            } ?: currentState
        }
    }*/

    fun saveDocument() {
        viewModelScope.launch {
            _state.value.document?.let { document ->
                try {
                    saveDocumentUseCase(document)
                } catch (e: Exception) {
                    _state.update { it.copy(error = e.message) }
                }
            }
        }
    }

    fun createPdf() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                _state.value.document?.let { document ->
                    val pdfPath = createPdfUseCase(document)
                    _pdfPath.value = pdfPath
                    _state.update {
                        it.copy(
                            isLoading = false,
                            message = "PDF created Successful"
                        )

                    }
                } ?: run {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "No document available to create PDF"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to create PDF: ${e.message}"
                    )
                }
            }
        }
    }

    fun uploadScannedDocument() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, message = null) }

            val imagePath = _state.value.document?.imagePath ?: run {
                _state.update { it.copy(isLoading = false, error = "No image to upload") }
                return@launch
            }

            val imageFile = File(imagePath.replace("file:", ""))

            documentRepository.uploadDocument(imageFile).collect { result ->
                result.onSuccess { response ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                message = "Uploaded successfully: ${response.message}"
                            )
                        }
                    }
                    .onFailure { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = error.message ?: "Unknown error during upload"
                            )
                        }
                    }
            }
        }
    }

    fun uploadPdf() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val pdfPath = _pdfPath.value
            if (pdfPath != null) {
                val file = File(pdfPath)
                documentRepository.uploadPdfFile(file).collect { result ->
                    result.onSuccess { response ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                message = "Uploaded successfully: ${response.message}"
                            )
                        }
                    }.onFailure { error ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = error.message ?: "Unknown error during upload"
                                )
                            }
                    }
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "No PDF to upload"
                    )
                }
            }
        }
    }

}

