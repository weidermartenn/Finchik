package com.example.finance.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.finance.AddDebtDialog
import com.example.finance.BoxList
import com.example.finance.R
import com.google.android.libraries.maps.model.Circle
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen(onProfileClick: () -> Unit) {
    var showAddDebtDialog by remember { mutableStateOf(false) }
    val donutChartData = PieChartData(
        slices = listOf(
            PieChartData.Slice("", 15f, Color(0xFF5F0A87)),
            PieChartData.Slice("", 30f, Color(0xFF20BF55)),
            PieChartData.Slice("", 40f,  Color(0xFFEC9F05)),
            PieChartData.Slice("", 10f, Color(0xFFF53844))
        ),
        plotType = PlotType.Donut
    )
    val donutChartConfig = PieChartConfig(
        showSliceLabels = false,
        isAnimationEnable = true,
        animationDuration = 1000,
        activeSliceAlpha = .9f,
        strokeWidth = 24f,
        backgroundColor = MaterialTheme.colorScheme.surfaceBright
    )

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.PROFILE),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onProfileClick()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Localized description",
                            modifier = Modifier
                                .size(35.dp)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(MaterialTheme.colorScheme.surfaceBright)
                        .border(
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .width(180.dp)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = stringResource(id = R.string.ALL_OPERATIONS_TITLE),
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = stringResource(id = R.string.OPTION_TEXT),
                                style = MaterialTheme.typography.bodyLarge,
                                softWrap = true,
                                maxLines = 3,
                                modifier = Modifier
                                    .width(190.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .width(100.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DonutPieChart(
                                modifier = Modifier
                                    .width(90.dp)
                                    .height(90.dp),
                                donutChartData,
                                donutChartConfig
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column (
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.DEBTS_LIST_TITLE),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(12.dp))
                BoxList()
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        showAddDebtDialog = true
                    },
                    modifier = Modifier
                        .width(240.dp),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.ADD_A_DEBT),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
    if (showAddDebtDialog) {
        AddDebtDialog(onDismiss = { showAddDebtDialog = false })
    }
}