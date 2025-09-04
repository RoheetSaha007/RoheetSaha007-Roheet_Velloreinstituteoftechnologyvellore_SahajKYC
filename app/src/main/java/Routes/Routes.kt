package com.example.sahajkyc

object Routes {
    const val WELCOME = "welcome"
    const val METHOD_SELECTION = "method_selection"
    const val DOCUMENT_SELECTION = "document_selection"
    const val DOCUMENT_CAPTURE = "document_capture"
    const val FACE_LIVENESS = "face_liveness"
    const val DIGILOCKER = "digilocker"

    const val STATUS = "status/{kycId}"

    fun statusWithId(id: Long) = "status/$id"
}