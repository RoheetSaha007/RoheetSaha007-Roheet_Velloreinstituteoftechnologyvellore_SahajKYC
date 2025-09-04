package com.example.sahajkyc.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sahajkyc.Routes
import com.example.sahajkyc.common.StepIndicator
import com.example.sahajkyc.viewmodel.KycViewModel

@Composable
fun MethodSelectionScreen(
    navController: NavController,
    langCode: String?,
    onProceed: (Long) -> Unit,
    kycViewModel: KycViewModel = viewModel()
) {
    data class ScreenStrings(
        val title: String,
        val digilockerTitle: String,
        val digilockerSubtitle: String,
        val uploadTitle: String,
        val uploadSubtitle: String
    )

    val strings = when (langCode) {
        "hi" -> ScreenStrings("विधि चुनें", "डिजिलॉकर", "सबसे तेज़ और आसान", "दस्तावेज़ अपलोड करें", "अपने फ़ोन के कैमरे का उपयोग करें")
        "bn" -> ScreenStrings("পদ্ধতি নির্বাচন করুন", "ডিজি-লকার", "দ্রুততম এবং সহজতম", "নথি আপলোড করুন", "আপনার ফোনের ক্যামেরা ব্যবহার করুন")
        "ta" -> ScreenStrings("முறையைத் தேர்ந்தெடுக்கவும்", "டிஜிலாக்கர்", "வேகமானது மற்றும் எளிதானது", "ஆவணத்தைப் பதிவேற்றவும்", "உங்கள் தொலைபேசி கேமராவைப் பயன்படுத்தவும்")
        "te" -> ScreenStrings("పద్ధతిని ఎంచుకోండి", "డిజీలాకర్", "వేగవంతమైనది మరియు సులభమైనది", "పత్రాన్ని అప్‌లోడ్ చేయండి", "మీ ఫోన్ కెమెరాను ఉపయోగించండి")
        "mr" -> ScreenStrings("पद्धत निवडा", "डिजीलॉकर", "सर्वात वेगवान आणि सोपे", "दस्तऐವಜ अपलोड करा", "आपल्या फोनचा कॅमेरा वापरा")
        else -> ScreenStrings("Choose Your Method", "Digilocker", "Fastest and Easiest", "Upload Document", "Use your phone's camera")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StepIndicator(currentStep = 1, totalSteps = 3)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = strings.title,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(32.dp))

        MethodCard(
            icon = Icons.Default.AccountBox,
            title = strings.digilockerTitle,
            subtitle = strings.digilockerSubtitle,
            onClick = {
                kycViewModel.startKycProcess { newId ->
                    onProceed(newId)
                    navController.navigate(Routes.DIGILOCKER)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        MethodCard(
            icon = Icons.Default.CameraAlt,
            title = strings.uploadTitle,
            subtitle = strings.uploadSubtitle,
            onClick = {
                kycViewModel.startKycProcess { newId ->
                    onProceed(newId)
                    navController.navigate(Routes.DOCUMENT_SELECTION)
                }
            }
        )
    }
}

@Composable
fun MethodCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}