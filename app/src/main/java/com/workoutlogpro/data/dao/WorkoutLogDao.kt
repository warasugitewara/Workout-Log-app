package com.workoutlogpro.data.dao

import androidx.room.*
import com.workoutlogpro.data.entity.WorkoutLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutLogDao {
    @Query("SELECT * FROM workout_logs ORDER BY date DESC")
    fun getAll(): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_logs WHERE menuId = :menuId ORDER BY date DESC")
    fun getByMenu(menuId: Int): Flow<List<WorkoutLog>>

    @Query("SELECT * FROM workout_logs WHERE date BETWEEN :startDate AND :endDate")
    fun getLogsForPeriod(startDate: Long, endDate: Long): Flow<List<WorkoutLog>>

    @Query("SELECT DISTINCT weight FROM workout_logs WHERE weight > 0 ORDER BY date ASC")
    fun getWeightHistory(): Flow<List<Float>>

    @Query("SELECT * FROM workout_logs WHERE weight > 0 ORDER BY date ASC")
    fun getLogsWithWeight(): Flow<List<WorkoutLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: WorkoutLog): Long

    @Delete
    suspend fun delete(log: WorkoutLog)

    @Query("SELECT COUNT(*) FROM workout_logs WHERE date BETWEEN :startDate AND :endDate")
    suspend fun countForPeriod(startDate: Long, endDate: Long): Int
}
