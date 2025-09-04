package com.example.sahajkyc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sahajkyc.screens.*
import com.example.sahajkyc.ui.theme.SahajKYCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SahajKYCTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var langCode by remember { mutableStateOf<String?>("en") }
    var kycId by remember { mutableStateOf<Long?>(null) }

    NavHost(navController = navController, startDestination = Routes.WELCOME) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                navController = navController,
                onLanguageSelected = { selectedLangCode -> langCode = selectedLangCode }
            )
        }
        composable(Routes.METHOD_SELECTION) {
            MethodSelectionScreen(
                navController = navController,
                langCode = langCode,
                onProceed = { newId -> kycId = newId }
            )
        }
        composable(Routes.DOCUMENT_SELECTION) {
            DocumentSelectionScreen(
                navController = navController,
                langCode = langCode,
                onProceed = { newId -> kycId = newId }
            )
        }
        composable(Routes.DIGILOCKER) {
            DigilockerScreen(
                navController = navController,
                langCode = langCode,
                onProceed = { newId -> kycId = newId }
            )
        }
        composable(Routes.DOCUMENT_CAPTURE) {
            kycId?.let { id ->
                DocumentCaptureScreen(
                    navController = navController,
                    langCode = langCode,
                    kycId = id
                )
            }
        }
        composable(Routes.FACE_LIVENESS) {
            kycId?.let { id ->
                FaceLivenessScreen(
                    navController = navController,
                    langCode = langCode,
                    kycId = id
                )
            }
        }
        composable(
            route = Routes.STATUS,
            arguments = listOf(navArgument("kycId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("kycId")
            if (id != null) {
                StatusScreen(navController = navController, langCode = langCode, kycId = id)
            }
        }
    }
}