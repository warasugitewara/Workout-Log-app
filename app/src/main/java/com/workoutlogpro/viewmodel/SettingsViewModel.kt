package com.workoutlogpro.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.workoutlogpro.WorkoutLogApp
import com.workoutlogpro.data.entity.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as WorkoutLogApp).repository

    val user: StateFlow<User?> = repo.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _exportStatus = MutableStateFlow("")
    val exportStatus: StateFlow<String> = _exportStatus

    fun saveUser(user: User) {
        viewModelScope.launch {
            repo.saveUser(user)
        }
    }

    fun exportCsv() {
        viewModelScope.launch {
            try {
                val app = getApplication<Application>()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN)
                val fileDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.JAPAN).format(Date())

                val exportDir = File(app.getExternalFilesDir(null), "exports")
                exportDir.mkdirs()

                // Export workout logs
                val logs = repo.getAllLogs().first()
                val menus = repo.getAllMenus().first()
                val menuMap = menus.associateBy { it.id }

                val logFile = File(exportDir, "workout_logs_$fileDate.csv")
                logFile.writeText(buildString {
                    appendLine("日付,メニュー名,カテゴリ,回数,セット数,時間(秒),消費kcal,体重(kg),体脂肪率(%),疲労度")
                    logs.forEach { log ->
                        val menu = menuMap[log.menuId]
                        appendLine(
                            "${dateFormat.format(Date(log.date))},${menu?.name ?: "不明"},${menu?.category ?: ""}," +
                            "${log.actualReps},${log.actualSets},${log.durationSec},${log.actualCalories}," +
                            "${log.weight},${log.fatRate ?: ""},${log.fatigueLevel}"
                        )
                    }
                })

                // Export protein logs
                val proteinLogs = repo.getAllProteinLogs().first()
                val proteinFile = File(exportDir, "protein_logs_$fileDate.csv")
                proteinFile.writeText(buildString {
                    appendLine("日付,摂取量(g),割り方")
                    proteinLogs.forEach { log ->
                        appendLine("${dateFormat.format(Date(log.date))},${log.amount},${log.type}")
                    }
                })

                _exportStatus.value = "✅ エクスポート完了\n${logFile.absolutePath}\n${proteinFile.absolutePath}"
            } catch (e: Exception) {
                _exportStatus.value = "❌ エクスポート失敗: ${e.message}"
            }
        }
    }
}
