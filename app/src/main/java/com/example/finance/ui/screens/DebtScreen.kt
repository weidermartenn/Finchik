package com.example.finance.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.finance.R
import com.example.finance.model.data.User
import com.example.finance.model.supabase.SupabaseHelper
import com.example.finance.ui.widgets.AddDebtDialog
import com.example.finance.ui.widgets.DebtsList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DebtScreen(
    onProfileClick: () -> Unit,
    goToDebtScreen: () -> Unit,
    sharedPreferences: SharedPreferences
) {
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
        isClickOnSliceEnabled = false,
        showSliceLabels = false,
        isAnimationEnable = true,
        animationDuration = 1000,
        activeSliceAlpha = .9f,
        strokeWidth = 24f,
        backgroundColor = MaterialTheme.colorScheme.surfaceBright
    )
    val navController = rememberNavController()

    val id = sharedPreferences.getString("userId", "") ?: ""
    val coroutineScope = rememberCoroutineScope()

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
                    IconButton(onClick = {

                    }) {
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
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable() {
                            goToDebtScreen()
                        }
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
            Spacer(modifier = Modifier.height(72.dp))
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
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.swipe_instructions),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(12.dp))
                DebtsList(id, sharedPreferences)
            }
        }
    }
    if (showAddDebtDialog) {
        AddDebtDialog(
            onDismiss = { showAddDebtDialog = false },
            userId = id,
            sharedPreferences = sharedPreferences
        )
    }
}