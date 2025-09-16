package com.app.mlkit.domain.model

data class ResultState(
    val isLoading: Boolean = false,
    val document: ScannedDocument? = null,
    val error: String? = null,
    val message: String? = null
)
