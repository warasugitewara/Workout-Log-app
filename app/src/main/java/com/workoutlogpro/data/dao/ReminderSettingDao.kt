package com.workoutlogpro.data.dao

import androidx.room.*
import com.workoutlogpro.data.entity.ReminderSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderSettingDao {
    @Query("SELECT * FROM reminder_settings ORDER BY dayOfWeek ASC")
    fun getAll(): Flow<List<ReminderSetting>>

    @Query("SELECT * FROM reminder_settings WHERE dayOfWeek = :dayOfWeek")
    suspend fun getByDay(dayOfWeek: Int): ReminderSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(setting: ReminderSetting)

    @Query("DELETE FROM reminder_settings WHERE dayOfWeek = :dayOfWeek")
    suspend fun deleteByDay(dayOfWeek: Int)
}
