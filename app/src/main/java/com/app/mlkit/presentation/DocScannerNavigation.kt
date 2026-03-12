package com.app.mlkit.presentation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.mlkit.presentation.ui.CameraScreen
import com.app.mlkit.presentation.ui.DocResultScreen
import com.app.mlkit.presentation.ui.HomeScreen
import com.app.mlkit.presentation.ui.ResultScreen

@Composable
fun DocScannerNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToOldTextScanner = {
                    navController.navigate("camera")
                },
                onNewDocumentScanned = { imageUris, pdfUri ->
                    val joinedUris = imageUris.joinToString(separator = "|")
                    val encodedImageUris = Uri.encode(joinedUris)
                    val encodedPdfUri = Uri.encode(pdfUri ?: "")
                    navController.navigate("doc_result/$encodedImageUris?pdfUri=$encodedPdfUri")
                }
            )
        }
        composable("camera") {
            CameraScreen(
                onDocumentCaptured = { imagePath ->
                    val cleanPath = imagePath.replace("file:", "")
                    val encodedPath = Uri.encode(cleanPath)
                    navController.navigate("result/$encodedPath")
                }
            )
        }
        composable(
            route = "result/{imagePath}",
            arguments = listOf(
                navArgument("imagePath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedImagePath = backStackEntry.arguments?.getString("imagePath") ?: ""
            val imagePath = Uri.decode(encodedImagePath)
            ResultScreen(
                imagePath = imagePath,
                onSaveClicked = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "doc_result/{imageUris}?pdfUri={pdfUri}", // changed name to imageUris
            arguments = listOf(
                navArgument("imageUris") { type = NavType.StringType },
                navArgument("pdfUri") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val encodedImageUris = backStackEntry.arguments?.getString("imageUris") ?: ""
            val encodedPdfUri = backStackEntry.arguments?.getString("pdfUri") ?: ""
            val decodedUrisString = Uri.decode(encodedImageUris)
            val imageUriList = decodedUrisString.split("|").filter { it.isNotEmpty() }
            val pdfUri = Uri.decode(encodedPdfUri).takeIf { it.isNotEmpty() }

            DocResultScreen(
                imageUris = imageUriList,
                pdfUri = pdfUri,
                onNavigateBack = {
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }
    }
}