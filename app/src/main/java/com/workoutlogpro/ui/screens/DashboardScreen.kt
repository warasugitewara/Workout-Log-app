package com.workoutlogpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp, top = 16.dp)
    ) {
        // Modern Header
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Hello, Warrior!",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "ダッシュボード",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Highlights
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernStatCard(
                    title = "体重",
                    value = user?.weight?.let { "%.1f".format(it) } ?: "--",
                    unit = "kg",
                    icon = Icons.Filled.MonitorWeight,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f)
                )
                ModernStatCard(
                    title = "BMI",
                    value = user?.calcBmi()?.let { "%.1f".format(it) } ?: "--",
                    unit = "",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Calories & Protein Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Color(0xFFF43F5E))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("今月の消費: %.0f kcal".format(monthlyCalories), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ElectricBolt, contentDescription = null, tint = Color(0xFFF59E0B))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("本日のプロテイン: %.1f g".format(todayProtein), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${weeklyRate.toInt()}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Today's Focus Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "今日の予定",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
                TextButton(onClick = onNavigateToSchedule) {
                    Text("すべて見る")
                }
            }
        }

        // Progress Visualizer
        if (totalCount > 0) {
            item {
                val progress = completedCount.toFloat() / totalCount.coerceAtLeast(1)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (completedCount == totalCount) "Great Job! 🎉" else "あと ${totalCount - completedCount} 種目",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("$completedCount / $totalCount", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    )
                }
            }
        }

        if (todaySchedules.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("今日は休みの日ですか？", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onNavigateToSchedule, shape = RoundedCornerShape(12.dp)) {
                            Text("スケジュールを追加")
                        }
                    }
                }
            }
        } else {
            items(todaySchedules) { schedule ->
                val menu = allMenus.find { it.id == schedule.menuId }
                ModernScheduleItem(
                    menuName = menu?.name ?: "不明",
                    category = menu?.category ?: "",
                    detail = menu?.let { "${it.defaultReps}回 × ${it.defaultSets}セット" } ?: "",
                    isCompleted = schedule.isCompleted,
                    onToggle = { viewModel.toggleScheduleComplete(schedule.id, !schedule.isCompleted) }
                )
            }
        }
    }
}

@Composable
fun ModernStatCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black)
                )
                if (unit.isNotBlank()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = unit, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernScheduleItem(
    menuName: String,
    category: String,
    detail: String,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    val containerColor = if (isCompleted) 
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) 
    else 
        MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        if (isCompleted) MaterialTheme.colorScheme.primary else Color.Transparent,
                        CircleShape
                    )
                    .border(
                        2.dp,
                        if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = menuName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$category • $detail",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            
            if (!isCompleted) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "READY",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
