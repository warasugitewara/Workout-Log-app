package com.workoutlogpro.data.dao

import androidx.room.*
import com.workoutlogpro.data.entity.WorkoutSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutScheduleDao {
    @Query("SELECT * FROM workout_schedules WHERE dayOfWeek = :dayOfWeek")
    fun getByDay(dayOfWeek: Int): Flow<List<WorkoutSchedule>>

    @Query("SELECT * FROM workout_schedules ORDER BY dayOfWeek ASC")
    fun getAll(): Flow<List<WorkoutSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(schedule: WorkoutSchedule): Long

    @Delete
    suspend fun delete(schedule: WorkoutSchedule)

    @Query("DELETE FROM workout_schedules WHERE dayOfWeek = :dayOfWeek")
    suspend fun deleteByDay(dayOfWeek: Int)

    @Query("UPDATE workout_schedules SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Int, completed: Boolean)
}
