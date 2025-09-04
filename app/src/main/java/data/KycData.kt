package com.example.sahajkyc.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kyc_data")
data class KycData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var documentUri: String? = null,
    var faceImageUri: String? = null,
    var status: String
)