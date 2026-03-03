package com.workoutlogpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "protein_logs")
data class ProteinLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long = 0L, // epoch millis
    val amount: Float = 0f, // grams
    val type: String = "" // 水 / 牛乳
)
