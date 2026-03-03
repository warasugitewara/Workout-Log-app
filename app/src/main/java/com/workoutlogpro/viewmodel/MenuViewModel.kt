package com.workoutlogpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.workoutlogpro.WorkoutLogApp
import com.workoutlogpro.data.entity.WorkoutMenu
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MenuViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as WorkoutLogApp).repository

    val menus: StateFlow<List<WorkoutMenu>> =
        repo.getAllMenus()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editingMenu = MutableStateFlow<WorkoutMenu?>(null)
    val editingMenu: StateFlow<WorkoutMenu?> = _editingMenu

    fun startEdit(menu: WorkoutMenu?) {
        _editingMenu.value = menu ?: WorkoutMenu()
    }

    fun clearEdit() {
        _editingMenu.value = null
    }

    fun saveMenu(menu: WorkoutMenu) {
        viewModelScope.launch {
            repo.saveMenu(menu)
            _editingMenu.value = null
        }
    }

    fun deleteMenu(menu: WorkoutMenu) {
        viewModelScope.launch {
            repo.deleteMenu(menu)
        }
    }
}
