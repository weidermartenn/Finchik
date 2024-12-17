package com.example.finance.ui.screens

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.finance.R
import com.example.finance.ui.widgets.OperationsList
import io.ktor.utils.io.concurrent.shared

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun OperationsScreen(
    sharedPreferences: SharedPreferences
) {
    var selectedYear by remember { mutableStateOf(2024) }
    var clickedMonth by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarHostState) {
        snackbarHostState.showSnackbar("Вы выбрали месяц: $clickedMonth")
    }

    val id = sharedPreferences.getString("userId", "") ?: ""
    val coroutineScope = rememberCoroutineScope()

    // Данные диаграммы
    val donutChartData = PieChartData(
        slices = listOf(
            PieChartData.Slice("Январь", 15f, Color(0xFF3F51B5)),
            PieChartData.Slice("Февраль", 5f, Color(0xFF4CAF50)),
            PieChartData.Slice("Март", 0f, Color(0xFFFFC107)),
            PieChartData.Slice("Апрель", 0f, Color(0xFFF44336)),
            PieChartData.Slice("Май", 0f, Color(0xFF703FB5)),
            PieChartData.Slice("Июнь", 0f, Color(0xFF009688)),
            PieChartData.Slice("Июль", 0f, Color(0xFF00BCD4)),
            PieChartData.Slice("Август", 0f, Color(0xFF686565))
        ),
        plotType = PlotType.Donut
    )

    // Конфигурация диаграммы
    val donutChartConfig = PieChartConfig(
        showSliceLabels = false, // Убираем подписи слайсов
        isAnimationEnable = true,
        animationDuration = 1000,
        strokeWidth = 30f,
        backgroundColor = Color.Transparent,
        activeSliceAlpha = .9f,
        isClickOnSliceEnabled = true // Включаем возможность клика
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.OPERATIONS), // Название экрана
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            OperationsList(id, sharedPreferences)
        }
    }
}
