package com.workoutlogpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.workoutlogpro.data.entity.WorkoutMenu
import com.workoutlogpro.ui.theme.*
import com.workoutlogpro.viewmodel.MenuViewModel

@Composable
fun MenuScreen(viewModel: MenuViewModel) {
    val menus by viewModel.menus.collectAsState()
    val editingMenu by viewModel.editingMenu.collectAsState()
    var showTemplateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (editingMenu != null) {
            MenuEditDialog(
                menu = editingMenu!!,
                onSave = { viewModel.saveMenu(it) },
                onDismiss = { viewModel.clearEdit() }
            )
        }

        if (showTemplateDialog) {
            TemplateDialog(
                viewModel = viewModel,
                onDismiss = { showTemplateDialog = false }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🏋️ メニュー管理",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    FloatingActionButton(
                        onClick = { showTemplateDialog = true },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Text("📋", style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = { viewModel.startEdit(null) },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "追加")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (menus.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "メニューがまだ登録されていません。",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Button(
                                onClick = { showTemplateDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("📋 テンプレートから追加")
                            }
                            OutlinedButton(
                                onClick = { viewModel.startEdit(null) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("＋ 手動で追加")
                            }
                        }
                    }
                }
            }

            items(menus) { menu ->
                MenuCard(
                    menu = menu,
                    onEdit = { viewModel.startEdit(menu) },
                    onDelete = { viewModel.deleteMenu(menu) }
                )
            }
        }
    }
}

@Composable
fun MenuCard(menu: WorkoutMenu, onEdit: () -> Unit, onDelete: () -> Unit) {
    val categoryColor = when (menu.category) {
        "上半身" -> CategoryUpper
        "下半身" -> CategoryLower
        "有酸素" -> CategoryCardio
        "体幹" -> CategoryCore
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = menu.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = categoryColor.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = menu.category,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColor
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "編集")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "削除", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("${menu.defaultReps}回", style = MaterialTheme.typography.bodyMedium)
                Text("${menu.defaultSets}セット", style = MaterialTheme.typography.bodyMedium)
                Text("${menu.avgTimeSec}秒/セット", style = MaterialTheme.typography.bodyMedium)
                Text("${menu.calorieEstimate} kcal", style = MaterialTheme.typography.bodyMedium)
            }
            if (menu.memo.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = menu.memo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuEditDialog(menu: WorkoutMenu, onSave: (WorkoutMenu) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(menu.name) }
    var category by remember { mutableStateOf(menu.category.ifEmpty { "上半身" }) }
    var reps by remember { mutableStateOf(menu.defaultReps.toString()) }
    var sets by remember { mutableStateOf(menu.defaultSets.toString()) }
    var avgTime by remember { mutableStateOf(menu.avgTimeSec.toString()) }
    var calories by remember { mutableStateOf(menu.calorieEstimate.toString()) }
    var memo by remember { mutableStateOf(menu.memo) }
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf("上半身", "下半身", "有酸素", "体幹")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (menu.id == 0) "メニュー追加" else "メニュー編集") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("メニュー名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("種類") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
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
                        value = avgTime,
                        onValueChange = { avgTime = it },
                        label = { Text("秒/セット") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it },
                        label = { Text("kcal") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("メモ") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        menu.copy(
                            name = name,
                            category = category,
                            defaultReps = reps.toIntOrNull() ?: 0,
                            defaultSets = sets.toIntOrNull() ?: 0,
                            avgTimeSec = avgTime.toIntOrNull() ?: 0,
                            calorieEstimate = calories.toFloatOrNull() ?: 0f,
                            memo = memo
                        )
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}

@Composable
fun TemplateDialog(viewModel: MenuViewModel, onDismiss: () -> Unit) {
    val categories = viewModel.templateCategories
    val selectedItems = remember { mutableStateMapOf<String, Boolean>() }
    var selectAll by remember { mutableStateOf(true) }

    // Initialize all as selected
    LaunchedEffect(Unit) {
        com.workoutlogpro.data.MenuTemplates.all.forEach {
            selectedItems[it.name] = true
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📋 テンプレートから追加") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                // Select all toggle
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = selectAll,
                                onValueChange = { checked ->
                                    selectAll = checked
                                    selectedItems.keys.forEach { selectedItems[it] = checked }
                                }
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = selectAll, onCheckedChange = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("すべて選択", fontWeight = FontWeight.Bold)
                    }
                    Divider()
                }

                categories.forEach { category ->
                    item {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (category) {
                                "上半身" -> CategoryUpper
                                "下半身" -> CategoryLower
                                "有酸素" -> CategoryCardio
                                "体幹" -> CategoryCore
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    val templates = viewModel.getTemplatesByCategory(category)
                    items(templates) { template ->
                        val isSelected = selectedItems[template.name] ?: true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .toggleable(
                                    value = isSelected,
                                    onValueChange = { checked ->
                                        selectedItems[template.name] = checked
                                        selectAll = selectedItems.values.all { it }
                                    }
                                )
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = isSelected, onCheckedChange = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(template.name, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "${template.defaultReps}回×${template.defaultSets}セット | ${template.calorieEstimate}kcal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val selected = com.workoutlogpro.data.MenuTemplates.all
                        .filter { selectedItems[it.name] == true }
                    viewModel.loadTemplates(selected)
                    onDismiss()
                }
            ) {
                Text("追加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}
