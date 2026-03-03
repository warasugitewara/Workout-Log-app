package com.workoutlogpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.workoutlogpro.WorkoutLogApp
import com.workoutlogpro.data.entity.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as WorkoutLogApp).repository

    val user: StateFlow<User?> = repo.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val todayDayOfWeek: Int
        get() {
            val cal = Calendar.getInstance()
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            // Convert Sunday=1..Saturday=7 to Monday=1..Sunday=7
            return if (dow == Calendar.SUNDAY) 7 else dow - 1
        }

    val todaySchedules: StateFlow<List<WorkoutSchedule>> =
        repo.getScheduleByDay(todayDayOfWeek)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMenus: StateFlow<List<WorkoutMenu>> =
        repo.getAllMenus()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _weeklyCompletionRate = MutableStateFlow(0f)
    val weeklyCompletionRate: StateFlow<Float> = _weeklyCompletionRate

    private val _monthlyCalories = MutableStateFlow(0f)
    val monthlyCalories: StateFlow<Float> = _monthlyCalories

    private val _todayProtein = MutableStateFlow(0f)
    val todayProtein: StateFlow<Float> = _todayProtein

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()

            // Today range
            val todayStart = cal.clone() as Calendar
            todayStart.set(Calendar.HOUR_OF_DAY, 0)
            todayStart.set(Calendar.MINUTE, 0)
            todayStart.set(Calendar.SECOND, 0)
            todayStart.set(Calendar.MILLISECOND, 0)
            val todayEnd = cal.clone() as Calendar
            todayEnd.set(Calendar.HOUR_OF_DAY, 23)
            todayEnd.set(Calendar.MINUTE, 59)
            todayEnd.set(Calendar.SECOND, 59)

            // Week range (Monday start)
            val weekStart = cal.clone() as Calendar
            weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            weekStart.set(Calendar.HOUR_OF_DAY, 0)
            weekStart.set(Calendar.MINUTE, 0)
            weekStart.set(Calendar.SECOND, 0)

            // Month range
            val monthStart = cal.clone() as Calendar
            monthStart.set(Calendar.DAY_OF_MONTH, 1)
            monthStart.set(Calendar.HOUR_OF_DAY, 0)
            monthStart.set(Calendar.MINUTE, 0)
            monthStart.set(Calendar.SECOND, 0)

            // Weekly completion rate (based on actual schedules)
            repo.getAllSchedules().first().let { schedules ->
                val scheduledCount = schedules.size.coerceAtLeast(1)
                val weeklyLogs = repo.countLogsForPeriod(weekStart.timeInMillis, cal.timeInMillis)
                _weeklyCompletionRate.value = (weeklyLogs.toFloat() / scheduledCount * 100f).coerceAtMost(100f)
            }

            // Monthly calories
            repo.getLogsByDateRange(monthStart.timeInMillis, cal.timeInMillis)
                .first()
                .let { logs ->
                    _monthlyCalories.value = logs.sumOf { it.actualCalories.toDouble() }.toFloat()
                }

            // Today protein
            _todayProtein.value = repo.totalProteinForPeriod(todayStart.timeInMillis, todayEnd.timeInMillis)
        }
    }

    fun toggleScheduleComplete(id: Int, completed: Boolean) {
        viewModelScope.launch {
            repo.setScheduleCompleted(id, completed)
            // 完了時に自動でトレーニングログを作成
            if (completed) {
                val schedule = todaySchedules.value.find { it.id == id } ?: return@launch
                val menu = allMenus.value.find { it.id == schedule.menuId } ?: return@launch
                repo.saveLog(
                    WorkoutLog(
                        date = System.currentTimeMillis(),
                        menuId = menu.id,
                        actualReps = menu.defaultReps,
                        actualSets = menu.defaultSets,
                        durationSec = menu.avgTimeSec * menu.defaultSets,
                        actualCalories = menu.calorieEstimate,
                        weight = user.value?.weight ?: 0f,
                        fatigueLevel = 3
                    )
                )
                loadStats() // 統計を再読み込み
            }
        }
    }
}
