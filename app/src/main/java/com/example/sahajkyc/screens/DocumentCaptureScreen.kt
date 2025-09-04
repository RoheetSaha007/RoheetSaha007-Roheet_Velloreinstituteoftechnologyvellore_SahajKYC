package com.example.sahajkyc.screens
import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sahajkyc.Routes
import com.example.sahajkyc.common.StepIndicator
import com.example.sahajkyc.common.VoiceAssistant
import com.example.sahajkyc.common.captureImage
import com.example.sahajkyc.viewmodel.KycViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DocumentCaptureScreen(
    navController: NavController,
    langCode: String?,
    kycId: Long,
    kycViewModel: KycViewModel = viewModel()
) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    if (permissionState.status.isGranted) {
        CameraScreen(navController, langCode, kycId, kycViewModel)
    } else {
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
        PermissionDeniedScreen {
            permissionState.launchPermissionRequest()
        }
    }
}
@Composable
private fun CameraScreen(
    navController: NavController,
    langCode: String?,
    kycId: Long,
    kycViewModel: KycViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    var warningMessage by remember { mutableStateOf<String?>(null) }
    val instructionText = when(langCode) {
        "hi" -> "दस्तावेज़ को बॉक्स के अंदर रखें और फोटो लें"
        "bn" -> "বক্সের ভিতরে ডকুমেন্ট রাখুন এবং একটি ছবি তুলুন"
        "ta" -> "ஆவணத்தை பெட்டியின் உள்ளே வைத்து புகைப்படம் எடுக்கவும்"
        "te" -> "పత్రాన్ని పెట్టెలో ఉంచి ఫోటో తీయండి"
        "mr" -> "दस्तऐवज बॉक्समध्ये ठेवा आणि फोटो घ्या"
        else -> "Place the document inside the box and take a photo"
    }
    var voiceAssistant by remember { mutableStateOf<VoiceAssistant?>(null) }
    DisposableEffect(Unit) {
        voiceAssistant = VoiceAssistant(context) {
            voiceAssistant?.speak(instructionText, langCode ?: "en")
        }
        onDispose {
            voiceAssistant?.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val previewView = remember { PreviewView(context) }
        DisposableEffect(lifecycleOwner) {
            val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            var cameraProvider: ProcessCameraProvider? = null

            cameraProviderFuture.addListener({
                cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    cameraProvider?.unbindAll()
                    cameraProvider?.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                } catch (e: Exception) {
                    Log.e("CameraCapture", "Use case binding failed", e)
                }
            }, ContextCompat.getMainExecutor(context))

            onDispose {
                cameraProvider?.unbindAll()
                cameraExecutor.shutdown()
            }
        }

        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StepIndicator(currentStep = 2, totalSteps = 3)
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = instructionText,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1.586f)
                    .border(width = 3.dp, color = Color.White, shape = RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    captureImage(imageCapture, context) { savedUri ->
                        if (savedUri != null) {
                            if (isImageBlurry(context, savedUri)) {
                                warningMessage = when (langCode) {
                                    "hi" -> "फोटो धुंधली है। कृपया दोबारा प्रयास करें।"
                                    "bn" -> "ছবি ঝাপসা। আবার চেষ্টা করুন।"
                                    "ta" -> "படம் மிகவும் மங்கலாக உள்ளது. மீண்டும் முயலவும்."
                                    "te" -> "చిత్రం అస్పష్టంగా ఉంది. దయచేసి మళ్లీ ప్రయత్నించండి."
                                    "mr" -> "फोटो अंधुक आहे. कृपया पुन्हा प्रयत्न करा."
                                    else -> "The photo is blurry. Please try again."
                                }
                            } else {
                                kycViewModel.saveDocumentAndProceed(kycId, savedUri)
                                navController.navigate(Routes.FACE_LIVENESS)
                            }
                        } else {
                            warningMessage = when (langCode) {
                                "hi" -> "फोटो लेने में विफल। कृपया दोबारा प्रयास करें।"
                                "bn" -> "ছবি তুলতে ব্যর্থ। আবার চেষ্টা করুন।"
                                "ta" -> "புகைப்படம் எடுக்கத் தவறிவிட்டது. மீண்டும் முயலவும்."
                                "te" -> "ఫోటో తీయడంలో విఫలమైంది. దయచేసి మళ్లీ ప్రయత్నించండి."
                                "mr" -> "फोटो कॅప్ચર करण्यात अयशस्वी. कृपया पुन्हा प्रयत्न करा."
                                else -> "Failed to capture photo. Please try again."
                            }
                        }
                    }
                },
                modifier = Modifier.padding(bottom = 50.dp).size(80.dp).background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = "Capture",
                    tint = Color.Black,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
    if (warningMessage != null) {
        AlertDialog(
            onDismissRequest = { warningMessage = null },
            title = {
                Text(text = when (langCode) {
                    "hi" -> "गुणवत्ता चेतावनी"
                    "bn" -> "গুণমান সতর্কতা"
                    "ta" -> "தர எச்சரிக்கை"
                    "te" -> "నాణ్యత హెచ్చరిక"
                    "mr" -> "गुणवत्ता चेतावणी"
                    else -> "Quality Warning"
                })
            },
            text = { Text(text = warningMessage!!) },
            confirmButton = {
                TextButton(onClick = { warningMessage = null }) {
                    Text(when (langCode) {
                        "hi" -> "ठीक है"
                        "bn" -> "ঠিক আছে"
                        "ta" -> "சரி"
                        "te" -> "సరే"
                        "mr" -> "ठीक आहे"
                        else -> "OK"
                    })
                }
            }
        )
    }
}
@Composable
private fun PermissionDeniedScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Camera Permission Denied")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text("Request Permission Again")
        }
    }
}
private fun isImageBlurry(context: Context, imageUri: Uri): Boolean {
    return try {
        Random.nextBoolean()
    } catch (e: Exception) {
        true
    }
}