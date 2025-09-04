package com.example.sahajkyc.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.sahajkyc.data.KycDao
import com.example.sahajkyc.data.KycDatabase
import com.example.sahajkyc.data.KycData
import com.example.sahajkyc.workers.UploadWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class KycViewModel(application: Application) : AndroidViewModel(application) {
    private val kycDao: KycDao = KycDatabase.getDatabase(application).kycDao()
    private val workManager = WorkManager.getInstance(application)

    fun getKycStatus(id: Long): Flow<KycData?> {
        return kycDao.getKycStatus(id)
    }

    fun startKycProcess(onKycIdCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val newKycData = KycData(status = "STARTED")
            val newId = kycDao.createOrUpdateKycData(newKycData)
            onKycIdCreated(newId)
        }
    }

    fun saveDocumentAndProceed(kycId: Long, docUri: Uri) {
        viewModelScope.launch {
            val currentData = kycDao.getKycStatus(kycId).first() ?: KycData(id = kycId.toInt(), status = "STARTED")
            currentData.documentUri = docUri.toString()
            kycDao.createOrUpdateKycData(currentData)
        }
    }

    fun saveFaceAndUpload(kycId: Long, faceUri: Uri) {
        viewModelScope.launch {
            val currentData = kycDao.getKycStatus(kycId).first() ?: return@launch
            currentData.faceImageUri = faceUri.toString()
            currentData.status = "PENDING_UPLOAD"
            kycDao.createOrUpdateKycData(currentData)

            val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
                .setInputData(workDataOf("KYC_ID" to kycId))
                .build()
            workManager.enqueue(uploadWorkRequest)
        }
    }
}