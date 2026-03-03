package com.workoutlogpro.data.repository

import com.workoutlogpro.data.dao.*
import com.workoutlogpro.data.entity.*
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val menuDao: WorkoutMenuDao,
    private val logDao: WorkoutLogDao,
    private val scheduleDao: WorkoutScheduleDao,
    private val proteinLogDao: ProteinLogDao,
    private val userDao: UserDao,
    private val reminderSettingDao: ReminderSettingDao
) {
    // --- User ---
    fun getUser(): Flow<User?> = userDao.getUser()
    suspend fun saveUser(user: User) = userDao.upsert(user)

    // --- Menu ---
    fun getAllMenus(): Flow<List<WorkoutMenu>> = menuDao.getAll()
    suspend fun getMenuById(id: Int): WorkoutMenu? = menuDao.getById(id)
    fun getMenusByCategory(category: String): Flow<List<WorkoutMenu>> = menuDao.getByCategory(category)
    suspend fun saveMenu(menu: WorkoutMenu): Long = menuDao.upsert(menu)
    suspend fun deleteMenu(menu: WorkoutMenu) = menuDao.delete(menu)

    // --- Workout Log ---
    fun getAllLogs(): Flow<List<WorkoutLog>> = logDao.getAll()
    fun getLogsByDateRange(start: Long, end: Long): Flow<List<WorkoutLog>> = logDao.getByDateRange(start, end)
    fun getLogsByMenu(menuId: Int): Flow<List<WorkoutLog>> = logDao.getByMenu(menuId)
    fun getLogsWithWeight(): Flow<List<WorkoutLog>> = logDao.getLogsWithWeight()
    suspend fun saveLog(log: WorkoutLog): Long = logDao.upsert(log)
    suspend fun deleteLog(log: WorkoutLog) = logDao.delete(log)
    suspend fun countLogsForPeriod(start: Long, end: Long): Int = logDao.countForPeriod(start, end)

    // --- Schedule ---
    fun getScheduleByDay(dayOfWeek: Int): Flow<List<WorkoutSchedule>> = scheduleDao.getByDay(dayOfWeek)
    fun getAllSchedules(): Flow<List<WorkoutSchedule>> = scheduleDao.getAll()
    suspend fun saveSchedule(schedule: WorkoutSchedule): Long = scheduleDao.upsert(schedule)
    suspend fun deleteSchedule(schedule: WorkoutSchedule) = scheduleDao.delete(schedule)
    suspend fun setScheduleCompleted(id: Int, completed: Boolean) = scheduleDao.setCompleted(id, completed)

    // --- Protein ---
    fun getAllProteinLogs(): Flow<List<ProteinLog>> = proteinLogDao.getAll()
    fun getProteinLogsByDateRange(start: Long, end: Long): Flow<List<ProteinLog>> = proteinLogDao.getByDateRange(start, end)
    suspend fun totalProteinForPeriod(start: Long, end: Long): Float = proteinLogDao.totalForPeriod(start, end) ?: 0f
    suspend fun saveProteinLog(log: ProteinLog): Long = proteinLogDao.upsert(log)
    suspend fun deleteProteinLog(log: ProteinLog) = proteinLogDao.delete(log)

    // --- Reminder ---
    fun getAllReminders(): Flow<List<ReminderSetting>> = reminderSettingDao.getAll()
    suspend fun getReminderByDay(dayOfWeek: Int): ReminderSetting? = reminderSettingDao.getByDay(dayOfWeek)
    suspend fun saveReminder(setting: ReminderSetting) = reminderSettingDao.upsert(setting)
    suspend fun deleteReminderByDay(dayOfWeek: Int) = reminderSettingDao.deleteByDay(dayOfWeek)

    // --- Schedule helpers ---
    suspend fun deleteSchedulesByDay(dayOfWeek: Int) = scheduleDao.deleteByDay(dayOfWeek)
}
