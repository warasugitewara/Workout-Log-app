package com.workoutlogpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.workoutlogpro.data.RoutineTemplate
import com.workoutlogpro.viewmodel.ScheduleViewModel
import java.util.Calendar

private val DAY_NAMES = listOf("月", "火", "水", "木", "金", "土", "日")

@OptIn(ExperimentalMaterial3Api::class)
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

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (showRoutineDialog) {
            RoutineSelectDialog(
                routines = viewModel.routineTemplates,
                allMenus = allMenus,
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
            ModernReminderDialog(
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
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "戻る")
                }
                Text(
                    "週間スケジュール",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
            }

            // Day selector (GitHub style tabs)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(7) { index ->
                    val day = index + 1
                    val isSelected = day == selectedDay
                    val dayCount = allSchedules.count { it.dayOfWeek == day }
                    
                    Surface(
                        onClick = { selectedDay = day },
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(60.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(DAY_NAMES[index], fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            if (dayCount > 0) {
                                Text("${dayCount}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Reminder Quick Access
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (dayReminder?.isEnabled == true)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        onClick = { showReminderDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (dayReminder?.isEnabled == true) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsNone,
                                    contentDescription = null,
                                    tint = if (dayReminder?.isEnabled == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${DAY_NAMES[selectedDay - 1]}曜日の通知",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    if (dayReminder?.isEnabled == true) "時刻: %02d:%02d".format(dayReminder.hour, dayReminder.minute) else "オフ設定中",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // Action Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showRoutineDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ルーティン")
                        }
                        Button(
                            onClick = { showAddMenuDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("個別追加")
                        }
                    }
                }

                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "トレーニングメニュー",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (daySchedules.isNotEmpty()) {
                            TextButton(onClick = { viewModel.clearDay(selectedDay) }) {
                                Text("すべて解除", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                if (daySchedules.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("予定がありません", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(daySchedules) { schedule ->
                        val menu = allMenus.find { it.id == schedule.menuId }
                        if (menu != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        val title = if (schedule.setNumber > 0) "${menu.name} (セット${schedule.setNumber})" else menu.name
                                        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                        Text(
                                            "${menu.category} • ${menu.defaultReps}回 • ${if (schedule.setNumber > 0) "%.1f".format(menu.calorieEstimate / menu.defaultSets.coerceAtLeast(1)) else menu.calorieEstimate} kcal",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = { viewModel.removeSchedule(schedule) }) {
                                        Icon(Icons.Filled.Close, contentDescription = "削除", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernReminderDialog(
    currentHour: Int,
    currentMinute: Int,
    isEnabled: Boolean,
    onSave: (Int, Int, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var enabled by remember { mutableStateOf(isEnabled) }
    val timePickerState = rememberTimePickerState(
        initialHour = currentHour,
        initialMinute = currentMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(16.dp),
        content = {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        "🔔 リマインド設定",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("通知を有効にする", style = MaterialTheme.typography.bodyLarge)
                        Switch(checked = enabled, onCheckedChange = { enabled = it })
                    }

                    if (enabled) {
                        TimePicker(
                            state = timePickerState,
                            colors = TimePickerDefaults.colors(
                                clockDialColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                selectorColor = MaterialTheme.colorScheme.primary,
                                periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("キャンセル") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onSave(timePickerState.hour, timePickerState.minute, enabled) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("保存する")
                        }
                    }
                }
            }
        }
    )
}

// Dialog Selectors (保持しつつGitHubスタイルに微調整)
@Composable
fun RoutineSelectDialog(
    routines: List<RoutineTemplate>,
    allMenus: List<com.workoutlogpro.data.entity.WorkoutMenu>,
    onSelect: (RoutineTemplate) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("テンプレート選択", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(routines) { routine ->
                    val estCalories = routine.menuNames.sumOf { name ->
                        allMenus.find { it.name == name }?.calorieEstimate?.toDouble() ?: 0.0
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onSelect(routine) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(routine.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        "🔥 %.0f kcal".format(estCalories),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(routine.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                routine.menuNames.joinToString(" → "),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1
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
        title = { Text("メニューを追加", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                categories.forEach { category ->
                    item {
                        Text(
                            category,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    val catMenus = menus.filter { it.category == category }
                    items(catMenus) { menu ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onSelect(menu.id) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(menu.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
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
