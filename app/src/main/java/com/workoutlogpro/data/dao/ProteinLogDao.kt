package com.workoutlogpro.data.dao

import androidx.room.*
import com.workoutlogpro.data.entity.ProteinLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ProteinLogDao {
    @Query("SELECT * FROM protein_logs ORDER BY date DESC")
    fun getAll(): Flow<List<ProteinLog>>

    @Query("SELECT * FROM protein_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<ProteinLog>>

    @Query("SELECT SUM(amount) FROM protein_logs WHERE date BETWEEN :startDate AND :endDate")
    suspend fun totalForPeriod(startDate: Long, endDate: Long): Float?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: ProteinLog): Long

    @Delete
    suspend fun delete(log: ProteinLog)
}
