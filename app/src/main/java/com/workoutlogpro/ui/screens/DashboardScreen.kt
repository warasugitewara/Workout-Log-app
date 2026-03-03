package com.workoutlogpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.workoutlogpro.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val user by viewModel.user.collectAsState()
    val todaySchedules by viewModel.todaySchedules.collectAsState()
    val allMenus by viewModel.allMenus.collectAsState()
    val weeklyRate by viewModel.weeklyCompletionRate.collectAsState()
    val monthlyCalories by viewModel.monthlyCalories.collectAsState()
    val todayProtein by viewModel.todayProtein.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadStats() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Welcome
        item {
            Text(
                text = "💪 ダッシュボード",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Stats Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "体重",
                    value = user?.weight?.let { "%.1f kg".format(it) } ?: "-- kg",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "BMI",
                    value = user?.calcBmi()?.let { "%.1f".format(it) } ?: "--",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "今週実施率",
                    value = "%.0f%%".format(weeklyRate),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "今月カロリー",
                    value = "%.0f kcal".format(monthlyCalories),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            StatCard(
                title = "本日プロテイン",
                value = "%.1f g".format(todayProtein),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Today's Schedule
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📋 今日の予定",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (todaySchedules.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "今日の予定はありません",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            items(todaySchedules) { schedule ->
                val menuName = allMenus.find { it.id == schedule.menuId }?.name ?: "不明"
                ScheduleItem(
                    menuName = menuName,
                    isCompleted = schedule.isCompleted,
                    onToggle = { viewModel.toggleScheduleComplete(schedule.id, !schedule.isCompleted) }
                )
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ScheduleItem(menuName: String, isCompleted: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = menuName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggle() }
            )
        }
    }
}
