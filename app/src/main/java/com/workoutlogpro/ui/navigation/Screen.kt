package com.workoutlogpro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "ホーム", Icons.Filled.Home)
    data object Menu : Screen("menu", "メニュー", Icons.Filled.FitnessCenter)
    data object Log : Screen("log", "記録", Icons.Filled.EditNote)
    data object Stats : Screen("stats", "統計", Icons.Filled.BarChart)
    data object Settings : Screen("settings", "設定", Icons.Filled.Settings)

    companion object {
        val bottomNavItems = listOf(Dashboard, Menu, Log, Stats, Settings)
    }
}
