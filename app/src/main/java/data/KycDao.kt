package com.example.sahajkyc.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KycDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdateKycData(kycData: KycData): Long

    @Query("SELECT * FROM kyc_data WHERE id = :id")
    fun getKycStatus(id: Long): Flow<KycData?>

    @Query("UPDATE kyc_data SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}