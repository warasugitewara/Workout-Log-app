package com.workoutlogpro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_logs",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutMenu::class,
            parentColumns = ["id"],
            childColumns = ["menuId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("menuId"), Index("date")]
)
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long = 0L, // epoch millis
    val menuId: Int = 0,
    val actualReps: Int = 0,
    val actualSets: Int = 0,
    val durationSec: Int = 0,
    val actualCalories: Float = 0f,
    val weight: Float = 0f,
    val fatRate: Float? = null,
    val fatigueLevel: Int = 3 // 1〜5
)
