package com.workoutlogpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_menus")
data class WorkoutMenu(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val category: String = "", // 上半身 / 下半身 / 有酸素 / 体幹
    val defaultReps: Int = 0,
    val defaultSets: Int = 0,
    val avgTimeSec: Int = 0,
    val calorieEstimate: Float = 0f,
    val memo: String = ""
)
