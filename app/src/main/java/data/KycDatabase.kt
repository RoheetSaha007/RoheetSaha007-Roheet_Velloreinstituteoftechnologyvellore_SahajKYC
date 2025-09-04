package com.example.sahajkyc.data
import android.content.Context
import androidx.room.*

@Database(entities = [KycData::class], version = 1)
abstract class KycDatabase : RoomDatabase() {
    abstract fun kycDao(): KycDao
    companion object {
        @Volatile private var INSTANCE: KycDatabase? = null
        fun getDatabase(context: Context): KycDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, KycDatabase::class.java, "kyc_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}