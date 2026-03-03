package com.workoutlogpro.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.workoutlogpro.ui.screens.*
import com.workoutlogpro.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isScheduleScreen = currentDestination?.route == Screen.Schedule.route

    Scaffold(
        topBar = {
            if (!isScheduleScreen) {
                TopAppBar(
                    title = {
                        val title = Screen.bottomNavItems.find {
                            currentDestination?.hierarchy?.any { dest -> dest.route == it.route } == true
                        }?.title ?: "Workout Log Pro"
                        Text(title)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        },
        bottomBar = {
            if (!isScheduleScreen) {
                NavigationBar {
                    Screen.bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel(),
                    onNavigateToSchedule = {
                        navController.navigate(Screen.Schedule.route)
                    }
                )
            }
            composable(Screen.Menu.route) {
                MenuScreen(viewModel = viewModel())
            }
            composable(Screen.Log.route) {
                LogScreen(viewModel = viewModel())
            }
            composable(Screen.Stats.route) {
                StatsScreen(viewModel = viewModel())
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel())
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen(
                    viewModel = viewModel(),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
