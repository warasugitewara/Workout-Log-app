package com.workoutlogpro.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.workoutlogpro.MainActivity
import com.workoutlogpro.WorkoutLogApp
import java.util.*
import java.util.concurrent.TimeUnit

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val menuName = inputData.getString("menu_name") ?: "トレーニング"
        showNotification(menuName)
        return Result.success()
    }

    private fun showNotification(menuName: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, WorkoutLogApp.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("💪 トレーニングの時間です！")
            .setContentText("$menuName の時間です。今日も頑張りましょう！")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        /**
         * Schedule a recurring reminder for a specific day of week and time.
         * @param dayOfWeek 1=Monday ... 7=Sunday
         * @param hour hour of day (0-23)
         * @param minute minute (0-59)
         * @param menuName name to display in notification
         */
        fun scheduleReminder(
            context: Context,
            dayOfWeek: Int,
            hour: Int,
            minute: Int,
            menuName: String
        ) {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                // Convert our Mon=1..Sun=7 to Calendar.MONDAY..SUNDAY
                val calDay = if (dayOfWeek == 7) Calendar.SUNDAY else dayOfWeek + 1
                set(Calendar.DAY_OF_WEEK, calDay)
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(now)) add(Calendar.WEEK_OF_YEAR, 1)
            }

            val delay = target.timeInMillis - now.timeInMillis

            val inputData = Data.Builder()
                .putString("menu_name", menuName)
                .build()

            val tag = "reminder_${dayOfWeek}_${hour}_${minute}"

            val request = PeriodicWorkRequestBuilder<ReminderWorker>(7, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(tag)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.UPDATE, request)
        }

        fun cancelReminder(context: Context, dayOfWeek: Int, hour: Int, minute: Int) {
            val tag = "reminder_${dayOfWeek}_${hour}_${minute}"
            WorkManager.getInstance(context).cancelUniqueWork(tag)
        }
    }
}
