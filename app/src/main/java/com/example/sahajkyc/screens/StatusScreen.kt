package com.example.sahajkyc.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sahajkyc.Routes
import com.example.sahajkyc.common.PrimaryButton
import com.example.sahajkyc.ui.theme.SahajGreen
import com.example.sahajkyc.viewmodel.KycViewModel

@Composable
fun StatusScreen(
    navController: NavController,
    langCode: String?,
    kycId: Long,
    kycViewModel: KycViewModel = viewModel()
) {
    val kycDataState by kycViewModel.getKycStatus(kycId).collectAsState(initial = null)
    val status = kycDataState?.status
    if (status == "COMPLETE") {
        SuccessView(langCode = langCode, navController = navController)
    } else {
        InProgressView(langCode = langCode, status = status)
    }
}

@Composable
fun InProgressView(langCode: String?, status: String?) {
    val progressText = when (langCode) {
        "hi" -> if (status == "UPLOADING") "अपलोड हो रहा है..." else "प्रतीक्षा में है..."
        "bn" -> if (status == "UPLOADING") "আপলোড চলছে..." else "অপেক্ষারত..."
        "ta" -> if (status == "UPLOADING") "பதிவேற்றம் চলছে..." else "காத்திருக்கிறது..."
        "te" -> if (status == "UPLOADING") "అప్‌లోడ్ ప్రోగ్రెస్‌లో ఉంది..." else "పెండింగ్‌లో ఉంది..."
        "mr" -> if (status == "UPLOADING") "अपलोड प्रगतीपथावर आहे..." else "प्रलंबित..."
        else -> if (status == "UPLOADING") "Upload in Progress..." else "Pending..."
    }
    val subtitleText = when (langCode) {
        "hi" -> "कृपया ऐप खुला रखें। आपका डेटा सुरक्षित रूप से अपलोड किया जा रहा है।"
        "bn" -> "অনুগ্রহ করে অ্যাপটি খোলা রাখুন। আপনার ডেটা নিরাপদে আপলোড করা হচ্ছে।"
        "ta" -> "செயலியைத் திறந்து வைக்கவும். உங்கள் தரவு பாதுகாப்பாக பதிவேற்றப்படுகிறது."
        "te" -> "దయచేసి యాప్‌ను తెరిచి ఉంచండి. మీ డేటా సురక్షితంగా అప్‌లోడ్ చేయబడుతోంది."
        "mr" -> "कृपया ॲप उघडे ठेवा. तुमचा डेटा सुरक्षितपणे अपलोड केला जात आहे."
        else -> "Please keep the app open. Your data is being securely uploaded."
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = progressText,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitleText,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SuccessView(langCode: String?, navController: NavController) {
    val title = when (langCode) {
        "hi" -> "दस्तावेज़ जमा हो गए हैं!"
        "bn" -> "নথি জমা দেওয়া হয়েছে!"
        "ta" -> "ஆவணங்கள் சமர்ப்பிக்கப்பட்டன!"
        "te" -> "పత్రాలు సమర్పించబడ్డాయి!"
        "mr" -> "दस्तऐवज सबमिट केले!"
        else -> "Documents Submitted!"
    }
    val subtitle = when (langCode) {
        "hi" -> "धन्यवाद! सत्यापन पूरा होने पर हम आपको सूचित करेंगे।"
        "bn" -> "ধন্যবাদ! যাচাইকরণ সম্পূর্ণ হলে আমরা আপনাকে অবহিত করব।"
        "ta" -> "நன்றி! சரிபார்ப்பு முடிந்ததும் நாங்கள் உங்களுக்குத் தெரிவிப்போம்।"
        "te" -> "ధన్యవాదాలు! ధృవీకరణ పూర్తయిన తర్వాత మేము మీకు తెలియజేస్తాము।"
        "mr" -> "धन्यवाद! पडताळणी पूर्ण झाल्यावर आम्ही तुम्हाला सूचित करू।"
        else -> "Thank you! We will notify you once the verification is complete."
    }
    val buttonText = when (langCode) {
        "hi" -> "पूर्ण"
        "bn" -> "সম্পন্ন"
        "ta" -> "முடிந்தது"
        "te" -> "పూర్తయింది"
        "mr" -> "झाले"
        else -> "Done"
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = SahajGreen,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "What happens next?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• We will review your submission.\n" +
                            "• You will be notified within 24 hours.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1.5f))
        PrimaryButton(
            text = buttonText,
            onClick = {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(Routes.WELCOME) { inclusive = true }
                }
            }
        )
    }
}