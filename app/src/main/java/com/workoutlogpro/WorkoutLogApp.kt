package com.workoutlogpro

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.workoutlogpro.data.database.AppDatabase
import com.workoutlogpro.data.repository.WorkoutRepository

class WorkoutLogApp : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy {
        WorkoutRepository(
            menuDao = database.workoutMenuDao(),
            logDao = database.workoutLogDao(),
            scheduleDao = database.workoutScheduleDao(),
            proteinLogDao = database.proteinLogDao(),
            userDao = database.userDao(),
            reminderSettingDao = database.reminderSettingDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "トレーニングリマインド",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "筋トレの予定をお知らせします"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "workout_reminder"
    }
}
