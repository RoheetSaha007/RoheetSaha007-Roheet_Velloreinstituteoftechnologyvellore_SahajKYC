package com.example.sahajkyc.screens

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sahajkyc.Routes
import com.example.sahajkyc.common.StepIndicator
import com.example.sahajkyc.common.VoiceAssistant
import com.example.sahajkyc.common.captureImage
import com.example.sahajkyc.ui.theme.SahajGreen
import com.example.sahajkyc.viewmodel.KycViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceLivenessScreen(
    navController: NavController,
    langCode: String?,
    kycId: Long?,
    kycViewModel: KycViewModel = viewModel()
) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    if (permissionState.status.isGranted) {
        if (kycId != null) {
            FaceLivenessCameraView(navController, langCode, kycId, kycViewModel)
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: KYC Session ID is missing.")
            }
        }
    } else {
        LaunchedEffect(Unit) {
            permissionState.launchPermissionRequest()
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Camera permission is required for face verification.")
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun FaceLivenessCameraView(
    navController: NavController,
    langCode: String?,
    kycId: Long,
    kycViewModel: KycViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val coroutineScope = rememberCoroutineScope()

    var faceDetected by remember { mutableStateOf(false) }
    var isLivenessCheckComplete by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(targetValue = if (faceDetected) SahajGreen else Color.White, label = "")
    val alphaAnimation by animateFloatAsState(targetValue = if (isLivenessCheckComplete) 1f else 0f, label = "")

    val successText = when (langCode) {
        "hi" -> "लाइवनेस जांच सफल"
        "bn" -> "লাইভনেস পরীক্ষা সফল"
        "ta" -> "நேரடி சோதனை வெற்றி"
        "te" -> "లైవ్‌నెస్ తనిఖీ విజయవంతమైంది"
        "mr" -> "लाइव्हनेस तपासणी यशस्वी"
        else -> "Liveness Check Successful"
    }
    val verificationCompleteText = when (langCode) {
        "hi" -> "सत्यापन पूर्ण"
        "bn" -> "যাচাইকরণ সম্পন্ন"
        "ta" -> "சரிபார்ப்பு முடிந்தது"
        "te" -> "ధృవీకరణ పూర్తయింది"
        "mr" -> "पडताळणी पूर्ण झाली"
        else -> "Verification Complete"
    }
    val holdStillText = when (langCode) {
        "hi" -> "स्थिर रहें…"
        "bn" -> "স্থির থাকুন…"
        "ta" -> "அப்படியே இருங்கள்…"
        "te" -> "నిశ్చలంగా ఉండండి…"
        "mr" -> "स्थिर राहा…"
        else -> "Hold Still..."
    }
    val instructionTextDefault = when (langCode) {
        "hi" -> "कृपया सीधे कैमरे में देखें"
        "bn" -> "অনুগ্রহ করে সরাসরি ক্যামেরার দিকে তাকান"
        "ta" -> "தயவுசெய்து கேமராவை நேராகப் பாருங்கள்"
        "te" -> "దయచేసి కెమెరాలోకి నిటారుగా చూడండి"
        "mr" -> "कृपया कॅमेऱ्यात सरळ पहा"
        else -> "Please look straight at the camera"
    }

    var voiceAssistant by remember { mutableStateOf<VoiceAssistant?>(null) }
    DisposableEffect(Unit) {
        voiceAssistant = VoiceAssistant(context) {
            voiceAssistant?.speak(instructionTextDefault, langCode ?: "en")
        }
        onDispose {
            voiceAssistant?.shutdown()
        }
    }

    LaunchedEffect(faceDetected) {
        if (faceDetected && !hasNavigated) {
            hasNavigated = true
            captureImage(imageCapture, context) { faceUri ->
                if (faceUri != null) {
                    kycViewModel.saveFaceAndUpload(kycId, faceUri)
                    coroutineScope.launch {
                        isLivenessCheckComplete = true
                        delay(1500)
                        navController.navigate(Routes.statusWithId(kycId)) {
                            popUpTo(Routes.WELCOME) { inclusive = false }
                        }
                    }
                } else {
                    hasNavigated = false
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val previewView = remember { PreviewView(context) }
        val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
        LaunchedEffect(Unit) {
            val cameraProvider = ProcessCameraProvider.getInstance(context).await()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            val faceDetector = FaceDetection.getClient(
                FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build()
            )
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null && !isLivenessCheckComplete) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            faceDetector.process(image)
                                .addOnSuccessListener { faces ->
                                    ContextCompat.getMainExecutor(context).execute {
                                        faceDetected = faces.any { face ->
                                            (face.leftEyeOpenProbability ?: 0f) > 0.8 && (face.rightEyeOpenProbability ?: 0f) > 0.8
                                        }
                                    }
                                }
                                .addOnFailureListener { e -> Log.e("FaceDetection", "Face detection failed", e) }
                                .addOnCompleteListener { imageProxy.close() }
                        } else {
                            imageProxy.close()
                        }
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer, imageCapture)
            } catch (e: Exception) {
                Log.e("FaceLiveness", "Use case binding failed", e)
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                cameraExecutor.shutdown()
                ProcessCameraProvider.getInstance(context).get().unbindAll()
            }
        }

        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StepIndicator(currentStep = 3, totalSteps = 3)
            Spacer(modifier = Modifier.weight(1f))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
                    .border(width = 5.dp, color = borderColor, shape = CircleShape)
            ) {
                if (isLivenessCheckComplete) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.alpha(alphaAnimation)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Success", tint = SahajGreen, modifier = Modifier.size(100.dp))
                        Text(successText, color = SahajGreen, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            val instructionText = when {
                isLivenessCheckComplete -> verificationCompleteText
                faceDetected -> holdStillText
                else -> instructionTextDefault
            }
            Text(
                text = instructionText,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 64.dp)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(16.dp)
            )
        }
    }
}