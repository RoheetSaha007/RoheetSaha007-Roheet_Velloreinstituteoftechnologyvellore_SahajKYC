package com.example.sahajkyc.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sahajkyc.R
import com.example.sahajkyc.common.PrimaryButton
import com.example.sahajkyc.viewmodel.KycViewModel
import com.example.sahajkyc.Routes
data class Language(val name: String, val script: String, val char: String)

val languages = listOf(
    Language("English", "en", "A"),
    Language("हिन्दी", "hi", "अ"),
    Language("বাংলা", "bn", "অ"),
    Language("தமிழ்", "ta", "அ"),
    Language("తెలుగు", "te", "అ"),
    Language("मराठी", "mr", "अ")
)

@Composable
fun WelcomeScreen(
    navController: NavController,
    onLanguageSelected: (String) -> Unit,
    kycViewModel: KycViewModel = viewModel()
) {
    var selectedLanguage by remember { mutableStateOf<Language?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        Image(
            painter = painterResource(id = R.drawable.ic_indian_people_logo),
            contentDescription = "Indian Flag Logo",
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Welcome to Sahaj KYC",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "A simple and secure way to verify your identity.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))

        Text("Please select your language", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(languages) { lang ->
                OutlinedButton(
                    onClick = {
                        selectedLanguage = lang
                        onLanguageSelected(lang.script)
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedLanguage == lang) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
                    ),
                    border = BorderStroke(2.dp, if(selectedLanguage == lang) MaterialTheme.colorScheme.primary else Color.LightGray)
                ) {
                    Text(lang.char, fontSize = 28.sp)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = "Start KYC",
            onClick = {
                kycViewModel.startKycProcess { newId ->
                    navController.navigate(Routes.METHOD_SELECTION)
                }
            },
            enabled = selectedLanguage != null
        )
    }
}