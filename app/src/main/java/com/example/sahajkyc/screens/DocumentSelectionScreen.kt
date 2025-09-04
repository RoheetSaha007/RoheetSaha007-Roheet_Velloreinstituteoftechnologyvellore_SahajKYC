package com.example.sahajkyc.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sahajkyc.Routes
import com.example.sahajkyc.common.StepIndicator
import com.example.sahajkyc.viewmodel.KycViewModel

@Composable
fun DocumentSelectionScreen(
    navController: NavController,
    langCode: String?,
    onProceed: (Long) -> Unit,
    kycViewModel: KycViewModel = viewModel()
) {
    data class ScreenStrings(
        val title: String,
        val aadhaarTitle: String,
        val aadhaarSubtitle: String,
        val panTitle: String,
        val panSubtitle: String,
        val voterTitle: String,
        val voterSubtitle: String
    )

    val strings = when (langCode) {
        "hi" -> ScreenStrings("अपना दस्तावेज़ चुनें", "आधार कार्ड", "अधिकांश सत्यापन के लिए", "पैन कार्ड", "वित्तीय केवाईसी के लिए", "मतदाता पहचान पत्र", "पते के सत्यापन के लिए")
        "bn" -> ScreenStrings("আপনার নথি নির্বাচন করুন", "আধার কার্ড", "বেশিরভাগ যাচাইয়ের জন্য", "প্যান কার্ড", "আর্থিক KYC-এর জন্য", "ভোটার আইডি কার্ড", "ঠিকানা যাচাইয়ের জন্য")
        "ta" -> ScreenStrings("உங்கள் ஆவணத்தைத் தேர்ந்தெடுக்கவும்", "ஆதார் அட்டை", "பெரும்பாலான சரிபார்ப்புகளுக்கு", "பான் கார்டு", "நிதி KYC-க்கு", "வாக்காளர் அடையாள அட்டை", "முகவரி சரிபார்ப்புக்கு")
        "te" -> ScreenStrings("మీ పత్రాన్ని ఎంచుకోండి", "ఆధార్ కార్డ్", "చాలా వరకు ధృవీకరణల కోసం", "పాన్ కార్డ్", "ఆర్థిక KYC కోసం", "ఓటరు ID కార్డ్", "చిరునామా ధృవీకరణ కోసం")
        "mr" -> ScreenStrings("तुमचे दस्तऐवज निवडा", "आधार कार्ड", "बहुतेक पडताळणीसाठी", "पॅन कार्ड", "आर्थिक KYC साठी", "मतदार ओळखपत्र", "पत्ता पडताळणीसाठी")
        else -> ScreenStrings("Select Your Document", "Aadhaar Card", "For most verifications", "PAN Card", "For financial KYC", "Voter ID Card", "For address verification")
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StepIndicator(currentStep = 1, totalSteps = 3)
        Spacer(modifier = Modifier.height(32.dp))
        Text(strings.title, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))

        val cardOnClick = {
            kycViewModel.startKycProcess { newId ->
                onProceed(newId)
                navController.navigate(Routes.DOCUMENT_CAPTURE)
            }
        }

        MethodCard(
            icon = Icons.Default.CreditCard,
            title = strings.aadhaarTitle,
            subtitle = strings.aadhaarSubtitle,
            onClick = cardOnClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        MethodCard(
            icon = Icons.Default.ContactPage,
            title = strings.panTitle,
            subtitle = strings.panSubtitle,
            onClick = cardOnClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        MethodCard(
            icon = Icons.Default.HowToVote,
            title = strings.voterTitle,
            subtitle = strings.voterSubtitle,
            onClick = cardOnClick
        )
    }
}