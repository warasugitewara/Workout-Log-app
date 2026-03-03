package com.workoutlogpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.workoutlogpro.data.entity.WorkoutMenu
import com.workoutlogpro.ui.theme.*
import com.workoutlogpro.viewmodel.MenuViewModel

@Composable
fun MenuScreen(viewModel: MenuViewModel) {
    val menus by viewModel.menus.collectAsState()
    val editingMenu by viewModel.editingMenu.collectAsState()
    var showTemplateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
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
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "My Library",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "メニュー管理",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1).sp
                            )
                        )
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalIconButton(
                            onClick = { showTemplateDialog = true },
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = "テンプレート")
                        }
                        FloatingActionButton(
                            onClick = { viewModel.startEdit(null) },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "追加")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (menus.isEmpty()) {
                item {
                    EmptyMenuState(onTemplateClick = { showTemplateDialog = true })
                }
            }

            items(menus) { menu ->
                ModernMenuCard(
                    menu = menu,
                    onEdit = { viewModel.startEdit(menu) },
                    onDelete = { viewModel.deleteMenu(menu) }
                )
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun EmptyMenuState(onTemplateClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Filled.FitnessCenter, 
                contentDescription = null, 
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "ライブラリが空です",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "テンプレートから人気のメニューを\n追加してみませんか？",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onTemplateClick,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("テンプレートを見る")
            }
        }
    }
}

@Composable
fun ModernMenuCard(menu: WorkoutMenu, onEdit: () -> Unit, onDelete: () -> Unit) {
    val categoryColor = when (menu.category) {
        "上半身" -> CategoryUpper
        "下半身" -> CategoryLower
        "有酸素" -> CategoryCardio
        "体幹" -> CategoryCore
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = categoryColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = menu.category,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = menu.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "編集", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "削除", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuStatBadge(value = "${menu.defaultReps}", label = "Reps")
                MenuStatBadge(value = "${menu.defaultSets}", label = "Sets")
                MenuStatBadge(value = "${menu.calorieEstimate.toInt()}", label = "kcal")
            }
            
            if (menu.memo.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = menu.memo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun MenuStatBadge(value: String, label: String) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(24.dp),
        content = {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (menu.id == 0) "新規メニュー" else "メニューを編集",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("メニュー名") },
                        placeholder = { Text("例: ベンチプレス") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("カテゴリー") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
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

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = reps,
                            onValueChange = { reps = it },
                            label = { Text("回数") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = sets,
                            onValueChange = { sets = it },
                            label = { Text("セット") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it },
                        label = { Text("消費カロリー (目安)") },
                        trailingIcon = { Text("kcal", modifier = Modifier.padding(end = 12.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = memo,
                        onValueChange = { memo = it },
                        label = { Text("メモ") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) { Text("キャンセル") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
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
                            enabled = name.isNotBlank(),
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

@Composable
fun TemplateDialog(viewModel: MenuViewModel, onDismiss: () -> Unit) {
    val categories = viewModel.templateCategories
    val selectedItems = remember { mutableStateMapOf<String, Boolean>() }
    var selectAll by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        com.workoutlogpro.data.MenuTemplates.all.forEach {
            selectedItems[it.name] = true
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📋 テンプレート", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
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
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = selectAll, onCheckedChange = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("すべて選択", fontWeight = FontWeight.Bold)
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }

                categories.forEach { category ->
                    item {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }

                    val templates = viewModel.getTemplatesByCategory(category)
                    items(templates) { template ->
                        val isSelected = selectedItems[template.name] ?: true
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .toggleable(
                                    value = isSelected,
                                    onValueChange = { checked ->
                                        selectedItems[template.name] = checked
                                        selectAll = selectedItems.values.all { it }
                                    }
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
                                else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = isSelected, onCheckedChange = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(template.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                    Text(
                                        "${template.defaultReps}回 × ${template.defaultSets}セット",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selected = com.workoutlogpro.data.MenuTemplates.all
                        .filter { selectedItems[it.name] == true }
                    viewModel.loadTemplates(selected)
                    onDismiss()
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("ライブラリに追加")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        }
    )
}
