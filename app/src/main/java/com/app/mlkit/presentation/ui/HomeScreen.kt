package com.app.mlkit.presentation.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

@Composable
fun HomeScreen(
    onNavigateToOldTextScanner: () -> Unit,
    onNewDocumentScanned: (imageUri: List<String>, pdfUri: String?) -> Unit
) {
    val context = LocalContext.current
    val options = remember {
        GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setResultFormats(
                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                GmsDocumentScannerOptions.RESULT_FORMAT_PDF
            )
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()
    }
    val scanner = remember { GmsDocumentScanning.getClient(options) }
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            val imageUris = scanResult?.pages?.map { it.imageUri.toString() } ?: emptyList()
            val pdfUri = scanResult?.pdf?.uri?.toString()
            if (imageUris.isNotEmpty()) {
                onNewDocumentScanned(imageUris, pdfUri)
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onNavigateToOldTextScanner) {
            Text("Scan Text (Old CameraX Approach)")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                scanner.getStartScanIntent(context as Activity)
                    .addOnSuccessListener { intentSender ->
                        scannerLauncher.launch(
                            IntentSenderRequest.Builder(intentSender).build()
                        )
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to start scanner", Toast.LENGTH_SHORT).show()
                    }
            }
        ) {
            Text("Scan Document (New ML Kit Approach)")
        }
    }
}