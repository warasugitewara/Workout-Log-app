package com.workoutlogpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.workoutlogpro.WorkoutLogApp
import com.workoutlogpro.data.MenuTemplates
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

    /** テンプレートのカテゴリ一覧 */
    val templateCategories: List<String> =
        MenuTemplates.all.map { it.category }.distinct()

    fun getTemplatesByCategory(category: String): List<WorkoutMenu> =
        MenuTemplates.all.filter { it.category == category }

    /** 選択したテンプレートを一括登録 */
    fun loadTemplates(templates: List<WorkoutMenu>) {
        viewModelScope.launch {
            templates.forEach { repo.saveMenu(it) }
        }
    }

    /** 全テンプレートを一括登録 */
    fun loadAllTemplates() {
        viewModelScope.launch {
            MenuTemplates.all.forEach { repo.saveMenu(it) }
        }
    }

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
