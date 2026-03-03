package com.workoutlogpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.workoutlogpro.data.entity.WorkoutLog
import com.workoutlogpro.viewmodel.LogViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(viewModel: LogViewModel) {
    val currentLog by viewModel.currentLog.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()
    val menus by viewModel.menus.collectAsState()

    var selectedMenuIndex by remember { mutableIntStateOf(-1) }
    var expanded by remember { mutableStateOf(false) }
    var reps by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var fatRate by remember { mutableStateOf("") }
    var fatigue by remember { mutableIntStateOf(3) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "📝 トレーニング記録",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Input form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("新規記録", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    // Menu selector
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = if (selectedMenuIndex >= 0 && selectedMenuIndex < menus.size) menus[selectedMenuIndex].name else "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("メニュー選択") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            menus.forEachIndexed { index, menu ->
                                DropdownMenuItem(
                                    text = { Text("${menu.name} (${menu.category})") },
                                    onClick = {
                                        selectedMenuIndex = index
                                        reps = menu.defaultReps.toString()
                                        sets = menu.defaultSets.toString()
                                        duration = menu.avgTimeSec.toString()
                                        calories = menu.calorieEstimate.toString()
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = reps,
                            onValueChange = { reps = it },
                            label = { Text("回数") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = sets,
                            onValueChange = { sets = it },
                            label = { Text("セット") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("実施時間(秒)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = calories,
                            onValueChange = { calories = it },
                            label = { Text("消費kcal") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text("体重(kg)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = fatRate,
                            onValueChange = { fatRate = it },
                            label = { Text("体脂肪率(%)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Fatigue slider
                    Text("主観疲労度: $fatigue / 5", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = fatigue.toFloat(),
                        onValueChange = { fatigue = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3
                    )

                    Button(
                        onClick = {
                            if (selectedMenuIndex >= 0 && selectedMenuIndex < menus.size) {
                                viewModel.updateCurrentLog(
                                    WorkoutLog(
                                        date = System.currentTimeMillis(),
                                        menuId = menus[selectedMenuIndex].id,
                                        actualReps = reps.toIntOrNull() ?: 0,
                                        actualSets = sets.toIntOrNull() ?: 0,
                                        durationSec = duration.toIntOrNull() ?: 0,
                                        actualCalories = calories.toFloatOrNull() ?: 0f,
                                        weight = weight.toFloatOrNull() ?: 0f,
                                        fatRate = fatRate.toFloatOrNull(),
                                        fatigueLevel = fatigue
                                    )
                                )
                                viewModel.saveLog()
                                // Reset form
                                selectedMenuIndex = -1
                                reps = ""; sets = ""; duration = ""; calories = ""
                                weight = ""; fatRate = ""; fatigue = 3
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedMenuIndex >= 0
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("記録を保存")
                    }
                }
            }
        }

        // Recent logs
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📜 最近の記録",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(recentLogs.take(20)) { log ->
            LogItem(
                log = log,
                menuName = menus.find { it.id == log.menuId }?.name ?: "不明",
                onDelete = { viewModel.deleteLog(log) }
            )
        }
    }
}

@Composable
fun LogItem(log: WorkoutLog, menuName: String, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("MM/dd HH:mm", Locale.JAPAN) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$menuName - ${dateFormat.format(Date(log.date))}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${log.actualReps}回×${log.actualSets}セット | ${log.durationSec}秒 | ${log.actualCalories}kcal",
                    style = MaterialTheme.typography.bodySmall
                )
                if (log.weight > 0f) {
                    Text(
                        text = "体重: ${log.weight}kg" + (log.fatRate?.let { " | 体脂肪: ${it}%" } ?: ""),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = "疲労度: ${"★".repeat(log.fatigueLevel)}${"☆".repeat(5 - log.fatigueLevel)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "削除", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
