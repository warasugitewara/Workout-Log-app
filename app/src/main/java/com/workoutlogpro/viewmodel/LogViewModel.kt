package com.workoutlogpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.workoutlogpro.WorkoutLogApp
import com.workoutlogpro.data.entity.ProteinLog
import com.workoutlogpro.data.entity.WorkoutLog
import com.workoutlogpro.data.entity.WorkoutMenu
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LogViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as WorkoutLogApp).repository

    val recentLogs: StateFlow<List<WorkoutLog>> =
        repo.getAllLogs()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val menus: StateFlow<List<WorkoutMenu>> =
        repo.getAllMenus()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val proteinLogs: StateFlow<List<ProteinLog>> =
        repo.getAllProteinLogs()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentLog = MutableStateFlow(
        WorkoutLog(date = System.currentTimeMillis())
    )
    val currentLog: StateFlow<WorkoutLog> = _currentLog

    fun updateCurrentLog(log: WorkoutLog) {
        _currentLog.value = log
    }

    fun resetCurrentLog() {
        _currentLog.value = WorkoutLog(date = System.currentTimeMillis())
    }

    fun saveLog() {
        viewModelScope.launch {
            repo.saveLog(_currentLog.value)
            resetCurrentLog()
        }
    }

    fun deleteLog(log: WorkoutLog) {
        viewModelScope.launch {
            repo.deleteLog(log)
        }
    }

    fun saveProteinLog(amount: Float, type: String) {
        viewModelScope.launch {
            repo.saveProteinLog(
                ProteinLog(
                    date = System.currentTimeMillis(),
                    amount = amount,
                    type = type
                )
            )
        }
    }

    fun deleteProteinLog(log: ProteinLog) {
        viewModelScope.launch {
            repo.deleteProteinLog(log)
        }
    }
}
