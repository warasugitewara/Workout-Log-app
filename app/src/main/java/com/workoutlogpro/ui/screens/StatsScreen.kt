package com.workoutlogpro.ui.screens

import android.view.ViewGroup
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.workoutlogpro.viewmodel.CalorieEntry
import com.workoutlogpro.viewmodel.StatsViewModel
import com.workoutlogpro.viewmodel.WeightEntry
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val weightHistory by viewModel.weightHistory.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val allSchedules by viewModel.allSchedules.collectAsState()
    val menus by viewModel.menus.collectAsState()
    val user by viewModel.user.collectAsState()

    val weeklyRate = viewModel.weeklyCompletionRate(allLogs, allSchedules)
    val monthlyRate = viewModel.monthlyCompletionRate(allLogs, allSchedules)
    val weeklyCalories = viewModel.weeklyCalories(allLogs)
    val monthlyCalories = viewModel.monthlyCalories(allLogs)
    val menuStatsList = viewModel.menuStats(allLogs, menus)
    val bmiHistory = user?.let { viewModel.bmiHistory(it.height) } ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "📊 統計・分析",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // ── 実施率 ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("トレーニング実施率", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                CompletionRateBar(label = "今週", rate = weeklyRate)
                Spacer(modifier = Modifier.height(8.dp))
                CompletionRateBar(label = "今月", rate = monthlyRate)
            }
        }

        // ── 体重推移 ──
        ChartCard(title = "体重推移") {
            if (weightHistory.isEmpty()) {
                EmptyChartMessage("トレーニング記録に体重を入力すると表示されます。")
            } else {
                WeightChart(entries = weightHistory, modifier = Modifier.fillMaxWidth().height(250.dp))
            }
        }

        // ── BMI推移 ──
        ChartCard(title = "BMI推移") {
            if (bmiHistory.isEmpty()) {
                EmptyChartMessage("ユーザー設定で身長を入力し、記録に体重を入力すると表示されます。")
            } else {
                BmiChart(entries = bmiHistory, modifier = Modifier.fillMaxWidth().height(250.dp))
            }
        }

        // ── 消費カロリー（週別） ──
        ChartCard(title = "消費カロリー（週別）") {
            if (weeklyCalories.all { it.calories == 0f }) {
                EmptyChartMessage("トレーニング記録を追加すると表示されます。")
            } else {
                CalorieBarChart(entries = weeklyCalories, modifier = Modifier.fillMaxWidth().height(220.dp))
            }
        }

        // ── 消費カロリー（月別） ──
        ChartCard(title = "消費カロリー（月別）") {
            if (monthlyCalories.all { it.calories == 0f }) {
                EmptyChartMessage("トレーニング記録を追加すると表示されます。")
            } else {
                CalorieBarChart(entries = monthlyCalories, modifier = Modifier.fillMaxWidth().height(220.dp))
            }
        }

        // ── 種目別統計 ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("種目別統計", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                if (menuStatsList.isEmpty()) {
                    EmptyChartMessage("トレーニングログを記録すると種目別の統計が表示されます。")
                } else {
                    menuStatsList.forEach { stat ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(stat.menuName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    StatChip("実施", "${stat.totalSessions}回")
                                    StatChip("平均時間", "${stat.avgDurationSec.toInt()}秒")
                                }
                                if (stat.repsHistory.size >= 2) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("回数推移", style = MaterialTheme.typography.labelSmall)
                                    RepsChart(
                                        repsHistory = stat.repsHistory,
                                        modifier = Modifier.fillMaxWidth().height(120.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── サマリー ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("サマリー", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                val totalSessions = allLogs.size
                val totalCalories = allLogs.sumOf { it.actualCalories.toDouble() }
                val avgFatigue = if (allLogs.isNotEmpty()) allLogs.map { it.fatigueLevel }.average() else 0.0

                SummaryRow("総トレーニング回数", "$totalSessions 回")
                SummaryRow("総消費カロリー", "%.0f kcal".format(totalCalories))
                SummaryRow("平均疲労度", "%.1f / 5".format(avgFatigue))

                if (weightHistory.isNotEmpty()) {
                    val first = weightHistory.first().weight
                    val last = weightHistory.last().weight
                    val diff = last - first
                    val sign = if (diff >= 0) "+" else ""
                    SummaryRow("体重変化", "${sign}%.1f kg".format(diff))
                }

                if (bmiHistory.isNotEmpty()) {
                    SummaryRow("現在のBMI", "%.1f".format(bmiHistory.last().weight))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── Reusable components ──

@Composable
fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun EmptyChartMessage(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 32.dp)
    )
}

@Composable
fun CompletionRateBar(label: String, rate: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("%.0f%%".format(rate), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (rate / 100f).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(10.dp),
            color = when {
                rate >= 80f -> MaterialTheme.colorScheme.primary
                rate >= 50f -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.error
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun StatChip(label: String, value: String) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text("$label: ", style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

// ── Charts ──

@Composable
fun WeightChart(entries: List<WeightEntry>, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                description.isEnabled = false
                setTouchEnabled(true); isDragEnabled = true; setScaleEnabled(true); setPinchZoom(true)
                legend.isEnabled = true
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM; granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        private val sdf = SimpleDateFormat("M/d", Locale.JAPAN)
                        override fun getFormattedValue(value: Float) = sdf.format(Date(value.toLong()))
                    }
                }
                axisLeft.granularity = 0.5f; axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val chartEntries = entries.map { Entry(it.date.toFloat(), it.weight) }
            val dataSet = LineDataSet(chartEntries, "体重 (kg)").apply {
                color = AndroidColor.parseColor("#1565C0"); lineWidth = 2.5f
                setCircleColor(AndroidColor.parseColor("#1565C0")); circleRadius = 4f
                setDrawCircleHole(true); circleHoleRadius = 2f; valueTextSize = 10f
                setDrawFilled(true); fillColor = AndroidColor.parseColor("#42A5F5"); fillAlpha = 50
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }
            chart.data = LineData(dataSet); chart.invalidate()
        },
        modifier = modifier
    )
}

@Composable
fun BmiChart(entries: List<WeightEntry>, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                description.isEnabled = false
                setTouchEnabled(true); isDragEnabled = true; setScaleEnabled(true); setPinchZoom(true)
                legend.isEnabled = true
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM; granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        private val sdf = SimpleDateFormat("M/d", Locale.JAPAN)
                        override fun getFormattedValue(value: Float) = sdf.format(Date(value.toLong()))
                    }
                }
                axisLeft.granularity = 0.1f; axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val chartEntries = entries.map { Entry(it.date.toFloat(), it.weight) }
            val dataSet = LineDataSet(chartEntries, "BMI").apply {
                color = AndroidColor.parseColor("#FF6D00"); lineWidth = 2.5f
                setCircleColor(AndroidColor.parseColor("#FF6D00")); circleRadius = 4f
                setDrawCircleHole(true); circleHoleRadius = 2f; valueTextSize = 10f
                setDrawFilled(true); fillColor = AndroidColor.parseColor("#FF9E40"); fillAlpha = 50
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }
            chart.data = LineData(dataSet); chart.invalidate()
        },
        modifier = modifier
    )
}

@Composable
fun CalorieBarChart(entries: List<CalorieEntry>, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                description.isEnabled = false
                setTouchEnabled(true); legend.isEnabled = false
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM; granularity = 1f
                    setDrawGridLines(false)
                }
                axisLeft.apply { axisMinimum = 0f; granularity = 50f }
                axisRight.isEnabled = false
                setFitBars(true)
            }
        },
        update = { chart ->
            val barEntries = entries.mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.calories)
            }
            val dataSet = BarDataSet(barEntries, "カロリー (kcal)").apply {
                color = AndroidColor.parseColor("#4CAF50")
                valueTextSize = 9f
            }
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(entries.map { it.label })
            chart.data = BarData(dataSet).apply { barWidth = 0.6f }
            chart.invalidate()
        },
        modifier = modifier
    )
}

@Composable
fun RepsChart(repsHistory: List<Pair<Long, Int>>, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                description.isEnabled = false; legend.isEnabled = false
                setTouchEnabled(false)
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM; granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        private val sdf = SimpleDateFormat("M/d", Locale.JAPAN)
                        override fun getFormattedValue(value: Float) = sdf.format(Date(value.toLong()))
                    }
                    setDrawGridLines(false)
                }
                axisLeft.granularity = 1f; axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val chartEntries = repsHistory.map { Entry(it.first.toFloat(), it.second.toFloat()) }
            val dataSet = LineDataSet(chartEntries, "回数").apply {
                color = AndroidColor.parseColor("#AB47BC"); lineWidth = 2f
                setCircleColor(AndroidColor.parseColor("#AB47BC")); circleRadius = 3f
                setDrawValues(false); mode = LineDataSet.Mode.CUBIC_BEZIER
            }
            chart.data = LineData(dataSet); chart.invalidate()
        },
        modifier = modifier
    )
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
