package com.example.finance.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.finance.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AddDebtScreen() {
    val donutChartData = PieChartData(
        slices = listOf(
            PieChartData.Slice("Январь", 15f, Color(0xFF3F51B5)), // Измененный цвет
            PieChartData.Slice("Февраль", 30f, Color(0xFF4CAF50)), // Измененный цвет
            PieChartData.Slice("Март", 40f,  Color(0xFFFFC107)), // Измененный цвет
            PieChartData.Slice("Апрель", 10f, Color(0xFFF44336)), // Измененный цвет
            PieChartData.Slice("Май", 15f, Color(0xFF703FB5)), // Измененный цвет
            PieChartData.Slice("Июнь", 30f, Color(0xFF009688)), // Измененный цвет
            PieChartData.Slice("Июль", 40f,  Color(0xFF00BCD4)), // Измененный цвет
            PieChartData.Slice("Август", 10f, Color(0xFF686565)) // Измененный цвет
        ),
        plotType = PlotType.Donut
    )

    val donutChartConfig = PieChartConfig(
        showSliceLabels = true,
        isAnimationEnable = true,
        animationDuration = 1000,
        activeSliceAlpha = .9f,
        strokeWidth = 55f,
        backgroundColor = Color.Transparent
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.DEBTS_LIST_TITLE),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Localized description",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.padding(12.dp)
            ) {
                items(1) { _ ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DonutPieChart(
                            modifier = Modifier
                                .width(200.dp)
                                .height(200.dp),
                            donutChartData,
                            donutChartConfig
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    // Вывод лейблов и цветов
                    Column(modifier = Modifier.padding(10.dp)) {
                        donutChartData.slices.forEach { slice ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(slice.color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = slice.label, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.SORT),
                            style = MaterialTheme.typography.titleMedium
                        )
                        var rotationState by remember { mutableStateOf(0f) }
                        val animatedRotation by animateFloatAsState(
                            targetValue = rotationState,
                            animationSpec = tween(durationMillis = 300)
                        )

                        IconButton(onClick = { rotationState += 180f }) {
                            Icon(
                                painter = painterResource(id = R.drawable.sort_icon),
                                contentDescription = "Localized description",
                                modifier = Modifier
                                    .size(30.dp)
                                    .graphicsLayer(rotationZ = animatedRotation)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
