package com.workoutlogpro.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.workoutlogpro.data.dao.*
import com.workoutlogpro.data.entity.*

@Database(
    entities = [
        User::class,
        WorkoutMenu::class,
        WorkoutLog::class,
        ProteinLog::class,
        WorkoutSchedule::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutMenuDao(): WorkoutMenuDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun proteinLogDao(): ProteinLogDao
    abstract fun workoutScheduleDao(): WorkoutScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "workout_log_pro.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
