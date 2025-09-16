package com.app.mlkit.presentation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.mlkit.presentation.ui.CameraScreen
import com.app.mlkit.presentation.ui.ResultScreen

@Composable
fun DocScannerNavigation(navController: NavHostController) {

    NavHost(navController = navController, startDestination = "camera") {
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
                    navController.navigate("camera") {
                        popUpTo("camera") { inclusive = true }
                    }
                }
            )
        }
    }
}