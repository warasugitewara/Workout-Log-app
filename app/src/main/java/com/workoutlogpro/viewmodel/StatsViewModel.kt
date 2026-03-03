package com.workoutlogpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.workoutlogpro.WorkoutLogApp
import com.workoutlogpro.data.entity.User
import com.workoutlogpro.data.entity.WorkoutLog
import com.workoutlogpro.data.entity.WorkoutMenu
import com.workoutlogpro.data.entity.WorkoutSchedule
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class WeightEntry(val date: Long, val weight: Float)

data class CalorieEntry(val label: String, val calories: Float)

data class MenuStats(
    val menuId: Int,
    val menuName: String,
    val avgDurationSec: Float,
    val totalSessions: Int,
    val repsHistory: List<Pair<Long, Int>> // date -> reps
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as WorkoutLogApp).repository

    val user: StateFlow<User?> = repo.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val menus: StateFlow<List<WorkoutMenu>> = repo.getAllMenus()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weightHistory: StateFlow<List<WeightEntry>> =
        repo.getLogsWithWeight()
            .map { logs ->
                logs.filter { it.weight > 0f }
                    .map { WeightEntry(it.date, it.weight) }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLogs: StateFlow<List<WorkoutLog>> =
        repo.getAllLogs()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSchedules: StateFlow<List<WorkoutSchedule>> =
        repo.getAllSchedules()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** BMI推移（体重ログ＋ユーザー身長から算出） */
    fun bmiHistory(userHeight: Float): List<WeightEntry> {
        if (userHeight <= 0f) return emptyList()
        val heightM = userHeight / 100f
        return weightHistory.value.map {
            WeightEntry(it.date, it.weight / (heightM * heightM))
        }
    }

    /** 週別消費カロリー（直近8週） */
    fun weeklyCalories(logs: List<WorkoutLog>): List<CalorieEntry> {
        val cal = Calendar.getInstance()
        val result = mutableListOf<CalorieEntry>()
        for (i in 7 downTo 0) {
            val weekCal = cal.clone() as Calendar
            weekCal.add(Calendar.WEEK_OF_YEAR, -i)
            weekCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            weekCal.set(Calendar.HOUR_OF_DAY, 0); weekCal.set(Calendar.MINUTE, 0); weekCal.set(Calendar.SECOND, 0)
            val weekStart = weekCal.timeInMillis
            weekCal.add(Calendar.DAY_OF_YEAR, 7)
            val weekEnd = weekCal.timeInMillis

            val total = logs.filter { it.date in weekStart until weekEnd }
                .sumOf { it.actualCalories.toDouble() }.toFloat()

            val label = java.text.SimpleDateFormat("M/d", Locale.JAPAN).format(Date(weekStart))
            result.add(CalorieEntry(label, total))
        }
        return result
    }

    /** 月別消費カロリー（直近6ヶ月） */
    fun monthlyCalories(logs: List<WorkoutLog>): List<CalorieEntry> {
        val cal = Calendar.getInstance()
        val result = mutableListOf<CalorieEntry>()
        for (i in 5 downTo 0) {
            val monthCal = cal.clone() as Calendar
            monthCal.add(Calendar.MONTH, -i)
            monthCal.set(Calendar.DAY_OF_MONTH, 1)
            monthCal.set(Calendar.HOUR_OF_DAY, 0); monthCal.set(Calendar.MINUTE, 0); monthCal.set(Calendar.SECOND, 0)
            val monthStart = monthCal.timeInMillis
            monthCal.add(Calendar.MONTH, 1)
            val monthEnd = monthCal.timeInMillis

            val total = logs.filter { it.date in monthStart until monthEnd }
                .sumOf { it.actualCalories.toDouble() }.toFloat()

            val label = java.text.SimpleDateFormat("M月", Locale.JAPAN).format(Date(monthStart))
            result.add(CalorieEntry(label, total))
        }
        return result
    }

    /** 種目別統計 */
    fun menuStats(logs: List<WorkoutLog>, menus: List<WorkoutMenu>): List<MenuStats> {
        return logs.groupBy { it.menuId }.mapNotNull { (menuId, menuLogs) ->
            val menu = menus.find { it.id == menuId } ?: return@mapNotNull null
            MenuStats(
                menuId = menuId,
                menuName = menu.name,
                avgDurationSec = menuLogs.map { it.durationSec }.average().toFloat(),
                totalSessions = menuLogs.size,
                repsHistory = menuLogs.sortedBy { it.date }.map { it.date to it.actualReps }
            )
        }.sortedByDescending { it.totalSessions }
    }

    /** 週間実施率（スケジュール件数 vs 実際のログ件数） */
    fun weeklyCompletionRate(logs: List<WorkoutLog>, schedules: List<WorkoutSchedule>): Float {
        val cal = Calendar.getInstance()
        val weekStart = (cal.clone() as Calendar).apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }
        val scheduledCount = schedules.size.coerceAtLeast(1)
        val actualCount = logs.count { it.date >= weekStart.timeInMillis }
        return (actualCount.toFloat() / scheduledCount * 100f).coerceAtMost(100f)
    }

    /** 月間実施率 */
    fun monthlyCompletionRate(logs: List<WorkoutLog>, schedules: List<WorkoutSchedule>): Float {
        val cal = Calendar.getInstance()
        val monthStart = (cal.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
        }
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val daysPassed = cal.get(Calendar.DAY_OF_MONTH)
        // Average scheduled per day × days passed
        val avgPerDay = schedules.size.toFloat() / 7f
        val expectedCount = (avgPerDay * daysPassed).coerceAtLeast(1f)
        val actualCount = logs.count { it.date >= monthStart.timeInMillis }
        return (actualCount.toFloat() / expectedCount * 100f).coerceAtMost(100f)
    }
}
