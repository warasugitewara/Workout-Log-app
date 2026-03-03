package com.workoutlogpro.ui.screens

import android.view.ViewGroup
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.workoutlogpro.viewmodel.StatsViewModel
import com.workoutlogpro.viewmodel.WeightEntry
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(viewModel: StatsViewModel) {
    val weightHistory by viewModel.weightHistory.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()

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

        // Weight chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "体重推移",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (weightHistory.isEmpty()) {
                    Text(
                        text = "データがまだありません。\nトレーニング記録に体重を入力すると表示されます。",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                } else {
                    WeightChart(
                        entries = weightHistory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                }
            }
        }

        // Summary stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "サマリー",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
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
            }
        }
    }
}

@Composable
fun WeightChart(entries: List<WeightEntry>, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                legend.isEnabled = true

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        private val sdf = SimpleDateFormat("M/d", Locale.JAPAN)
                        override fun getFormattedValue(value: Float): String {
                            return sdf.format(Date(value.toLong()))
                        }
                    }
                }
                axisLeft.apply {
                    granularity = 0.5f
                }
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val chartEntries = entries.mapIndexed { _, entry ->
                Entry(entry.date.toFloat(), entry.weight)
            }
            val dataSet = LineDataSet(chartEntries, "体重 (kg)").apply {
                color = AndroidColor.parseColor("#1565C0")
                lineWidth = 2.5f
                setCircleColor(AndroidColor.parseColor("#1565C0"))
                circleRadius = 4f
                setDrawCircleHole(true)
                circleHoleRadius = 2f
                valueTextSize = 10f
                setDrawFilled(true)
                fillColor = AndroidColor.parseColor("#42A5F5")
                fillAlpha = 50
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        },
        modifier = modifier
    )
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
