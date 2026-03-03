package com.workoutlogpro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_schedules",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutMenu::class,
            parentColumns = ["id"],
            childColumns = ["menuId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("menuId")]
)
data class WorkoutSchedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayOfWeek: Int = 0, // 1=Mon ... 7=Sun
    val menuId: Int = 0,
    val isCompleted: Boolean = false
)
