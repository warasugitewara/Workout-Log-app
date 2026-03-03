package com.workoutlogpro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val height: Float = 0f,
    val weight: Float = 0f,
    val targetWeight: Float = 0f,
    val gender: String = "",
    val birthDate: Long = 0L, // epoch millis
    val setTrackingMode: String = "together" // "together" = まとめて1チェック, "per_set" = 1セット毎
) {
    /** BMI = 体重(kg) / 身長(m)^2 */
    fun calcBmi(): Float {
        if (height <= 0f) return 0f
        val heightM = height / 100f
        return weight / (heightM * heightM)
    }

    /** Mifflin-St Jeor 推定基礎代謝量 (kcal/day) */
    fun calcBmr(): Float {
        if (height <= 0f || weight <= 0f) return 0f
        val age = ((System.currentTimeMillis() - birthDate) / (365.25 * 24 * 60 * 60 * 1000)).toInt()
        return if (gender == "male") {
            10f * weight + 6.25f * height - 5f * age + 5f
        } else {
            10f * weight + 6.25f * height - 5f * age - 161f
        }
    }
}
