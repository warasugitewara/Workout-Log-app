package com.workoutlogpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.workoutlogpro.data.RoutineTemplate
import com.workoutlogpro.viewmodel.ScheduleViewModel
import java.util.Calendar

private val DAY_NAMES = listOf("月", "火", "水", "木", "金", "土", "日")

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel, onBack: () -> Unit) {
    val allSchedules by viewModel.allSchedules.collectAsState()
    val allMenus by viewModel.allMenus.collectAsState()
    val allReminders by viewModel.allReminders.collectAsState()

    var selectedDay by remember {
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        mutableIntStateOf(if (dow == Calendar.SUNDAY) 7 else dow - 1)
    }
    var showRoutineDialog by remember { mutableStateOf(false) }
    var showAddMenuDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }

    val daySchedules = allSchedules.filter { it.dayOfWeek == selectedDay }
    val dayReminder = allReminders.find { it.dayOfWeek == selectedDay }

    if (showRoutineDialog) {
        RoutineSelectDialog(
            routines = viewModel.routineTemplates,
            onSelect = { routine ->
                viewModel.applyRoutineToDay(selectedDay, routine.menuNames)
                showRoutineDialog = false
            },
            onDismiss = { showRoutineDialog = false }
        )
    }

    if (showAddMenuDialog) {
        MenuSelectDialog(
            menus = allMenus,
            onSelect = { menuId ->
                viewModel.addMenuToDay(selectedDay, menuId)
                showAddMenuDialog = false
            },
            onDismiss = { showAddMenuDialog = false }
        )
    }

    if (showReminderDialog) {
        ReminderTimeDialog(
            currentHour = dayReminder?.hour ?: 9,
            currentMinute = dayReminder?.minute ?: 0,
            isEnabled = dayReminder?.isEnabled ?: false,
            onSave = { hour, minute, enabled ->
                viewModel.saveReminder(selectedDay, hour, minute, enabled)
                showReminderDialog = false
            },
            onDismiss = { showReminderDialog = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "戻る")
            }
            Text(
                "📅 週間スケジュール",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        // Day selector
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(7) { index ->
                val day = index + 1
                val isSelected = day == selectedDay
                val dayCount = allSchedules.count { it.dayOfWeek == day }
                FilterChip(
                    onClick = { selectedDay = day },
                    label = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(DAY_NAMES[index], fontWeight = FontWeight.Bold)
                            if (dayCount > 0) {
                                Text("${dayCount}種目", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    },
                    selected = isSelected,
                    modifier = Modifier.width(56.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Reminder card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (dayReminder?.isEnabled == true)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showReminderDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Notifications, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "${DAY_NAMES[selectedDay - 1]}曜日のリマインド",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (dayReminder?.isEnabled == true) {
                                    Text(
                                        "%02d:%02d に通知".format(dayReminder.hour, dayReminder.minute),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                } else {
                                    Text("タップして設定", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        if (dayReminder?.isEnabled == true) {
                            Icon(Icons.Filled.Check, contentDescription = "有効", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // Action buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showRoutineDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.PlaylistAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ルーティン", style = MaterialTheme.typography.labelLarge)
                    }
                    OutlinedButton(
                        onClick = { showAddMenuDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("個別追加", style = MaterialTheme.typography.labelLarge)
                    }
                    if (daySchedules.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { viewModel.clearDay(selectedDay) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Filled.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // Day's menu list
            item {
                Text(
                    "${DAY_NAMES[selectedDay - 1]}曜日のメニュー",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (daySchedules.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            "メニューが未設定です。\nルーティンテンプレートまたは個別追加で設定しましょう。",
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(daySchedules) { schedule ->
                    val menu = allMenus.find { it.id == schedule.menuId }
                    if (menu != null) {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(menu.name, fontWeight = FontWeight.Bold)
                                    Text(
                                        "${menu.category} | ${menu.defaultReps}回×${menu.defaultSets}セット | ${menu.calorieEstimate}kcal",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { viewModel.removeSchedule(schedule) }) {
                                    Icon(Icons.Filled.Close, contentDescription = "削除", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ── Dialogs ──

@Composable
fun RoutineSelectDialog(
    routines: List<RoutineTemplate>,
    onSelect: (RoutineTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ルーティンテンプレート選択") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(routines) { routine ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(routine) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(routine.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            Text(routine.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                routine.menuNames.joinToString(" → "),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("閉じる") }
        }
    )
}

@Composable
fun MenuSelectDialog(
    menus: List<com.workoutlogpro.data.entity.WorkoutMenu>,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val categories = menus.map { it.category }.distinct()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("メニューを追加") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                categories.forEach { category ->
                    item {
                        Text(
                            category,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    val catMenus = menus.filter { it.category == category }
                    items(catMenus) { menu ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(menu.id) }
                        ) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                Text(menu.name, modifier = Modifier.weight(1f))
                                Text(
                                    "${menu.defaultReps}回×${menu.defaultSets}セット",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("閉じる") }
        }
    )
}

@Composable
fun ReminderTimeDialog(
    currentHour: Int,
    currentMinute: Int,
    isEnabled: Boolean,
    onSave: (Int, Int, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableIntStateOf(currentHour) }
    var minute by remember { mutableIntStateOf(currentMinute) }
    var enabled by remember { mutableStateOf(isEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🔔 リマインド設定") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("通知を有効にする")
                    Switch(checked = enabled, onCheckedChange = { enabled = it })
                }

                if (enabled) {
                    Text("通知時刻", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = "%02d".format(hour),
                            onValueChange = { hour = it.toIntOrNull()?.coerceIn(0, 23) ?: hour },
                            label = { Text("時") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Text(":", style = MaterialTheme.typography.headlineMedium)
                        OutlinedTextField(
                            value = "%02d".format(minute),
                            onValueChange = { minute = it.toIntOrNull()?.coerceIn(0, 59) ?: minute },
                            label = { Text("分") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(hour, minute, enabled) }) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}
