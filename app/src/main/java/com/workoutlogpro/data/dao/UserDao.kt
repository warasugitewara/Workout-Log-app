package com.workoutlogpro.data.dao

import androidx.room.*
import com.workoutlogpro.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: User)

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}
