package com.example.finance.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarStyle
import com.example.finance.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtScreen() {
    val barChartListSize = 7
    val maxRange = 100
    val yStepSize = 5

    val barChartData = DataUtils.getBarChartData(
        barChartListSize,
        maxRange,
        barChartType = BarChartType.VERTICAL,
        dataCategoryOptions = DataCategoryOptions()
    )
    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(barChartData.size - 1)
        .bottomPadding(10.dp)
        .startDrawPadding(30.dp)
        .axisLabelAngle(20f)
        .labelData { index -> barChartData[index].label }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(yStepSize)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { index -> ((index * (maxRange / yStepSize)).toString()) }
        .build()

    val finalBarChartData = BarChartData(
        chartData = barChartData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        tapPadding = 20.dp,
        barStyle = BarStyle(barWidth = 25.dp),
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
                items(1) { parentIndex ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.inverseOnSurface)
                            .border(
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        BarChart(
                            modifier = Modifier.height(350.dp),
                            barChartData = finalBarChartData
                        )
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                    DebtBoxList()
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun DebtBoxList() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = true
    ) {
        items(7) { childIndex -> // Nested LazyColumn
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .border(
                        border = BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {

            }
        }
    }
}
