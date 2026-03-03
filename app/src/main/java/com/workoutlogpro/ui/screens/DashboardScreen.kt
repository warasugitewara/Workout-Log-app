package com.workoutlogpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.workoutlogpro.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel, onNavigateToSchedule: () -> Unit = {}) {
    val user by viewModel.user.collectAsState()
    val todaySchedules by viewModel.todaySchedules.collectAsState()
    val allMenus by viewModel.allMenus.collectAsState()
    val weeklyRate by viewModel.weeklyCompletionRate.collectAsState()
    val monthlyCalories by viewModel.monthlyCalories.collectAsState()
    val todayProtein by viewModel.todayProtein.collectAsState()

    val completedCount = todaySchedules.count { it.isCompleted }
    val totalCount = todaySchedules.size

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

        // Today's Schedule - prominent section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 今日のメニュー",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                FilledTonalButton(onClick = onNavigateToSchedule) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("スケジュール管理")
                }
            }
        }

        // Progress for today
        if (totalCount > 0) {
            item {
                val todayEstCalories = todaySchedules.sumOf { schedule ->
                    allMenus.find { it.id == schedule.menuId }?.calorieEstimate?.toDouble() ?: 0.0
                }
                val todayDoneCalories = todaySchedules
                    .filter { it.isCompleted }
                    .sumOf { schedule ->
                        allMenus.find { it.id == schedule.menuId }?.calorieEstimate?.toDouble() ?: 0.0
                    }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (completedCount == totalCount)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (completedCount == totalCount) "🎉 今日のメニュー完了！" else "今日の進捗",
                                fontWeight = FontWeight.Bold
                            )
                            Text("$completedCount / $totalCount 完了", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🔥 %.0f / %.0f kcal 消費".format(todayDoneCalories, todayEstCalories),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = completedCount.toFloat() / totalCount.coerceAtLeast(1),
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            }
        }

        if (todaySchedules.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "今日の予定はまだ設定されていません",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = onNavigateToSchedule) {
                            Text("スケジュールを設定する")
                        }
                    }
                }
            }
        } else {
            items(todaySchedules) { schedule ->
                val menu = allMenus.find { it.id == schedule.menuId }
                val menuName = menu?.name ?: "不明"
                val setLabel = if (schedule.setNumber > 0) {
                    " (セット${schedule.setNumber})"
                } else ""
                val detail = menu?.let {
                    if (schedule.setNumber > 0) {
                        "${it.defaultReps}回 | ${"%.1f".format(it.calorieEstimate / it.defaultSets.coerceAtLeast(1))}kcal"
                    } else {
                        "${it.defaultReps}回×${it.defaultSets}セット | ${it.calorieEstimate}kcal"
                    }
                } ?: ""
                ScheduleItem(
                    menuName = menuName + setLabel,
                    detail = detail,
                    category = menu?.category ?: "",
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
fun ScheduleItem(
    menuName: String,
    detail: String,
    category: String,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = menuName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                if (detail.isNotBlank()) {
                    Text(
                        text = "$category | $detail",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (isCompleted) {
                Text("✅", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
