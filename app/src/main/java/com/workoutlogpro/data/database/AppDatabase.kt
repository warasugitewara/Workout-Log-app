package com.workoutlogpro.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.workoutlogpro.data.dao.*
import com.workoutlogpro.data.entity.*

@Database(
    entities = [
        User::class,
        WorkoutMenu::class,
        WorkoutLog::class,
        ProteinLog::class,
        WorkoutSchedule::class,
        ReminderSetting::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutMenuDao(): WorkoutMenuDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun proteinLogDao(): ProteinLogDao
    abstract fun workoutScheduleDao(): WorkoutScheduleDao
    abstract fun reminderSettingDao(): ReminderSettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS reminder_settings (
                        dayOfWeek INTEGER NOT NULL PRIMARY KEY,
                        hour INTEGER NOT NULL DEFAULT 9,
                        minute INTEGER NOT NULL DEFAULT 0,
                        isEnabled INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN setTrackingMode TEXT NOT NULL DEFAULT 'together'")
                db.execSQL("ALTER TABLE workout_schedules ADD COLUMN setNumber INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "workout_log_pro.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
