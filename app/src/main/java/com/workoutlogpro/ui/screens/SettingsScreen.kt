package com.workoutlogpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.workoutlogpro.data.entity.User
import com.workoutlogpro.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val user by viewModel.user.collectAsState()
    val exportStatus by viewModel.exportStatus.collectAsState()

    var height by remember(user) { mutableStateOf(user?.height?.toString() ?: "") }
    var weight by remember(user) { mutableStateOf(user?.weight?.toString() ?: "") }
    var targetWeight by remember(user) { mutableStateOf(user?.targetWeight?.toString() ?: "") }
    var gender by remember(user) { mutableStateOf(user?.gender ?: "male") }
    var setTrackingMode by remember(user) { mutableStateOf(user?.setTrackingMode ?: "together") }
    var birthYear by remember(user) {
        mutableStateOf(
            if (user != null && user!!.birthDate > 0) {
                val cal = java.util.Calendar.getInstance()
                cal.timeInMillis = user!!.birthDate
                cal.get(java.util.Calendar.YEAR).toString()
            } else ""
        )
    }
    var birthMonth by remember(user) {
        mutableStateOf(
            if (user != null && user!!.birthDate > 0) {
                val cal = java.util.Calendar.getInstance()
                cal.timeInMillis = user!!.birthDate
                (cal.get(java.util.Calendar.MONTH) + 1).toString()
            } else ""
        )
    }
    var birthDay by remember(user) {
        mutableStateOf(
            if (user != null && user!!.birthDate > 0) {
                val cal = java.util.Calendar.getInstance()
                cal.timeInMillis = user!!.birthDate
                cal.get(java.util.Calendar.DAY_OF_MONTH).toString()
            } else ""
        )
    }

    var genderExpanded by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "⚙️ 設定",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("ユーザー情報", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("身長 (cm)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("現在の体重 (kg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetWeight,
                    onValueChange = { targetWeight = it },
                    label = { Text("目標体重 (kg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Gender selector
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    OutlinedTextField(
                        value = if (gender == "male") "男性" else "女性",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("性別") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(genderExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("男性") },
                            onClick = { gender = "male"; genderExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("女性") },
                            onClick = { gender = "female"; genderExpanded = false }
                        )
                    }
                }

                // Birth date
                Text("生年月日", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = birthYear,
                        onValueChange = { birthYear = it },
                        label = { Text("年") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = birthMonth,
                        onValueChange = { birthMonth = it },
                        label = { Text("月") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = birthDay,
                        onValueChange = { birthDay = it },
                        label = { Text("日") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // セットトラッキングモード
                Text("セット管理方式", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = setTrackingMode == "together",
                        onClick = { setTrackingMode = "together" },
                        label = { Text("まとめて1チェック") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = setTrackingMode == "per_set",
                        onClick = { setTrackingMode = "per_set" },
                        label = { Text("1セット毎") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val birthDate = try {
                            val cal = java.util.Calendar.getInstance()
                            cal.set(
                                birthYear.toInt(),
                                (birthMonth.toInt()) - 1,
                                birthDay.toInt()
                            )
                            cal.timeInMillis
                        } catch (e: Exception) { 0L }

                        viewModel.saveUser(
                            User(
                                id = user?.id ?: 1,
                                height = height.toFloatOrNull() ?: 0f,
                                weight = weight.toFloatOrNull() ?: 0f,
                                targetWeight = targetWeight.toFloatOrNull() ?: 0f,
                                gender = gender,
                                birthDate = birthDate,
                                setTrackingMode = setTrackingMode
                            )
                        )
                        saved = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("保存")
                }

                if (saved) {
                    Text(
                        text = "✅ 保存しました",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Calculated info
        val h = height.toFloatOrNull() ?: 0f
        val w = weight.toFloatOrNull() ?: 0f
        if (h > 0f && w > 0f) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("自動計算", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    val bmi = w / ((h / 100f) * (h / 100f))
                    SummaryRow("BMI", "%.1f".format(bmi))

                    val age = try {
                        val cal = java.util.Calendar.getInstance()
                        cal.set(birthYear.toInt(), (birthMonth.toInt()) - 1, birthDay.toInt())
                        ((System.currentTimeMillis() - cal.timeInMillis) / (365.25 * 24 * 60 * 60 * 1000)).toInt()
                    } catch (e: Exception) { 0 }

                    if (age > 0) {
                        val bmr = if (gender == "male") {
                            10f * w + 6.25f * h - 5f * age + 5f
                        } else {
                            10f * w + 6.25f * h - 5f * age - 161f
                        }
                        SummaryRow("推定基礎代謝量", "%.0f kcal/日".format(bmr))
                    }
                }
            }
        }

        // Data export section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("データ管理", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.exportCsv() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📤 CSVエクスポート")
                }
                if (exportStatus.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = exportStatus,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}
