package com.example.sahajkyc.workers
import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.sahajkyc.data.KycDatabase
import kotlinx.coroutines.delay

class UploadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    private val kycDao = KycDatabase.getDatabase(appContext).kycDao()
    override suspend fun doWork(): Result {
        val kycId = inputData.getLong("KYC_ID", -1L)
        if (kycId == -1L) return Result.failure()
        return try {
            kycDao.updateStatus(kycId, "UPLOADING")
            Log.d("UploadWorker", "Uploading KYC data for ID: $kycId")
            delay(5000) // Simulate a 5-second network upload
            Log.d("UploadWorker", "Upload successful for ID: $kycId")
            kycDao.updateStatus(kycId, "COMPLETE")
            Result.success()
        } catch (e: Exception) {
            Log.e("UploadWorker", "Upload failed for ID: $kycId", e)
            kycDao.updateStatus(kycId, "FAILED")
            Result.retry()
        }
    }
}