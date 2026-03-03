package com.workoutlogpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.workoutlogpro.WorkoutLogApp
import com.workoutlogpro.data.entity.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as WorkoutLogApp).repository

    val user: StateFlow<User?> = repo.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveUser(user: User) {
        viewModelScope.launch {
            repo.saveUser(user)
        }
    }
}
