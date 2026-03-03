package com.workoutlogpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_settings")
data class ReminderSetting(
    @PrimaryKey
    val dayOfWeek: Int = 0, // 1=Mon ... 7=Sun
    val hour: Int = 9,
    val minute: Int = 0,
    val isEnabled: Boolean = false
)
