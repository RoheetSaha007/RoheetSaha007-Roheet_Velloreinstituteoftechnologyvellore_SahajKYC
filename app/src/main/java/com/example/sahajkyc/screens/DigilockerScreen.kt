package com.example.sahajkyc.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sahajkyc.Routes
import com.example.sahajkyc.common.PrimaryButton
import com.example.sahajkyc.common.StepIndicator
import com.example.sahajkyc.viewmodel.KycViewModel
@Composable
fun DigilockerScreen(
    navController: NavController,
    langCode: String?,
    onProceed: (Long) -> Unit,
    kycViewModel: KycViewModel = viewModel()
) {
    var aadhaarNumber by remember { mutableStateOf("") }
    val isAadhaarValid = aadhaarNumber.length == 12

    data class ScreenStrings(
        val title: String,
        val subtitle: String,
        val label: String,
        val buttonText: String,
        val secureText: String
    )

    val strings = when (langCode) {
        "hi" -> ScreenStrings("डिजिलॉकर सत्यापन", "डिजिलॉकर से अपने दस्तावेज़ों को सुरक्षित रूप से प्राप्त करने के लिए इसका उपयोग किया जाता है।", "अपना 12 अंकों का आधार नंबर दर्ज करें", "सत्यापित करें", "आपकी जानकारी सुरक्षित और एन्क्रिप्टेड है")
        "bn" -> ScreenStrings("ডিজি-লকার যাচাইকরণ", "ডিজি-লকার থেকে আপনার নথি নিরাপদে আনতে এটি ব্যবহৃত হয়।", "আপনার ১২ সংখ্যার আধার নম্বর লিখুন", "যাচাই করুন", "আপনার তথ্য নিরাপদ এবং এনক্রিপ্টেড")
        "ta" -> ScreenStrings("டிஜிலாக்கர் சரிபார்ப்பு", "டிஜிலாக்கரிலிருந்து உங்கள் ஆவணங்களைப் பாதுகாப்பாகப் பெற இது பயன்படுகிறது.", "உங்கள் 12 இலக்க ஆதார் எண்ணை உள்ளிடவும்", "சரிபார்க்கவும்", "உங்கள் தகவல் பாதுகாப்பானது மற்றும் குறியாக்கம் செய்யப்பட்டது")
        "te" -> ScreenStrings("డిజీలాకర్ ధృవీకరణ", "డిజీలాకర్ నుండి మీ పత్రాలను సురక్షితంగా తీసుకురావడానికి ఇది ఉపయోగించబడుతుంది.", "మీ 12-అంకెల ఆధార్ నంబర్‌ను నమోదు చేయండి", "ధృవీకరించండి", "మీ సమాచారం సురక్షితమైనది మరియు గుప్తీకరించబడింది")
        "mr" -> ScreenStrings("डिजीलॉकर पडताळणी", "डिजीलॉकरमधून तुमची कागदपत्रे सुरक्षितपणे आणण्यासाठी याचा वापर केला जातो.", "तुमचा १२-अंकी आधार क्रमांक प्रविष्ट करा", "पडताळा", "तुमची माहिती सुरक्षित आणि एनक्रिप्टेड आहे")
        else -> ScreenStrings("Digilocker Verification", "This is used to securely fetch your documents from Digilocker.", "Enter your 12-digit Aadhaar Number", "Verify", "Your information is safe and encrypted")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StepIndicator(currentStep = 2, totalSteps = 3)
        Spacer(modifier = Modifier.height(32.dp))
        Icon(
            imageVector = Icons.Default.CreditCard,
            contentDescription = "Aadhaar Card Icon",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = strings.title,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = strings.subtitle,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = aadhaarNumber,
            onValueChange = {
                if (it.length <= 12 && it.all { char -> char.isDigit() }) {
                    aadhaarNumber = it
                }
            },
            label = { Text(strings.label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = "Aadhaar Icon") },
            supportingText = {
                Text(
                    text = "${aadhaarNumber.length} / 12",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Security Lock",
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = strings.secureText,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = strings.buttonText,
            onClick = {
                kycViewModel.startKycProcess { newId ->
                    onProceed(newId)
                    navController.navigate(Routes.FACE_LIVENESS)
                }
            },
            enabled = isAadhaarValid
        )
    }
}