package com.workoutlogpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.workoutlogpro.WorkoutLogApp
import com.workoutlogpro.data.RoutineTemplates
import com.workoutlogpro.data.entity.ReminderSetting
import com.workoutlogpro.data.entity.WorkoutMenu
import com.workoutlogpro.data.entity.WorkoutSchedule
import com.workoutlogpro.worker.ReminderWorker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as WorkoutLogApp).repository
    private val app = application

    val allSchedules: StateFlow<List<WorkoutSchedule>> =
        repo.getAllSchedules()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMenus: StateFlow<List<WorkoutMenu>> =
        repo.getAllMenus()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReminders: StateFlow<List<ReminderSetting>> =
        repo.getAllReminders()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val routineTemplates = RoutineTemplates.all

    fun getSchedulesForDay(dayOfWeek: Int): List<WorkoutSchedule> =
        allSchedules.value.filter { it.dayOfWeek == dayOfWeek }

    /** 個別メニューをスケジュールに追加 */
    fun addMenuToDay(dayOfWeek: Int, menuId: Int) {
        viewModelScope.launch {
            repo.saveSchedule(WorkoutSchedule(dayOfWeek = dayOfWeek, menuId = menuId))
        }
    }

    /** スケジュールからメニューを削除 */
    fun removeSchedule(schedule: WorkoutSchedule) {
        viewModelScope.launch {
            repo.deleteSchedule(schedule)
        }
    }

    /** ルーティンテンプレートを指定曜日に一括登録 */
    fun applyRoutineToDay(dayOfWeek: Int, menuNames: List<String>) {
        viewModelScope.launch {
            // 既存スケジュールをクリア
            repo.deleteSchedulesByDay(dayOfWeek)
            // メニュー名からIDを逆引きして登録
            val menus = allMenus.value
            menuNames.forEach { name ->
                val menu = menus.find { it.name == name }
                if (menu != null) {
                    repo.saveSchedule(WorkoutSchedule(dayOfWeek = dayOfWeek, menuId = menu.id))
                }
            }
        }
    }

    /** 曜日のスケジュールをすべてクリア */
    fun clearDay(dayOfWeek: Int) {
        viewModelScope.launch {
            repo.deleteSchedulesByDay(dayOfWeek)
        }
    }

    /** リマインド設定を保存 + WorkManager に登録 */
    fun saveReminder(dayOfWeek: Int, hour: Int, minute: Int, isEnabled: Boolean) {
        viewModelScope.launch {
            repo.saveReminder(ReminderSetting(dayOfWeek, hour, minute, isEnabled))
            if (isEnabled) {
                val daySchedules = allSchedules.value.filter { it.dayOfWeek == dayOfWeek }
                val menuNames = daySchedules.mapNotNull { s ->
                    allMenus.value.find { it.id == s.menuId }?.name
                }
                val summary = if (menuNames.isNotEmpty()) menuNames.joinToString("・") else "トレーニング"
                ReminderWorker.scheduleReminder(app, dayOfWeek, hour, minute, summary)
            } else {
                ReminderWorker.cancelReminder(app, dayOfWeek, hour, minute)
            }
        }
    }
}
