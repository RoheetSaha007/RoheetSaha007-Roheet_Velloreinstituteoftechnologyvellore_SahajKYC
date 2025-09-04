package com.example.sahajkyc.common
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*
class VoiceAssistant(context: Context, private val onReady: () -> Unit) {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                Log.d("TTS", "Initialization Success")
                onReady()
            } else {
                Log.e("TTS", "Initialization Failed")
            }
        }
    }
    fun speak(text: String, langCode: String) {
        val locale = when (langCode) {
            "hi" -> Locale("hi", "IN")
            "bn" -> Locale("bn", "IN")
            "ta" -> Locale("ta", "IN")
            "te" -> Locale("te", "IN")
            "mr" -> Locale("mr", "IN")
            else -> Locale.ENGLISH
        }
        tts?.setSpeechRate(0.85f)

        tts?.language = locale
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}