package com.workoutlogpro.data.dao

import androidx.room.*
import com.workoutlogpro.data.entity.WorkoutMenu
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutMenuDao {
    @Query("SELECT * FROM workout_menus ORDER BY name ASC")
    fun getAll(): Flow<List<WorkoutMenu>>

    @Query("SELECT * FROM workout_menus WHERE id = :id")
    suspend fun getById(id: Int): WorkoutMenu?

    @Query("SELECT * FROM workout_menus WHERE category = :category ORDER BY name ASC")
    fun getByCategory(category: String): Flow<List<WorkoutMenu>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(menu: WorkoutMenu): Long

    @Delete
    suspend fun delete(menu: WorkoutMenu)

    @Query("DELETE FROM workout_menus WHERE id = :id")
    suspend fun deleteById(id: Int)
}
