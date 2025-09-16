package com.app.mlkit.presentation.ui


import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.mlkit.presentation.viewModels.ResultViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    imagePath: String,
    onSaveClicked: () -> Unit,
) {

    val viewModelStoreOwner = checkNotNull(LocalContext.current as? ViewModelStoreOwner) {
        Log.e("TAG","No ViewModelStoreOwner was provided via LocalContext")
    }
    val viewModel: ResultViewModel = viewModel(viewModelStoreOwner = viewModelStoreOwner)


    val state by viewModel.state.collectAsState()
    val pdfPath by viewModel.pdfPath.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = imagePath) {
        val cleanPath = imagePath.replace("file:", "")
        viewModel.processImage(cleanPath)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = state.message, key2 = state.error) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
        }
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Document Result") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error != null -> {
                    Text(
                        text = "Error: ${state.error}",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                state.document != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        AsyncImage(
                            model = state.document?.imagePath,
                            contentDescription = "Scanned Document",
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = {
                                viewModel.saveDocument()
                                onSaveClicked()
                            }) {
                                Text("Save")
                            }

                            Button(onClick = { viewModel.createPdf() }) {
                                Text("Create PDF")
                            }

                            Button(onClick = {
                                viewModel.uploadScannedDocument()
                            }) {
                               Text("Upload")
                            }
                        }

                        // Show PDF view/share options if PDF is created
                        pdfPath?.let { path ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    val pdfFile = File(path)
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.provider",
                                        pdfFile
                                    )

                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "application/pdf")
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }

                                    try {
                                        context.startActivity(intent)
                                    } catch (e: ActivityNotFoundException) {
                                        Toast.makeText(
                                            context,
                                            "No PDF viewer found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View PDF")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                viewModel.uploadPdf()
                            },Modifier.fillMaxWidth()) {
                                Text("Upload Pdf")
                            }
                        }
                    }
                }
            }
        }
    }
}