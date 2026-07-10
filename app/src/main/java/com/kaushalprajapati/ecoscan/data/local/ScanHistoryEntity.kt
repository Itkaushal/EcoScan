package com.kaushalprajapati.ecoscan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val barcode: String,
    val productName: String,
    val brand: String,
    val imageEmoji: String,
    val ecoScore: Int,
    val ecoGrade: String,
    val scannedAtEpochMillis: Long
)
