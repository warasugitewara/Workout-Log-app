package com.workoutlogpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.workoutlogpro.WorkoutLogApp
import com.workoutlogpro.data.entity.WorkoutLog
import kotlinx.coroutines.flow.*

data class WeightEntry(val date: Long, val weight: Float)

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as WorkoutLogApp).repository

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
}
